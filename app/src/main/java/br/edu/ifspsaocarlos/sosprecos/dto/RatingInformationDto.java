package br.edu.ifspsaocarlos.sosprecos.dto;

import java.util.Date;
import java.util.Objects;

import br.edu.ifspsaocarlos.sosprecos.model.Rating;

/**
 * Created by Andrey R. Brugnera on 15/09/2018.
 */
public class RatingInformationDto {
    private Rating rating;
    private String userName;
    private Date registrationDate;
    private String description;
    private Float score;

    public static RatingInformationDto getInstance(Rating rating) {
        RatingInformationDto dto = new RatingInformationDto();
        dto.setUserName(rating.getUserName());
        dto.setDescription(rating.getDescription());
        dto.setRegistrationDate(rating.getRegistrationDate());
        dto.setScore(calculateScore(rating));
        dto.setRating(rating);
        return dto;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    private static Float calculateScore(Rating rating){
        return (rating.getLocationScore() + rating.getPriceScore() + rating.getQualityScore()) / 3;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RatingInformationDto)) return false;
        RatingInformationDto that = (RatingInformationDto) o;
        return Objects.equals(rating, that.rating);
    }

    @Override
    public int hashCode() {

        return Objects.hash(rating);
    }
}
