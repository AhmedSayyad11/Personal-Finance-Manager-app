package edu.birzeit.courseproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.birzeit.courseproject.data.UserRepo;
import edu.birzeit.courseproject.utils.Validators;

public class SignupActivity extends AppCompatActivity {

    private EditText etEmail, etFirst, etLast, etPass, etConfirm;
    private UserRepo userRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        userRepo = new UserRepo(this);

        etEmail = findViewById(R.id.etEmail);
        etFirst = findViewById(R.id.etFirst);
        etLast = findViewById(R.id.etLast);
        etPass = findViewById(R.id.etPassword);
        etConfirm = findViewById(R.id.etConfirm);

        Button btnCreate = findViewById(R.id.btnCreate);
        Button btnBack = findViewById(R.id.btnBack);

        btnCreate.setOnClickListener(v -> createAccount());
        btnBack.setOnClickListener(v -> finish());
    }

    private void createAccount() {
        String email = etEmail.getText().toString().trim();
        String first = etFirst.getText().toString().trim();
        String last = etLast.getText().toString().trim();
        String pass = etPass.getText().toString().trim();
        String confirm = etConfirm.getText().toString().trim();

        boolean ok = true;

        if (!Validators.isValidEmail(email)) {
            etEmail.setError("Invalid email");
            ok = false;
        }
        if (!Validators.isValidName(first)) {
            etFirst.setError("3–10 chars");
            ok = false;
        }
        if (!Validators.isValidName(last)) {
            etLast.setError("3–10 chars");
            ok = false;
        }
        if (!Validators.isValidPassword(pass)) {
            etPass.setError("6–12 chars, 1 digit, 1 lower, 1 upper");
            ok = false;
        }
        if (!pass.equals(confirm)) {
            etConfirm.setError("Passwords do not match");
            ok = false;
        }

        if (!ok) return;

        boolean inserted = userRepo.insertUser(email, first, last, pass);
        if (!inserted) {
            Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show();
        finish();
    }
}
