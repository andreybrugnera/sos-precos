package br.edu.ifspsaocarlos.sosprecos.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Andrey R. Brugnera on 05/03/2018.
 */
public class ServiceCategory implements Serializable{
    private String name;
    private Map<String, ServiceSubCategory> subCategories;

    public ServiceCategory() {
    }

    public ServiceCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, ServiceSubCategory> getSubCategories() {
        if (subCategories == null) {
            subCategories = new HashMap<>();
        }
        return subCategories;
    }

    public void setSubCategories(Map<String, ServiceSubCategory> subCategories) {
        this.subCategories = subCategories;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServiceCategory)) return false;

        ServiceCategory that = (ServiceCategory) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
