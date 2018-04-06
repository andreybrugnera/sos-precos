package br.edu.ifspsaocarlos.sosprecos.dao;

import android.content.Context;

import br.edu.ifspsaocarlos.sosprecos.dao.exception.DaoException;
import br.edu.ifspsaocarlos.sosprecos.model.Service;
import br.edu.ifspsaocarlos.sosprecos.model.ServiceProvider;

/**
 * Created by Andrey R. Brugnera on 06/04/2018.
 */
public class ServiceDao extends AbstractDao<Service> {
    public static final String DATABASE_REFERENCE = "services";
    private ServiceProviderDao serviceProviderDao;

    public ServiceDao(Context context) {
        super(context, DATABASE_REFERENCE);
        this.serviceProviderDao = new ServiceProviderDao(context);
    }

    public String addService(Service service, ServiceProvider serviceProvider) throws DaoException {
        String serviceId = getDatabaseReference().push().getKey();
        service.setId(serviceId);
        add(serviceId, service);

        serviceProvider.getServices().put(serviceId, service);
        this.serviceProviderDao.updateServiceProvider(serviceProvider);
        return serviceId;
    }

    public void deleteService(Service service, ServiceProvider serviceProvider) throws DaoException {
        delete(service.getId());

        serviceProvider.getServices().remove(service.getId());
        this.serviceProviderDao.updateServiceProvider(serviceProvider);
    }

    public void updateService(Service service) throws DaoException{
        update(service.getId(), service);
    }
}
