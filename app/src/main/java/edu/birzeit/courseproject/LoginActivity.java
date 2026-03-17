package edu.birzeit.courseproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.birzeit.courseproject.data.UserRepo;
import edu.birzeit.courseproject.utils.PrefManager;
import edu.birzeit.courseproject.utils.Validators;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private CheckBox cbRemember;
    private PrefManager pref;
    private UserRepo userRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pref = new PrefManager(this);
        userRepo = new UserRepo(this);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        cbRemember = findViewById(R.id.cbRemember);

        Button btnSignUp = findViewById(R.id.btnSignUp);
        Button btnSignIn = findViewById(R.id.btnSignIn);

        // If Remember me enabled, show saved email in the login screen
        if (pref.isRememberMe()) {
            etEmail.setText(pref.getSavedEmail());
            cbRemember.setChecked(true);
        }

        btnSignUp.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, SignupActivity.class))
        );

        btnSignIn.setOnClickListener(v -> signIn());
    }

    private void signIn() {
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        // Validation
        if (!Validators.isValidEmail(email)) {
            etEmail.setError("Invalid email");
            return;
        }
        if (pass.isEmpty()) {
            etPassword.setError("Password required");
            return;
        }

        // Check login from SQLite
        if (!userRepo.checkLogin(email, pass)) {
            Toast.makeText(this, "Wrong email or password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save current logged-in user email
        pref.setCurrentEmail(email);


        pref.setRememberMe(cbRemember.isChecked());
        if (cbRemember.isChecked()) {
            pref.setSavedEmail(email);
        } else {
            pref.clearSavedEmail();
        }

        // Go to Main Drawer
        startActivity(new Intent(LoginActivity.this, MainDrawerActivity.class));
        finish();
    }
}
