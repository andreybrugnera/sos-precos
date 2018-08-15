package br.edu.ifspsaocarlos.sosprecos.dao;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.dao.exception.DaoException;
import br.edu.ifspsaocarlos.sosprecos.model.PlaceRating;

/**
 * Created by Andrey R. Brugnera on 17/04/2018.
 */
public class PlaceRatingDao extends FirebaseHelper<PlaceRating> {
    public static final String DATABASE_REFERENCE = "places_ratings";

    public PlaceRatingDao(Context context) {
        super(context, DATABASE_REFERENCE);
    }

    @Override
    public void add(PlaceRating placeRating) throws DaoException {
        validate(placeRating, false);

        String key = getDatabaseReference().push().getKey();
        placeRating.setId(key);
        add(key, placeRating);
    }

    @Override
    public void update(PlaceRating placeRating) throws DaoException {
        validate(placeRating, true);
        update(placeRating.getId(), placeRating);
    }

    @Override
    public void delete(PlaceRating placeRating) throws DaoException {
        validate(placeRating, true);
        delete(placeRating.getId());
    }

    private void validate(PlaceRating placeRating, boolean checkId) throws DaoException {
        if (TextUtils.isEmpty(placeRating.getId()) && checkId) {
            Log.d(DATABASE_LOGGER_TAG, getContext().getResources().getString(R.string.id_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.id_not_set));
        }

        if (TextUtils.isEmpty(placeRating.getPlaceId())) {
            Log.d(DATABASE_LOGGER_TAG, getContext().getResources().getString(R.string.place_id_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.place_id_not_set));
        }

        if (TextUtils.isEmpty(placeRating.getRateId())) {
            Log.d(DATABASE_LOGGER_TAG, getContext().getResources().getString(R.string.rate_id_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.rate_id_not_set));
        }
    }
}
