package br.edu.ifspsaocarlos.sosprecos.dao;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.dao.exception.DaoException;
import br.edu.ifspsaocarlos.sosprecos.model.ProviderQualification;

/**
 * Created by Andrey R. Brugnera on 17/04/2018.
 */
public class ProviderQualificationDao extends FirebaseHelper<ProviderQualification> {
    public static final String DATABASE_REFERENCE = "providers_qualifications";

    public ProviderQualificationDao(Context context, String referenceName) {
        super(context, DATABASE_REFERENCE);
    }

    @Override
    public void add(ProviderQualification providerQualification) throws DaoException {
        validate(providerQualification, false);

        String key = getDatabaseReference().push().getKey();
        providerQualification.setId(key);
        add(key, providerQualification);
    }

    @Override
    public void update(ProviderQualification providerQualification) throws DaoException {
        validate(providerQualification, true);
        update(providerQualification.getId(), providerQualification);
    }

    @Override
    public void delete(ProviderQualification providerQualification) throws DaoException {
        validate(providerQualification, true);
        delete(providerQualification.getId());
    }

    private void validate(ProviderQualification providerQualification, boolean checkId) throws DaoException {
        if (TextUtils.isEmpty(providerQualification.getId()) && checkId) {
            Log.d(DATABASE_LOGGER_TAG, getContext().getResources().getString(R.string.id_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.id_not_set));
        }

        if (TextUtils.isEmpty(providerQualification.getProviderId())) {
            Log.d(DATABASE_LOGGER_TAG, getContext().getResources().getString(R.string.provider_id_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.provider_id_not_set));
        }

        if (TextUtils.isEmpty(providerQualification.getQualificationId())) {
            Log.d(DATABASE_LOGGER_TAG, getContext().getResources().getString(R.string.qualification_id_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.qualification_id_not_set));
        }
    }
}
