package com.example.myapplication.data;

// Importation des annotations nécessaires de la bibliothèque Room
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Classe représentant un utilisateur dans la base de données Room.
 * Elle est annotée avec @Entity pour indiquer à Room qu'il s'agit d'une table.
 */

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true) public long id;
    public String email;
    public String displayName;
    public String firebaseUid; // ← ключ связи с Firebase

    public User(String email, String displayName, String firebaseUid) {
        this.email = email;
        this.displayName = displayName;
        this.firebaseUid = firebaseUid;
    }
}
