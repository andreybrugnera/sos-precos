package br.edu.ifspsaocarlos.sosprecos.dao;

import android.content.Context;
import android.text.TextUtils;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.dao.exception.DaoException;
import br.edu.ifspsaocarlos.sosprecos.model.User;

/**
 * Created by Andrey R. Brugnera on 15/03/2018.
 */
public class UserDao extends AbstractDao<User> {

    public UserDao(Context context) {
        super(context,"users");
    }

    public void addUser(User user) throws DaoException{
        if(!TextUtils.isEmpty(user.getEmail())){
            throw new DaoException(getContext().getResources().getString(R.string.user_email_not_set));
        }
        if(!TextUtils.isEmpty(user.getUuid())){
            throw new DaoException(getContext().getResources().getString(R.string.user_uuid_not_set));
        }
        add(user.getUuid(), user);
    }

    public void deleteUser(User user) throws DaoException{
        if(!TextUtils.isEmpty(user.getUuid())){
            throw new DaoException(getContext().getResources().getString(R.string.user_uuid_not_set));
        }
        delete(user.getUuid());
    }

    public void updateUser(User user) throws DaoException{
        addUser(user);
    }
}
