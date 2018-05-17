package br.edu.ifspsaocarlos.sosprecos.dao;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.dao.exception.DaoException;
import br.edu.ifspsaocarlos.sosprecos.model.Service;

/**
 * Created by Andrey R. Brugnera on 06/04/2018.
 */
public class ServiceDao extends FirebaseHelper<Service> {
    public static final String DATABASE_REFERENCE = "services";

    public ServiceDao(Context context) {
        super(context, DATABASE_REFERENCE);
    }

    @Override
    public void add(Service service) throws DaoException {
        validate(service, false);

        String serviceId = getDatabaseReference().push().getKey();
        service.setId(serviceId);
        add(serviceId, service);
    }

    @Override
    public void delete(Service service) throws DaoException {
        validate(service, true);
        delete(service.getId());
    }

    @Override
    public void update(Service service) throws DaoException{
        validate(service, true);
        update(service.getId(), service);
    }

    private void validate(Service service, boolean checkId)throws DaoException{
        if (TextUtils.isEmpty(service.getName()) && checkId) {
            Log.d(DATABASE_LOGGER_TAG, getContext().getResources().getString(R.string.id_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.id_not_set));
        }

        if (service.getPrice() == null) {
            Log.d(DATABASE_LOGGER_TAG, getContext().getResources().getString(R.string.price_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.price_not_set));
        }

        if (TextUtils.isEmpty(service.getDescription())) {
            Log.d(DATABASE_LOGGER_TAG, getContext().getResources().getString(R.string.description_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.description_not_set));
        }
    }
}
