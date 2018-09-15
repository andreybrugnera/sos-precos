package br.edu.ifspsaocarlos.sosprecos.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.dao.CategoryDao;
import br.edu.ifspsaocarlos.sosprecos.dao.CategoryPlaceDao;
import br.edu.ifspsaocarlos.sosprecos.model.Category;
import br.edu.ifspsaocarlos.sosprecos.model.CategoryPlace;
import br.edu.ifspsaocarlos.sosprecos.model.Place;
import br.edu.ifspsaocarlos.sosprecos.util.SystemConstants;
import br.edu.ifspsaocarlos.sosprecos.util.ViewUtils;
import br.edu.ifspsaocarlos.sosprecos.view.maps.MapActivity;

public class PlaceInfoActivity extends AppCompatActivity {

    private static final String LOG_TAG = "PLACE_INFO";

    private FrameLayout progressBarHolder;
    private TextView tvCategory;

    private Place place;
    private CategoryPlaceDao categoryPlaceDao;
    private CategoryDao categoryDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_info);

        this.progressBarHolder = findViewById(R.id.progress_bar_holder);
        this.tvCategory = findViewById(R.id.tv_category);

        this.categoryPlaceDao = new CategoryPlaceDao(this);
        this.categoryDao = new CategoryDao(this);

        this.place = (Place) getIntent().getSerializableExtra(SystemConstants.PLACE);

        configureToolbar();
        updateUI();
    }

    private void updateUI() {
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(this.place.getName());

        TextView tvPhone = findViewById(R.id.tv_phone);
        tvPhone.setText(this.place.getPhoneNumber());

        TextView tvAddress = findViewById(R.id.tv_address);
        tvAddress.setText(this.place.getAddress());

        RatingBar ratingBar = findViewById(R.id.rating_bar);
        ratingBar.setRating(place.getAverageScore());

        TextView tvScore = findViewById(R.id.tv_score);
        tvScore.setText("(" + String.valueOf(place.getAverageScore()) + ")");

        TextView tvDescription = findViewById(R.id.tv_description);
        tvDescription.setText(place.getDescription());
        loadPlacesCategory();
    }

    private void loadPlacesCategory() {
        Log.d(LOG_TAG, getString(R.string.loading_category));
        ViewUtils.showProgressBar(progressBarHolder);

        Query query = categoryPlaceDao.getDatabaseReference().orderByChild("placeId").equalTo(place.getId());
        query.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (DataSnapshot child : children) {
                            CategoryPlace categoryPlace = child.getValue(CategoryPlace.class);
                            loadCategory(categoryPlace.getCategoryId());
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

    private void loadCategory(final String categoryId) {
        Log.d(LOG_TAG, getString(R.string.loading_category));
        ViewUtils.showProgressBar(progressBarHolder);

        Query query = categoryDao.getDatabaseReference().orderByChild("id").equalTo(categoryId);
        query.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (DataSnapshot child : children) {
                            Category category = child.getValue(Category.class);
                            tvCategory.setText(category.getName());
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

    /**
     * Open maps with place's location
     *
     * @param v
     */
    public void showLocation(View v) {
        Intent intentMap = new Intent(this, MapActivity.class);
        intentMap.putExtra(SystemConstants.PLACE, place);
        startActivity(intentMap);
    }

    /**
     * Opens activity with all available services
     *
     * @param v
     */
    public void showServices(View v) {
        Intent intentServices = new Intent(this, ServiceListActivity.class);
        intentServices.putExtra(SystemConstants.PLACE, place);
        startActivity(intentServices);
    }

    public void rateProvider(View V) {
        Intent intentRateProvider = new Intent(this, RatingPlaceActivity.class);
        intentRateProvider.putExtra(SystemConstants.PLACE, place);
        intentRateProvider.putExtra(SystemConstants.CATEGORY, tvCategory.getText().toString());
        startActivity(intentRateProvider);
    }
}
