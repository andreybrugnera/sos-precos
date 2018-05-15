package br.edu.ifspsaocarlos.sosprecos.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by Andrey R. Brugnera on 05/03/2018.
 */
public class Category implements Serializable{
    private String id;
    private String name;
    private String imagePath;
    @Exclude
    private Boolean selected;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
