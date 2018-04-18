package br.edu.ifspsaocarlos.sosprecos.dao;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.dao.exception.DaoException;
import br.edu.ifspsaocarlos.sosprecos.model.CategoryProvider;

/**
 * Created by Andrey R. Brugnera on 17/04/2018.
 */
public class CategoryProviderDao extends AbstractDao<CategoryProvider>{
    public static final String DATABASE_REFERENCE = "categories_providers";

    public CategoryProviderDao(Context context) {
        super(context, DATABASE_REFERENCE);
    }

    @Override
    public void add(CategoryProvider categoryProvider) throws DaoException {
        validate(categoryProvider, false);

        String key = getDatabaseReference().push().getKey();
        categoryProvider.setId(key);
        add(key, categoryProvider);
    }

    @Override
    public void update(CategoryProvider categoryProvider) throws DaoException {
        validate(categoryProvider, true);
        update(categoryProvider.getId(), categoryProvider);
    }

    @Override
    public void delete(CategoryProvider categoryProvider) throws DaoException {
        validate(categoryProvider, true);
        delete(categoryProvider.getId());
    }

    private void validate(CategoryProvider categoryProvider, boolean checkId) throws DaoException {
        if (TextUtils.isEmpty(categoryProvider.getId()) && checkId) {
            Log.d(DATABASE_LOGGER_TAG, getContext().getResources().getString(R.string.id_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.id_not_set));
        }

        if (TextUtils.isEmpty(categoryProvider.getCategoryId())) {
            Log.d(DATABASE_LOGGER_TAG, getContext().getResources().getString(R.string.category_id_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.category_id_not_set));
        }

        if (TextUtils.isEmpty(categoryProvider.getProviderId())) {
            Log.d(DATABASE_LOGGER_TAG, getContext().getResources().getString(R.string.provider_id_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.provider_id_not_set));
        }
    }
}
