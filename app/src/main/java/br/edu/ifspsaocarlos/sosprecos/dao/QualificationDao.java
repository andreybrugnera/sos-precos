package br.edu.ifspsaocarlos.sosprecos.dao;

import android.content.Context;

import br.edu.ifspsaocarlos.sosprecos.dao.exception.DaoException;
import br.edu.ifspsaocarlos.sosprecos.model.Qualification;
import br.edu.ifspsaocarlos.sosprecos.model.Service;

/**
 * Created by Andrey R. Brugnera on 06/04/2018.
 */
public class QualificationDao extends AbstractDao<Qualification> {
    public static final String DATABASE_REFERENCE = "qualifications";
    private ServiceDao serviceDao;

    public QualificationDao(Context context) {
        super(context, DATABASE_REFERENCE);
        this.serviceDao = new ServiceDao(context);
    }

    public String addQualification(Qualification qualification, Service service) throws DaoException {
        String qualificationId = getDatabaseReference().push().getKey();
        qualification.setDescription(qualificationId);
        add(qualificationId, qualification);

        service.getQualifications().put(qualificationId, qualification);
        this.serviceDao.updateService(service);
        return qualificationId;
    }

    public void deleteQualification(Qualification qualification, Service service) throws DaoException {
        delete(qualification.getId());

        service.getQualifications().remove(qualification.getId());
        this.serviceDao.updateService(service);
    }

    public void updateQualification(Qualification qualification) throws DaoException {
        update(qualification.getId(), qualification);
    }
}
