package br.edu.ifspsaocarlos.sosprecos.view.maps;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.model.Place;
import br.edu.ifspsaocarlos.sosprecos.util.ImageUtils;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googleMaps;

    private final float MAPS_MIN_ZOOM = 0;
    private final float MAPS_MAX_ZOOM = 18;

    public static final String PLACE = "place";
    private Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        this.place = (Place) getIntent().getSerializableExtra(PLACE);

        // Get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMaps = googleMap;
        googleMaps.clear();

        LatLng atuaiLocation = new LatLng(place.getLatitude(), place.getLongitude());
        googleMaps.addMarker(new MarkerOptions()
                .position(atuaiLocation)
                .icon(ImageUtils.createBitmapDescriptorFromVector(this, R.drawable.ic_location_pointer, 2))
                .title(place.getName()));
        googleMaps.moveCamera(CameraUpdateFactory.newLatLng(atuaiLocation));
        googleMaps.setMinZoomPreference(MAPS_MIN_ZOOM);
        googleMaps.setMaxZoomPreference(MAPS_MAX_ZOOM);
        //Aplica zoom
        googleMaps.animateCamera(CameraUpdateFactory.zoomTo(MAPS_MAX_ZOOM), 1000, null);
    }
}
