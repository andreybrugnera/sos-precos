package br.edu.ifspsaocarlos.sosprecos.model;

import android.location.Location;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Created by Andrey R. Brugnera on 05/03/2018.
 */
public class ServiceProvider implements Serializable{
    private String id;
    private String name;
    private Set<String> services;
    private Set<String> qualifications;
    private String description;
    private String phoneNumber;
    private String email;
    private String city;
    private String state;
    private Double latitude;
    private Double longitude;
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

    public Location getLocation(){
        if(location == null){
            location = new Location("");
            if(latitude != null && longitude != null){
                location.setLatitude(latitude);
                location.setLongitude(longitude);
            }
        }
        return location;
    }

    public Set<String> getServices() {
        if(services == null){
            services = new HashSet<>();
        }
        return services;
    }

    public void setServices(Set<String> services) {
        this.services = services;
    }

    public Set<String> getQualifications() {
        if(qualifications == null){
            qualifications = new HashSet<>();
        }
        return qualifications;
    }

    public void setQualifications(Set<String> qualifications) {
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
