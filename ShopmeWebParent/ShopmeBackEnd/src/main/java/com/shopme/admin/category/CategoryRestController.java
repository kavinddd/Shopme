package com.shopme.admin.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

        String name = payload.get("name");
        String alias = payload.get("alias");
        Integer id = Integer.valueOf(payload.get("categoryId"));

        if (!categoryService.isNameUnique(name, id)) return "Name Duplicated";
        if (!categoryService.isAliasUnique(alias, id)) return "Alias Duplicated";
        return "OK";
    }

}
