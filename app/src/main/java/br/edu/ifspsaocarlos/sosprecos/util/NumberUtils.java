package br.edu.ifspsaocarlos.sosprecos.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by Andrey R. Brugnera on 09/09/2018.
 */
public class NumberUtils {

    private static final int MAX_FRACTION_DIGITS = 2;
    private static final String CURRENCY_PATTERN = "###,###.##";

    public static String format(Float value) {
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(MAX_FRACTION_DIGITS);
        return decimalFormat.format(value);
    }

    public static String formatCurrency(Float value, Locale locale) {
        NumberFormat nf = NumberFormat.getNumberInstance(locale);
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern(CURRENCY_PATTERN);
        return df.format(value);
    }
}
