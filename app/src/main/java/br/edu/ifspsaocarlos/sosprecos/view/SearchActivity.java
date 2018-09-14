package br.edu.ifspsaocarlos.sosprecos.view;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;

import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.adapter.SearchServiceResultAdapter;
import br.edu.ifspsaocarlos.sosprecos.dao.GeoFireHelper;
import br.edu.ifspsaocarlos.sosprecos.dao.PlaceDao;
import br.edu.ifspsaocarlos.sosprecos.dao.ServiceDao;
import br.edu.ifspsaocarlos.sosprecos.dto.SearchServiceResultDto;
import br.edu.ifspsaocarlos.sosprecos.model.Place;
import br.edu.ifspsaocarlos.sosprecos.model.Service;
import br.edu.ifspsaocarlos.sosprecos.util.SystemConstants;
import br.edu.ifspsaocarlos.sosprecos.util.ViewUtils;
import br.edu.ifspsaocarlos.sosprecos.util.comparator.SearchServiceResultComparator;

public class SearchActivity extends AppCompatActivity implements LocationListener {

    private static final String LOG_TAG = "SEARCH";
    private static final String MAX_DIST_KM = "MAX_DIST_KM";

    private PlaceDao placeDao;
    private ServiceDao serviceDao;

    private EditText etSearch;
    private FrameLayout progressBarHolder;
    private ImageView ivSearch;
    private RadioButton rbOrderByLocation;
    private RadioButton rbOrderByPrice;
    private RadioButton rbOrderByQuality;

    private ListView listView;
    private SearchServiceResultAdapter listAdapter;
    private List<SearchServiceResultDto> results;

    private String searchString;

    private LocationManager locationManager;
    private Location currentLocation;
    private boolean isLocationAccessGranted;
    private boolean isCurrentLocationLoaded = false;

    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private SharedPreferences sharedPreferences;
    private Integer maxRangeInKilometersToSearch;

    private int totalPlacesChecked;

    private Map<String, Place> placeMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        this.listView = findViewById(R.id.list_view);
        this.etSearch = findViewById(R.id.et_search);
        this.progressBarHolder = findViewById(R.id.progress_bar_holder);
        this.rbOrderByLocation = findViewById(R.id.radio_order_location);
        this.rbOrderByPrice = findViewById(R.id.radio_order_price);
        this.rbOrderByQuality = findViewById(R.id.radio_order_quality);
        this.ivSearch = findViewById(R.id.iv_search);

