package br.edu.ifspsaocarlos.sosprecos.dao;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.dao.exception.DaoException;
import br.edu.ifspsaocarlos.sosprecos.model.ServiceRating;

/**
 * Created by Andrey R. Brugnera on 17/04/2018.
 */
public class ServiceRatingDao extends FirebaseHelper<ServiceRating> {
    public static final String DATABASE_REFERENCE = "services_rates";

    public ServiceRatingDao(Context context) {
        super(context, DATABASE_REFERENCE);
    }

    @Override
    public void add(ServiceRating serviceRating) throws DaoException {
        validate(serviceRating, false);

        String key = getDatabaseReference().push().getKey();
        serviceRating.setId(key);
        add(key, serviceRating);
    }

    @Override
    public void update(ServiceRating serviceRating) throws DaoException {
        validate(serviceRating, true);
        update(serviceRating.getId(), serviceRating);
    }

    @Override
    public void delete(ServiceRating serviceRating) throws DaoException {
        validate(serviceRating, true);
        delete(serviceRating.getId());
    }

    private void validate(ServiceRating serviceRating, boolean checkId) throws DaoException {
        if (TextUtils.isEmpty(serviceRating.getId()) && checkId) {
            Log.d(DATABASE_LOGGER_TAG, getContext().getResources().getString(R.string.id_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.id_not_set));
        }

        if (TextUtils.isEmpty(serviceRating.getServiceId())) {
            Log.d(DATABASE_LOGGER_TAG, getContext().getResources().getString(R.string.service_id_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.service_id_not_set));
        }

        if (TextUtils.isEmpty(serviceRating.getRateId())) {
            Log.d(DATABASE_LOGGER_TAG, getContext().getResources().getString(R.string.rate_id_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.rate_id_not_set));
        }
    }
}
