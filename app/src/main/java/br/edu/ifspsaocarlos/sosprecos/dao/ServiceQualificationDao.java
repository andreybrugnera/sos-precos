package br.edu.ifspsaocarlos.sosprecos.dao;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.dao.exception.DaoException;
import br.edu.ifspsaocarlos.sosprecos.model.ServiceQualification;

/**
 * Created by Andrey R. Brugnera on 17/04/2018.
 */
public class ServiceQualificationDao extends FirebaseHelper<ServiceQualification> {
    public static final String DATABASE_REFERENCE = "services_qualifications";

    public ServiceQualificationDao(Context context) {
        super(context, DATABASE_REFERENCE);
    }

    @Override
    public void add(ServiceQualification serviceQualification) throws DaoException {
        validate(serviceQualification, false);

        String key = getDatabaseReference().push().getKey();
        serviceQualification.setId(key);
        add(key, serviceQualification);
    }

    @Override
    public void update(ServiceQualification serviceQualification) throws DaoException {
        validate(serviceQualification, true);
        update(serviceQualification.getId(), serviceQualification);
    }

    @Override
    public void delete(ServiceQualification serviceQualification) throws DaoException {
        validate(serviceQualification, true);
        delete(serviceQualification.getId());
    }

    private void validate(ServiceQualification serviceQualification, boolean checkId) throws DaoException {
        if (TextUtils.isEmpty(serviceQualification.getId()) && checkId) {
            Log.d(DATABASE_LOGGER_TAG, getContext().getResources().getString(R.string.id_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.id_not_set));
        }

        if (TextUtils.isEmpty(serviceQualification.getServiceId())) {
            Log.d(DATABASE_LOGGER_TAG, getContext().getResources().getString(R.string.service_id_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.service_id_not_set));
        }

        if (TextUtils.isEmpty(serviceQualification.getQualificationId())) {
            Log.d(DATABASE_LOGGER_TAG, getContext().getResources().getString(R.string.qualification_id_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.qualification_id_not_set));
        }
    }
}
