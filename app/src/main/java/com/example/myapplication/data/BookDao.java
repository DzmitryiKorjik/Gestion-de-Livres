package com.example.myapplication.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BookDao {
    /**
     * Récupère tous les livres triés par titre.
     * SELECT * : retourne toutes les colonnes de l'entité Book.
     * ORDER BY title ASC : tri croissant par titre (NULLS d'abord selon SQLite).
     */
    @Query("SELECT * FROM books ORDER BY title ASC")
    List<Book> getAll();

    /**
     * Récupère un livre par identifiant primaire.
     * :id est un paramètre nommé, lié au paramètre method 'id'.
     * Retourne null si aucune ligne trouvée (en Java, Room renvoie null; en Kotlin, utilisez Book?).
     */
    @Query("SELECT * FROM books WHERE id = :id")
    Book getById(long id);

    /**
     * Insère un nouveau livre et retourne la clé primaire générée.
     */
    @Insert
    long insert(Book book);

    /**
     * Met à jour uniquement le statut de lecture (champ boolean -> INTEGER 0/1 en SQLite).
     * UPDATE ciblé : efficace quand on modifie un champ spécifique sans recharger l'objet complet.
     */
    @Query("UPDATE books SET read = :read WHERE id = :id")
    void setRead(long id, boolean read);

    @Query("UPDATE books SET imageBase64 = :base64 WHERE id = :id")
    void setImage(long id, String base64);
}
