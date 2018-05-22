package br.edu.ifspsaocarlos.sosprecos.util;

/**
 * Created by Andrey R. Brugnera on 20/05/2018.
 */
public class ZipCodeWebService {

    protected static final String ZIP_WS = "https://viacep.com.br/ws/?/json/";
    public static final String STREET = "logradouro";
    public static final String STATE = "uf";
    public static final String CITY = "localidade";

    public static final String getResourceURL(String zipCode){
        return ZIP_WS.replace("?", zipCode);
    }
}
