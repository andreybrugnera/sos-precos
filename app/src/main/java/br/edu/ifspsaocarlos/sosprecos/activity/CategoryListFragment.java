package br.edu.ifspsaocarlos.sosprecos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import br.edu.ifspsaocarlos.sosprecos.model.Category;

public class CategoryListFragment extends Fragment {

    private static final String LOG_TAG = "CATEGORIES";

    private ProgressBar progressBar;
    private Button btAddCategory;
    private CategoryAdapter listAdapter;

    private CategoryDao categoryDao;
    private List<Category> categories;

    private static final int ADD_CATEGORY = 1;

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

        this.btAddCategory = getView().findViewById(R.id.bt_add_category);
        this.btAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addCategoryIntent = new Intent(getContext(), CategoryActivity.class);
                startActivityForResult(addCategoryIntent, ADD_CATEGORY);
            }
        });

        configureListAdapter();
        loadCategories();
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
        ListView categoryListView = getView().findViewById(R.id.category_list);
        categoryListView.setAdapter(listAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_CATEGORY) {
            if (resultCode == CategoryActivity.OPERATION_STATUS_OK) {
                Category addedCategory = (Category) data.getSerializableExtra(CategoryActivity.ADDED_CATEGORY);
                categories.add(addedCategory);
                sortCategoriesByName();
                listAdapter.notifyDataSetChanged();
            } else if (resultCode == CategoryActivity.OPERATION_STATUS_ERROR) {
                Toast.makeText(getContext(), getString(R.string.error_adding_category),
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
