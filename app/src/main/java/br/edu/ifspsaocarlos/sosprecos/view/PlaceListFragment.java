package br.edu.ifspsaocarlos.sosprecos.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.adapter.PlaceAdapter;
import br.edu.ifspsaocarlos.sosprecos.dao.GeoFireHelper;
import br.edu.ifspsaocarlos.sosprecos.dao.PlaceDao;
import br.edu.ifspsaocarlos.sosprecos.dao.exception.DaoException;
import br.edu.ifspsaocarlos.sosprecos.model.Place;
import br.edu.ifspsaocarlos.sosprecos.util.SessionUtils;
import br.edu.ifspsaocarlos.sosprecos.util.SystemConstants;
import br.edu.ifspsaocarlos.sosprecos.util.ViewUtils;

/**
 * Created by Andrey R. Brugnera on 16/05/2018.
 */
public class PlaceListFragment extends Fragment implements LocationListener {

    private static final String LOG_TAG = "SERVICE_PLACES";

    private FrameLayout progressBarHolder;
    private Button btAddPlace;
    private ListView placesListView;
    private PlaceAdapter listAdapter;
    private TextView viewTitle;

    private PlaceDao placeDao;
    private List<Place> places;
    private Place selectedPlace;

    private LocationManager locationManager;
    private Location currentLocation;
    private boolean isLocationAccessGranted;

    private static final int ADD = 1;
    private static final int EDIT = 2;

    public PlaceListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.placeDao = new PlaceDao(getContext());
        this.places = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_crud_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        this.progressBarHolder = getView().findViewById(R.id.progress_bar_holder);
        this.placesListView = getView().findViewById(R.id.list_view);
        this.viewTitle = getView().findViewById(R.id.list_title);
        this.viewTitle.setText(getString(R.string.places));

