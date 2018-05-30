package br.edu.ifspsaocarlos.sosprecos.util.location;

import java.io.Serializable;

/**
 * Created by Andrey R. Brugnera on 23/05/2018.
 */
public class LocationAddress implements Serializable{
    private String street;
    private String city;
    private String state;
    private String country;
    private String zipCode;

    public LocationAddress(String street, String city, String state,
                           String country, String zipCode) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.country = country;
        this.zipCode = zipCode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
}
