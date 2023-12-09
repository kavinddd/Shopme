package com.shopme.admin.category;

import com.shopme.common.entity.Category;

import java.util.List;
import java.util.Map;

public interface CategoryService {

    List<Category> listAll();

    Category save(Category category);

    boolean isNameUnique(String name);

    boolean isAliasUnique(String alias);

    void deleteById(Integer id);

    Category findById(Integer id);

    Map<Integer, String> countAllHierarchyLevel();
}
