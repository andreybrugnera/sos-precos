package br.edu.ifspsaocarlos.sosprecos.view;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.model.Provider;
import br.edu.ifspsaocarlos.sosprecos.util.ZipCodeWebService;

public class ProviderActivity extends AppCompatActivity {
    private static final String LOG_TAG = "ADD_EDIT_PROVIDER";

    public static final int OPERATION_STATUS_ERROR = -1;
    public static final int OPERATION_STATUS_OK = 1;

    public static final int OPERATION_ADD = 2;
    public static final int OPERATION_EDIT = 3;

    public static final String OPERATION = "operation";
    public static final String PROVIDER = "provider";

    private ProgressBar progressBar;
    private TextView tvTitle;
    private EditText etProviderName;
    private AutoCompleteTextView acTvProviderEmail;
    private EditText etProviderPhone;
    private EditText etProviderStreet;
    private EditText etProviderNumber;
    private EditText etProviderCity;
    private EditText etProviderState;
    private EditText etProviderZIP;
    private Button btAddOrEditProvider;
    private Button btSearchZip;

    private Provider editingProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider);

        this.progressBar = findViewById(R.id.progress_bar);
        this.tvTitle = findViewById(R.id.tv_title);
        this.etProviderName = findViewById(R.id.et_provider_name);
        this.acTvProviderEmail = findViewById(R.id.actv_provider_email);
        this.etProviderPhone = findViewById(R.id.et_provider_phone);
        this.etProviderStreet = findViewById(R.id.et_provider_street);
        this.etProviderNumber = findViewById(R.id.et_provider_number);
        this.etProviderCity = findViewById(R.id.et_provider_city);
        this.etProviderState = findViewById(R.id.et_provider_state);
        this.etProviderZIP = findViewById(R.id.et_provider_zip);
        this.btAddOrEditProvider = findViewById(R.id.bt_add_edit_provider);

        this.btSearchZip = findViewById(R.id.bt_search_zip);
        this.btSearchZip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestZipCodeSearch();
            }
        });

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

    private void defineOperation() {
        int operation = getIntent().getIntExtra(OPERATION, OPERATION_ADD);
        switch (operation) {
            case OPERATION_EDIT:
                this.tvTitle.setText(R.string.edit_provider);
                this.btAddOrEditProvider.setText(R.string.edit);
                this.editingProvider = (Provider) getIntent().getSerializableExtra(PROVIDER);
                fillInputFields();
                this.btAddOrEditProvider.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editProvider();
                    }
                });
                break;
            case OPERATION_ADD:
                this.btAddOrEditProvider.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addProvider();
                    }
                });
        }
    }

    private void fillInputFields() {
        this.etProviderName.setText(this.editingProvider.getName());
        this.acTvProviderEmail.setText(this.editingProvider.getEmail());
        this.etProviderPhone.setText(this.editingProvider.getPhoneNumber());
        this.etProviderStreet.setText(this.editingProvider.getStreet());
        this.etProviderCity.setText(this.editingProvider.getCity());
        this.etProviderState.setText(this.editingProvider.getState());
        this.etProviderNumber.setText(this.editingProvider.getNumber());
        this.etProviderZIP.setText(this.editingProvider.getZipCode());
    }

    private void editProvider() {
    }

    private void addProvider() {
    }

    private void requestZipCodeSearch() {
        String zipCode = this.etProviderZIP.getText().toString();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Response.Listener<JSONObject> responseListener = searchZipCode();
        Response.ErrorListener errorListener = createErrorResponseListener();
        String zipWebService = ZipCodeWebService.getResourceURL(zipCode);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, zipWebService, null, responseListener, errorListener);
        requestQueue.add(jsonRequest);
    }

    private Response.Listener<JSONObject> searchZipCode() {
        progressBar.setVisibility(View.VISIBLE);
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response != null) {
                        etProviderStreet.setText(response.getString(ZipCodeWebService.STREET));
                        etProviderState.setText(response.getString(ZipCodeWebService.STATE));
                        etProviderCity.setText(response.getString(ZipCodeWebService.CITY));
                    }
                } catch (JSONException ex) {
                    etProviderStreet.getText().clear();
                    etProviderState.getText().clear();
                    etProviderCity.getText().clear();
                    etProviderZIP.getText().clear();
                    Toast.makeText(ProviderActivity.this, getString(R.string.search_zip_error), Toast.LENGTH_LONG).show();
                    Log.e(LOG_TAG, getString(R.string.search_zip_error), ex);
                }
                progressBar.setVisibility(View.GONE);
            }
        };
    }

    private Response.ErrorListener createErrorResponseListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ProviderActivity.this, getString(R.string.search_zip_error), Toast.LENGTH_LONG).show();
            }
        };
    }
}
