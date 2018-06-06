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
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.model.Provider;
import br.edu.ifspsaocarlos.sosprecos.service.FetchLocationService;
import br.edu.ifspsaocarlos.sosprecos.util.location.LocationAddress;

public class ProviderActivity extends AppCompatActivity implements LocationListener {
    private static final String LOG_TAG = "ADD_EDIT_PROVIDER";

    public static final int OPERATION_STATUS_ERROR = -1;
    public static final int OPERATION_STATUS_OK = 1;

    public static final int OPERATION_ADD = 2;
    public static final int OPERATION_EDIT = 3;

    public static final String OPERATION = "operation";
    public static final String PROVIDER = "provider";

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int REQUEST_LOCATION_PICKER = 2;

    private ProgressBar progressBar;
    private TextView tvTitle;
    private EditText etProviderName;
    private AutoCompleteTextView acTvProviderEmail;
    private EditText etProviderAddress;
    private EditText etProviderPhone;
    private Button btGetCurrentLocation;
    private Button btGetLocationFromMap;
    private Button btAddOrEditProvider;

    private Provider editingProvider;

    private LocationManager locationManager;
    private Location currentLocation;
    private AddressResultReceiver addressResultReceiver;
    private LatLng providerLatLng;

    private boolean isLocationAccessGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider);

        this.progressBar = findViewById(R.id.progress_bar);
        this.tvTitle = findViewById(R.id.tv_title);
        this.etProviderName = findViewById(R.id.et_provider_name);
        this.acTvProviderEmail = findViewById(R.id.actv_provider_email);
        this.etProviderAddress = findViewById(R.id.et_provider_address);
        this.etProviderPhone = findViewById(R.id.et_provider_phone);
        this.btGetCurrentLocation = findViewById(R.id.bt_get_current_location);
        this.btGetLocationFromMap = findViewById(R.id.bt_get_location_from_map);

        this.btAddOrEditProvider = findViewById(R.id.bt_add_edit_provider);

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

        configureToolbar();
        defineOperation();
        checkLocationAccessPermission();
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
                this.tvTitle.setText(R.string.edit_provider);
                this.btAddOrEditProvider.setText(R.string.edit);
                this.editingProvider = (Provider) getIntent().getSerializableExtra(PROVIDER);
                fillInputFields();
                this.btAddOrEditProvider.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editProvider();
                    }
                });
                break;
            case OPERATION_ADD:
                this.btAddOrEditProvider.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addProvider();
                    }
                });
        }
    }

    private void fillInputFields() {
        this.etProviderName.setText(this.editingProvider.getName());
        this.acTvProviderEmail.setText(this.editingProvider.getEmail());
        this.etProviderPhone.setText(this.editingProvider.getPhoneNumber());
        this.etProviderAddress.setText(this.editingProvider.getAddress());
    }

    private void editProvider() {
    }

    private void addProvider() {
    }

    @Override
    public void onLocationChanged(Location location) {
        this.currentLocation = location;
        this.providerLatLng = new LatLng(location.getLatitude(), location.getLongitude());
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
                    Place place = PlacePicker.getPlace(data, this);
                    etProviderAddress.setText(place.getAddress().toString());
                    providerLatLng = place.getLatLng();
                }
        }
    }

    private void getCurrentLocation() {
        this.progressBar.setVisibility(View.VISIBLE);
        this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!isLocationAccessGranted) {
            this.progressBar.setVisibility(View.GONE);
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
                startActivityForResult(builder.build(this), REQUEST_LOCATION_PICKER);
            } catch (Exception ex) {
                Toast.makeText(ProviderActivity.this, getString(R.string.service_not_available),
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
            progressBar.setVisibility(View.GONE);

            if (resultData == null) {
                return;
            }

            // Display the address string
            LocationAddress locationAddress = (LocationAddress) resultData.getSerializable(FetchLocationService.RESULT_DATA_KEY);
            if (locationAddress != null) {
                etProviderAddress.setText(locationAddress.getAddress());
            } else {
                Toast.makeText(ProviderActivity.this, getString(R.string.cannot_fetch_address),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
