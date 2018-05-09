package br.edu.ifspsaocarlos.sosprecos.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.dao.CategoryDao;
import br.edu.ifspsaocarlos.sosprecos.model.Category;

public class CategoryListFragment extends Fragment {

    private static final String LOG_TAG = "CATEGORIES";

    private ProgressBar progressBar;
    private Button btAddCategory;

    private CategoryDao categoryDao;
    private List<Category> categories;

    public CategoryListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.categoryDao = new CategoryDao(getContext());
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
        loadCategories();
    }

    private void loadCategories() {
        Log.d(LOG_TAG, getString(R.string.loading_categories));

        this.progressBar.setVisibility(View.VISIBLE);
        this.categories = new ArrayList<>();

        categoryDao.getDatabaseReference().addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> firebaseCategories = (Map<String, Object>) dataSnapshot.getValue();
                        if (firebaseCategories != null) {
                            categoryDao.refreshElements(firebaseCategories);

                            for (String key : categoryDao.getElementsMap().keySet()) {
                                Category category = categoryDao.get(key);
                                categories.add(category);
                            }

                            sortCategoriesByName();
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

    private void sortCategoriesByName() {
        Comparator comparator = new Comparator<Category>() {

            @Override
            public int compare(Category cat1, Category cat2) {
                return cat1.getName().compareTo(cat2.getName());
            }
        };

        Collections.sort(categories, comparator);
    }
}
