package com.example.myapplication.data;

// Importation des annotations nécessaires pour définir un DAO Room
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

/**
 * Interface Data Access Object (DAO) utilisée par Room pour interagir avec la table "users".
 * Elle contient les méthodes permettant d'accéder, d'insérer ou de rechercher des utilisateurs.
 */
@Dao // Indique que cette interface est un DAO pour la base de données Room
public interface UserDao {

    /**
     * Recherche un utilisateur dans la table "users" en fonction de son adresse email.
     *
     * @param email L'adresse email à rechercher.
     * @return L'objet User correspondant, ou null si aucun utilisateur n'existe avec cet email.
     */
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User findByEmail(String email);

    /**
     * Insère un nouvel utilisateur dans la base de données.
     *
     * @param user L'objet User à insérer.
     * @return L'identifiant (ID) généré automatiquement pour le nouvel utilisateur.
     */
    @Insert
    long insert(User user);
}
