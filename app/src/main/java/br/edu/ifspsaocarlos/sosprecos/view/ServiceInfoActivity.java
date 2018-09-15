package br.edu.ifspsaocarlos.sosprecos.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.model.Place;
import br.edu.ifspsaocarlos.sosprecos.model.Service;
import br.edu.ifspsaocarlos.sosprecos.util.NumberUtils;

public class ServiceInfoActivity extends AppCompatActivity {

    private static final String LOG_TAG = "SERVICE_INFO";

    public static final String PLACE = "place";
    public static final String SERVICE = "service";

    private Service service;
    private Place place;
    private TextView tvPlaceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_info);

        this.service = (Service) getIntent().getSerializableExtra(SERVICE);
        this.place = (Place) getIntent().getSerializableExtra(PLACE);

        this.tvPlaceName = findViewById(R.id.tv_place_name);
        this.tvPlaceName.setText(place.getName());

        configureToolbar();
        updateUI();
    }

    /**
     * Trace route to the place location
     * @param v
     */
    public void traceRoute(View v) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + place.getLatitude() + "," + place.getLongitude());
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
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

    private void updateUI() {
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(this.service.getName());

        TextView tvDescription = findViewById(R.id.tv_description);
        tvDescription.setText(this.service.getDescription());

        TextView tvScore = findViewById(R.id.tv_score);
        tvScore.setText("(" + String.valueOf(service.getAverageScore()) + ")");

        TextView tvPrice = findViewById(R.id.tv_price);
        tvPrice.setText("$" + NumberUtils.format(this.service.getPrice()));
    }

    public void rateService(View v) {
        Intent intentRateService = new Intent(this, RatingServiceActivity.class);
        intentRateService.putExtra(RatingServiceActivity.SERVICE, service);
        intentRateService.putExtra(RatingServiceActivity.PLACE, place);
        startActivity(intentRateService);
    }
}
