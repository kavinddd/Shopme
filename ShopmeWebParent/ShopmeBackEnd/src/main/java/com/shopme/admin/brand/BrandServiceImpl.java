package com.shopme.admin.brand;

import com.shopme.common.entity.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BrandServiceImpl implements BrandService{
    BrandRepository brandRepository;

    @Autowired
    public BrandServiceImpl(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Override
    public List<Brand> listAllBrands() {

        return brandRepository.findAll();
    }

    @Override
    public Brand findById(Integer id) {
        Optional<Brand> brand = brandRepository.findById(id);
        if (brand.isEmpty()) throw new BrandNotFoundException("Brand ID %d was not found".formatted(id));

        return brand.get();
    }

    @Override
    public boolean isNameUnique(String name, Integer id) {

        Optional<Brand> brand = brandRepository.findByName(name);

        if (brand.isEmpty()) {
            return true;
        }

        return brand.get().getId().equals(id);
    }

    @Override
    public Brand save(Brand brand) {

        return brandRepository.save(brand);
    }


}
