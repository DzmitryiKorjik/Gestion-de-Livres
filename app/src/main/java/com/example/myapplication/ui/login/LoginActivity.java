package com.example.myapplication.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.ui.main.MainActivity;
import com.example.myapplication.util.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Écran de connexion utilisant Firebase Authentication (Email/Mot de passe).
 *
 * Remplace l'ancienne vérification Room (UserDao) par FirebaseAuth.
 * La base locale peut rester pour le profil utilisateur, mais ne doit plus contenir de mot de passe.
 */

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnGoRegister, btnReset;
    private SessionManager session;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        session = new SessionManager(this);
        if (session.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        auth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoRegister = findViewById(R.id.btnGoRegister);
        // Si vous avez un bouton "Mot de passe oublié" dans le layout
        btnReset = findViewById(R.id.btnReset);

        btnLogin.setOnClickListener(v -> tryLogin());
        btnGoRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
        if (btnReset != null) btnReset.setOnClickListener(v -> sendPasswordReset());
    }
    private void tryLogin() {
        String email = etEmail.getText().toString().trim().toLowerCase();
        String password = etPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Saisis email et mot de passe", Toast.LENGTH_SHORT).show();
            return;
        }

        setUiEnabled(false);
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            setUiEnabled(true);
            if (task.isSuccessful()) {
                FirebaseUser user = auth.getCurrentUser();

                if (user != null) {
                // Conserver l'email (ou mieux : l'UID) dans la session applicative
                    session.setLoggedInEmail(user.getEmail());
                }
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                String message = task.getException() != null ? task.getException().getLocalizedMessage() : "Échec de connexion";
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendPasswordReset() {
        String email = etEmail.getText().toString().trim().toLowerCase();
        if (email.isEmpty()) {
            Toast.makeText(this, "Entre ton email pour réinitialiser le mot de passe", Toast.LENGTH_SHORT).show();
            return;
        }
        auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Email de réinitialisation envoyé.", Toast.LENGTH_SHORT).show();
            } else {
                String message = task.getException() != null ? task.getException().getLocalizedMessage() : "Impossible d'envoyer l'email";
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUiEnabled(boolean enabled) {
        etEmail.setEnabled(enabled);
        etPassword.setEnabled(enabled);
        btnLogin.setEnabled(enabled);
        if (btnReset != null) btnReset.setEnabled(enabled);
        btnGoRegister.setEnabled(enabled);
    }
}