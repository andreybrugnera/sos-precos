package br.edu.ifspsaocarlos.sosprecos.util;

import android.content.res.Resources;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.edu.ifspsaocarlos.sosprecos.R;

/**
 * Created by Andrey R. Brugnera on 09/08/2018.
 */
public class DateTimeUtils {

    public static String formatDate(Date date){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return dateFormat.format(date);
    }
}
