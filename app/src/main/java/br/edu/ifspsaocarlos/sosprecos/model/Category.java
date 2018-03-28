package br.edu.ifspsaocarlos.sosprecos.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Andrey R. Brugnera on 05/03/2018.
 */
public class Category implements Serializable{
    private String name;
    private Set<String> serviceProviders;

    public Category() {
    }

    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getServiceProviders() {
        if(serviceProviders == null){
            serviceProviders = new HashSet<>();
        }
        return serviceProviders;
    }

    public void setServiceProviders(Set<String> serviceProviders) {
        this.serviceProviders = serviceProviders;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;

        Category that = (Category) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
