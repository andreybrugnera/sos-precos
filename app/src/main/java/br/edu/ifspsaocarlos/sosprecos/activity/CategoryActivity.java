package br.edu.ifspsaocarlos.sosprecos.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.dao.CategoryDao;
import br.edu.ifspsaocarlos.sosprecos.dao.exception.DaoException;
import br.edu.ifspsaocarlos.sosprecos.model.Category;

public class CategoryActivity extends Activity {
    private static final String LOG_TAG = "ADD_CATEGORY";

    public static final int OPERATION_STATUS_ERROR = -1;
    public static final int OPERATION_STATUS_OK = 1;
    public static final String ADDED_CATEGORY = "added_category";

    private CategoryDao categoryDao;
    private ProgressBar progressBar;
    private Button btAddCategory;
    private List<Category> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        this.progressBar = findViewById(R.id.pb_category);
        this.btAddCategory = findViewById(R.id.bt_add_category);

        this.categoryDao = new CategoryDao(this);
        this.categories = new ArrayList<>();

        loadCategories();
    }

    public void addCategory(View v) {
        EditText etCategoryName = findViewById(R.id.et_category_name);
        String categoryName = etCategoryName.getText().toString();

        if (TextUtils.isEmpty(categoryName)) {
            etCategoryName.setError(getString(R.string.enter_category_name));
            etCategoryName.requestFocus();
            return;
        }

        if (existsCategoryName(categoryName)){
            etCategoryName.setError(getString(R.string.category_name_exists));
            etCategoryName.requestFocus();
            return;
        }

        try {
            Category category = new Category();
            category.setName(categoryName);
            this.categoryDao.add(category);

            Intent returnIntent = new Intent();
            returnIntent.putExtra(ADDED_CATEGORY, category);
            setResult(OPERATION_STATUS_OK, returnIntent);
        } catch (DaoException ex) {
            Log.e(LOG_TAG, getString(R.string.error_adding_category), ex);
        }
        finish();
    }

    private void loadCategories() {
        Log.d(LOG_TAG, getString(R.string.loading_categories));

        progressBar.setVisibility(View.VISIBLE);

        categoryDao.getDatabaseReference().addValueEventListener(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        categories.clear();
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (DataSnapshot child : children) {
                            Category category = child.getValue(Category.class);
                            categories.add(category);
                        }
                        progressBar.setVisibility(View.GONE);
                        btAddCategory.setEnabled(true);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(LOG_TAG, databaseError.getMessage());
                        Log.e(LOG_TAG, databaseError.getDetails());
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private boolean existsCategoryName(String name) {
        String upperCaseName = name.toUpperCase();
        for (Category category : categories) {
            String categoryName = category.getName().toUpperCase();
            if (upperCaseName.equals(categoryName)) {
                return false;
            }
        }
        return true;
    }
}