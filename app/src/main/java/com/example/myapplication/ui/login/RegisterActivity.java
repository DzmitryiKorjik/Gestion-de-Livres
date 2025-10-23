package com.example.myapplication.ui.login;


import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * Écran d'inscription via Firebase Authentication.
 *
 * Remplace l'ancienne insertion dans Room par createUserWithEmailAndPassword.
 * Vous pouvez ensuite synchroniser/compléter un profil local (sans mot de passe) si souhaité.
 */
public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etName; // adaptez aux IDs de votre layout
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
        String pwd = etPassword.getText().toString();
        String name = etName.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            toast("Email invalide");
            return;
        }
        if (pwd.length() < 6) {
            toast("Mot de passe trop court (≥ 6)");
            return;
        }
        if (name.isEmpty()) {
            toast("Entre un nom");
            return;
        }

        setUiEnabled(false);
        auth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(task -> {
            setUiEnabled(true);
            if (task.isSuccessful()) {
                // Mettre à jour le displayName côté Firebase (optionnel)
                if (auth.getCurrentUser() != null) {
                    auth.getCurrentUser().updateProfile(
                            new UserProfileChangeRequest.Builder().setDisplayName(name).build()
                    );
                }

                // (Optionnel) Envoyer un email de vérification
                // if (auth.getCurrentUser() != null) auth.getCurrentUser().sendEmailVerification();
                toast("Compte créé. Connecte-toi !");
                finish(); // retour à l'écran de login
            } else {
                String msg = task.getException() != null ? task.getException().getLocalizedMessage() : "Échec de l'inscription";
                toast(msg);
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