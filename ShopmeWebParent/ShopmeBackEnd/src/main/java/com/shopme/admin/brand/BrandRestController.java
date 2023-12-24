package com.shopme.admin.brand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class BrandRestController {

    BrandService brandService;

    @Autowired
    public BrandRestController(BrandService brandService) {
        this.brandService = brandService;
    }

    @PostMapping("brands/new/checkUnique")
    public String checkUnique(@RequestParam Map<String, String> payload) {
        String name = payload.get("name");
        Integer id = Integer.valueOf(payload.get("brandId"));

        boolean isNameUnique = brandService.isNameUnique(name, id);
        if (isNameUnique){
            return "OK";
        }

        return "Name Duplicated";
    }
}
