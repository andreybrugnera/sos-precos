package br.edu.ifspsaocarlos.sosprecos.dao;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.dao.exception.DaoException;
import br.edu.ifspsaocarlos.sosprecos.model.ProviderService;

/**
 * Created by Andrey R. Brugnera on 17/04/2018.
 */
public class ProviderServiceDao extends AbstractDao<ProviderService>{
    public static final String DATABASE_REFERENCE = "providers_services";

    public ProviderServiceDao(Context context, String referenceName) {
        super(context, DATABASE_REFERENCE);
    }

    @Override
    public void add(ProviderService providerService) throws DaoException {
        validate(providerService, false);

        String key = getDatabaseReference().push().getKey();
        providerService.setId(key);
        add(key, providerService);
    }

    @Override
    public void update(ProviderService providerService) throws DaoException {
        validate(providerService, true);
        update(providerService.getId(), providerService);
    }

    @Override
    public void delete(ProviderService providerService) throws DaoException {
        validate(providerService, true);
        delete(providerService.getId());
    }

    private void validate(ProviderService providerService, boolean checkId) throws DaoException {
        if (TextUtils.isEmpty(providerService.getId()) && checkId) {
            Log.d(DATABASE_LOGGER_TAG, getContext().getResources().getString(R.string.id_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.id_not_set));
        }

        if (TextUtils.isEmpty(providerService.getServiceId())) {
            Log.d(DATABASE_LOGGER_TAG, getContext().getResources().getString(R.string.service_id_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.service_id_not_set));
        }

        if (TextUtils.isEmpty(providerService.getProviderId())) {
            Log.d(DATABASE_LOGGER_TAG, getContext().getResources().getString(R.string.provider_id_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.provider_id_not_set));
        }
    }
}
