package br.edu.ifspsaocarlos.sosprecos.dao;

import android.text.TextUtils;

import br.edu.ifspsaocarlos.sosprecos.model.User;

/**
 * Created by Andrey R. Brugnera on 15/03/2018.
 */
public class UserDao extends AbstractDao<User> {

    public UserDao() {
        super("users");
    }

    public void addUser(User user){
        if(!TextUtils.isEmpty(user.getEmail())){
            add(user.getUuid(), user);
        }
    }

    public void deleteUser(User user){
        if(!TextUtils.isEmpty(user.getEmail())){
            delete(user.getEmail());
        }
    }
}
