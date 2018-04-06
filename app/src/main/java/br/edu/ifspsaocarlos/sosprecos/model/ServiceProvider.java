package br.edu.ifspsaocarlos.sosprecos.model;

import android.location.Location;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Andrey R. Brugnera on 05/03/2018.
 */
public class ServiceProvider implements Serializable {
    private String id;
    private String name;
    private Map<String, Service> services;
    private Map<String, Qualification> qualifications;
    private String description;
    private String phoneNumber;
    private String email;
    private String city;
    private String state;
    private Double latitude;
    private Double longitude;
    @Exclude
    private Location location;
    private Date registrationDate;
    private int qualificationsCount;
    private float averageScore;

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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
        getLocation().setLatitude(latitude);
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
        getLocation().setLongitude(longitude);
    }

    public Location getLocation() {
        if (location == null) {
            if (latitude != null && longitude != null) {
                location = new Location("");
                location.setLatitude(latitude);
                location.setLongitude(longitude);
            }
        }
        return location;
    }

    public Map<String, Service> getServices() {
        if (services == null) {
            services = new HashMap<>();
        }
        return services;
    }

    public void setServices(Map<String, Service> services) {
        this.services = services;
    }

    public Map<String, Qualification> getQualifications() {
        if (qualifications == null) {
            qualifications = new HashMap<>();
        }
        return qualifications;
    }

    public void setQualifications(Map<String, Qualification> qualifications) {
        this.qualifications = qualifications;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServiceProvider)) return false;
        ServiceProvider that = (ServiceProvider) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
