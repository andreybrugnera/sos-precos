package br.edu.ifspsaocarlos.sosprecos.dao;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.dao.exception.DaoException;
import br.edu.ifspsaocarlos.sosprecos.model.ProviderRating;

/**
 * Created by Andrey R. Brugnera on 17/04/2018.
 */
public class ProviderRatingDao extends FirebaseHelper<ProviderRating> {
    public static final String DATABASE_REFERENCE = "providers_rates";

    public ProviderRatingDao(Context context) {
        super(context, DATABASE_REFERENCE);
    }

    @Override
    public void add(ProviderRating providerRating) throws DaoException {
        validate(providerRating, false);

        String key = getDatabaseReference().push().getKey();
        providerRating.setId(key);
        add(key, providerRating);
    }

    @Override
    public void update(ProviderRating providerRating) throws DaoException {
        validate(providerRating, true);
        update(providerRating.getId(), providerRating);
    }

    @Override
    public void delete(ProviderRating providerRating) throws DaoException {
        validate(providerRating, true);
        delete(providerRating.getId());
    }

    private void validate(ProviderRating providerRating, boolean checkId) throws DaoException {
        if (TextUtils.isEmpty(providerRating.getId()) && checkId) {
            Log.d(DATABASE_LOGGER_TAG, getContext().getResources().getString(R.string.id_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.id_not_set));
        }

        if (TextUtils.isEmpty(providerRating.getProviderId())) {
            Log.d(DATABASE_LOGGER_TAG, getContext().getResources().getString(R.string.provider_id_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.provider_id_not_set));
        }

        if (TextUtils.isEmpty(providerRating.getRateId())) {
            Log.d(DATABASE_LOGGER_TAG, getContext().getResources().getString(R.string.rate_id_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.rate_id_not_set));
        }
    }
}
