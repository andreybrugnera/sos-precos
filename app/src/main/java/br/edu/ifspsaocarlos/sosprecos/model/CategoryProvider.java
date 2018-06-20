package br.edu.ifspsaocarlos.sosprecos.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by Andrey R. Brugnera on 17/04/2018.
 */
public class CategoryProvider implements Serializable {
    private String id;
    private String categoryId;
    private String providerId;

    public CategoryProvider() {
    }

    public CategoryProvider(String categoryId, String providerId) {
        this.categoryId = categoryId;
        this.providerId = providerId;
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

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategoryProvider)) return false;
        CategoryProvider that = (CategoryProvider) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
