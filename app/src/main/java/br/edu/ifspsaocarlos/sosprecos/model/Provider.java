package br.edu.ifspsaocarlos.sosprecos.model;

import android.location.Location;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Created by Andrey R. Brugnera on 05/03/2018.
 */
public class Provider implements Serializable {
    private String id;
    private String name;
    private String description;
    private String phoneNumber;
    private String email;
    private String address;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Provider)) return false;
        Provider that = (Provider) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}