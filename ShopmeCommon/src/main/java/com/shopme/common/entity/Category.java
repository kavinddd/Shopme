package com.shopme.common.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 128, nullable = false, unique = true)
    private String name;
    @Column(length = 64, nullable = false, unique = true)
    private String alias;
    @Column(length = 128)
    private String image;
    private boolean enabled;
    @ManyToOne(cascade = {CascadeType.DETACH})
    @JoinColumn(name="parent_id", unique = false)
    private Category parent;
    @OneToMany(mappedBy = "parent")
    private Set<Category> children = new HashSet<>();

    public Category() {
        this("", null);
    }

    public Category(String name) {
        this(name, null);
    }

    public Category(String name, Category parent) {
        this(name, parent, name.toLowerCase().replace(" ", "_"));
    }

    public Category(String name, Category parent, String alias) {
        this(0, name ,alias, parent, null);
    }

    public Category(int id, String name, String alias, Category parent, String image) {
        this.id = id;
        this.name = name;
        this.alias = alias;
        this.image = image;
        this.parent = parent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public Set<Category> getChildren() {
        return children;
    }

    public void setChildren(Set<Category> children) {
        this.children = children;
    }

    @Transient
    public String getImagePath() {
        if (image == null) return "/images/image-thumbnail.png";
        return "%s/%d/%s".formatted("/category-images", id, image);
    }
    @Transient
    public boolean isRoot() {
        return this.getParent() == null;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                ", image='" + image + '\'' +
                ", enabled=" + enabled +
                ", parent=" + parent + "}";
    }


    @Transient
    public boolean isChildren() {
        return this.getParent() != null;
    }

    @Transient
    public boolean hasChildren() {
        return !this.getChildren().isEmpty();
    }

    @Transient
    public boolean isDeletable() {
        return !hasChildren();
    }

    @Transient
    public Category copy() {
        Category copiedCategory = new Category(id,name, alias, parent, image);
        copiedCategory.setChildren(children);
        return copiedCategory;
    }
}
