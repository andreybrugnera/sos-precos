package br.edu.ifspsaocarlos.sosprecos.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Andrey R. Brugnera on 05/03/2018.
 */
public class Category implements Serializable{
    private String id;
    private String name;
    private String imagePath;
    private List<String> serviceProviders;

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

    public List<String> getServiceProviders() {
        if(serviceProviders == null){
            serviceProviders = new ArrayList();
        }
        return serviceProviders;
    }

    public void setServiceProviders(List<String> serviceProviders) {
        this.serviceProviders = serviceProviders;
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
