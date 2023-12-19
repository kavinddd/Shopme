package com.shopme.admin.category;

import com.shopme.common.entity.Category;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.Set;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase()
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

        assertTrue(savedCategory.isRoot());
        assertTrue(savedCategory.getId() > 0);
    }
    @Test
    public void testCreateSubCategory() {
        Category parent = repo.findById(1).get();
        Category subCategory = new Category("Desktops", parent);
        Category savedCategory = repo.save(subCategory);
        assertTrue(savedCategory.isChildren());
        assertTrue(savedCategory.getId() > 0);
    }
    @Test
    public void testGetCategoryById() {
        Optional<Category> retrievedCategory = repo.findById(1);
        assertTrue(retrievedCategory.isPresent());
    }

    @Test
    public void testCreateMoreCategoryWithSameParent() {
        Category parent = repo.findById(1).get();
        Category laptop = new Category("Laptop", parent);
        repo.save(laptop);

        assertTrue(parent.getChildren().size() > 1);
    }

    @Test
    public void testFindByName() {
        Category category = repo.findCategoryByName("Laptop");

        assertNotNull(category);
        assertEquals("Laptop", category.getName());

    }

    @Test
    public void testFindByAlias() {
        String alias = "desktops";
        Category category = repo.findCategoryByAlias(alias);
        assertNotNull(category);
        assertEquals(alias, category.getAlias());
    }


}
