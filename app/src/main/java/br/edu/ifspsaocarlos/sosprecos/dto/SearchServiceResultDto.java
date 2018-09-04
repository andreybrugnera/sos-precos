package br.edu.ifspsaocarlos.sosprecos.dto;

import br.edu.ifspsaocarlos.sosprecos.model.Place;
import br.edu.ifspsaocarlos.sosprecos.model.Service;

/**
 * Created by Andrey R. Brugnera on 03/09/2018.
 */
public class SearchServiceResultDto {
    private Place place;
    private Service service;
    private Float distanceFromCurrentLocation;
    private Float serviceAverageScore;
    private Float placeAverageScore;
    private Float servicePrice;

    public SearchServiceResultDto(Place place, Service service) {
        this.place = place;
        this.service = service;
        this.serviceAverageScore = service.getAverageScore();
        this.placeAverageScore = place.getAverageScore();
        this.servicePrice = service.getPrice();
    }

    public Place getPlace() {
        return place;
    }

    public Service getService() {
        return service;
    }

    public Float getDistanceFromCurrentLocation() {
        return distanceFromCurrentLocation;
    }

    public Float getServiceAverageScore() {
        return serviceAverageScore;
    }

    public Float getPlaceAverageScore() {
        return placeAverageScore;
    }

    public Float getServicePrice() {
        return servicePrice;
    }

    public void setDistanceFromCurrentLocation(Float distanceFromCurrentLocation) {
        this.distanceFromCurrentLocation = distanceFromCurrentLocation;
    }
}
