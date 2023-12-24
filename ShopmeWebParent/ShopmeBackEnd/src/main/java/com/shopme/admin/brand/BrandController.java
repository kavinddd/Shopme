package com.shopme.admin.brand;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.category.CategoryService;
import com.shopme.common.entity.Brand;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Optional;

@Controller
public class BrandController {

    BrandService brandService;
    CategoryService categoryService;

    @Autowired
    public BrandController(BrandService brandService, CategoryService categoryService) {
        this.brandService = brandService;
        this.categoryService = categoryService;
    }

    @GetMapping("/brands")
    public String listAllBrands(Model model) {
        List<Brand> brands = brandService.listAllBrands();

        model.addAttribute("brands", brands);

        return "/brands/brands";
    }

    @GetMapping("/brands/new")
    public String addNewBrand(Model model) {

        Brand brand = new Brand();

        Map<Integer, String> categoriesWithHierarchyLevel = categoryService.countAllHierarchyLevel();

        model.addAttribute("brand", brand);
        model.addAttribute("pageTitle", "Creating new brand");
        model.addAttribute("categoriesWithHierarchyLevel", categoriesWithHierarchyLevel);

        return "/brands/brand_form";
    }

    @GetMapping("brands/edit/{id}")
    public String editBrand(Model model, @PathVariable(name="id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            Brand brand = brandService.findById(id);
            Map<Integer, String> categoriesWithHierarchyLevel = categoryService.countAllHierarchyLevel();
            model.addAttribute("brand", brand);
            model.addAttribute("categoriesWithHierarchyLevel", categoriesWithHierarchyLevel);
            return "/brands/brand_form";
        } catch (BrandNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/brands";
        }
    }

    @PostMapping("brands/save")
    public String saveBrand(Brand brand, @RequestParam("fileImage") MultipartFile multipartFile) throws IOException {
        boolean isLogoUploaded = !multipartFile.isEmpty();
        boolean isBrandHadLogo = !brand.getLogo().isEmpty();

        System.out.println(brand);
        String logoName = multipartFile.getOriginalFilename();
        String cleanedImageLogoName = StringUtils.cleanPath(logoName);

        if (isLogoUploaded) {
            brand.setLogo(cleanedImageLogoName);
        }

        Brand savedBrand = brandService.save(brand);

        if (isLogoUploaded){
            String uploadDir = "brand-logos/" + savedBrand.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, savedBrand.getLogo(), multipartFile);
        }

        return "redirect:/brands";
    }

}
