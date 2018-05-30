package br.edu.ifspsaocarlos.sosprecos.util.ws;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrey R. Brugnera on 21/05/2018.
 */
public class GeoNamesWebService {
    private static final String WS = "http://www.geonames.org/childrenJSON?geonameId=ID";
    public static final String CHILDREN_LIST_NAME = "geonames";
    public static final String CHILD_ID = "geonameId";
    public static final String CHILD_NAME = "name";
    public static final String CHILD_ISO_CODE = "ISO3166_2";
    public static final List<GeoLocation> BR_STATES_LIST;

    static{
        BR_STATES_LIST = new ArrayList<>();
        BR_STATES_LIST.add(new GeoLocation("3665474", "Acre", "AC"));
        BR_STATES_LIST.add(new GeoLocation("3408096", "Alagoas", "AL"));
        BR_STATES_LIST.add(new GeoLocation("3407762", "Amapá", "AP"));
        BR_STATES_LIST.add(new GeoLocation("3665361", "Amazonas", "AM"));
        BR_STATES_LIST.add(new GeoLocation("3471168", "Bahia", "BA"));
        BR_STATES_LIST.add(new GeoLocation("3402362", "Ceará", "CE"));
        BR_STATES_LIST.add(new GeoLocation("3463930", "Espírito Santo", "ES"));
        BR_STATES_LIST.add(new GeoLocation("3463504", "Distrito Federal", "DF"));
        BR_STATES_LIST.add(new GeoLocation("3462372", "Goiás", "GO"));
        BR_STATES_LIST.add(new GeoLocation("3395443", "Maranhão", "MA"));
        BR_STATES_LIST.add(new GeoLocation("3457419", "Mato Grosso", "MT"));
        BR_STATES_LIST.add(new GeoLocation("3457415", "Mato Grosso do Sul", "MS"));
        BR_STATES_LIST.add(new GeoLocation("3457153", "Minas Gerais", "MG"));
        BR_STATES_LIST.add(new GeoLocation("3455077", "Paraná", "PR"));
        BR_STATES_LIST.add(new GeoLocation("3393098", "Paraíba", "PB"));
        BR_STATES_LIST.add(new GeoLocation("3393129", "Pará", "PA"));
        BR_STATES_LIST.add(new GeoLocation("3392268", "Pernambuco", "PE"));
        BR_STATES_LIST.add(new GeoLocation("3392213", "Piauí", "PI"));
        BR_STATES_LIST.add(new GeoLocation("3390290", "Rio Grande do Norte", "RN"));
        BR_STATES_LIST.add(new GeoLocation("3451133", "Rio Grande do Sul", "RS"));
        BR_STATES_LIST.add(new GeoLocation("3451189", "Rio de Janeiro", "RJ"));
        BR_STATES_LIST.add(new GeoLocation("3924825", "Rondônia", "RO"));
        BR_STATES_LIST.add(new GeoLocation("3662560", "Roraima", "RR"));
        BR_STATES_LIST.add(new GeoLocation("3450387", "Santa Catarina", "SC"));
        BR_STATES_LIST.add(new GeoLocation("3447799", "Sergipe", "SE"));
        BR_STATES_LIST.add(new GeoLocation("3448433", "São Paulo", "SP"));
        BR_STATES_LIST.add(new GeoLocation("3474575", "Tocantins", "TO"));
    }

    private static final String getCitiesResourceURL(String stateId) {
        return WS.replace("ID", stateId);
    }
}
