package br.edu.ifspsaocarlos.sosprecos.model;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Andrey R. Brugnera on 05/03/2018.
 */
public class ServiceSubCategory implements Serializable{
    private String name;
    private Map<String, Service> services;

    public ServiceSubCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Service> getServices() {
        return services;
    }

    public void setServices(Map<String, Service> services) {
        this.services = services;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServiceSubCategory)) return false;

        ServiceSubCategory that = (ServiceSubCategory) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
