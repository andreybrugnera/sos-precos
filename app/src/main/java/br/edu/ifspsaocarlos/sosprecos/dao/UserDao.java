package br.edu.ifspsaocarlos.sosprecos.dao;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.dao.exception.DaoException;
import br.edu.ifspsaocarlos.sosprecos.model.User;

/**
 * Created by Andrey R. Brugnera on 15/03/2018.
 */
public class UserDao extends AbstractDao<User> {
    public static final String DATABASE_REFERENCE = "users";

    public UserDao(Context context) {
        super(context, DATABASE_REFERENCE);
    }

    @Override
    public void add(User user) throws DaoException {
        validate(user);
        add(user.getUuid(), user);
    }

    @Override
    public void delete(User user) throws DaoException {
        validate(user);
        delete(user.getUuid());
    }

    @Override
    public void update(User user) throws DaoException {
        validate(user);
        add(user);
    }

    private void validate(User user) throws DaoException{
        if (TextUtils.isEmpty(user.getUuid())) {
            Log.d(DATABASE_LOGGER_TAG, getContext().getResources().getString(R.string.id_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.id_not_set));
        }

        if (TextUtils.isEmpty(user.getEmail())) {
            Log.d(DATABASE_LOGGER_TAG, getContext().getResources().getString(R.string.user_email_not_set));
            throw new DaoException(getContext().getResources().getString(R.string.user_email_not_set));
        }
    }
}