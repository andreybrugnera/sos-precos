package br.edu.ifspsaocarlos.sosprecos.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by Andrey R. Brugnera on 17/04/2018.
 */
public class ProviderRating implements Serializable {
    private String id;
    private String providerId;
    private String rateId;
    private String userId;
    private String providerIdUserId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
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

    public String getProviderIdUserId() {
        return providerIdUserId;
    }

    public void setProviderIdUserId(String providerIdUserId) {
        this.providerIdUserId = providerIdUserId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProviderRating)) return false;
        ProviderRating that = (ProviderRating) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
