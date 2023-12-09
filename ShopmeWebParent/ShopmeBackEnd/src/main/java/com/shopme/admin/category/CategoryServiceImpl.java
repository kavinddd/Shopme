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
    public Map<Category, String> countAllHierarchyLevel() {
        List<Category> categories = categoryRepo.findAll();

        // Used to store Category : hierarchyLevel
        // LinkedHashMap to keep order of insertion
        Map<Category, Integer> levels = new LinkedHashMap<>();

        int hierarchyLevel; // higher the level, more parent it has

        // these are root (no parent)
        for (Category category: categories) {
            // skip non-root category
            if (levels.containsKey(category.getName())) {
                continue;
            }
            // since they are root, so hierarchy level starts at 0
            hierarchyLevel = 0;
            flattenHierarchy(hierarchyLevel, category, levels);
        }
        System.out.println(levels);
        Map<Category, String> result = levels.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> "--".repeat(entry.getValue()) + entry.getKey().getName()
                ));
        System.out.println(result);

        return result;
    }

    private void flattenHierarchy(int level, Category category, Map<Category, Integer> levels) {
        levels.put(category, level);
        Set<Category> children = category.getChildren();
        int tempLevel;

        if (children.size() > 0) {
            for (Category child: children) {
                tempLevel = level;
                flattenHierarchy(++tempLevel, child, levels);
            }
        }

    }
}
