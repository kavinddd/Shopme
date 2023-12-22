package com.shopme.admin.category;

import com.shopme.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;


@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class CategoryServiceTests {
    @MockBean
    private CategoryRepository repo;

    @InjectMocks
    private CategoryServiceImpl service;

    @Test
    public void testCheckUniqueByName() {
        Integer id = 55;
        String name = "whatever";

        Category category = new Category();
        category.setName(name);
        category.setId(id);

        Optional<Category> optionalCategory = Optional.of(category);
        Mockito.when(repo.findCategoryByName(name)).thenReturn(optionalCategory);

        assertTrue(service.isNameUnique(name, id), "Should return true (is unique)");
        assertFalse(service.isNameUnique(name, 30), "Should return false (is not unique)");
    }

    @Test
    public void testCheckUniqueByAlias() {

        Integer id = 55;
        String alias = "this_should_be_duplicated";

        Category category = new Category();
        category.setId(id);
        category.setAlias(alias);

        Optional<Category> optionalCategory = Optional.of(category);
        Mockito.when(repo.findCategoryByAlias(alias)).thenReturn(optionalCategory);

        assertTrue(service.isAliasUnique(alias, id ), "Should return true (is Unique)");
        assertFalse(service.isAliasUnique(alias, id-1), "Should return false (is not unique)");

    }
}
