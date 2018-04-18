package br.edu.ifspsaocarlos.sosprecos.dao;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.dao.exception.DaoException;
import br.edu.ifspsaocarlos.sosprecos.model.Qualification;

/**
 * Created by Andrey R. Brugnera on 06/04/2018.
 */
public class QualificationDao extends AbstractDao<Qualification> {
    public static final String DATABASE_REFERENCE = "qualifications";

    public QualificationDao(Context context) {
        super(context, DATABASE_REFERENCE);
    }

    @Override
    public void add(Qualification qualification) throws DaoException {
        validate(qualification, false);

        String qualificationId = getDatabaseReference().push().getKey();
        qualification.setId(qualificationId);
        add(qualificationId, qualification);
    }

    @Override
    public void delete(Qualification qualification) throws DaoException {
        validate(qualification, true);
        delete(qualification.getId());
    }

    @Override
    public void update(Qualification qualification) throws DaoException {
        validate(qualification, true);
        update(qualification.getId(), qualification);
    }

    private void validate(Qualification qualification, boolean checkId) throws DaoException {
        if (TextUtils.isEmpty(qualification.getId()) && checkId) {
            Log.d(DATABASE_LOGGER_TAG, getContext().getResources().getString(R.string.id_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.id_not_set));
        }

        if (TextUtils.isEmpty(qualification.getUserId())) {
            Log.d(DATABASE_LOGGER_TAG, getContext().getResources().getString(R.string.user_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.user_not_set));
        }
    }
}
