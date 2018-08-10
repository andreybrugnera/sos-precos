package br.edu.ifspsaocarlos.sosprecos.view;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.dao.ProviderRatingDao;
import br.edu.ifspsaocarlos.sosprecos.dao.RatingDao;
import br.edu.ifspsaocarlos.sosprecos.dao.exception.DaoException;
import br.edu.ifspsaocarlos.sosprecos.model.Provider;
import br.edu.ifspsaocarlos.sosprecos.model.ProviderRating;
import br.edu.ifspsaocarlos.sosprecos.model.Rating;
import br.edu.ifspsaocarlos.sosprecos.util.DateTimeUtils;
import br.edu.ifspsaocarlos.sosprecos.util.SessionUtils;

public class RatingProviderActivity extends AppCompatActivity {

    private static final String LOG_TAG = "RATING_PROVIDER";

    public static final String PROVIDER = "provider";

    private ProgressBar progressBar;
    private EditText etRateDescription;
    private RatingBar priceRatingBar;
    private RatingBar qualityRatingBar;
    private RatingBar locationRatingBar;

    private Provider provider;
    private RatingDao ratingDao;
    private ProviderRatingDao providerRatingDao;
    private Rating existingRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_provider);

        this.ratingDao = new RatingDao(this);
        this.providerRatingDao = new ProviderRatingDao(this);

        this.progressBar = findViewById(R.id.progress_bar);
        this.etRateDescription = findViewById(R.id.et_rate_description);
        this.priceRatingBar = findViewById(R.id.rating_price);
        this.qualityRatingBar = findViewById(R.id.rating_quality);
        this.locationRatingBar = findViewById(R.id.rating_location);

        this.provider = (Provider) getIntent().getSerializableExtra(PROVIDER);

        configureToolbar();
        loadExistingRating();

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
        tvTitle.setText(this.provider.getName());

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
        tvRatingRegistrationDate.setText(DateTimeUtils.formatDate(existingRating.getRegistrationDate()));
        registrationDateInformation.setVisibility(View.VISIBLE);
    }

    private void loadExistingRating() {
        Log.d(LOG_TAG, getString(R.string.loading_existing_rate));
        progressBar.setVisibility(View.VISIBLE);

        Query query = providerRatingDao.getDatabaseReference()
                .orderByChild("providerIdUserId").equalTo(provider.getId() + "_" + SessionUtils.getCurrentUserId());

        query.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (DataSnapshot child : children) {
                            ProviderRating providerRating = child.getValue(ProviderRating.class);
                            loadExistingRating(providerRating.getRateId());
                            break;
                        }
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(LOG_TAG, databaseError.getMessage());
                        Log.e(LOG_TAG, databaseError.getDetails());
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void loadExistingRating(String rateId) {
        Log.d(LOG_TAG, getString(R.string.loading_existing_rate_by_id));
        progressBar.setVisibility(View.VISIBLE);

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
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(LOG_TAG, databaseError.getMessage());
                        Log.e(LOG_TAG, databaseError.getDetails());
                        progressBar.setVisibility(View.GONE);
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
            rating.setUserId(SessionUtils.getCurrentUserId());
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

                    ProviderRating providerRating = new ProviderRating();
                    providerRating.setProviderId(provider.getId());
                    providerRating.setRateId(rating.getId());
                    providerRating.setUserId(SessionUtils.getCurrentUserId());
                    providerRating.setProviderIdUserId(provider.getId() + "_" + SessionUtils.getCurrentUserId());

                    providerRatingDao.add(providerRating);
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
