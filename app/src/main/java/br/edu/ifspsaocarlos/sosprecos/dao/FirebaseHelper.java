package br.edu.ifspsaocarlos.sosprecos.dao;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import br.edu.ifspsaocarlos.sosprecos.dao.exception.DaoException;

/**
 * Created by Andrey R. Brugnera on 15/03/2018.
 */
public abstract class FirebaseHelper<E> {
    protected final String DATABASE_LOGGER_TAG = "DATABASE";
    private Context context;
    protected DatabaseReference mDatabase;
    protected String referenceName;
    private DatabaseReference dRef;
    //All items on database
    private Map<String, E> elementsMap;

    public FirebaseHelper(Context context, String referenceName) {
        this.context = context;
        this.referenceName = referenceName;
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public DatabaseReference getDatabaseReference() {
        if (dRef == null && referenceName != null) {
            dRef = mDatabase.getDatabase().getReference(referenceName);
        }
        return dRef;
    }

    public Context getContext() {
        return context;
    }

    /**
     * Inserts new element into database
     *
     * @param key
     * @param e
     */
    protected void add(String key, E e) {
        DatabaseReference dRef = getDatabaseReference();
        dRef.child(key).setValue(e);
    }

    /**
     * Deletes database element by it's key
     *
     * @param key
     */
    protected void delete(String key) {
        DatabaseReference dRef = getDatabaseReference();
        dRef.child(key).setValue(null);
    }

    /**
     * Gets element by key
     *
     * @param key
     * @return element found with specified key
     */
    public E get(String key) {
        return getElementsMap().get(key);
    }

    /**
     * Updates existing element
     *
     * @param key
     * @param e
     */
    protected void update(String key, E e) {
        add(key, e);
    }

    public void refreshElements(Map<String, Object> mapCategories) {
        Map<String, E> elements = getElementsMap();
        for (String key : mapCategories.keySet()) {
            E element = (E) mapCategories.get(key);
            elements.put(key, element);
        }
    }

    /**
     * Retrieve all items
     *
     * @return all items
     */
    public Map<String, E> getElementsMap() {
        if (elementsMap == null) {
            elementsMap = new HashMap<>();
        }
        return elementsMap;
    }

    /**
     * Updates database reference name.
     * <p>
     * Changing the reference name will force
     * the database reference to be recreated.
     *
     * @param referenceName
     */
    public void updateDatabaseReferenceName(String referenceName) {
        this.referenceName = referenceName;
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        this.dRef = null;
        this.elementsMap = null;
    }

    public abstract void add(E e) throws DaoException;
    public abstract void update(E e) throws DaoException;
    public abstract void delete(E e) throws DaoException;
}