package br.edu.ifspsaocarlos.sosprecos.util.comparator;

import java.util.Comparator;

import br.edu.ifspsaocarlos.sosprecos.dto.RatingInformationDto;
import br.edu.ifspsaocarlos.sosprecos.model.Rating;

/**
 * Created by Andrey R. Brugnera on 22/09/2018.
 */
public class RatingComparator implements Comparator<RatingInformationDto> {

    public static final int ORDER_BY_DATE_DESC = 0;
    public static final int ORDER_BY_DATE_ASC = 1;

    private int orderBy = ORDER_BY_DATE_DESC;

    public RatingComparator() {
    }

    public RatingComparator(int orderBy) {
        this.orderBy = orderBy;
    }

    @Override
    public int compare(RatingInformationDto t1, RatingInformationDto t2) {
        switch (orderBy) {
            case ORDER_BY_DATE_DESC:
                return t2.getRegistrationDate().compareTo(t1.getRegistrationDate());
            case ORDER_BY_DATE_ASC:
                return t1.getRegistrationDate().compareTo(t2.getRegistrationDate());
        }
        return 0;
    }
}
