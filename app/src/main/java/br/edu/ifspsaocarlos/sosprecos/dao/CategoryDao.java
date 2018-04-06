package br.edu.ifspsaocarlos.sosprecos.dao;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.dao.exception.DaoException;
import br.edu.ifspsaocarlos.sosprecos.model.Category;

/**
 * Created by Andrey R. Brugnera on 30/03/2018.
 */
public class CategoryDao extends AbstractDao<Category> {
    public static final String DATABASE_REFERENCE = "categories";

    public CategoryDao(Context context) {
        super(context, DATABASE_REFERENCE);
    }

    public void addCategory(Category category) throws DaoException {
        if (TextUtils.isEmpty(category.getName())) {
            Log.d(LOGGER_TAG, getContext().getResources().getString(R.string.category_name_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.category_name_not_set));
        }
        /* @ToDo: Check if there are any category with the same name on firebase */
        if (isCategoryNameValid(category.getName())) {
            String categoryId = getDatabaseReference().push().getKey();
            category.setId(categoryId);
            add(categoryId, category);
        }
    }

    public void deleteCategory(Category category) throws DaoException {
        if (TextUtils.isEmpty(category.getId())) {
            Log.d(LOGGER_TAG, getContext().getResources().getString(R.string.category_id_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.category_id_not_set));
        }
        delete(category.getId());
    }

    public void updateCategory(Category category) throws DaoException {
        if (TextUtils.isEmpty(category.getId())) {
            Log.d(LOGGER_TAG, getContext().getResources().getString(R.string.category_id_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.category_id_not_set));
        }
        if (!TextUtils.isEmpty(category.getName())) {
            Log.d(LOGGER_TAG, getContext().getResources().getString(R.string.category_name_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.category_name_not_set));
        }
        /* @ToDo: Check if there are any category with the same name on firebase */
        update(category.getId(), category);
    }

    private boolean isCategoryNameValid(String name) {
        String upperCaseName = name.toUpperCase();
        for (String categoryId : getElementsMap().keySet()) {
            Category category = getElementsMap().get(categoryId);
            if (category.getName().toUpperCase().equals(upperCaseName)) {
                return false;
            }
        }
        return true;
    }
}