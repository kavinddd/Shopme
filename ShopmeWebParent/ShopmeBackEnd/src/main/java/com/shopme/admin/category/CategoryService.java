package com.shopme.admin.category;

import com.shopme.common.entity.Category;

import java.util.List;
import java.util.Map;

public interface CategoryService {

    List<Category> listAll();

    Category save(Category category);

    boolean isUnique(String name, String alias, Integer id);
    boolean isNameUnique(String name, Integer id);

    boolean isAliasUnique(String alias, Integer id);

    void deleteById(Integer id) throws CategoryNotFoundException;

    Category findById(Integer id) throws CategoryNotFoundException;

    Map<Integer, String> countAllHierarchyLevel();

    List<Category> listAllWithHierarchicalName();

    List<Category> listCategoriesByPage(CategoryPageInfo categoryPageInfo,int pageNum, String sortDir, String keyword);

    void updateCategoryStatusById(Integer id, boolean status) throws CategoryNotFoundException;
}
