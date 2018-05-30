package br.edu.ifspsaocarlos.sosprecos.service;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.util.location.LocationAddress;


/**
 * Created by Andrey R. Brugnera on 28/05/2018.
 */
public class FetchLocationService extends IntentService {
    private static final String TAG = "FetchLocationService";

    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String PACKAGE_NAME = "br.edu.ifspsaocarlos.sosprecos.service";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";

    protected ResultReceiver mReceiver;

    public FetchLocationService() {
        super("fetchLocationService");
    }

    private void deliverResultToReceiver(int resultCode, LocationAddress address) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(RESULT_DATA_KEY, address);
        mReceiver.send(resultCode, bundle);
    }

    private LocationAddress buildLocationAddress(Address address) {
        String street = address.getAddressLine(0);
        String city = address.getLocality();
        String state = address.getAdminArea();
        String country = address.getCountryName();
        String postalCode = address.getPostalCode();

        LocationAddress locationAddress = new LocationAddress(street,
                city, state, country, postalCode);

        return locationAddress;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String errorMessage = "";

        // Get the location passed to this service through an extra.
        Location location = intent.getParcelableExtra(LOCATION_DATA_EXTRA);
        this.mReceiver = intent.getParcelableExtra(RECEIVER);

        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = getString(R.string.service_not_available);
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = getString(R.string.invalid_lat_long_used);
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + location.getLatitude() + ", Longitude = " +
                    location.getLongitude(), illegalArgumentException);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size() == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
                Log.e(TAG, errorMessage);
            }
            deliverResultToReceiver(FAILURE_RESULT, null);
        } else {
            Address address = addresses.get(0);
            LocationAddress locationAddress = buildLocationAddress(address);
            Log.i(TAG, getString(R.string.address_found));
            deliverResultToReceiver(SUCCESS_RESULT, locationAddress);
        }
    }
}
