package br.edu.ifspsaocarlos.sosprecos.util;

import br.edu.ifspsaocarlos.sosprecos.model.User;

/**
 * Created by Andrey R. Brugnera on 08/08/2018.
 */
public class SessionUtils {
    private static User currentUser;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        SessionUtils.currentUser = currentUser;
    }
}
