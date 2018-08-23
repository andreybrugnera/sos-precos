package br.edu.ifspsaocarlos.sosprecos.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.adapter.CategorySpinnerAdapter;
import br.edu.ifspsaocarlos.sosprecos.dao.CategoryDao;
import br.edu.ifspsaocarlos.sosprecos.dao.CategoryPlaceDao;
import br.edu.ifspsaocarlos.sosprecos.dao.PlaceDao;
import br.edu.ifspsaocarlos.sosprecos.dao.exception.DaoException;
import br.edu.ifspsaocarlos.sosprecos.model.Category;
import br.edu.ifspsaocarlos.sosprecos.model.CategoryPlace;
import br.edu.ifspsaocarlos.sosprecos.model.Place;
import br.edu.ifspsaocarlos.sosprecos.service.FetchLocationService;
import br.edu.ifspsaocarlos.sosprecos.util.ViewUtils;
import br.edu.ifspsaocarlos.sosprecos.util.location.LocationAddress;

public class PlaceActivity extends AppCompatActivity implements LocationListener {
    private static final String LOG_TAG = "ADD_EDIT_PLACE";

    public static final int OPERATION_STATUS_ERROR = -1;
    public static final int OPERATION_STATUS_OK = 1;

    public static final int OPERATION_ADD = 2;
    public static final int OPERATION_EDIT = 3;

    public static final String OPERATION = "operation";
    public static final String PLACE = "place";

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int REQUEST_LOCATION_PICKER = 2;

    private FrameLayout progressBarHolder;
    private TextView tvTitle;
    private EditText etPlaceName;
    private Spinner spCategory;
    private AutoCompleteTextView acTvPlaceEmail;
    private EditText etPlaceAddress;
    private EditText etPlacePhone;
    private EditText etPlaceDescription;
    private Button btGetCurrentLocation;
    private Button btGetLocationFromMap;
    private Button btAddOrEditPlace;

    private Place editingPlace;
    private List<Category> categories;
    private CategoryPlace categoryPlace;

    private CategorySpinnerAdapter categorySpinnerAdapter;

    private LocationManager locationManager;
    private Location currentLocation;
    private AddressResultReceiver addressResultReceiver;
    private LatLng placeLatLng;

    private boolean isLocationAccessGranted;

    private PlaceDao placeDao;
    private CategoryDao categoryDao;
    private CategoryPlaceDao categoryPlaceDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        this.placeDao = new PlaceDao(this);
        this.categoryDao = new CategoryDao(this);
        this.categoryPlaceDao = new CategoryPlaceDao(this);
        this.categories = new ArrayList<>();

        this.progressBarHolder = findViewById(R.id.progress_bar_holder);
        this.tvTitle = findViewById(R.id.tv_title);
        this.etPlaceName = findViewById(R.id.et_place_name);
        this.spCategory = findViewById(R.id.sp_category);
        this.acTvPlaceEmail = findViewById(R.id.actv_place_email);
        this.etPlaceAddress = findViewById(R.id.et_place_address);
        this.etPlacePhone = findViewById(R.id.et_place_phone);
        this.etPlaceDescription = findViewById(R.id.et_place_description);
        this.btGetCurrentLocation = findViewById(R.id.bt_get_current_location);
        this.btGetLocationFromMap = findViewById(R.id.bt_get_location_from_map);

        this.btAddOrEditPlace = findViewById(R.id.bt_add_edit_place);

