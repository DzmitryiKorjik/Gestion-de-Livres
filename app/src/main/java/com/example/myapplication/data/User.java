package com.example.myapplication.data;

// Importation des annotations nécessaires de la bibliothèque Room
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Classe représentant un utilisateur dans la base de données Room.
 * Elle est annotée avec @Entity pour indiquer à Room qu'il s'agit d'une table.
 */
@Entity(tableName = "users") // Définit le nom de la table dans la base de données
public class User {

    /**
     * Identifiant unique de l'utilisateur, généré automatiquement par Room.
     */
    @PrimaryKey(autoGenerate = true)
    public long id;

    /**
     * Adresse email de l'utilisateur (unique pour chaque compte).
     */
    public String email;

    /**
     * Mot de passe de l'utilisateur (stocké en clair ici, à chiffrer dans une vraie application).
     */
    public String password;

    /**
     * Nom d'affichage de l'utilisateur (peut être un pseudonyme ou le prénom).
     */
    public String displayName;

    /**
     * Constructeur de la classe User.
     * Utilisé par l'application pour créer de nouveaux utilisateurs avant insertion en base.
     *
     * @param email adresse email de l'utilisateur
     * @param password mot de passe de l'utilisateur
     * @param displayName nom d'affichage à associer
     */
    public User(String email, String password, String displayName) {
        this.email = email;
        this.password = password;
        this.displayName = displayName;
    }
}
