package br.edu.ifspsaocarlos.sosprecos.dao;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Andrey R. Brugnera on 15/03/2018.
 */
public abstract class AbstractDao<E> {
    protected final String LOGGER_TAG = "DATABASE";
    private Context context;
    protected DatabaseReference mDatabase;
    protected String referenceName;
    private DatabaseReference dRef;
    //All elements on database
    private Map<String, E> elementsMap;

    public AbstractDao(Context context, String referenceName) {
        this.context = context;
        this.referenceName = referenceName;
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public DatabaseReference getDatabaseReference() {
        if (dRef == null) {
            dRef = mDatabase.getDatabase().getReference(referenceName);
            dRef.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            refreshElements((Map<String, Object>) dataSnapshot.getValue());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(LOGGER_TAG, databaseError.getMessage());
                            Log.e(LOGGER_TAG, databaseError.getDetails());
                        }
                    });
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
    public void add(String key, E e) {
        DatabaseReference dRef = getDatabaseReference();
        dRef.child(key).setValue(e);
    }

    /**
     * Deletes database element by it's key
     *
     * @param key
     */
    public void delete(String key) {
        DatabaseReference dRef = getDatabaseReference();
        dRef.child(key).setValue(null);
    }

    /**
     * Gets element by key
     * @param key
     * @return element found with specified key
     */
    public E get(String key){
        return getElementsMap().get(key);
    }

    /**
     * Updates existing element
     *
     * @param key
     * @param e
     */
    public void update(String key, E e) {
        add(key, e);
    }

    protected void refreshElements(Map<String, Object> mapCategories) {
        Map<String, E> elements = getElementsMap();
        for (String key : mapCategories.keySet()) {
            E element = (E) mapCategories.get(key);
            elements.put(key, element);
        }
    }

    /**
     * Retrieve all elements
     *
     * @return all elements
     */
    public Map<String, E> getElementsMap() {
        if(elementsMap == null){
            elementsMap = new HashMap<>();
        }
        return elementsMap;
    }

    /**
     * Updates database reference name.
     *
     * Changing the reference name will force
     * the database reference to be recreated.
     *
     * @param referenceName
     */
    public void updateDatabaseReferenceName(String referenceName){
        this.referenceName = referenceName;
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        this.dRef = null;
        this.elementsMap = null;
    }
}