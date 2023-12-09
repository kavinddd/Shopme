package com.shopme.admin.category;

import com.shopme.common.entity.Category;
import com.shopme.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class CategoryRestController {

    CategoryService categoryService;

    @Autowired
    public CategoryRestController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/categories/new/checkUnique")
    public String checkUnique(@RequestParam Map<String, String> payload) {
        System.out.println("checkUnique is being called" );
        String name = payload.get("name");
        String alias = payload.get("alias");
        System.out.println(name + " " + alias);
        if (!categoryService.isNameUnique(name)) return "Name Duplicated";
        if (!categoryService.isAliasUnique(alias)) return "Alias Duplicated";
        return "OK";
    }

    @GetMapping("/categories/test")
    public Map<Integer, String> test() {
        Map<Integer, String> levels = categoryService.countAllHierarchyLevel();
        return levels;
    }
}
