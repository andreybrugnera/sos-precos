package br.edu.ifspsaocarlos.sosprecos.util.location;

import java.io.Serializable;

/**
 * Created by Andrey R. Brugnera on 23/05/2018.
 */
public class LocationAddress implements Serializable{
    private String address;
    private String city;
    private String state;
    private String country;
    private String zipCode;

    public LocationAddress(String address, String city, String state,
                           String country, String zipCode) {
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
        this.zipCode = zipCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
