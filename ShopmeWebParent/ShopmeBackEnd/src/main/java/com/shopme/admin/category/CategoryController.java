package com.shopme.admin.category;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.export.CategoryCsvExporterStrategy;
import com.shopme.admin.export.Exporter;
import com.shopme.common.entity.Category;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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


    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories")
    public String listFirstPage(@RequestParam(name = "sortDir", required = false) String sortDir, Model model){
        return listByPage(1, "asc", model, null);
    }

    @GetMapping("/categories/page/{pageNum}")
    public String listByPage(@PathVariable(name="pageNum") int pageNum,
                             @RequestParam(name = "sortDir", required = false) String sortDir,
                             Model model,
                             @RequestParam(name="keyword", required = false) String keyword) {

        sortDir = ( sortDir == null ) ? "asc" : sortDir;

        CategoryPageInfo categoryPageInfo = new CategoryPageInfo();

        List<Category> categories;

        // categoryPageInfo is mutable here, the method will update properties inside it
        categories = categoryService.listCategoriesByPage(categoryPageInfo, pageNum, sortDir, keyword);

        int totalPages = categoryPageInfo.getTotalPages();
        long totalElements = categoryPageInfo.getTotalElements();

        // if category per page = 2
        // page 1, should start at 1
        int startCount = 1 +  ( CategoryServiceImpl.CATEGORY_PER_PAGE * ( pageNum - 1 ));
        // page 1, should end at 2
        long endCount = startCount + CategoryServiceImpl.CATEGORY_PER_PAGE - 1;
        // end count should not exceed number of total elements
        endCount = endCount > totalElements ? totalElements : endCount;

        model.addAttribute("categories", categories);
        model.addAttribute("reversedSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalElements);
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("sortDir", sortDir);
        // this is fixed in category
        model.addAttribute("sortField", "name");
        model.addAttribute("keyword", keyword);
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
        String message = "Category ID %d has been deleted.".formatted(id);
        try {
            categoryService.deleteById(id);
        }
        catch (CategoryNotFoundException e) {
            message = e.getMessage();
        }
        redirectAttributes.addFlashAttribute("message", message);
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

    @GetMapping("/categories/{id}/enabled/{status}")
    public String reverseStatusCategory(@PathVariable(name="id") Integer id, @PathVariable(name="status") boolean enabled,
                                        RedirectAttributes redirectAttributes) throws CategoryNotFoundException {

        categoryService.updateCategoryStatusById(id, enabled);
        redirectAttributes.addFlashAttribute("message", "Category " + id + " has been " + (enabled ? "enabled" : "disabled"));
        return "redirect:/categories";
    }

    @GetMapping("/categories/export/{exportType}")
    public void export(@PathVariable(name="exportType") String exportType,
                         HttpServletResponse response) throws IOException {

        List<Category> allCategories = categoryService.listAllWithHierarchicalName();
        Exporter<Category> categoryExporter =
                switch (exportType) {
                    case "csv" -> new Exporter<>(new CategoryCsvExporterStrategy());
                    default -> null;
                };
        if (categoryExporter != null) categoryExporter.export(allCategories, response);
    }
}
