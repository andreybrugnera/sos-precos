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
import br.edu.ifspsaocarlos.sosprecos.adapter.PlaceAdapter;
import br.edu.ifspsaocarlos.sosprecos.dao.PlaceDao;
import br.edu.ifspsaocarlos.sosprecos.dao.exception.DaoException;
import br.edu.ifspsaocarlos.sosprecos.model.Place;

/**
 * Created by Andrey R. Brugnera on 16/05/2018.
 */
public class PlaceListFragment extends Fragment {

    private static final String LOG_TAG = "SERVICE_PLACES";

    private ProgressBar progressBar;
    private Button btAddPlace;
    private ListView placesListView;
    private PlaceAdapter listAdapter;
    private TextView viewTitle;

    private PlaceDao placeDao;
    private List<Place> places;
    private Place selectedPlace;

    private static final int ADD = 1;
    private static final int EDIT = 2;

    public PlaceListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.placeDao = new PlaceDao(getContext());
        this.places = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_crud_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        this.progressBar = getView().findViewById(R.id.progress_bar);
        this.placesListView = getView().findViewById(R.id.list_view);
        this.viewTitle = getView().findViewById(R.id.list_title);
        this.viewTitle.setText(getString(R.string.places));

        this.btAddPlace = getView().findViewById(R.id.bt_add_edit_item);
        this.btAddPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPlace();
            }
        });

        this.placesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Place place = listAdapter.getItem(position);
                if (place != null) {
                    openPlaceInfo(place);
                }
            }
        });

        configureListAdapter();
        registerForContextMenu(this.placesListView);
        loadPlaces();
    }

    private void openPlaceInfo(Place place) {
        Intent openPlaceInfoIntent = new Intent(getContext(), PlaceInfoActivity.class);
        openPlaceInfoIntent.putExtra(PlaceInfoActivity.PLACE, place);
        startActivity(openPlaceInfoIntent);
    }

    private void addPlace() {
        Intent addPlaceIntent = new Intent(getContext(), PlaceActivity.class);
        addPlaceIntent.putExtra(PlaceActivity.OPERATION, PlaceActivity.OPERATION_ADD);
        startActivityForResult(addPlaceIntent, ADD);
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
                editSelectedPlace(info);
                return true;
            case R.id.menu_remove:
                removeSelectedPlace(info);
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void editSelectedPlace(final AdapterView.AdapterContextMenuInfo info) {
        this.selectedPlace = listAdapter.getItem(info.position);
        Intent editPlaceIntent = new Intent(getContext(), PlaceActivity.class);
        editPlaceIntent.putExtra(PlaceActivity.OPERATION, PlaceActivity.OPERATION_EDIT);
        editPlaceIntent.putExtra(PlaceActivity.PLACE, selectedPlace);
        startActivityForResult(editPlaceIntent, EDIT);
    }

    private void removeSelectedPlace(final AdapterView.AdapterContextMenuInfo info) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext(), R.style.AlertDialog);
        dialog.setTitle(getString(R.string.remove_place));
        dialog.setMessage(getString(R.string.confirm_remove_place));
        dialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                progressBar.setVisibility(View.VISIBLE);
                selectedPlace = listAdapter.getItem(info.position);
                try {
                    placeDao.delete(selectedPlace);
                    places.remove(selectedPlace);
                    listAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                } catch (DaoException e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), getString(R.string.error_removing_place),
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

    private void loadPlaces() {
        Log.d(LOG_TAG, getString(R.string.loading_places));
        progressBar.setVisibility(View.VISIBLE);

        placeDao.getDatabaseReference().addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        places.clear();
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (DataSnapshot child : children) {
                            Place place = child.getValue(Place.class);
                            places.add(place);
                        }
                        sortPlacesByName();
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

    private void sortPlacesByName() {
        Comparator comparator = new Comparator<Place>() {

            @Override
            public int compare(Place prov1, Place prov2) {
                return prov1.getName().compareTo(prov2.getName());
            }
        };

        Collections.sort(places, comparator);
    }

    private void configureListAdapter() {
        this.listAdapter = new PlaceAdapter(getContext(), R.id.list_view, places);
        this.placesListView.setAdapter(listAdapter);

        this.placesListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if (btAddPlace.getVisibility() == View.VISIBLE) {
                    btAddPlace.setVisibility(View.GONE);
                } else {
                    btAddPlace.setVisibility(View.VISIBLE);
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
            if (resultCode == PlaceActivity.OPERATION_STATUS_OK) {
                Place addedPlace = (Place) data.getSerializableExtra(PlaceActivity.PLACE);
                places.add(addedPlace);
                sortPlacesByName();
                listAdapter.notifyDataSetChanged();
            } else if (resultCode == PlaceActivity.OPERATION_STATUS_ERROR) {
                Toast.makeText(getContext(), getString(R.string.error_adding_place),
                        Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == EDIT) {
            if (resultCode == PlaceActivity.OPERATION_STATUS_OK) {
                Place editedPlace = (Place) data.getSerializableExtra(PlaceActivity.PLACE);
                this.selectedPlace.setName(editedPlace.getName());
                sortPlacesByName();
                listAdapter.notifyDataSetChanged();
            } else if (resultCode == PlaceActivity.OPERATION_STATUS_ERROR) {
                Toast.makeText(getContext(), getString(R.string.error_editing_place),
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
