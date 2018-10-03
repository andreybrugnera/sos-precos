package br.edu.ifspsaocarlos.sosprecos.util.comparator;

import java.util.Comparator;

import br.edu.ifspsaocarlos.sosprecos.dto.SearchServiceResultDto;

/**
 * Created by Andrey R. Brugnera on 03/09/2018.
 */
public class SearchServiceResultComparator implements Comparator<SearchServiceResultDto> {

    public static final int ORDER_BY_PRICE = 0;
    public static final int ORDER_BY_QUALITY = 1;
    public static final int ORDER_BY_LOCATION = 2;

    private int orderBy = ORDER_BY_PRICE;

    public SearchServiceResultComparator(int orderBy) {
        this.orderBy = orderBy;
    }

    @Override
    public int compare(SearchServiceResultDto t1, SearchServiceResultDto t2) {
        switch (orderBy){
            case ORDER_BY_PRICE:
                return t1.getServicePrice().compareTo(t2.getServicePrice());
            case ORDER_BY_QUALITY:
                return t2.getPlaceAverageScore().compareTo(t1.getPlaceAverageScore());
            case ORDER_BY_LOCATION:
                return t1.getDistanceFromCurrentLocation().compareTo(t2.getDistanceFromCurrentLocation());
        }
        return 0;
    }
}
