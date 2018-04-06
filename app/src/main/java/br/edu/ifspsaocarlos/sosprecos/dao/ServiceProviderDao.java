package br.edu.ifspsaocarlos.sosprecos.dao;

import android.content.Context;

import br.edu.ifspsaocarlos.sosprecos.dao.exception.DaoException;
import br.edu.ifspsaocarlos.sosprecos.model.Category;
import br.edu.ifspsaocarlos.sosprecos.model.ServiceProvider;

/**
 * Created by Andrey R. Brugnera on 30/03/2018.
 */
public class ServiceProviderDao extends AbstractDao<ServiceProvider>{
    public static final String DATABASE_REFERENCE = "service_providers";
    private CategoryDao categoryDao;

    public ServiceProviderDao(Context context) {
        super(context, DATABASE_REFERENCE);
        this.categoryDao = new CategoryDao(context);
    }

    public void addServiceProvider(ServiceProvider serviceProvider, Category category) throws DaoException {
        String serviceProviderId = getDatabaseReference().push().getKey();
        serviceProvider.setId(serviceProviderId);
        add(serviceProviderId, serviceProvider);

        category.getServiceProviders().add(serviceProviderId);
        categoryDao.updateCategory(category);
    }

    public void deleteServiceProvider(ServiceProvider serviceProvider, Category category) throws DaoException{
        delete(serviceProvider.getId());

        category.getServiceProviders().remove(serviceProvider.getId());
        categoryDao.updateCategory(category);
    }

    public void updateServiceProvider(ServiceProvider serviceProvider) throws DaoException{
        update(serviceProvider.getId(), serviceProvider);
    }
}
