package com.shopme.admin.category;

import com.shopme.common.entity.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Set;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class CategoryRepositoryTest {
    private CategoryRepository repo;

    @Autowired
    public CategoryRepositoryTest(CategoryRepository repo) {
        this.repo = repo;
    }

    @Test
    public void testCreateRootCategory() {
        Category category = new Category("Computers");
        Category savedCategory = repo.save(category);

        assert savedCategory.getId() >= 0;
    }
    @Test
    public void testCreateSubCategory() {
        Category parent = repo.findById(1).get();
        Category subCategory = new Category("Desktops", parent);
        Category savedCategory = repo.save(subCategory);
        assert savedCategory.getId() >= 0;
    }
    @Test
    public void testGetCategory() {
        Category retrievedCategory = repo.findById(1).get();
        System.out.println(retrievedCategory);
        Set<Category> children = retrievedCategory.getChildren();
        System.out.println(children);
        System.out.println(children.size());
        children.forEach(category -> System.out.println(category.getName()));
        assert children.size() > 0;
    }

    @Test
    public void testCreateMoreCategoryWithSameParent() {
        Category parent = repo.findById(1).get();
        Category labtop = new Category("Labtop", parent);
        repo.save(labtop);
        assert parent.getChildren().size() > 1;
    }

    @Test
    public void testPrintHierachicalCategories(){
        Iterable<Category> categories = repo.findAll();
        String whitespace = "---";

        for (Category category : categories) {
            System.out.println(category.getName());
            if (category.getChildren() != null) {
                category.getChildren().forEach(
                        subCategory -> System.out.println(whitespace + subCategory.getName())
                );
            }
        }
    }
}
