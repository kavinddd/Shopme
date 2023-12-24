package com.shopme.admin.brand;

import com.shopme.common.entity.Brand;

import java.util.List;
import java.util.Optional;

public interface BrandService {

    List<Brand> listAllBrands();

    Brand findById(Integer id);

    boolean isNameUnique(String name, Integer id);

    Brand save(Brand brand);
}
