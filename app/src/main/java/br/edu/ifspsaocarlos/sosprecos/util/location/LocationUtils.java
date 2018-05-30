package br.edu.ifspsaocarlos.sosprecos.util.location;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Andrey R. Brugnera on 23/05/2018.
 */
public class LocationUtils {

    public static LatLng getLocationFromAddress(Context context, String strAddress) throws IOException {
        Geocoder geocoder = new Geocoder(context,  Locale.getDefault());
        if (geocoder.isPresent()){
            List<Address> addresses = geocoder.getFromLocationName(strAddress, 1);
            if (addresses == null) {
                Address location = addresses.get(0);
                LatLng latLang = new LatLng(location.getLatitude(), location.getLongitude());
                return latLang;
            }
        }
        return null;
    }

    public static String getAddressString(LocationAddress locationAddress) {
        return locationAddress.getStreet() + ", " + locationAddress.getCity() + ", " + locationAddress.getState() + ", " + locationAddress.getCountry() + " - " + locationAddress.getZipCode();
    }
}
