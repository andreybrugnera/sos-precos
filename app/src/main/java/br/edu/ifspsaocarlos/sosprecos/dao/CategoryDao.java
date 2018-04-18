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

    @Override
    public void add(Category category) throws DaoException {
        validate(category, false);
        if (isCategoryNameValid(category.getName())) {
            String categoryId = getDatabaseReference().push().getKey();
            category.setId(categoryId);
            add(categoryId, category);
        }
    }

    @Override
    public void delete(Category category) throws DaoException {
        validate(category, true);
        delete(category.getId());
    }

    @Override
    public void update(Category category) throws DaoException {
        validate(category, true);
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

    private void validate(Category category, boolean checkId) throws DaoException {
        if (TextUtils.isEmpty(category.getId()) && checkId) {
            Log.d(DATABASE_LOGGER_TAG, getContext().getResources().getString(R.string.id_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.id_not_set));
        }
        if (TextUtils.isEmpty(category.getName())) {
            Log.d(DATABASE_LOGGER_TAG, getContext().getResources().getString(R.string.name_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.name_not_set));
        }
    }
}