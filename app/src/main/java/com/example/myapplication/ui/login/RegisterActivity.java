package com.example.myapplication.ui.login;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.data.AppDatabase;
import com.example.myapplication.data.User;
import com.example.myapplication.util.ExecutorsProvider;

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etDisplay;
    private Button btnCreate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail    = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etDisplay  = findViewById(R.id.etDisplayName);
        btnCreate  = findViewById(R.id.btnCreate);

        btnCreate.setOnClickListener(v -> tryRegister());
    }

    private void tryRegister() {
        String email = etEmail.getText().toString().trim().toLowerCase();
        String pwd   = etPassword.getText().toString();
        String name  = etDisplay.getText().toString().trim();

        if (email.isEmpty() || pwd.isEmpty()) { toast("Email et mot de passe requis"); return; }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { toast("Email invalide"); return; }
        if (pwd.length() < 4) { toast("Mot de passe trop court (min 4)"); return; }

        ExecutorsProvider.io().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            if (db.userDao().findByEmail(email) != null) {
                runOnUiThread(() -> toast("Email déjà utilisé"));
                return;
            }
            db.userDao().insert(new User(email, pwd, name));
            runOnUiThread(() -> { toast("Compte créé. Connecte-toi !"); finish(); });
        });
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
