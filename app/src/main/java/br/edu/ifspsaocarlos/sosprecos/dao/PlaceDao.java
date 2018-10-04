package br.edu.ifspsaocarlos.sosprecos.dao;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.dao.exception.DaoException;
import br.edu.ifspsaocarlos.sosprecos.model.Place;
import br.edu.ifspsaocarlos.sosprecos.util.SessionUtils;

/**
 * Created by Andrey R. Brugnera on 30/03/2018.
 */
public class PlaceDao extends FirebaseHelper<Place> {
    public static final String DATABASE_REFERENCE = "places";

    public PlaceDao(Context context) {
        super(context, DATABASE_REFERENCE);
    }

    @Override
    public void add(Place place) throws DaoException {
        validate(place, false);

        String placeId = getDatabaseReference().push().getKey();
        place.setId(placeId);
        place.setUserId(SessionUtils.getCurrentUser().getUuid());
        add(placeId, place);
    }

    @Override
    public void delete(Place place) throws DaoException{
        validate(place, true);
        delete(place.getId());
    }

    @Override
    public void update(Place place) throws DaoException {
        validate(place, true);
        update(place.getId(), place);
    }

    private void validate(Place place, boolean checkId) throws DaoException{
        if (TextUtils.isEmpty(place.getName()) && checkId) {
            Log.d(DATABASE_LOGGER_TAG, getContext().getResources().getString(R.string.id_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.id_not_set));
        }

        if (TextUtils.isEmpty(place.getName())) {
            Log.d(DATABASE_LOGGER_TAG, getContext().getResources().getString(R.string.name_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.name_not_set));
        }

        if (TextUtils.isEmpty(place.getAddress())) {
            Log.d(DATABASE_LOGGER_TAG, getContext().getResources().getString(R.string.address_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.address_not_set));
        }
    }
}
