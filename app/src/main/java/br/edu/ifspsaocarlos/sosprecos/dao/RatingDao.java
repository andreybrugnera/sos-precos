package br.edu.ifspsaocarlos.sosprecos.dao;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.dao.exception.DaoException;
import br.edu.ifspsaocarlos.sosprecos.model.Rating;

/**
 * Created by Andrey R. Brugnera on 06/04/2018.
 */
public class RatingDao extends FirebaseHelper<Rating> {
    public static final String DATABASE_REFERENCE = "ratings";

    public RatingDao(Context context) {
        super(context, DATABASE_REFERENCE);
    }

    @Override
    public void add(Rating rating) throws DaoException {
        validate(rating, false);

        String ratingId = getDatabaseReference().push().getKey();
        rating.setId(ratingId);
        rating.setKeyRegistrationDate(ratingId + "_" + rating.getRegistrationDate().getTime());
        add(ratingId, rating);
    }

    @Override
    public void delete(Rating rating) throws DaoException {
        validate(rating, true);
        delete(rating.getId());
    }

    @Override
    public void update(Rating rating) throws DaoException {
        validate(rating, true);
        rating.setKeyRegistrationDate(rating.getId() + "_" + rating.getRegistrationDate().getTime());
        update(rating.getId(), rating);
    }

    private void validate(Rating rating, boolean checkId) throws DaoException {
        if (TextUtils.isEmpty(rating.getId()) && checkId) {
            Log.d(DATABASE_LOGGER_TAG, getContext().getResources().getString(R.string.id_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.id_not_set));
        }

        if (TextUtils.isEmpty(rating.getUserId())) {
            Log.d(DATABASE_LOGGER_TAG, getContext().getResources().getString(R.string.user_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.user_not_set));
        }
    }
}