        this.btAddPlace = getView().findViewById(R.id.bt_add_edit_item);
        this.btAddPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPlace();
            }
        });

        this.placesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Place place = listAdapter.getItem(position);
                if (place != null) {
                    openPlaceInfo(place);
                }
            }
        });

        configureListAdapter();
        registerForContextMenu(this.placesListView);
        checkLocationAccessPermission();
        loadPlaces();
    }

    private void openPlaceInfo(Place place) {
        Intent openPlaceInfoIntent = new Intent(getContext(), PlaceInfoActivity.class);
        openPlaceInfoIntent.putExtra(SystemConstants.PLACE, place);
        startActivity(openPlaceInfoIntent);
    }

    private void addPlace() {
        Intent addPlaceIntent = new Intent(getContext(), PlaceActivity.class);
        addPlaceIntent.putExtra(SystemConstants.OPERATION, PlaceActivity.OPERATION_ADD);
        startActivityForResult(addPlaceIntent, ADD);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.crud_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.menu_edit:
                editSelectedPlace(info);
                return true;
            case R.id.menu_remove:
                removeSelectedPlace(info);
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void editSelectedPlace(final AdapterView.AdapterContextMenuInfo info) {
        this.selectedPlace = listAdapter.getItem(info.position);
        Intent editPlaceIntent = new Intent(getContext(), PlaceActivity.class);
        editPlaceIntent.putExtra(SystemConstants.OPERATION, PlaceActivity.OPERATION_EDIT);
        editPlaceIntent.putExtra(SystemConstants.PLACE, selectedPlace);
        startActivityForResult(editPlaceIntent, EDIT);
    }

    private void removeSelectedPlace(final AdapterView.AdapterContextMenuInfo info) {
        final Place selectedPlace = listAdapter.getItem(info.position);
        if (!selectedPlace.getUserId().equals(SessionUtils.getCurrentUser().getUuid())) {
            ViewUtils.showAlertDialog(getActivity(), getString(R.string.access_denied), getString(R.string.only_owner_can_remove_selected_item));
            return;
        }
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext(), R.style.AlertDialog);
        dialog.setTitle(getString(R.string.remove_place));
        dialog.setMessage(getString(R.string.confirm_remove_place));
        dialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ViewUtils.showProgressBar(progressBarHolder);
                try {
                    placeDao.delete(selectedPlace);
                    places.remove(selectedPlace);
                    removePlaceLocation(selectedPlace);
                    listAdapter.notifyDataSetChanged();
                    ViewUtils.hideProgressBar(progressBarHolder);
                } catch (DaoException e) {
                    ViewUtils.hideProgressBar(progressBarHolder);
                    Toast.makeText(getContext(), getString(R.string.error_removing_place),
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        dialog.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void removePlaceLocation(Place place){
        GeoFireHelper.getGeoFire().removeLocation(place.getId(), new GeoFire.CompletionListener() {

            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error != null) {
                    Log.e(LOG_TAG, getString(R.string.geofire_remove_place_error));
                }
            }
        });
    }

    private void loadCurrentLocation() {
        ViewUtils.showProgressBar(progressBarHolder);
        this.locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!isLocationAccessGranted) {
            ViewUtils.hideProgressBar(progressBarHolder);
            requestLocationAccessPermission();
        } else if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 5, this);
            this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 15000, 5, this);
        }
    }

    /**
     * Calculate the places distances from
     * the current location
     */
    private void calculatePlacesDistances() {
        ViewUtils.showProgressBar(progressBarHolder);
        LatLng curLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        for (Place place : places) {
            LatLng placeLocation = new LatLng(place.getLatitude(), place.getLongitude());
            Double distance = SphericalUtil.computeDistanceBetween(curLocation, placeLocation) / 1000;
            place.setDistanceFromCurrentLocation(distance.floatValue());
        }
        listAdapter.notifyDataSetChanged();
        ViewUtils.hideProgressBar(progressBarHolder);
    }

    private void calculatePlaceDistance(Place place) {
        LatLng curLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        LatLng placeLocation = new LatLng(place.getLatitude(), place.getLongitude());
        Double distance = SphericalUtil.computeDistanceBetween(curLocation, placeLocation) / 1000;
        place.setDistanceFromCurrentLocation(distance.floatValue());
    }

    private void loadPlaces() {
        Log.d(LOG_TAG, getString(R.string.loading_places));
        ViewUtils.showProgressBar(progressBarHolder);

        placeDao.getDatabaseReference().addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        places.clear();
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (DataSnapshot child : children) {
                            Place place = child.getValue(Place.class);
                            places.add(place);
                        }
                        sortPlacesByName();
                        loadCurrentLocation();
                        listAdapter.notifyDataSetChanged();
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

    private void sortPlacesByName() {
        Comparator comparator = new Comparator<Place>() {

            @Override
            public int compare(Place prov1, Place prov2) {
                return prov1.getName().compareTo(prov2.getName());
            }
        };

        Collections.sort(places, comparator);
    }

    private void configureListAdapter() {
        this.listAdapter = new PlaceAdapter(getContext(), R.id.list_view, places);
        this.placesListView.setAdapter(listAdapter);

        this.placesListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if (btAddPlace.getVisibility() == View.VISIBLE) {
                    btAddPlace.setVisibility(View.GONE);
                } else {
                    btAddPlace.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ADD:
                if (resultCode == SystemConstants.OPERATION_STATUS_OK) {
                    Place addedPlace = (Place) data.getSerializableExtra(SystemConstants.PLACE);
                    places.add(addedPlace);
                    sortPlacesByName();
                    calculatePlaceDistance(addedPlace);
                    listAdapter.notifyDataSetChanged();
                } else if (resultCode == SystemConstants.OPERATION_STATUS_ERROR) {
                    Toast.makeText(getContext(), getString(R.string.error_adding_place),
                            Toast.LENGTH_LONG).show();
                }
                break;
            case EDIT:
                if (resultCode == SystemConstants.OPERATION_STATUS_OK) {
                    Place editedPlace = (Place) data.getSerializableExtra(SystemConstants.PLACE);
                    updateSelectedPlace(editedPlace);
                    sortPlacesByName();
                    listAdapter.notifyDataSetChanged();
                } else if (resultCode == SystemConstants.OPERATION_STATUS_ERROR) {
                    Toast.makeText(getContext(), getString(R.string.error_editing_place),
                            Toast.LENGTH_LONG).show();
                }
        }
    }

    private void updateSelectedPlace(Place place) {
        this.selectedPlace.setName(place.getName());
        this.selectedPlace.setPhoneNumber(place.getPhoneNumber());
        this.selectedPlace.setEmail(place.getEmail());
        this.selectedPlace.setAddress(place.getAddress());
        this.selectedPlace.setDescription(place.getDescription());
        this.selectedPlace.setLatitude(place.getLatitude());
        this.selectedPlace.setLongitude(place.getLongitude());
        calculatePlaceDistance(this.selectedPlace);
    }

    private void checkLocationAccessPermission() {
        this.isLocationAccessGranted = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationAccessPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, SystemConstants.REQUEST_LOCATION_PERMISSION);
    }

    @Override
    public void onLocationChanged(Location location) {
        this.currentLocation = location;
        calculatePlacesDistances();
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
            case SystemConstants.REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.isLocationAccessGranted = true;
                } else {
                    this.isLocationAccessGranted = false;
                }
        }
    }
}
