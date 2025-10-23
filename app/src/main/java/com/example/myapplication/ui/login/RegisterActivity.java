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
import com.example.myapplication.data.UserDao;
import com.example.myapplication.util.ExecutorsProvider;
import com.example.myapplication.util.Toaster;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etName;
    private Button btnRegister, btnGoLogin;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etName = findViewById(R.id.etName);
        btnRegister = findViewById(R.id.btnRegister);
        btnGoLogin = findViewById(R.id.btnGoLogin);

        btnRegister.setOnClickListener(v -> tryRegister());
        if (btnGoLogin != null) btnGoLogin.setOnClickListener(v -> finish());
    }

    private void tryRegister() {
        String email = etEmail.getText().toString().trim().toLowerCase();
        String pwd   = etPassword.getText().toString();
        String name  = etName.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toaster.show(this, "Email invalide");
            return;
        }
        if (pwd.length() < 6) {
            Toaster.show(this, "Le mot de passe doit contenir au moins 6 caractères");
            return;
        }
        if (name.isEmpty()) {
            Toaster.show(this, "Entre un nom");
            return;
        }

        setUiEnabled(false);
        auth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(task -> {
            setUiEnabled(true);
            if (task.isSuccessful()) {
                // 1) Mettre à jour le displayName coté Firebase (optionnel)
                if (auth.getCurrentUser() != null) {
                    auth.getCurrentUser().updateProfile(
                            new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build()
                    );
                }

                // 2) >>> AJOUT: créer/mettre à jour le profil local dans Room
                FirebaseUser fu = FirebaseAuth.getInstance().getCurrentUser();
                if (fu != null) {
                    final String display = (fu.getDisplayName() != null && !fu.getDisplayName().isEmpty())
                            ? fu.getDisplayName() : name; // fallback si updateProfile pas encore propagé
                    ExecutorsProvider.io().execute(() -> {
                        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                        UserDao dao = db.userDao();
                        if (dao.findByUid(fu.getUid()) == null) {
                            dao.insert(new User(fu.getEmail(), display, fu.getUid()));
                        }
                    });
                }


                Toaster.show(this, "Compte créé. Connecte-toi !");
                finish();
            } else {
                String msg = (task.getException() != null)
                        ? task.getException().getLocalizedMessage()
                        : "Échec de l'inscription";
                Toaster.show(this, msg);
            }
        });
    }

    private void setUiEnabled(boolean enabled) {
        etEmail.setEnabled(enabled);
        etPassword.setEnabled(enabled);
        etName.setEnabled(enabled);
        btnRegister.setEnabled(enabled);
        if (btnGoLogin != null) btnGoLogin.setEnabled(enabled);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
