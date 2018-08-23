package br.edu.ifspsaocarlos.sosprecos.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.dao.CategoryDao;
import br.edu.ifspsaocarlos.sosprecos.dao.exception.DaoException;
import br.edu.ifspsaocarlos.sosprecos.model.Category;
import br.edu.ifspsaocarlos.sosprecos.util.ViewUtils;

public class CategoryActivity extends AppCompatActivity {
    private static final String LOG_TAG = "ADD_EDIT_CATEGORY";

    public static final int OPERATION_STATUS_ERROR = -1;
    public static final int OPERATION_STATUS_OK = 1;

    public static final int OPERATION_ADD = 2;
    public static final int OPERATION_EDIT = 3;

    public static final String OPERATION = "operation";
    public static final String CATEGORY = "category";

    private CategoryDao categoryDao;

    private FrameLayout progressBarHolder;
    private Button btAddOrEditCategory;
    private TextView tvTitle;
    private EditText etCategoryName;

    private List<Category> categories;
    private Category editingCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        this.progressBarHolder = findViewById(R.id.progress_bar_holder);
        this.btAddOrEditCategory = findViewById(R.id.bt_add_edit_category);
        this.etCategoryName = findViewById(R.id.et_category_name);
        this.tvTitle = findViewById(R.id.tv_title);

        this.categoryDao = new CategoryDao(this);
        this.categories = new ArrayList<>();

        configureToolbar();
        defineOperation();
        loadCategories();
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
                this.tvTitle.setText(R.string.edit_category);
                this.btAddOrEditCategory.setText(R.string.edit);
                this.editingCategory = (Category) getIntent().getSerializableExtra(CATEGORY);
                this.etCategoryName.setText(this.editingCategory.getName());
                this.btAddOrEditCategory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editCategory();
                    }
                });
                break;
            case OPERATION_ADD:
                this.btAddOrEditCategory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addCategory();
                    }
                });
        }
    }

    private void editCategory() {
        String categoryName = checkCategoryName();
        if (categoryName == null) return;

        try {
            editingCategory.setName(categoryName);
            this.categoryDao.update(editingCategory);
            Intent returnIntent = new Intent();
            returnIntent.putExtra(CATEGORY, editingCategory);
            setResult(OPERATION_STATUS_OK, returnIntent);
        } catch (DaoException ex) {
            Log.e(LOG_TAG, getString(R.string.error_editing_category), ex);
        }
        finish();
    }

    public void addCategory() {
        String categoryName = checkCategoryName();
        if (categoryName == null) return;

        try {
            Category category = new Category();
            category.setName(categoryName);
            this.categoryDao.add(category);

            Intent returnIntent = new Intent();
            returnIntent.putExtra(CATEGORY, category);
            setResult(OPERATION_STATUS_OK, returnIntent);
        } catch (DaoException ex) {
            Log.e(LOG_TAG, getString(R.string.error_adding_category), ex);
        }
        finish();
    }

    @Nullable
    private String checkCategoryName() {
        String categoryName = etCategoryName.getText().toString();

        if (TextUtils.isEmpty(categoryName)) {
            etCategoryName.setError(getString(R.string.enter_category_name));
            etCategoryName.requestFocus();
            return null;
        }

        if (existsCategoryName(categoryName)) {
            etCategoryName.setError(getString(R.string.category_name_exists));
            etCategoryName.requestFocus();
            return null;
        }
        return categoryName;
    }

    private void loadCategories() {
        Log.d(LOG_TAG, getString(R.string.loading_categories));

        ViewUtils.showProgressBar(progressBarHolder);

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
                        ViewUtils.hideProgressBar(progressBarHolder);
                        btAddOrEditCategory.setEnabled(true);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(LOG_TAG, databaseError.getMessage());
                        Log.e(LOG_TAG, databaseError.getDetails());
                        ViewUtils.hideProgressBar(progressBarHolder);
                    }
                });
    }

    private boolean existsCategoryName(String name) {
        String upperCaseName = name.toUpperCase();
        for (Category category : categories) {
            String categoryName = category.getName().toUpperCase();
            if (upperCaseName.equals(categoryName)) {
                return true;
            }
        }
        return false;
    }
}