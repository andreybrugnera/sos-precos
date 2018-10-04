package br.edu.ifspsaocarlos.sosprecos.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.adapter.RatingInformationAdapter;
import br.edu.ifspsaocarlos.sosprecos.dao.RatingDao;
import br.edu.ifspsaocarlos.sosprecos.dao.ServiceRatingDao;
import br.edu.ifspsaocarlos.sosprecos.dto.RatingInformationDto;
import br.edu.ifspsaocarlos.sosprecos.model.Place;
import br.edu.ifspsaocarlos.sosprecos.model.Rating;
import br.edu.ifspsaocarlos.sosprecos.model.Service;
import br.edu.ifspsaocarlos.sosprecos.model.ServiceRating;
import br.edu.ifspsaocarlos.sosprecos.util.NumberUtils;
import br.edu.ifspsaocarlos.sosprecos.util.SystemConstants;
import br.edu.ifspsaocarlos.sosprecos.util.ViewUtils;
import br.edu.ifspsaocarlos.sosprecos.util.comparator.RatingComparator;

public class ServiceInfoActivity extends AppCompatActivity {

    private static final String LOG_TAG = "SERVICE_INFO";

    private Service service;
    private Place place;
    private TextView tvPlaceName;

    private ListView listView;
    private RatingInformationAdapter listAdapter;
    private List<RatingInformationDto> ratingsList;

    private RatingDao ratingDao;
    private ServiceRatingDao serviceRatingDao;
    private int totalRatingsLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_info);

        this.service = (Service) getIntent().getSerializableExtra(SystemConstants.SERVICE);
        this.place = (Place) getIntent().getSerializableExtra(SystemConstants.PLACE);

        this.tvPlaceName = findViewById(R.id.tv_place_name);
        this.tvPlaceName.setText(place.getName());

        this.ratingDao = new RatingDao(this);
        this.serviceRatingDao = new ServiceRatingDao(this);

        this.listView = findViewById(R.id.list_view);
        this.ratingsList = new ArrayList<>();

        configureToolbar();
        configureListAdapter();
        updateUI();
        loadRatings();
    }

    private void configureListAdapter() {
        this.listAdapter = new RatingInformationAdapter(this, R.id.list_view, ratingsList);
        this.listView.setAdapter(listAdapter);
        ViewUtils.setListViewHeightBasedOnItems(this.listView);
    }

    /**
     * Trace route to the place location
     *
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
        tvScore.setText("(" + String.valueOf(NumberUtils.format(service.getAverageScore())) + ")");

        RatingBar ratingBar = findViewById(R.id.rating_bar);
        ratingBar.setRating(service.getAverageScore());

        TextView tvPrice = findViewById(R.id.tv_price);
        tvPrice.setText("$" + NumberUtils.format(this.service.getPrice()));
    }

    public void rateService(View v) {
        Intent intentRateService = new Intent(this, RatingServiceActivity.class);
        intentRateService.putExtra(SystemConstants.SERVICE, service);
        intentRateService.putExtra(SystemConstants.PLACE, place);
        startActivity(intentRateService);
    }

    private void loadRatings() {
        final List<ServiceRating> serviceRatings = new ArrayList<>();

        Query query = serviceRatingDao.getDatabaseReference()
                .orderByChild("serviceIdRegistrationDate")
                .startAt(service.getId() + "_0")
                .endAt(service.getId() + "_" + Long.MAX_VALUE)
                .limitToLast(5);

        query.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (DataSnapshot child : children) {
                            ServiceRating serviceRating = child.getValue(ServiceRating.class);
                            serviceRatings.add(serviceRating);
                        }
                        loadRatings(serviceRatings);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(LOG_TAG, databaseError.getMessage());
                        Log.e(LOG_TAG, databaseError.getDetails());
                    }
                });
    }

    private int getTotalRatingsLoaded() {
        return totalRatingsLoaded;
    }

    private void loadRatings(final List<ServiceRating> serviceRatings) {
        this.ratingsList.clear();
        this.totalRatingsLoaded = 0;
        for (ServiceRating serviceRating : serviceRatings) {
            Query query = ratingDao.getDatabaseReference().orderByChild("id").equalTo(serviceRating.getRateId());
            query.addListenerForSingleValueEvent(
                    new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                            for (DataSnapshot child : children) {
                                Rating rating = child.getValue(Rating.class);
                                RatingInformationDto ratingInformationDto = RatingInformationDto.getInstance(rating);
                                if (!ratingsList.contains(ratingInformationDto)) {
                                    totalRatingsLoaded++;
                                    ratingsList.add(ratingInformationDto);
                                }
                            }
                            if (getTotalRatingsLoaded() == serviceRatings.size()) {
                                //All ratings loaded
                                Collections.sort(ratingsList, new RatingComparator(RatingComparator.ORDER_BY_DATE_DESC));
                                listAdapter.notifyDataSetChanged();
                                ViewUtils.setListViewHeightBasedOnItems(listView);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(LOG_TAG, databaseError.getMessage());
                            Log.e(LOG_TAG, databaseError.getDetails());
                        }
                    });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRatings();
    }
}
