package com.shopme.admin.category;

import com.shopme.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepo;
    public static final int CATEGORY_PER_PAGE = 2;

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
        return categoryRepo.save(category);
    }

    @Override
    public boolean isUnique(String name, String alias, Integer id) {
        boolean isNameUnique = isNameUnique(name, id );
        boolean isAliasUnique = isAliasUnique(alias, id);
        return isAliasUnique && isNameUnique;
    }

    @Override
    public boolean isNameUnique(String name, Integer id) {
        Optional<Category> category = categoryRepo.findCategoryByName(name);
        if (category.isEmpty()) {
            return true;
        }
        if (category.get().getId() == id) {
            return true;
        }
        return false;
    }
    @Override
    public boolean isAliasUnique(String alias, Integer id) {
        Optional<Category> category = categoryRepo.findCategoryByAlias(alias);

        if (category.isEmpty()) {
            return true;
        }

        if (category.get().getId() == id) {
            return true;
        }

        return false;

    }

    @Override
    public void deleteById(Integer id) throws CategoryNotFoundException {

        Optional<Category> theCategoryOptional = categoryRepo.findById(id);

        if (theCategoryOptional.isEmpty()) {
            throw new CategoryNotFoundException("Category %d is not found".formatted(id));
        }
//
//        Category theCategory = theCategoryOptional.get();
//
//        Set<Category> children = theCategory.getChildren();
//
//        if (!children.isEmpty()) {
//            for (Category child : theCategory.getChildren()) {
//                child.setParent(null);
//            }
//        }

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
    @Transactional
    public void updateCategoryStatusById(Integer id, boolean status) throws CategoryNotFoundException {
        Optional<Category> theCategory = categoryRepo.findById(id);
        if (theCategory.isEmpty()) throw new CategoryNotFoundException("Category with ID %d was not found".formatted(id));

        categoryRepo.updateEnabled(id, status);
    }

    @Override
    public Map<Integer, String> countAllHierarchyLevel() {
        List<Category> categories = listAllWithHierarchicalName();

        // {1: Computer, 2: --Laptop}
        Map<Integer, String> categoryIdAndHierarchyName = categories.stream()
                .collect(
                        Collectors.toMap(
                                Category::getId,
                                Category::getName,
                                (a, b) -> b,
                                LinkedHashMap::new
                        ));

        System.out.println(categoryIdAndHierarchyName);
        return categoryIdAndHierarchyName;
    }

    @Override
    public List<Category> listAllWithHierarchicalName() {
        CategoryPageInfo dummyCategoryPageInfo = new CategoryPageInfo();
        List<Category> categories = listCategoriesByPage(dummyCategoryPageInfo, -1,"asc", null);

        return categories;
    }

    @Override
    public List<Category> listCategoriesByPage(CategoryPageInfo categoryPageInfo, int pageNum, String sortDir, String keyword) {

        Sort sort = Sort.by("name");
        sort = sortDir.equals("desc") ? sort.descending() : sort.ascending();

        boolean isUsedInForm = pageNum < 0;

        // doesn't require pagination
        if (isUsedInForm) {
            // this returns all root categories
            List<Category> allRootCategories = categoryRepo.findRootCategories(sort);
            // make it hierarchical
            return topologicalSortCategory(allRootCategories, sortDir);
        }


        Pageable pageable = PageRequest.of(pageNum - 1, CATEGORY_PER_PAGE, sort);

        Page<Category> pageCategories;

        boolean isSearching = (keyword != null) && (!keyword.isEmpty());

        if (isSearching) {
            pageCategories = categoryRepo.search(keyword, pageable);
        }
        else {
            pageCategories = categoryRepo.findRootCategories(pageable);
        }

        categoryPageInfo.setTotalPages(pageCategories.getTotalPages());
        categoryPageInfo.setTotalElements(pageCategories.getTotalElements());

        List<Category> rootCategories = pageCategories.getContent();

        // if search, don't implement hierarchy form
        if (isSearching) {
            return rootCategories;
        }

        return topologicalSortCategory(rootCategories, sortDir);

    }



    private List<Category> topologicalSortCategory(List<Category> rootCategories, String sortDir) {
        Stack<Category> stack = new Stack<>();
        Stack<Category> resultStack = new Stack<>();
        List<Category> sortedCategories = new ArrayList<>();

        for (Category rootCategory: rootCategories) {

            stack.push(rootCategory);
            // hierarchy level starts at 1 since root is already in the stack
            Stack<Category> reversedStack = traverseThroughAllChildren(stack, resultStack, 1, sortDir);

            // pop all the result stack to get a topological sort result
            while ( !reversedStack.isEmpty() ) {
                sortedCategories.add(reversedStack.pop());
            }

        }
        return sortedCategories;
    }

    private Stack<Category> traverseThroughAllChildren(Stack<Category> tempStack, Stack<Category> resultStack, Integer hierarchyLevel,
                                                       String sortDir) {

        Category currentCategory = tempStack.peek();

        Set<Category> childrenCategoriesSet = currentCategory.getChildren();

        // need to sort with name, otherwise result is inconsistent
        List<Category> soretdCategoryList = sortCategory(childrenCategoriesSet, sortDir);

        for (Category childCategory : soretdCategoryList) {
            String hierarchyName = "--".repeat(hierarchyLevel) + childCategory.getName();
            childCategory.setName(hierarchyName);
            tempStack.push(childCategory);
            // recursion
            traverseThroughAllChildren(tempStack, resultStack, hierarchyLevel + 1, sortDir);
        }

        // base case = no more children to iterate
        // pop itself, when all of its children are visited
        resultStack.push(tempStack.pop());

        // need to pop again to properly sort
        return resultStack;

    }

    private List<Category> sortCategory(Set<Category> categories, String sortDir) {
        Comparator<Category> categoryComparator = new Comparator<Category>() {
            @Override
            public int compare(Category o1, Category o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };

        if (sortDir.equals("desc")) categoryComparator.reversed();

        List<Category> sortedCategories = new ArrayList<>(categories);

        sortedCategories.sort(categoryComparator);

        return sortedCategories;

    }

}
