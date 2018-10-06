package br.edu.ifspsaocarlos.sosprecos.view.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.dao.UserDao;
import br.edu.ifspsaocarlos.sosprecos.model.User;
import br.edu.ifspsaocarlos.sosprecos.util.SessionUtils;
import br.edu.ifspsaocarlos.sosprecos.util.SystemConstants;
import br.edu.ifspsaocarlos.sosprecos.util.ViewUtils;
import br.edu.ifspsaocarlos.sosprecos.view.MainActivity;

public class LoginActivity extends Activity {
    private String LOG_TAG = "LOGIN";

    private SharedPreferences sharedPreferences;
    private AutoCompleteTextView acTvEmail;
    private EditText etPassword;
    private FrameLayout progressBarHolder;

    private FirebaseAuth auth;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.acTvEmail = findViewById(R.id.actv_email);
        this.etPassword = findViewById(R.id.et_password);
        this.progressBarHolder = findViewById(R.id.progress_bar_holder);
        this.auth = FirebaseAuth.getInstance();

        this.sharedPreferences = getSharedPreferences(SystemConstants.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);

        this.userDao = new UserDao(this);

        updateUI();
    }

    private void updateUI() {
        String userEmail = sharedPreferences.getString(SystemConstants.USER_EMAIL, "");
        this.acTvEmail.setText(userEmail);
        ViewUtils.hideKeyboard(this);
    }

    private void saveUserEmail() {
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString(SystemConstants.USER_EMAIL, this.acTvEmail.getText().toString());
        sharedPreferencesEditor.commit();
    }

    /**
     * Process login
     * @param v
     */
    public void startLogin(View v){
        String email = this.acTvEmail.getText().toString().trim();
        String password = this.etPassword.getText().toString().trim();

        /* Check if is a valid email address */
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            this.acTvEmail.setError(getString(R.string.enter_valid_email));
            this.acTvEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            this.etPassword.setError(getString(R.string.enter_password));
            this.etPassword.requestFocus();
            return;
        }

        ViewUtils.showProgressBar(progressBarHolder);

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            loadCurrentUser(firebaseUser.getUid());
                            saveUserEmail();
                        } else if (task.getException() instanceof FirebaseAuthInvalidUserException || task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                            Toast.makeText(LoginActivity.this, getString(R.string.invalid_email_passeord),
                                    Toast.LENGTH_SHORT).show();
                            ViewUtils.hideProgressBar(progressBarHolder);
                        } else {
                            Toast.makeText(LoginActivity.this, getString(R.string.login_failure),
                                    Toast.LENGTH_SHORT).show();
                            ViewUtils.hideProgressBar(progressBarHolder);
                        }
                    }
                });
    }

    private void loadCurrentUser(String uuid){
        Query query = userDao.getDatabaseReference().orderByChild("uuid").equalTo(uuid);

        query.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (DataSnapshot child : children) {
                            User user = child.getValue(User.class);
                            SessionUtils.setCurrentUser(user);
                            break;
                        }
                        ViewUtils.hideProgressBar(progressBarHolder);
                        startMainActivity();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(LOG_TAG, databaseError.getMessage());
                        Log.e(LOG_TAG, databaseError.getDetails());
                        ViewUtils.hideProgressBar(progressBarHolder);
                    }
                });
    }

    /**
     * Opens new activity to
     * register new user
     * @param v
     */
    public void registerNewUser(View v){
        startActivity(new Intent(this, RegisterActivity.class));
    }

    /**
     * Open new activity to
     * reset lost password
     * @param v
     */
    public void resetPassword(View v){
        startActivity(new Intent(this, ResetPasswordActivity.class));
    }

    /**
     * Starts main activity
     */
    private void startMainActivity(){
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
