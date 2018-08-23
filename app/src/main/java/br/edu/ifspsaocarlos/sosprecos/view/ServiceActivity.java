package br.edu.ifspsaocarlos.sosprecos.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.dao.ServiceDao;
import br.edu.ifspsaocarlos.sosprecos.dao.exception.DaoException;
import br.edu.ifspsaocarlos.sosprecos.model.Place;
import br.edu.ifspsaocarlos.sosprecos.model.Service;
import br.edu.ifspsaocarlos.sosprecos.util.ViewUtils;

public class ServiceActivity extends AppCompatActivity {
    private static final String LOG_TAG = "ADD_EDIT_SERVICE";

    public static final int OPERATION_STATUS_ERROR = -1;
    public static final int OPERATION_STATUS_OK = 1;

    public static final int OPERATION_ADD = 2;
    public static final int OPERATION_EDIT = 3;

    public static final String OPERATION = "operation";
    public static final String PLACE = "place";
    public static final String SERVICE = "service";

    private FrameLayout progressBarHolder;
    private TextView tvTitle;
    private EditText etServiceName;
    private Button btAddOrEditService;
    private EditText etServicePrice;
    private EditText etServiceDescription;
    private TextView tvPlaceName;

    private ServiceDao serviceDao;
    private Place place;
    private Service editingService;
    private List<Service> services;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        this.place = (Place) getIntent().getSerializableExtra(PLACE);
        this.editingService = (Service) getIntent().getSerializableExtra(SERVICE);
        this.serviceDao = new ServiceDao(this);

        this.tvTitle = findViewById(R.id.tv_title);
        this.progressBarHolder = findViewById(R.id.progress_bar_holder);
        this.etServiceName = findViewById(R.id.et_service_name);
        this.etServiceDescription = findViewById(R.id.et_service_description);
        this.etServicePrice = findViewById(R.id.et_service_price);
        this.btAddOrEditService = findViewById(R.id.bt_add_edit_service);

        this.tvPlaceName = findViewById(R.id.tv_place_name);
        this.tvPlaceName.setText(place.getName());

        configureToolbar();
        defineOperation();
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

    private void updateUIWithEditingServiceData() {
        this.tvTitle.setText(getString(R.string.edit_service));
        this.btAddOrEditService.setText(getString(R.string.edit));
        this.etServiceName.setText(editingService.getName());
        this.etServiceDescription.setText(editingService.getDescription());
        this.etServicePrice.setText(String.valueOf(editingService.getPrice()));
    }

    private boolean validateInputFields(Service service) {
        String serviceName = this.etServiceName.getText().toString();
        if (TextUtils.isEmpty(serviceName)) {
            this.etServiceName.setError(getString(R.string.enter_service_name));
            this.etServiceName.requestFocus();
            return false;
        }

        String serviceDescription = this.etServiceDescription.getText().toString();
        if (TextUtils.isEmpty(serviceDescription)) {
            this.etServiceDescription.setError(getString(R.string.enter_service_description));
            this.etServiceDescription.requestFocus();
            return false;
        }

        String servicePrice = this.etServicePrice.getText().toString();
        if (TextUtils.isEmpty(servicePrice)) {
            this.etServicePrice.setError(getString(R.string.enter_service_price));
            this.etServicePrice.requestFocus();
            return false;
        }

        service.setName(serviceName);
        service.setDescription(serviceDescription);
        service.setPrice(Float.valueOf(servicePrice));
        service.setPlaceId(place.getId());

        return true;
    }

    private void editService() {
        ViewUtils.showProgressBar(progressBarHolder);
        if (validateInputFields(editingService)) {
            try {
                serviceDao.update(editingService);
                Intent returnIntent = new Intent();
                returnIntent.putExtra(SERVICE, editingService);
                setResult(OPERATION_STATUS_OK, returnIntent);
                ViewUtils.hideProgressBar(progressBarHolder);
            } catch (DaoException ex) {
                Log.e(LOG_TAG, getString(R.string.error_editing_service), ex);
            }
            finish();
        }
    }

    private void addService() {
        ViewUtils.showProgressBar(progressBarHolder);
        Service service = Service.getInstance();
        if (validateInputFields(service)) {
            try {
                serviceDao.add(service);
                Intent returnIntent = new Intent();
                returnIntent.putExtra(SERVICE, service);
                setResult(OPERATION_STATUS_OK, returnIntent);
                ViewUtils.hideProgressBar(progressBarHolder);
            } catch (DaoException ex) {
                Log.e(LOG_TAG, getString(R.string.error_adding_service), ex);
            }
            finish();
        }
    }

    private void defineOperation() {
        int operation = getIntent().getIntExtra(OPERATION, OPERATION_ADD);
        switch (operation) {
            case OPERATION_EDIT:
                updateUIWithEditingServiceData();
                this.btAddOrEditService.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editService();
                    }
                });
                break;
            case OPERATION_ADD:
                this.btAddOrEditService.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addService();
                    }
                });
        }
    }
}
