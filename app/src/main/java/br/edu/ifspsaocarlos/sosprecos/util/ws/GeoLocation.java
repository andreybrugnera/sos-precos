package br.edu.ifspsaocarlos.sosprecos.util.ws;

import java.util.Objects;

/**
 * Created by Andrey R. Brugnera on 21/05/2018.
 */
public class GeoLocation {
    private String id;
    private String name;
    private String isoCode;

    public GeoLocation(String id, String name, String isoCode) {
        this.id = id;
        this.name = name;
        this.isoCode = isoCode;
    }

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

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GeoLocation)) return false;
        GeoLocation geoLocation = (GeoLocation) o;
        return Objects.equals(id, geoLocation.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
