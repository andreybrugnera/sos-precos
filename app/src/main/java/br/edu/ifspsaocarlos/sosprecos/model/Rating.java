package br.edu.ifspsaocarlos.sosprecos.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Created by Andrey R. Brugnera on 05/03/2018.
 */
public class Rating implements Serializable {
    private String id;
    private String userName;
    private String userId;
    private String description;
    private Date registrationDate;
    //Combined index (key + registrationDate)
    private String keyRegistrationDate;
    private float priceScore;
    private float qualityScore;
    private float locationScore;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getKeyRegistrationDate() {
        return keyRegistrationDate;
    }

    public void setKeyRegistrationDate(String keyRegistrationDate) {
        this.keyRegistrationDate = keyRegistrationDate;
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
        if (!(o instanceof Rating)) return false;
        Rating that = (Rating) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
