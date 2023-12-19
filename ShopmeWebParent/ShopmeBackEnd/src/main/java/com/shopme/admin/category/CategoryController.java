package com.shopme.admin.category;

import com.shopme.admin.FileUploadUtil;
import com.shopme.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
public class CategoryController {
    private CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories")
    public String listAllCategories(Model model){
        List<Category> categories = categoryService.listAllInHierachical();
        model.addAttribute("categories", categories);
        return "categories/categories";
    }

    @GetMapping("/categories/new")
    public String addCategory(Model model) {
        Category newCategory = new Category();
        Map<Integer,String> categoryIdWithHierarchyLevel = categoryService.countAllHierarchyLevel();
        model.addAttribute("category", newCategory);
        model.addAttribute("categoriesWithHierarchyLevel", categoryIdWithHierarchyLevel);
        model.addAttribute("pageTitle", "Create new category");
        return "categories/category_form";
    }

    @PostMapping("/categories/save")
    public String saveCategory(Category category, RedirectAttributes redirectAttributes,
                               @RequestParam("fileImage")MultipartFile multipartFile) throws IOException {
        boolean isImageUploaded = !multipartFile.isEmpty();
        boolean isCategoryHadPhoto = !category.getImage().isEmpty();

        String imageFileName = multipartFile.getOriginalFilename();
        String cleanedImageFileName = StringUtils.cleanPath(imageFileName);

        if (isImageUploaded) {
            category.setImage(cleanedImageFileName);
        } else if (!isCategoryHadPhoto) {
            category.setImage(null);
        }

        Category savedCategory = categoryService.save(category);

        if (isImageUploaded) {
            String uploadDir = "category-images/" + savedCategory.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, cleanedImageFileName, multipartFile);
        }


        redirectAttributes.addFlashAttribute("message", "The category has been saved successfully!");
        return "redirect:/categories";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable(name="id") Integer id, RedirectAttributes redirectAttributes) throws CategoryNotFoundException {
        categoryService.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Category ID %d has been deleted.".formatted(id));
        return "redirect:/categories";
    }

    @GetMapping("/categories/edit/{id}")
    public String editCategory(@PathVariable(name="id") Integer id, Model model) throws CategoryNotFoundException {
        Category category = categoryService.findById(id);
        Map<Integer, String> categoryIdWithHierarchyLevel = categoryService.countAllHierarchyLevel();
        model.addAttribute("category", category);
        model.addAttribute("categoriesWithHierarchyLevel", categoryIdWithHierarchyLevel);
        model.addAttribute("pageTitle", "Edit category id %d".formatted(id));
        return "categories/category_form";
    }
}
