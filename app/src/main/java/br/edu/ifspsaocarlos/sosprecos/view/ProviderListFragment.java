package br.edu.ifspsaocarlos.sosprecos.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.adapter.ProviderAdapter;
import br.edu.ifspsaocarlos.sosprecos.dao.ProviderDao;
import br.edu.ifspsaocarlos.sosprecos.dao.exception.DaoException;
import br.edu.ifspsaocarlos.sosprecos.model.Provider;

/**
 * Created by Andrey R. Brugnera on 16/05/2018.
 */
public class ProviderListFragment extends Fragment {

    private static final String LOG_TAG = "SERVICE_PROVIDERS";

    private ProgressBar progressBar;
    private Button btAddProvider;
    private ListView providersListView;
    private ProviderAdapter listAdapter;
    private TextView viewTitle;

    private ProviderDao providerDao;
    private List<Provider> providers;
    private Provider selectedProvider;

    private static final int ADD = 1;
    private static final int EDIT = 2;

    public ProviderListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.providerDao = new ProviderDao(getContext());
        this.providers = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_crud_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        this.progressBar = getView().findViewById(R.id.progress_bar);
        this.providersListView = getView().findViewById(R.id.list_view);
        this.viewTitle = getView().findViewById(R.id.list_title);
        this.viewTitle.setText(getString(R.string.providers));

        this.btAddProvider = getView().findViewById(R.id.bt_add_edit_item);
        this.btAddProvider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProvider();
            }
        });

        this.providersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Provider provider = listAdapter.getItem(position);
                if (provider != null) {
                    openProviderInfo(provider);
                }
            }
        });

        configureListAdapter();
        registerForContextMenu(this.providersListView);
        loadProviders();
    }

    private void openProviderInfo(Provider provider) {
        Intent openProviderInfoIntent = new Intent(getContext(), ProviderInfoActivity.class);
        openProviderInfoIntent.putExtra(ProviderInfoActivity.PROVIDER, provider);
        startActivity(openProviderInfoIntent);
    }

    private void addProvider() {
        Intent addProviderIntent = new Intent(getContext(), ProviderActivity.class);
        addProviderIntent.putExtra(ProviderActivity.OPERATION, ProviderActivity.OPERATION_ADD);
        startActivityForResult(addProviderIntent, ADD);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.crud_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.menu_edit:
                editSelectedProvider(info);
                return true;
            case R.id.menu_remove:
                removeSelectedProvider(info);
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void editSelectedProvider(final AdapterView.AdapterContextMenuInfo info) {
        this.selectedProvider = listAdapter.getItem(info.position);
        Intent editProviderIntent = new Intent(getContext(), ProviderActivity.class);
        editProviderIntent.putExtra(ProviderActivity.OPERATION, ProviderActivity.OPERATION_EDIT);
        editProviderIntent.putExtra(ProviderActivity.PROVIDER, selectedProvider);
        startActivityForResult(editProviderIntent, EDIT);
    }

    private void removeSelectedProvider(final AdapterView.AdapterContextMenuInfo info) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle(getString(R.string.remove_provider));
        dialog.setMessage(getString(R.string.confirm_remove_provider));
        dialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                progressBar.setVisibility(View.VISIBLE);
                selectedProvider = listAdapter.getItem(info.position);
                try {
                    providerDao.delete(selectedProvider);
                    providers.remove(selectedProvider);
                    listAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                } catch (DaoException e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), getString(R.string.error_removing_provider),
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

    private void loadProviders() {
        Log.d(LOG_TAG, getString(R.string.loading_providers));
        progressBar.setVisibility(View.VISIBLE);

        providerDao.getDatabaseReference().addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        providers.clear();
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (DataSnapshot child : children) {
                            Provider provider = child.getValue(Provider.class);
                            providers.add(provider);
                        }
                        sortProvidersByName();
                        listAdapter.notifyDataSetChanged();
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

    private void sortProvidersByName() {
        Comparator comparator = new Comparator<Provider>() {

            @Override
            public int compare(Provider prov1, Provider prov2) {
                return prov1.getName().compareTo(prov2.getName());
            }
        };

        Collections.sort(providers, comparator);
    }

    private void configureListAdapter() {
        this.listAdapter = new ProviderAdapter(getContext(), R.id.list_view, providers);
        this.providersListView.setAdapter(listAdapter);

        this.providersListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if (btAddProvider.getVisibility() == View.VISIBLE) {
                    btAddProvider.setVisibility(View.GONE);
                } else {
                    btAddProvider.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD) {
            if (resultCode == ProviderActivity.OPERATION_STATUS_OK) {
                Provider addedProvider = (Provider) data.getSerializableExtra(ProviderActivity.PROVIDER);
                providers.add(addedProvider);
                sortProvidersByName();
                listAdapter.notifyDataSetChanged();
            } else if (resultCode == ProviderActivity.OPERATION_STATUS_ERROR) {
                Toast.makeText(getContext(), getString(R.string.error_adding_provider),
                        Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == EDIT) {
            if (resultCode == ProviderActivity.OPERATION_STATUS_OK) {
                Provider editedProvider = (Provider) data.getSerializableExtra(ProviderActivity.PROVIDER);
                this.selectedProvider.setName(editedProvider.getName());
                sortProvidersByName();
                listAdapter.notifyDataSetChanged();
            } else if (resultCode == ProviderActivity.OPERATION_STATUS_ERROR) {
                Toast.makeText(getContext(), getString(R.string.error_editing_provider),
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
