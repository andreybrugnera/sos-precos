package br.edu.ifspsaocarlos.sosprecos.view;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.dao.RatingDao;
import br.edu.ifspsaocarlos.sosprecos.dao.ServiceRatingDao;
import br.edu.ifspsaocarlos.sosprecos.dao.exception.DaoException;
import br.edu.ifspsaocarlos.sosprecos.model.Place;
import br.edu.ifspsaocarlos.sosprecos.model.Rating;
import br.edu.ifspsaocarlos.sosprecos.model.Service;
import br.edu.ifspsaocarlos.sosprecos.model.ServiceRating;
import br.edu.ifspsaocarlos.sosprecos.util.DateTimeUtils;
import br.edu.ifspsaocarlos.sosprecos.util.SessionUtils;
import br.edu.ifspsaocarlos.sosprecos.util.SystemConstants;
import br.edu.ifspsaocarlos.sosprecos.util.ViewUtils;

public class RatingServiceActivity extends AppCompatActivity {
    private static final String LOG_TAG = "RATING_SERVICE";

    private FrameLayout progressBarHolder;
    private TextView tvPlaceName;
    private EditText etRateDescription;
    private RatingBar priceRatingBar;
    private RatingBar qualityRatingBar;

    private Service service;
    private Place place;
    private RatingDao ratingDao;
    private ServiceRatingDao serviceRatingDao;
    private Rating existingRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_service);

        this.ratingDao = new RatingDao(this);
        this.serviceRatingDao = new ServiceRatingDao(this);

        this.progressBarHolder = findViewById(R.id.progress_bar_holder);
        this.etRateDescription = findViewById(R.id.et_rate_description);
        this.priceRatingBar = findViewById(R.id.rating_price);
        this.qualityRatingBar = findViewById(R.id.rating_quality);
        this.tvPlaceName = findViewById(R.id.tv_place_name);

        this.service = (Service) getIntent().getSerializableExtra(SystemConstants.SERVICE);

        this.place = (Place) getIntent().getSerializableExtra(SystemConstants.PLACE);
        this.tvPlaceName.setText(place.getName());

        configureToolbar();
        loadExistingRating();
        updateUI();
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

        if (existingRating != null) {
            etRateDescription.setText(existingRating.getDescription());
            priceRatingBar.setRating(existingRating.getPriceScore());
            qualityRatingBar.setRating(existingRating.getQualityScore());
            fillRatingRegistrationDateInformation();
        }
    }

    private void fillRatingRegistrationDateInformation() {
        LinearLayout registrationDateInformation = findViewById(R.id.rating_registration_date_information);
        TextView tvRatingRegistrationDate = findViewById(R.id.tv_rating_registration_date);
        tvRatingRegistrationDate.setText(DateTimeUtils.formatDateTimestamp(existingRating.getRegistrationDate()));
        registrationDateInformation.setVisibility(View.VISIBLE);
    }

    private void loadExistingRating() {
        Log.d(LOG_TAG, getString(R.string.loading_existing_rate));
        ViewUtils.showProgressBar(progressBarHolder);

        Query query = serviceRatingDao.getDatabaseReference()
                .orderByChild("serviceIdUserId").equalTo(service.getId() + "_" + SessionUtils.getCurrentUser().getUuid());

        query.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (DataSnapshot child : children) {
                            ServiceRating serviceRating = child.getValue(ServiceRating.class);
                            loadExistingRating(serviceRating.getRateId());
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

    private void loadExistingRating(String rateId) {
        Log.d(LOG_TAG, getString(R.string.loading_existing_rate_by_id));
        ViewUtils.showProgressBar(progressBarHolder);

        Query query = ratingDao.getDatabaseReference().orderByChild("id").equalTo(rateId);

        query.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (DataSnapshot child : children) {
                            Rating rating = child.getValue(Rating.class);
                            existingRating = rating;
                            updateUI();
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

    private boolean validateInputFields(Rating rating) {
        String description = etRateDescription.getText().toString();
        Float priceRating = priceRatingBar.getRating();
        Float qualityRating = qualityRatingBar.getRating();

        if (!priceRating.equals(0) || !qualityRating.equals(0)) {
            rating.setPriceScore(priceRating);
            rating.setQualityScore(qualityRating);
            rating.setLocationScore(5);
            rating.setDescription(description);
            rating.setRegistrationDate(new Date());
            rating.setUserName(SessionUtils.getCurrentUser().getName());
            rating.setUserId(SessionUtils.getCurrentUser().getUuid());
            rating.setOwnerId(service.getId());
            return true;
        }
        return false;
    }

    public void saveRating(View v) {
        Rating rating = existingRating == null ? new Rating() : existingRating;
        if (validateInputFields(rating)) {
            try {
                if (existingRating == null) {
                    ratingDao.add(rating);

                    ServiceRating serviceRating = new ServiceRating();
                    serviceRating.setServiceId(service.getId());
                    serviceRating.setRateId(rating.getId());
                    serviceRating.setUserId(SessionUtils.getCurrentUser().getUuid());
                    serviceRating.setServiceIdUserId(service.getId() + "_" + SessionUtils.getCurrentUser().getUuid());
                    serviceRating.setRegistrationDate(rating.getRegistrationDate());
                    serviceRating.setServiceIdRegistrationDate(service.getId() + "_" + rating.getRegistrationDate().getTime());
                    serviceRatingDao.add(serviceRating);
                } else {
                    ratingDao.update(rating);
                }
            } catch (DaoException ex) {
                Log.e(LOG_TAG, getString(R.string.error_saving_or_updating_rating), ex);
            }
        }
        finish();
    }
}
