package br.edu.ifspsaocarlos.sosprecos.view.login;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.util.ViewUtils;

public class ResetPasswordActivity extends AppCompatActivity {

    private FrameLayout progressBarHolder;
    private AutoCompleteTextView acTvEmail;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        this.acTvEmail = findViewById(R.id.actv_email);
        this.progressBarHolder = findViewById(R.id.progress_bar_holder);

        this.auth = FirebaseAuth.getInstance();
        configureToolbar();
        ViewUtils.hideKeyboard(this);
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

        ViewUtils.showProgressBar(progressBarHolder);
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        ViewUtils.hideProgressBar(progressBarHolder);
                        if (task.isSuccessful()) {
                            Toast.makeText(ResetPasswordActivity.this,
                                    getString(R.string.send_reset_password_email_message),
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ResetPasswordActivity.this,
                                    getString(R.string.send_reset_password_email_failed),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