        this.btGetLocationFromMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocationFromMap();
            }
        });

        this.btGetCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentLocation();
            }
        });

        loadCategories();
        configureToolbar();
        defineOperation();
        checkLocationAccessPermission();
    }

    private void configureCategorySpinner() {
        //Add fake category to be used as spinner hint
        Category fakeCategory = new Category(getString(R.string.select_category));
        fakeCategory.setId("fake_id");
        categories.add(0, fakeCategory);
        categorySpinnerAdapter = new CategorySpinnerAdapter(this,
                android.R.layout.simple_spinner_item, categories);
        categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(categorySpinnerAdapter);
    }

    private void loadCategories() {
        Log.d(LOG_TAG, getString(R.string.loading_categories));
        ViewUtils.showProgressBar(progressBarHolder);

        categoryDao.getDatabaseReference().addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        categories.clear();
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (DataSnapshot child : children) {
                            Category category = child.getValue(Category.class);
                            categories.add(category);
                        }
                        sortCategoriesByName();
                        configureCategorySpinner();
                        ViewUtils.hideProgressBar(progressBarHolder);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(LOG_TAG, databaseError.getMessage());
                        Log.e(LOG_TAG, databaseError.getDetails());
                        ViewUtils.hideProgressBar(progressBarHolder);
                    }
                });
    }

    private void sortCategoriesByName() {
        Comparator comparator = new Comparator<Category>() {

            @Override
            public int compare(Category cat1, Category cat2) {
                return cat1.getName().compareTo(cat2.getName());
            }
        };

        Collections.sort(categories, comparator);
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

    private void defineOperation() {
        int operation = getIntent().getIntExtra(OPERATION, OPERATION_ADD);
        switch (operation) {
            case OPERATION_EDIT:
                updateUIWithEditingPlaceData();
                loadEditingPlaceLocation();
                this.btAddOrEditPlace.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editPlace();
                    }
                });
                break;
            case OPERATION_ADD:
                this.btAddOrEditPlace.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addPlace();
                    }
                });
        }
    }

    private void updateUIWithEditingPlaceData() {
        this.tvTitle.setText(R.string.edit_place);
        this.btAddOrEditPlace.setText(R.string.edit);
        this.editingPlace = (Place) getIntent().getSerializableExtra(PLACE);
        this.etPlaceName.setText(this.editingPlace.getName());
        this.acTvPlaceEmail.setText(this.editingPlace.getEmail());
        this.etPlacePhone.setText(this.editingPlace.getPhoneNumber());
        this.etPlaceAddress.setText(this.editingPlace.getAddress());
        this.etPlaceDescription.setText(this.editingPlace.getDescription());
        loadPlacesCategory();
    }

    private void loadPlacesCategory() {
        Log.d(LOG_TAG, getString(R.string.loading_selected_category));
        ViewUtils.showProgressBar(progressBarHolder);

        Query query = categoryPlaceDao.getDatabaseReference().orderByChild("placeId").equalTo(editingPlace.getId());
        query.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (DataSnapshot child : children) {
                            categoryPlace = child.getValue(CategoryPlace.class);
                            String categoryId = categoryPlace.getCategoryId();
                            setSelectedCategory(categoryId);
                            break;
                        }
                        ViewUtils.hideProgressBar(progressBarHolder);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(LOG_TAG, databaseError.getMessage());
                        Log.e(LOG_TAG, databaseError.getDetails());
                        ViewUtils.hideProgressBar(progressBarHolder);
                    }
                });
    }

    private void setSelectedCategory(String categoryId) {
        for (int i = 0; i < categories.size(); i++) {
            Category category = categories.get(i);
            if (category.getId().equals(categoryId)) {
                spCategory.setSelection(i);
                break;
            }
        }
    }

    private void loadEditingPlaceLocation() {
        this.placeLatLng = new LatLng(editingPlace.getLatitude(), editingPlace.getLongitude());
    }

    private boolean validateInputFields(Place place) {
        String placeName = this.etPlaceName.getText().toString();
        if (TextUtils.isEmpty(placeName)) {
            this.etPlaceName.setError(getString(R.string.enter_place_name));
            this.etPlaceName.requestFocus();
            return false;
        }

        String placePhone = this.etPlacePhone.getText().toString();
        if (TextUtils.isEmpty(placePhone)) {
            this.etPlacePhone.setError(getString(R.string.enter_place_phone));
            this.etPlacePhone.requestFocus();
            return false;
        }

        String placeDescription = this.etPlaceDescription.getText().toString();
        if (TextUtils.isEmpty(placeDescription)) {
            this.etPlaceDescription.setError(getString(R.string.enter_place_description));
            this.etPlaceDescription.requestFocus();
            return false;
        }

        String placeAddress = this.etPlaceAddress.getText().toString();
        if (TextUtils.isEmpty(placeAddress)) {
            this.etPlaceAddress.setError(getString(R.string.enter_place_address));
            this.etPlaceAddress.requestFocus();
            return false;
        }

        //The selected category id must be > 0, as the first one is just a hint
        if (spCategory.getSelectedItemPosition() == 0) {
            //show message to user to select a category
            Toast.makeText(PlaceActivity.this, getString(R.string.select_category),
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        place.setName(placeName);
        place.setDescription(placeDescription);
        place.setPhoneNumber(placePhone);
        place.setAddress(placeAddress);

        return true;
    }

    private void editPlace() {
        if (validateInputFields(editingPlace)) {
            String placeEmail = this.acTvPlaceEmail.getText().toString();

            editingPlace.setEmail(placeEmail);
            editingPlace.setLatitude(this.placeLatLng.latitude);
            editingPlace.setLongitude(this.placeLatLng.longitude);
            try {
                placeDao.update(editingPlace);
                updateCategoryPlace();
                categoryPlaceDao.update(categoryPlace);
                Intent returnIntent = new Intent();
                returnIntent.putExtra(PLACE, editingPlace);
                setResult(OPERATION_STATUS_OK, returnIntent);
            } catch (DaoException ex) {
                Log.e(LOG_TAG, getString(R.string.error_editing_place), ex);
            }
            finish();
        }
    }

    private void addPlace() {
        Place place = Place.getInstance();
        if (validateInputFields(place)) {
            String placeEmail = this.acTvPlaceEmail.getText().toString();

            place.setEmail(placeEmail);
            place.setLatitude(this.placeLatLng.latitude);
            place.setLongitude(this.placeLatLng.longitude);
            try {
                placeDao.add(place);
                updateCategoryPlace(place);
                categoryPlaceDao.add(categoryPlace);
                Intent returnIntent = new Intent();
                returnIntent.putExtra(PLACE, place);
                setResult(OPERATION_STATUS_OK, returnIntent);
            } catch (DaoException ex) {
                Log.e(LOG_TAG, getString(R.string.error_adding_place), ex);
            }
            finish();
        }
    }

    private void updateCategoryPlace(Place place) {
        Category selectedCategory = categorySpinnerAdapter.getItem(spCategory.getSelectedItemPosition());
        categoryPlace = new CategoryPlace(selectedCategory.getId(), place.getId());
    }

    private void updateCategoryPlace() {
        Category selectedCategory = categorySpinnerAdapter.getItem(spCategory.getSelectedItemPosition());
        categoryPlace.setCategoryId(selectedCategory.getId());
    }

    @Override
    public void onLocationChanged(Location location) {
        this.currentLocation = location;
        this.placeLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        startFetchLocationService();
        //Stop listening for location updates
        this.locationManager.removeUpdates(this);
    }

    private void startFetchLocationService() {
        this.addressResultReceiver = new AddressResultReceiver(new Handler());
        Intent intent = new Intent(this, FetchLocationService.class);
        intent.putExtra(FetchLocationService.RECEIVER, this.addressResultReceiver);
        intent.putExtra(FetchLocationService.LOCATION_DATA_EXTRA, this.currentLocation);
        startService(intent);
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

    private void checkLocationAccessPermission() {
        this.isLocationAccessGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationAccessPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.isLocationAccessGranted = true;
                    getCurrentLocation();
                } else {
                    this.isLocationAccessGranted = false;
                }
                break;
            case REQUEST_LOCATION_PICKER:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.isLocationAccessGranted = true;
                    getLocationFromMap();
                } else {
                    this.isLocationAccessGranted = false;
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_LOCATION_PICKER:
                if (resultCode == RESULT_OK) {
                    com.google.android.gms.location.places.Place place = PlacePicker.getPlace(data, this);
                    etPlaceAddress.setText(place.getAddress().toString());
                    placeLatLng = place.getLatLng();
                }
        }
    }

    private void getCurrentLocation() {
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
    }

    private void getLocationFromMap() {
        if (!isLocationAccessGranted) {
            requestLocationAccessPermission();
        } else {
            try {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                if (placeLatLng != null) {
                    LatLngBounds latLngBounds = new LatLngBounds(placeLatLng, placeLatLng);
                    builder.setLatLngBounds(latLngBounds);
                }
                startActivityForResult(builder.build(this), REQUEST_LOCATION_PICKER);
            } catch (Exception ex) {
                Toast.makeText(PlaceActivity.this, getString(R.string.service_not_available),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            ViewUtils.hideProgressBar(progressBarHolder);

            if (resultData == null) {
                return;
            }

            // Display the address string
            LocationAddress locationAddress = (LocationAddress) resultData.getSerializable(FetchLocationService.RESULT_DATA_KEY);
            if (locationAddress != null) {
                etPlaceAddress.setText(locationAddress.getAddress());
            } else {
                Toast.makeText(PlaceActivity.this, getString(R.string.cannot_fetch_address),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
