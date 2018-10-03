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
import br.edu.ifspsaocarlos.sosprecos.dao.PlaceRatingDao;
import br.edu.ifspsaocarlos.sosprecos.dao.RatingDao;
import br.edu.ifspsaocarlos.sosprecos.dao.exception.DaoException;
import br.edu.ifspsaocarlos.sosprecos.model.Place;
import br.edu.ifspsaocarlos.sosprecos.model.PlaceRating;
import br.edu.ifspsaocarlos.sosprecos.model.Rating;
import br.edu.ifspsaocarlos.sosprecos.util.DateTimeUtils;
import br.edu.ifspsaocarlos.sosprecos.util.SessionUtils;
import br.edu.ifspsaocarlos.sosprecos.util.SystemConstants;
import br.edu.ifspsaocarlos.sosprecos.util.ViewUtils;

public class RatingPlaceActivity extends AppCompatActivity {

    private static final String LOG_TAG = "RATING_PLACE";

    private FrameLayout progressBarHolder;
    private EditText etRateDescription;
    private TextView tvCategoryName;
    private RatingBar priceRatingBar;
    private RatingBar qualityRatingBar;
    private RatingBar locationRatingBar;

    private Place place;
    private String categoryName;
    private RatingDao ratingDao;
    private PlaceRatingDao placeRatingDao;
    private Rating existingRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_place);

        this.ratingDao = new RatingDao(this);
        this.placeRatingDao = new PlaceRatingDao(this);

        this.progressBarHolder = findViewById(R.id.progress_bar_holder);
        this.etRateDescription = findViewById(R.id.et_rate_description);
        this.tvCategoryName = findViewById(R.id.tv_category_name);
        this.priceRatingBar = findViewById(R.id.rating_price);
        this.qualityRatingBar = findViewById(R.id.rating_quality);
        this.locationRatingBar = findViewById(R.id.rating_location);

        this.place = (Place) getIntent().getSerializableExtra(SystemConstants.PLACE);

        this.categoryName = getIntent().getStringExtra(SystemConstants.CATEGORY);
        tvCategoryName.setText(categoryName);

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
        tvTitle.setText(this.place.getName());

        if (existingRating != null) {
            etRateDescription.setText(existingRating.getDescription());
            priceRatingBar.setRating(existingRating.getPriceScore());
            qualityRatingBar.setRating(existingRating.getQualityScore());
            locationRatingBar.setRating(existingRating.getLocationScore());
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

        Query query = placeRatingDao.getDatabaseReference()
                .orderByChild("placeIdUserId").equalTo(place.getId() + "_" + SessionUtils.getCurrentUser().getUuid());

        query.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (DataSnapshot child : children) {
                            PlaceRating placeRating = child.getValue(PlaceRating.class);
                            loadExistingRating(placeRating.getRateId());
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
        Float locationRating = locationRatingBar.getRating();

        if (!priceRating.equals(0) || !qualityRating.equals(0) || !locationRating.equals(0)) {
            rating.setPriceScore(priceRating);
            rating.setQualityScore(qualityRating);
            rating.setLocationScore(locationRating);
            rating.setDescription(description);
            rating.setRegistrationDate(new Date());
            rating.setUserId(SessionUtils.getCurrentUser().getUuid());
            rating.setUserName(SessionUtils.getCurrentUser().getName());
            rating.setOwnerId(place.getId());
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

                    PlaceRating placeRating = new PlaceRating();
                    placeRating.setPlaceId(place.getId());
                    placeRating.setRateId(rating.getId());
                    placeRating.setUserId(SessionUtils.getCurrentUser().getUuid());
                    placeRating.setPlaceIdUserId(place.getId() + "_" + SessionUtils.getCurrentUser().getUuid());
                    placeRating.setRegistrationDate(rating.getRegistrationDate());
                    placeRating.setPlaceIdRegistrationDate(place.getId() + "_" + rating.getRegistrationDate().getTime());
                    placeRatingDao.add(placeRating);
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
