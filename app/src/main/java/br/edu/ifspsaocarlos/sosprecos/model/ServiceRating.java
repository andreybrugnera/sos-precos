package br.edu.ifspsaocarlos.sosprecos.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by Andrey R. Brugnera on 17/04/2018.
 */
public class ServiceRating implements Serializable {
    private String id;
    private String serviceId;
    private String rateId;
    private String userId;
    private String serviceIdUserId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
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

    public String getServiceIdUserId() {
        return serviceIdUserId;
    }

    public void setServiceIdUserId(String serviceIdUserId) {
        this.serviceIdUserId = serviceIdUserId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServiceRating)) return false;
        ServiceRating that = (ServiceRating) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
