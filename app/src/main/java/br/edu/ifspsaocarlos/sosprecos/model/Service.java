package br.edu.ifspsaocarlos.sosprecos.model;

import android.location.Location;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Created by Andrey R. Brugnera on 05/03/2018.
 */
public class Service implements Serializable{
    private String name;
    private Map<String, Qualification> qualifications;
    private String description;
    private String phoneNumber;
    private String email;
    private String city;
    private String state;
    private Location location;
    private Date registrationDate;
    private int qualificationsCount;
    private float averageScore;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public int getQualificationsCount() {
        return qualificationsCount;
    }

    public void setQualificationsCount(int qualificationsCount) {
        this.qualificationsCount = qualificationsCount;
    }

    public float getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(float averageScore) {
        this.averageScore = averageScore;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Map<String, Qualification> getQualifications() {
        return qualifications;
    }

    public void setQualifications(Map<String, Qualification> qualifications) {
        this.qualifications = qualifications;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Service)) return false;

        Service service = (Service) o;

        return name.equals(service.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
