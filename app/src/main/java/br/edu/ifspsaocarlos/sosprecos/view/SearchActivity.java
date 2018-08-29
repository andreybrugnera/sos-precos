package br.edu.ifspsaocarlos.sosprecos.view;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;

import java.util.List;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.adapter.ServiceAdapter;
import br.edu.ifspsaocarlos.sosprecos.dao.PlaceDao;
import br.edu.ifspsaocarlos.sosprecos.dao.ServiceDao;
import br.edu.ifspsaocarlos.sosprecos.model.Service;
import br.edu.ifspsaocarlos.sosprecos.util.ViewUtils;

public class SearchActivity extends AppCompatActivity {

    private static final String LOG_TAG = "SEARCH";

    private PlaceDao placeDao;
    private ServiceDao serviceDao;

    private EditText etSearch;
    private FrameLayout progressBarHolder;
    private ImageView ivSearch;
    private RadioButton rbOrderByLocation;
    private RadioButton rbOrderByPrice;
    private RadioButton rbOrderByQuality;

    private ListView listView;
    private ServiceAdapter listAdapter;
    private List<Service> services;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        this.listView = findViewById(R.id.list_view);
        this.etSearch = findViewById(R.id.et_search);
        this.progressBarHolder = findViewById(R.id.progress_bar_holder);
        this.rbOrderByLocation = findViewById(R.id.radio_order_location);
        this.rbOrderByPrice = findViewById(R.id.radio_order_price);
        this.rbOrderByQuality = findViewById(R.id.radio_order_quality);
        this.ivSearch = findViewById(R.id.iv_search);

        this.ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchString = etSearch.getText().toString();
                if (validateSearchString(searchString)) {
                    search(searchString);
                }
            }
        });

        this.placeDao = new PlaceDao(this);
        this.serviceDao = new ServiceDao(this);

        configureToolbar();
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

    private boolean validateSearchString(String searchString) {
        if (TextUtils.isEmpty(searchString)) {
            this.etSearch.setError(getString(R.string.enter_valid_search_text));
            this.etSearch.requestFocus();
            return false;
        }
        return true;
    }

    private void search(String searchString) {
        ViewUtils.showProgressBar(progressBarHolder);

        Log.i(LOG_TAG, getString(R.string.searching) + " > " + searchString);

        boolean orderByPrice = this.rbOrderByPrice.isChecked();
        boolean orderByLocation = this.rbOrderByLocation.isChecked();
        boolean orderByQuality = this.rbOrderByQuality.isChecked();

        ViewUtils.hideProgressBar(progressBarHolder);
    }
}
