package br.edu.ifspsaocarlos.sosprecos.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.dao.CategoryDao;
import br.edu.ifspsaocarlos.sosprecos.dao.CategoryProviderDao;
import br.edu.ifspsaocarlos.sosprecos.model.Category;
import br.edu.ifspsaocarlos.sosprecos.model.CategoryProvider;
import br.edu.ifspsaocarlos.sosprecos.model.Provider;
import br.edu.ifspsaocarlos.sosprecos.view.maps.MapActivity;

public class ProviderInfoActivity extends AppCompatActivity {

    private static final String LOG_TAG = "PROVIDER_INFO";

    public static final String PROVIDER = "provider";

    private ProgressBar progressBar;
    private TextView tvCategory;

    private Provider provider;
    private CategoryProviderDao categoryProviderDao;
    private CategoryDao categoryDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_info);

        this.progressBar = findViewById(R.id.progress_bar);
        this.tvCategory = findViewById(R.id.tv_category);

        this.categoryProviderDao = new CategoryProviderDao(this);
        this.categoryDao = new CategoryDao(this);

        this.provider = (Provider) getIntent().getSerializableExtra(PROVIDER);

        configureToolbar();
        updateUI();
    }

    private void updateUI() {
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(this.provider.getName());

        TextView tvPhone = findViewById(R.id.tv_phone);
        tvPhone.setText(this.provider.getPhoneNumber());

        TextView tvAddress = findViewById(R.id.tv_address);
        tvAddress.setText(this.provider.getAddress());

        RatingBar ratingBar = findViewById(R.id.rating_bar);
        ratingBar.setRating(provider.getAverageScore());

        TextView tvScore = findViewById(R.id.tv_score);
        tvScore.setText("(" + String.valueOf(provider.getAverageScore()) + ")");

        TextView tvDescription = findViewById(R.id.tv_description);
        tvDescription.setText(provider.getDescription());
        loadProvidersCategory();
    }

    private void loadProvidersCategory() {
        Log.d(LOG_TAG, getString(R.string.loading_category));
        progressBar.setVisibility(View.VISIBLE);

        Query query = categoryProviderDao.getDatabaseReference().orderByChild("providerId").equalTo(provider.getId());
        query.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (DataSnapshot child : children) {
                            CategoryProvider categoryProvider = child.getValue(CategoryProvider.class);
                            loadCategory(categoryProvider.getCategoryId());
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

    private void loadCategory(final String categoryId) {
        Log.d(LOG_TAG, getString(R.string.loading_category));
        progressBar.setVisibility(View.VISIBLE);

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
     * Open maps with provider's location
     *
     * @param v
     */
    public void showLocation(View v) {
        Intent intentMap = new Intent(this, MapActivity.class);
        intentMap.putExtra(MapActivity.PROVIDER, provider);
        startActivity(intentMap);
    }

    /**
     * Opens activity with all available services
     *
     * @param v
     */
    public void showServices(View v) {
        Intent intentServices = new Intent(this, ServiceListActivity.class);
        intentServices.putExtra(ServiceListActivity.PROVIDER, provider);
        startActivity(intentServices);
    }

    public void rateProvider(View V) {
        Intent intentRateProvider = new Intent(this, RatingProviderActivity.class);
        intentRateProvider.putExtra(RatingProviderActivity.PROVIDER, provider);
        startActivity(intentRateProvider);
    }
}
