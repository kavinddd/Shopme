package com.shopme.admin.brand;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BrandRepositoryTest {

    @Autowired
    BrandRepository repository;
    @Test
    void shouldAddBrand() {
        Brand brand = new Brand();
        brand.setName("Test");
        brand.setLogo("test.png");
        repository.save(brand);

        List<Brand> brands = repository.findAll();

        brands.forEach( b -> System.out.println(b.getId()));

        assertEquals(1, brands.size());
    }

    @Test
    void shouldDeleteBrand() {
        Brand brand = new Brand();
        brand.setName("Test");
        brand.setLogo("test.png");
        repository.save(brand);

        List<Brand> brands = repository.findAll();

        assertEquals(1, brands.size());

        repository.deleteAll();

        List<Brand> afterDeleteBrands = repository.findAll();
        assertEquals(0, afterDeleteBrands.size());


    }

    @Test
    void shouldHaveChildrenEntities() {

        Category firstCategory = new Category("Test First Category");
        Category secondCategory = new Category("Test Second Category");


        Brand brand = new Brand();
        brand.setName("test");
        brand.setLogo("test.png");
        brand.getCategories().addAll(List.of(firstCategory, secondCategory));

        repository.save(brand);

        Optional<Brand> foundBrand = repository.findByName("test");

        assertTrue(foundBrand.isPresent());

        assertEquals(2, foundBrand.get().getCategories().size());

    }
}
