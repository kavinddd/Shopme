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

@Controller
public class CategoryController {
    private CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories")
    public String listAllCategories(Model model){
        List<Category> categories = categoryService.listAll();
        model.addAttribute("categories", categories);
        return "categories/categories";
    }

    @GetMapping("/categories/new")
    public String addCategory(Model model) {
        Category newCategory = new Category();
//        List<String> categories = categoryService.listHierarchyName();
        model.addAttribute("category", newCategory);
//        model.addAttribute("categories", categories);

        return "categories/category_form";
    }

    @PostMapping("/categories/save")
    public String saveCategory(Category category, RedirectAttributes redirectAttributes,
                               @RequestParam("fileImage") MultipartFile multipartFile) throws IOException {

        FileUploadUtil fileUploadUtil = new FileUploadUtil();
        String imageFileName = multipartFile.getOriginalFilename();
        String cleanedImageFileName = StringUtils.cleanPath(imageFileName);
        String uploadDir = "../category-images/" + category.getId();

        fileUploadUtil.saveFile(uploadDir, cleanedImageFileName, multipartFile);

        System.out.println(category);
        categoryService.save(category);
        redirectAttributes.addFlashAttribute("message", "New category has been created");
        return "redirect:/categories";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable(name="id") Integer id, RedirectAttributes redirectAttributes){
        categoryService.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Category ID %d has been deleted.".formatted(id));
        return "redirect:/categories";
    }

    @GetMapping("/categories/edit/{id}")
    public String editCategory(@PathVariable(name="id") Integer id, Model model) {
        Category category = categoryService.findById(id);
        model.addAttribute("category", category);


//        model.addAttribute("categories", categories);
        return "categories/category_form";
    }
}
