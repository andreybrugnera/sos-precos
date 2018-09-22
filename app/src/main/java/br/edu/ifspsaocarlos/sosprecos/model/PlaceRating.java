package br.edu.ifspsaocarlos.sosprecos.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Created by Andrey R. Brugnera on 17/04/2018.
 */
public class PlaceRating implements Serializable {
    private String id;
    private Date registrationDate;
    private String placeId;
    private String rateId;
    private String userId;
    private String placeIdUserId;
    private String placeIdRegistrationDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getRateId() {
        return rateId;
    }

    public void setRateId(String rateId) {
        this.rateId = rateId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPlaceIdUserId() {
        return placeIdUserId;
    }

    public void setPlaceIdUserId(String placeIdUserId) {
        this.placeIdUserId = placeIdUserId;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getPlaceIdRegistrationDate() {
        return placeIdRegistrationDate;
    }

    public void setPlaceIdRegistrationDate(String placeIdRegistrationDate) {
        this.placeIdRegistrationDate = placeIdRegistrationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlaceRating)) return false;
        PlaceRating that = (PlaceRating) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
