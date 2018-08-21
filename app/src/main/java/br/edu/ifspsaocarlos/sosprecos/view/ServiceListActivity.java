package br.edu.ifspsaocarlos.sosprecos.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.adapter.ServiceAdapter;
import br.edu.ifspsaocarlos.sosprecos.dao.ServiceDao;
import br.edu.ifspsaocarlos.sosprecos.dao.exception.DaoException;
import br.edu.ifspsaocarlos.sosprecos.model.Place;
import br.edu.ifspsaocarlos.sosprecos.model.Service;

public class ServiceListActivity extends AppCompatActivity {

    private static final String LOG_TAG = "SERVICES";

    public static final String PLACE = "place";

    private ProgressBar progressBar;
    private Button btAddService;
    private ListView servicesListView;
    private ServiceAdapter listAdapter;
    private TextView viewTitle;

    private ServiceDao serviceDao;
    private List<Service> services;
    private Service selectedService;
    private Place place;

    private static final int ADD = 1;
    private static final int EDIT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_list);

        this.place = (Place) getIntent().getSerializableExtra(PLACE);

        this.serviceDao = new ServiceDao(this);
        this.services = new ArrayList<>();

        this.progressBar = findViewById(R.id.progress_bar);
        this.servicesListView = findViewById(R.id.list_view);
        this.viewTitle = findViewById(R.id.list_title);
        this.viewTitle.setText(getString(R.string.services));

        this.btAddService = findViewById(R.id.bt_add_edit_item);
        this.btAddService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addService();
            }
        });

        this.servicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedService = listAdapter.getItem(position);
                if (selectedService != null) {
                    editService(selectedService);
                }
            }
        });

        this.servicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Service service = listAdapter.getItem(position);
                if (service != null) {
                    openServiceInfo(service);
                }
            }
        });

        configureToolbar();
        configureListAdapter();
        registerForContextMenu(this.servicesListView);
        loadServices(place.getId());
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

    private void editService(Service service) {
        Intent editServiceIntent = new Intent(this, ServiceActivity.class);
        editServiceIntent.putExtra(ServiceActivity.OPERATION, ServiceActivity.OPERATION_EDIT);
        editServiceIntent.putExtra(ServiceActivity.PLACE, place);
        editServiceIntent.putExtra(ServiceActivity.SERVICE, service);
        startActivityForResult(editServiceIntent, EDIT);
    }

    private void addService() {
        Intent addServiceIntent = new Intent(this, ServiceActivity.class);
        addServiceIntent.putExtra(ServiceActivity.OPERATION, ServiceActivity.OPERATION_ADD);
        addServiceIntent.putExtra(ServiceActivity.PLACE, place);
        startActivityForResult(addServiceIntent, ADD);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.crud_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.menu_edit:
                editSelectedService(info);
                return true;
            case R.id.menu_remove:
                removeSelectedService(info);
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void editSelectedService(final AdapterView.AdapterContextMenuInfo info) {
        this.selectedService = listAdapter.getItem(info.position);
        editService(selectedService);
    }

    private void removeSelectedService(final AdapterView.AdapterContextMenuInfo info) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.AlertDialog);
        dialog.setTitle(getString(R.string.remove_service));
        dialog.setMessage(getString(R.string.confirm_remove_service));
        dialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                progressBar.setVisibility(View.VISIBLE);
                selectedService = listAdapter.getItem(info.position);
                try {
                    serviceDao.delete(selectedService);
                    services.remove(selectedService);
                    listAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                } catch (DaoException e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), getString(R.string.error_removing_service),
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        dialog.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void loadServices(final String placeId) {
        Log.d(LOG_TAG, getString(R.string.loading_services));
        progressBar.setVisibility(View.VISIBLE);

        Query query = serviceDao.getDatabaseReference().orderByChild("placeId").equalTo(placeId);
        query.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (DataSnapshot child : children) {
                            Service service = child.getValue(Service.class);
                            services.add(service);
                        }
                        sortServicesByName();
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

    private void sortServicesByName() {
        Comparator comparator = new Comparator<Service>() {

            @Override
            public int compare(Service srv1, Service srv2) {
                return srv1.getName().compareTo(srv2.getName());
            }
        };

        Collections.sort(services, comparator);
    }

    private void configureListAdapter() {
        this.listAdapter = new ServiceAdapter(this, R.id.list_view, services);
        this.servicesListView.setAdapter(listAdapter);

        this.servicesListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if (btAddService.getVisibility() == View.VISIBLE) {
                    btAddService.setVisibility(View.GONE);
                } else {
                    btAddService.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ADD:
                if (resultCode == ServiceActivity.OPERATION_STATUS_OK) {
                    Service service = (Service) data.getSerializableExtra(ServiceActivity.SERVICE);
                    services.add(service);
                    sortServicesByName();
                    listAdapter.notifyDataSetChanged();
                } else if (resultCode == ServiceActivity.OPERATION_STATUS_ERROR) {
                    Toast.makeText(this, getString(R.string.error_adding_service),
                            Toast.LENGTH_LONG).show();
                }
                break;
            case EDIT:
                if (resultCode == ServiceActivity.OPERATION_STATUS_OK) {
                    Service service = (Service) data.getSerializableExtra(ServiceActivity.SERVICE);
                    updateSelectedService(service);
                    sortServicesByName();
                    listAdapter.notifyDataSetChanged();
                } else if (resultCode == ServiceActivity.OPERATION_STATUS_ERROR) {
                    Toast.makeText(this, getString(R.string.error_editing_service),
                            Toast.LENGTH_LONG).show();
                }
        }
    }

    private void updateSelectedService(Service service) {
        this.selectedService.setPrice(service.getPrice());
        this.selectedService.setName(service.getName());
        this.selectedService.setDescription(service.getDescription());
    }

    private void openServiceInfo(Service service) {
        Intent openServiceInfoIntent = new Intent(this, ServiceInfoActivity.class);
        openServiceInfoIntent.putExtra(ServiceInfoActivity.SERVICE, service);
        startActivity(openServiceInfoIntent);
    }
}
