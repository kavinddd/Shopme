package com.shopme.admin.category;

import com.shopme.common.entity.Category;
import org.assertj.core.util.Streams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CategoryServiceImpl implements CategoryService{

    private CategoryRepository categoryRepo;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    @Override
    public List<Category> listAll() {
        return categoryRepo.findAll();
    }

    @Override
    public Category save(Category category) {
        Category savedCategory = categoryRepo.save(category);
        return savedCategory;
    }

    @Override
    public boolean isNameUnique(String name) {
        List<Category> allCategories = categoryRepo.findAll();
        for (Category category : allCategories) {
            if (category.getName().equals(name)) return false;
        }
        return true;
    }
    @Override
    public boolean isAliasUnique(String alias) {
        List<Category> allCategories = categoryRepo.findAll();
        for (Category category : allCategories) {
            if (category.getAlias().equals(alias)) return false;
        }
        return true;
    }

    @Override
    public void deleteById(Integer id) {
        categoryRepo.deleteById(id);
    }

    @Override
    public Category findById(Integer id) {
        return categoryRepo.findById(id).get();
    }


    @Override
    public Map<Integer, String> countAllHierarchyLevel() {
        List<Category> categories = categoryRepo.findAll();

        // Used to store Category.id() : hierarchyLevel
        // LinkedHashMap to keep order of insertion
        Map<Integer, String> categoryIdWithHierarchyLevel = new LinkedHashMap<>();

        int hierarchyLevel; // higher the level, more parent it has

        // these are root (no parent)
        for (Category category: categories) {
            // skip non-root category
            if (categoryIdWithHierarchyLevel.containsKey(category.getId())) {
                continue;
            }
            // since they are root, so hierarchy level starts at 0
            hierarchyLevel = 0;
            countAndStoreHierarchyLevel(hierarchyLevel, category, categoryIdWithHierarchyLevel);
        }
        System.out.println(categoryIdWithHierarchyLevel);

        return categoryIdWithHierarchyLevel;
    }

    private void countAndStoreHierarchyLevel(int level, Category category, Map<Integer, String> levels) {
        levels.put(category.getId(), "--".repeat(level) + category.getName());
        Set<Category> children = category.getChildren();
        int tempLevel;

        if (children.size() > 0) {
            for (Category child: children) {
                tempLevel = level+1; // children level
                // do the same thing if there is a children
                countAndStoreHierarchyLevel(tempLevel, child, levels);
            }
        }
    }
}
