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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.adapter.CategoryAdapter;
import br.edu.ifspsaocarlos.sosprecos.dao.CategoryDao;
import br.edu.ifspsaocarlos.sosprecos.dao.exception.DaoException;
import br.edu.ifspsaocarlos.sosprecos.model.Category;

public class CategoryListFragment extends Fragment {

    private static final String LOG_TAG = "CATEGORIES";

    private ProgressBar progressBar;
    private Button btAddCategory;
    private ListView categoriesListView;
    private CategoryAdapter listAdapter;

    private CategoryDao categoryDao;
    private List<Category> categories;
    private Category selectedCategory;

    private static final int ADD_CATEGORY = 1;
    private static final int EDIT_CATEGORY = 2;

    public CategoryListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.categoryDao = new CategoryDao(getContext());
        this.categories = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        this.progressBar = getView().findViewById(R.id.pb_categories);
        this.categoriesListView = getView().findViewById(R.id.category_list);

        this.btAddCategory = getView().findViewById(R.id.bt_add_edit_category);
        this.btAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCategory();
            }
        });

        configureListAdapter();
        registerForContextMenu(this.categoriesListView);
        loadCategories();
    }

    private void addCategory() {
        Intent addCategoryIntent = new Intent(getContext(), CategoryActivity.class);
        addCategoryIntent.putExtra(CategoryActivity.OPERATION, CategoryActivity.OPERATION_ADD);
        startActivityForResult(addCategoryIntent, ADD_CATEGORY);
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
                editSelectedCategory(info);
                return true;
            case R.id.menu_remove:
                removeSelectedCategory(info);
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void editSelectedCategory(final AdapterView.AdapterContextMenuInfo info) {
        this.selectedCategory = listAdapter.getItem(info.position);
        Intent editCategoryIntent = new Intent(getContext(), CategoryActivity.class);
        editCategoryIntent.putExtra(CategoryActivity.OPERATION, CategoryActivity.OPERATION_EDIT);
        editCategoryIntent.putExtra(CategoryActivity.CATEGORY, selectedCategory);
        startActivityForResult(editCategoryIntent, EDIT_CATEGORY);
    }

    private void removeSelectedCategory(final AdapterView.AdapterContextMenuInfo info) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle(getString(R.string.remove_category));
        dialog.setMessage(getString(R.string.confirm_remove_category));
        dialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                progressBar.setVisibility(View.VISIBLE);
                selectedCategory = listAdapter.getItem(info.position);
                try {
                    categoryDao.delete(selectedCategory);
                    categories.remove(selectedCategory);
                    listAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                } catch (DaoException e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), getString(R.string.error_removing_category),
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

    private void loadCategories() {
        Log.d(LOG_TAG, getString(R.string.loading_categories));
        progressBar.setVisibility(View.VISIBLE);

        categoryDao.getDatabaseReference().addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        categories.clear();
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (DataSnapshot child : children) {
                            Category category = child.getValue(Category.class);
                            categories.add(category);
                        }
                        sortCategoriesByName();
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

    private void sortCategoriesByName() {
        Comparator comparator = new Comparator<Category>() {

            @Override
            public int compare(Category cat1, Category cat2) {
                return cat1.getName().compareTo(cat2.getName());
            }
        };

        Collections.sort(categories, comparator);
    }

    private void configureListAdapter() {
        this.listAdapter = new CategoryAdapter(getContext(), R.id.category_list, categories);
        this.categoriesListView.setAdapter(listAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_CATEGORY) {
            if (resultCode == CategoryActivity.OPERATION_STATUS_OK) {
                Category addedCategory = (Category) data.getSerializableExtra(CategoryActivity.CATEGORY);
                categories.add(addedCategory);
                sortCategoriesByName();
                listAdapter.notifyDataSetChanged();
            } else if (resultCode == CategoryActivity.OPERATION_STATUS_ERROR) {
                Toast.makeText(getContext(), getString(R.string.error_adding_category),
                        Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == EDIT_CATEGORY) {
            if (resultCode == CategoryActivity.OPERATION_STATUS_OK) {
                Category editedCategory = (Category) data.getSerializableExtra(CategoryActivity.CATEGORY);
                this.selectedCategory.setName(editedCategory.getName());
                sortCategoriesByName();
                listAdapter.notifyDataSetChanged();
            } else if (resultCode == CategoryActivity.OPERATION_STATUS_ERROR) {
                Toast.makeText(getContext(), getString(R.string.error_editing_category),
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}