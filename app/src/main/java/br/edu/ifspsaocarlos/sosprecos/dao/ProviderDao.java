package br.edu.ifspsaocarlos.sosprecos.dao;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.dao.exception.DaoException;
import br.edu.ifspsaocarlos.sosprecos.model.Provider;

/**
 * Created by Andrey R. Brugnera on 30/03/2018.
 */
public class ProviderDao extends AbstractDao<Provider> {
    public static final String DATABASE_REFERENCE = "providers";

    public ProviderDao(Context context) {
        super(context, DATABASE_REFERENCE);
    }

    @Override
    public void add(Provider provider) throws DaoException {
        validate(provider, false);

        String serviceProviderId = getDatabaseReference().push().getKey();
        provider.setId(serviceProviderId);
        add(serviceProviderId, provider);
    }

    @Override
    public void delete(Provider provider) throws DaoException{
        validate(provider, true);
        delete(provider.getId());
    }

    @Override
    public void update(Provider provider) throws DaoException {
        validate(provider, true);
        update(provider.getId(), provider);
    }

    private void validate(Provider provider, boolean checkId) throws DaoException{
        if (TextUtils.isEmpty(provider.getName()) && checkId) {
            Log.d(DATABASE_LOGGER_TAG, getContext().getResources().getString(R.string.id_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.id_not_set));
        }

        if (TextUtils.isEmpty(provider.getName())) {
            Log.d(DATABASE_LOGGER_TAG, getContext().getResources().getString(R.string.name_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.name_not_set));
        }

        if (TextUtils.isEmpty(provider.getCity())) {
            Log.d(DATABASE_LOGGER_TAG, getContext().getResources().getString(R.string.city_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.city_not_set));
        }

        if (TextUtils.isEmpty(provider.getState())) {
            Log.d(DATABASE_LOGGER_TAG, getContext().getResources().getString(R.string.state_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.state_not_set));
        }
    }
}
