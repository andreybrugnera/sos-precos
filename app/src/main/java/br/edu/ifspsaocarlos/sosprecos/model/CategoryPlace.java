package br.edu.ifspsaocarlos.sosprecos.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by Andrey R. Brugnera on 17/04/2018.
 */
public class CategoryPlace implements Serializable {
    private String id;
    private String categoryId;
    private String placeId;

    public CategoryPlace() {
    }

    public CategoryPlace(String categoryId, String placeId) {
        this.categoryId = categoryId;
        this.placeId = placeId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategoryPlace)) return false;
        CategoryPlace that = (CategoryPlace) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
