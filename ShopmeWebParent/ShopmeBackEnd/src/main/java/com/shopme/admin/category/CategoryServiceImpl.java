package com.shopme.admin.category;

import com.shopme.common.entity.Category;
import org.assertj.core.util.Streams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CategoryServiceImpl implements CategoryService {

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
    public boolean isNameUnique(String name, Integer id) {
        Category category = categoryRepo.findCategoryByName(name);
        if (category == null) return true;
        if (category.getId() == id) return true;
        return false;
    }
    @Override
    public boolean isAliasUnique(String alias, Integer id) {
        Category category = categoryRepo.findCategoryByAlias(alias);
        if (category == null) return true;
        if (category.getId() == id) return true;
        return false;
    }

    @Override
    public void deleteById(Integer id) throws CategoryNotFoundException {

        Optional<Category> theCategoryOptional = categoryRepo.findById(id);

        if (theCategoryOptional.isEmpty()) {
            throw new CategoryNotFoundException("Category %d is not found".formatted(id));
        }

        Category theCategory = theCategoryOptional.get();

        Set<Category> children = theCategory.getChildren();

        if (children.size() != 0) {
            for (Category child : theCategory.getChildren()) {
                child.setParent(null);
            }
        }

        categoryRepo.deleteById(id);
    }

    @Override
    public Category findById(Integer id) throws CategoryNotFoundException {
        Optional<Category> theCategoryOptional = categoryRepo.findById(id);

        if (theCategoryOptional.isEmpty()) {
            throw new CategoryNotFoundException("Category %d is not found".formatted(id));
        }

        return theCategoryOptional.get();
    }


    @Override
    public Map<Integer, String> countAllHierarchyLevel() {
        List<Category> categories = categoryRepo.findAll();

        // Used to store Category.id() : hierarchyLevel
        // LinkedHashMap to keep order of insertion
        Map<Integer, String> categoryIdWithHierarchyLevel = new LinkedHashMap<>();

        int hierarchyLevel; // higher the level, more parent it has

        // loop all category
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

    private void countAndStoreHierarchyLevel(int level, Category category, Map<Integer, String> categoryIdWithHierarchyName) {
        categoryIdWithHierarchyName.put(category.getId(), "--".repeat(level) + category.getName());
        Set<Category> children = category.getChildren();
        int tempLevel;

        if (children.size() > 0) {
            for (Category child: children) {
                tempLevel = level+1; // children level
                // do the same thing if there is a children
                countAndStoreHierarchyLevel(tempLevel, child, categoryIdWithHierarchyName);
            }
        }
    }
    @Override
    public List<Category> listAllInHierachical() {
        List<Category> categories = categoryRepo.findAll();

        List<Category> rootCategories = categories.stream()
                .filter(Category::isRoot)
                .toList();

        return topologicalSortCategory(rootCategories);
    }

    private List<Category> topologicalSortCategory(List<Category> rootCategories) {
        Stack<Category> stack = new Stack<>();
        Stack<Category> resultStack = new Stack<>();
        List<Category> sortedCategories = new ArrayList<>();

        for (Category rootCategory: rootCategories) {

            stack.push(rootCategory);
            // hierarchy level starts at 1 since root is already in the stack
            Stack<Category> reversedStack = DFS(stack, resultStack, 1);

            // pop all the result stack to get a topological sort result
            while ( !reversedStack.isEmpty() ) {
                sortedCategories.add(reversedStack.pop());
            }

        }
        return sortedCategories;
    }

    private Stack<Category> DFS(Stack<Category> tempStack, Stack<Category> resultStack, Integer hierarchyLevel) {

        Category currentCategory = tempStack.peek();
        Set<Category> childrenCategories = currentCategory.getChildren();

        for (Category childCategory : childrenCategories) {
            String hierarchyName = "--".repeat(hierarchyLevel) + childCategory.getName();
            childCategory.setName(hierarchyName);
            tempStack.push(childCategory);
            // recursion
            DFS(tempStack, resultStack, hierarchyLevel + 1);
        }

        // base case = no more children to iterate
        // pop itself, when all of its children are visited
        resultStack.push(tempStack.pop());

        // need to pop again to properly sort
        return resultStack;

    }

}
