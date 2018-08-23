package br.edu.ifspsaocarlos.sosprecos.view.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
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

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.util.SessionUtils;
import br.edu.ifspsaocarlos.sosprecos.util.ViewUtils;
import br.edu.ifspsaocarlos.sosprecos.view.MainActivity;

public class LoginActivity extends Activity {

    private AutoCompleteTextView acTvEmail;
    private EditText etPassword;
    private FrameLayout progressBarHolder;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.acTvEmail = findViewById(R.id.actv_email);
        this.etPassword = findViewById(R.id.et_password);
        this.progressBarHolder = findViewById(R.id.progress_bar_holder);
        this.auth = FirebaseAuth.getInstance();
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
                        ViewUtils.hideProgressBar(progressBarHolder);
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            SessionUtils.setCurrentUserId(firebaseUser.getUid());
                            startMainActivity();
                        } else if (task.getException() instanceof FirebaseAuthInvalidUserException || task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                            Toast.makeText(LoginActivity.this, getString(R.string.invalid_email_passeord),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, getString(R.string.login_failure),
                                    Toast.LENGTH_SHORT).show();
                        }
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
