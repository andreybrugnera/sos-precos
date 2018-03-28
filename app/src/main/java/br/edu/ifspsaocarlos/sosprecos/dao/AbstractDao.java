package br.edu.ifspsaocarlos.sosprecos.dao;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Andrey R. Brugnera on 15/03/2018.
 */
public abstract class AbstractDao<E> {
    private Context context;
    protected DatabaseReference mDatabase;
    protected String referenceName;

    public AbstractDao(Context context, String referenceName) {
        this.context = context;
        this.referenceName = referenceName;
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public DatabaseReference getDatabaseReference(){
        DatabaseReference dRef = mDatabase.getDatabase().getReference(referenceName);
        return dRef;
    }

    public Context getContext() {
        return context;
    }

    /**
     * Inserts new element into database
     * @param key
     * @param e
     */
    public void add(String key, E e){
        DatabaseReference dRef = getDatabaseReference();
        dRef.child(key).setValue(e);
    }

    /**
     * Deletes database element by it's key
     * @param key
     */
    public void delete(String key){
        DatabaseReference dRef = getDatabaseReference();
        dRef.child(key).setValue(null);
    }

    /**
     * Updates existing element
     * @param key
     * @param e
     */
    public void update(String key, E e){
        add(key, e);
    }
}
