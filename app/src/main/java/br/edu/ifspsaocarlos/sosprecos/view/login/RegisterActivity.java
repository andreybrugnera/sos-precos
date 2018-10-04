package br.edu.ifspsaocarlos.sosprecos.view.login;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.dao.UserDao;
import br.edu.ifspsaocarlos.sosprecos.dao.exception.DaoException;
import br.edu.ifspsaocarlos.sosprecos.model.User;
import br.edu.ifspsaocarlos.sosprecos.util.ViewUtils;

public class RegisterActivity extends AppCompatActivity {

    private AutoCompleteTextView acTvEmail;
    private EditText etName;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private FrameLayout progressBarHolder;
    private UserDao userDao;

    private static final int PASSWORD_MIN_LENGTH = 8;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        this.etName = findViewById(R.id.et_name);
        this.acTvEmail = findViewById(R.id.actv_email);
        this.etPassword = findViewById(R.id.et_password);
        this.etConfirmPassword = findViewById(R.id.et_confirm_password);
        this.progressBarHolder = findViewById(R.id.progress_bar_holder);
        this.userDao = new UserDao(this);

        this.auth = FirebaseAuth.getInstance();
        configureToolbar();
    }

    private void configureToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Registers a new user into firebase
     *
     * @param v
     */
    public void registerUser(View v) {
        String name = this.etName.getText().toString().trim();
        String email = this.acTvEmail.getText().toString().trim();
        String password = this.etPassword.getText().toString().trim();
        String confirmPassword = this.etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            this.etName.setError(getString(R.string.enter_valid_name));
            this.etName.requestFocus();
            return;
        }

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

        if (password.length() < PASSWORD_MIN_LENGTH) {
            this.etPassword.setError(getString(R.string.password_length_error));
            this.etPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            this.etConfirmPassword.setError(getString(R.string.enter_password));
            this.etConfirmPassword.requestFocus();
            return;
        }

        if (!confirmPassword.equals(password)) {
            this.etConfirmPassword.setError(getString(R.string.password_mismatch));
            this.etConfirmPassword.requestFocus();
            return;
        }

        final User newUser = new User(name, email);

        ViewUtils.showProgressBar(progressBarHolder);

        auth.createUserWithEmailAndPassword(newUser.getEmail(),  password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        ViewUtils.hideProgressBar(progressBarHolder);

                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            //Register user into firebase app database
                            try {
                                newUser.setUuid(user.getUid());
                                userDao.add(newUser);
                            } catch (DaoException ex) {
                                Log.e(getString(R.string.firebase_error), ex.getMessage());
                            }
                            //Go back to login
                            finish();
                        } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(RegisterActivity.this, getString(R.string.registration_failure_user_exists),
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, getString(R.string.registration_failure),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
