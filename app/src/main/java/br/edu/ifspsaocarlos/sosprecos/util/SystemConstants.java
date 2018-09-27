package br.edu.ifspsaocarlos.sosprecos.util;

/**
 * Created by Andrey R. Brugnera on 25/08/2018.
 */
public interface SystemConstants {
    int MAX_DISTANCE_IN_KILOMETERS = 20;
    String MAX_DIST_KM = "MAX_DIST_KM";
    String SHARED_PREFERENCES_FILE = "application_preferences";
    String USER_EMAIL = "user_email";

    int MIN_PHONE_NUMBER_LENGTH = 15;

    String PLACE = "place";
    String SERVICE = "service";
    String CATEGORY = "category";

    String OPERATION = "operation";
    int OPERATION_STATUS_ERROR = -1;
    int OPERATION_STATUS_OK = 1;

    int REQUEST_LOCATION_PERMISSION = 1;
    int REQUEST_LOCATION_PICKER = 2;

    //GeoFire
    String GEOFIRE_DATABASE_REFERENCE = "geolocations";
}
