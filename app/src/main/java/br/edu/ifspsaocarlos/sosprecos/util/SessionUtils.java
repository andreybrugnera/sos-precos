package br.edu.ifspsaocarlos.sosprecos.util;

import br.edu.ifspsaocarlos.sosprecos.model.User;

/**
 * Created by Andrey R. Brugnera on 08/08/2018.
 */
public class SessionUtils {
    private static String currentUserId;

    public static String getCurrentUserId() {
        return currentUserId;
    }

    public static void setCurrentUserId(String currentUserId) {
        SessionUtils.currentUserId = currentUserId;
    }
}
