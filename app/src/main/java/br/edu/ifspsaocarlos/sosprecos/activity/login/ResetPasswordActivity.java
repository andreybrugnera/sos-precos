package br.edu.ifspsaocarlos.sosprecos.activity.login;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import br.edu.ifspsaocarlos.sosprecos.R;

public class ResetPasswordActivity extends Activity {

    private ProgressBar progressBar;
    private AutoCompleteTextView acTvEmail;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        this.acTvEmail = findViewById(R.id.actv_email);
        this.progressBar = findViewById(R.id.pb_reset_password);

        this.auth = FirebaseAuth.getInstance();
    }

    /**
     * Send email message
     * to reset user password
     *
     * @param v
     */
    public void resetPassword(View v) {
        String email = this.acTvEmail.getText().toString().trim();

        /* Check if is a valid email address */
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            this.acTvEmail.setError(getString(R.string.enter_valid_email));
            this.acTvEmail.requestFocus();
            return;
        }

        this.progressBar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(ResetPasswordActivity.this,
                                    getString(R.string.send_reset_password_email_message),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ResetPasswordActivity.this,
                                    getString(R.string.send_reset_password_email_failed),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void goBackToLogin(View v){
        finish();
    }
}
