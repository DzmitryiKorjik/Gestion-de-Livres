package com.example.myapplication.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Représente une ligne de la table "books".
 *
 * Notes Room
 * - @Entity(tableName = "books") lie cette classe à la table SQLite "books".
 * - Chaque champ public devient une colonne (sauf si annoté avec @Ignore).
 * - Le type boolean est mappé en INTEGER (0/1) côté SQLite.
 *
 * Évolution du schéma
 * - Ajouter/Supprimer/Modifier un champ => incrémentez la version dans @Database
 *   et fournissez une Migration correspondante.
 * - Renommer un champ = ALTER TABLE ... RENAME COLUMN (ou créer une nouvelle table + copie).
 */
@Entity(tableName = "books")
public class Book {
    /**
     * Identifiant unique auto-incrémenté.
     * Room génère la valeur lors de l'insert si id == 0.
     */
    @PrimaryKey(autoGenerate = true)
    public long id;

    /** Titre du livre (nullable autorisé ici). */
    public String title;

    /** Auteur (nullable autorisé). */
    public String author;

    /** Description / résumé (nullable autorisé). */
    public String description;

    /**
     * Indique si le livre est marqué comme "lu".
     * Stocké en INTEGER 0 (false) / 1 (true) en SQLite.
     */
    public boolean read;

    /**
     * Référence de ressource image (par ex. nom dans assets/URL/uri), ici chaîne libre.
     * Si vous passez à un @DrawableRes int, pensez à une migration.
     */
    public String imageRes;

    public String imageBase64;

    /**
     * Constructeur utilisé par Room (le plus complet non-ignoré).
     *
     * Remarque : Ici nous initialisons read à false par défaut. Si vous ajoutez d'autres
     * constructeurs, annotez ceux à ignorer avec @Ignore pour éviter l'ambiguïté.
     */
    public Book(String title, String author, String description, String imageRes) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.read = false; // valeur par défaut lors de la création
        this.imageRes = imageRes;
        this.imageBase64 = null;
    }

    // constructeur pratique si on a déjà du base64
    public static Book withBase64(String title, String author, String description, String imageBase64) {
        Book b = new Book(title, author, description, null);
        b.imageBase64 = imageBase64;
        return b;
    }
}
