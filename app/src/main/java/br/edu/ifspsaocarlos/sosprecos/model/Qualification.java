package br.edu.ifspsaocarlos.sosprecos.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Andrey R. Brugnera on 05/03/2018.
 */

public class Qualification implements Serializable {
    private User user;
    private Service service;
    private String description;
    private Date registrationDate;
    private float priceScore;
    private float qualityScore;
    private float locationScore;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public float getPriceScore() {
        return priceScore;
    }

    public void setPriceScore(float priceScore) {
        this.priceScore = priceScore;
    }

    public float getQualityScore() {
        return qualityScore;
    }

    public void setQualityScore(float qualityScore) {
        this.qualityScore = qualityScore;
    }

    public float getLocationScore() {
        return locationScore;
    }

    public void setLocationScore(float locationScore) {
        this.locationScore = locationScore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Qualification)) return false;

        Qualification that = (Qualification) o;

        if (!user.equals(that.user)) return false;
        if (!service.equals(that.service)) return false;
        return registrationDate.equals(that.registrationDate);
    }

    @Override
    public int hashCode() {
        int result = user.hashCode();
        result = 31 * result + service.hashCode();
        result = 31 * result + registrationDate.hashCode();
        return result;
    }
}
