package br.edu.ifspsaocarlos.sosprecos.dao;

import com.firebase.geofire.GeoFire;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.edu.ifspsaocarlos.sosprecos.util.SystemConstants;

/**
 * Created by Andrey R. Brugnera on 29/08/2018.
 */
public abstract class GeoFireHelper {
    private static GeoFire geoFire;

    public static GeoFire getGeoFire() {
        if (geoFire == null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(SystemConstants.GEOFIRE_DATABASE_REFERENCE);
            geoFire = new GeoFire(ref);
        }
        return geoFire;
    }
}
