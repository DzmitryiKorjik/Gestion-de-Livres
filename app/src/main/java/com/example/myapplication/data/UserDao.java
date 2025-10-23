package com.example.myapplication.data;

// Importation des annotations nécessaires pour définir un DAO Room
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

/**
 * Interface Data Access Object (DAO) utilisée par Room pour interagir avec la table "users".
 * Elle contient les méthodes permettant d'accéder, d'insérer ou de rechercher des utilisateurs.
 */
@Dao
public interface UserDao {
    @Insert long insert(User user);

    @Query("SELECT * FROM users WHERE firebaseUid = :uid LIMIT 1")
    User findByUid(String uid);
}