        this.ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchString = etSearch.getText().toString();
                if (validateSearchString(searchString)) {
                    search();
                }
            }
        });

        this.rbOrderByPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderResults();
            }
        });

        this.rbOrderByQuality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderResults();
            }
        });

        this.rbOrderByLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderResults();
            }
        });

        this.placeDao = new PlaceDao(this);
        this.serviceDao = new ServiceDao(this);

        this.sharedPreferences = getSharedPreferences(SystemConstants.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
        maxRangeInKilometersToSearch = sharedPreferences.getInt(MAX_DIST_KM, SystemConstants.MAX_DISTANCE_IN_KILOMETERS);

        this.results = new ArrayList<>();
        this.placeMap = new HashMap<>();

        configureToolbar();
        checkLocationAccessPermission();
        configureListAdapter();
        loadCurrentLocation();
    }

    private void configureListAdapter() {
        this.listAdapter = new SearchServiceResultAdapter(this, R.id.list_view, results);
        this.listView.setAdapter(listAdapter);
    }

    private void configureToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validateSearchString(String searchString) {
        if (TextUtils.isEmpty(searchString)) {
            this.etSearch.setError(getString(R.string.enter_valid_search_text));
            this.etSearch.requestFocus();
            return false;
        }
        return true;
    }

    private void search() {
        ViewUtils.showProgressBar(progressBarHolder);
        Log.i(LOG_TAG, getString(R.string.searching) + " > " + searchString);
        loadPlacesCandidatesByUsersCurrentLocation();
    }

    private void loadPlacesCandidatesByUsersCurrentLocation() {
        if (!isCurrentLocationLoaded) {
            return;
        }

        final List<String> placesIdsInSearchRange = new ArrayList<>();

        final GeoQuery geoQuery = GeoFireHelper.getGeoFire()
                .queryAtLocation(new GeoLocation(currentLocation.getLatitude(),
                                currentLocation.getLongitude()),
                        Double.valueOf(maxRangeInKilometersToSearch));

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                placesIdsInSearchRange.add(key);
            }

            @Override
            public void onKeyExited(String key) {
                placesIdsInSearchRange.remove(key);
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                geoQuery.removeAllListeners();
                //All keys loaded, fetch places
                loadPlacesInRange(placesIdsInSearchRange);
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.e(LOG_TAG, getString(R.string.geofire_error_fetching_location_data));
            }
        });
    }

    private void loadPlacesInRange(final List<String> placesIdsInSearchRange) {
        this.placeMap.clear();

        for (String placeId : placesIdsInSearchRange) {
            Query query = placeDao.getDatabaseReference().orderByChild("id").equalTo(placeId);

            query.addListenerForSingleValueEvent(
                    new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                            for (DataSnapshot child : children) {
                                Place place = child.getValue(Place.class);
                                placeMap.put(place.getId(), place);
                                loadPlacesServices(placesIdsInSearchRange);
                                break;
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(LOG_TAG, databaseError.getMessage());
                            Log.e(LOG_TAG, databaseError.getDetails());
                            ViewUtils.hideProgressBar(progressBarHolder);
                        }
                    });
        }
    }

    private void loadPlacesServices(final List<String> placesIdsInSearchRange) {
        if (placeMap.keySet().size() < placesIdsInSearchRange.size()) {
            //The method will stop if not all the places had been checked
            return;
        }

        this.results.clear();
        this.totalPlacesChecked = 0;
        final String searchText = searchString.toUpperCase();

        //Search for the services by String
        for (String placeId : placesIdsInSearchRange) {
            Query query = serviceDao.getDatabaseReference().orderByChild("placeId").equalTo(placeId);
            query.addListenerForSingleValueEvent(new ServiceValueEventListener(searchText));
        }
    }

    private void loadCurrentLocation() {
        ViewUtils.showProgressBar(progressBarHolder);
        this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!isLocationAccessGranted) {
            ViewUtils.hideProgressBar(progressBarHolder);
            requestLocationAccessPermission();
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 5, this);
            this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 15000, 5, this);
        }
        ViewUtils.hideProgressBar(progressBarHolder);
    }

    private void checkLocationAccessPermission() {
        this.isLocationAccessGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationAccessPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
    }

    @Override
    public void onLocationChanged(Location location) {
        this.currentLocation = location;
        this.isCurrentLocationLoaded = true;
        //Stop listening for location updates
        this.locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.isLocationAccessGranted = true;
                } else {
                    this.isLocationAccessGranted = false;
                }
        }
    }

    private void updateTotalPlacesChecked() {
        totalPlacesChecked++;
    }

    private boolean hasAllPlacesChecked() {
        return placeMap.keySet().size() == totalPlacesChecked;
    }

    private void updateResultsLocation() {
        LatLng curLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        for (SearchServiceResultDto resultDto : results) {
            LatLng placeLocation = new LatLng(resultDto.getPlace().getLatitude(), resultDto.getPlace().getLongitude());
            Double distance = SphericalUtil.computeDistanceBetween(curLocation, placeLocation) / 1000;
            resultDto.setDistanceFromCurrentLocation(distance.floatValue());
        }
    }

    private void orderResults() {
        boolean orderByPrice = this.rbOrderByPrice.isChecked();
        boolean orderByLocation = this.rbOrderByLocation.isChecked();
        boolean orderByQuality = this.rbOrderByQuality.isChecked();

        if (orderByPrice) {
            Collections.sort(results, new SearchServiceResultComparator(SearchServiceResultComparator.ORDER_BY_PRICE));
        } else if (orderByQuality) {
            Collections.sort(results, new SearchServiceResultComparator(SearchServiceResultComparator.ORDER_BY_QUALITY));
        } else if (orderByLocation) {
            Collections.sort(results, new SearchServiceResultComparator(SearchServiceResultComparator.ORDER_BY_LOCATION));
        }

        listAdapter.notifyDataSetChanged();
    }

    private class ServiceValueEventListener implements ValueEventListener {
        private String searchText;

        public ServiceValueEventListener(String searchText) {
            this.searchText = searchText;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Iterable<DataSnapshot> children = dataSnapshot.getChildren();
            for (DataSnapshot child : children) {
                Service service = child.getValue(Service.class);
                String serviceName = service.getName().toUpperCase();
                String serviceDescription = service.getDescription().toUpperCase();
                if (serviceName.contains(searchText) || (serviceDescription != null && serviceDescription.contains(searchText))) {
                    Place place = placeMap.get(service.getPlaceId());
                    SearchServiceResultDto resultDto = new SearchServiceResultDto(place, service);
                    results.add(resultDto);
                }
            }
            updateTotalPlacesChecked();
            if (hasAllPlacesChecked()) {
                updateResultsLocation();
                orderResults();
                ViewUtils.hideProgressBar(progressBarHolder);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(LOG_TAG, databaseError.getMessage());
            Log.e(LOG_TAG, databaseError.getDetails());
            ViewUtils.hideProgressBar(progressBarHolder);
        }
    }
}
