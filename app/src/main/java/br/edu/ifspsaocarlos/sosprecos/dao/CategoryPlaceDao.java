package br.edu.ifspsaocarlos.sosprecos.dao;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.dao.exception.DaoException;
import br.edu.ifspsaocarlos.sosprecos.model.CategoryPlace;

/**
 * Created by Andrey R. Brugnera on 17/04/2018.
 */
public class CategoryPlaceDao extends FirebaseHelper<CategoryPlace> {
    public static final String DATABASE_REFERENCE = "categories_places";

    public CategoryPlaceDao(Context context) {
        super(context, DATABASE_REFERENCE);
    }

    @Override
    public void add(CategoryPlace categoryPlace) throws DaoException {
        validate(categoryPlace, false);

        String key = getDatabaseReference().push().getKey();
        categoryPlace.setId(key);
        add(key, categoryPlace);
    }

    @Override
    public void update(CategoryPlace categoryPlace) throws DaoException {
        validate(categoryPlace, true);
        update(categoryPlace.getId(), categoryPlace);
    }

    @Override
    public void delete(CategoryPlace categoryPlace) throws DaoException {
        validate(categoryPlace, true);
        delete(categoryPlace.getId());
    }

    private void validate(CategoryPlace categoryPlace, boolean checkId) throws DaoException {
        if (TextUtils.isEmpty(categoryPlace.getId()) && checkId) {
            Log.d(DATABASE_LOGGER_TAG, getContext().getResources().getString(R.string.id_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.id_not_set));
        }

        if (TextUtils.isEmpty(categoryPlace.getCategoryId())) {
            Log.d(DATABASE_LOGGER_TAG, getContext().getResources().getString(R.string.category_id_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.category_id_not_set));
        }

        if (TextUtils.isEmpty(categoryPlace.getPlaceId())) {
            Log.d(DATABASE_LOGGER_TAG, getContext().getResources().getString(R.string.place_id_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.place_id_not_set));
        }
    }
}
