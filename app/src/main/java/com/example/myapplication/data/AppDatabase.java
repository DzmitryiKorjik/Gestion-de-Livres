package com.example.myapplication.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Classe principale Room qui décrit le schéma de la base et expose les DAO.
 * - @Database liste les entités et la version de schéma (à incrémenter si le schéma change).
 * - exportSchema=false évite d'exporter le schéma JSON dans /schemas (utile en prod tests si true).
 * - La classe étend RoomDatabase et fournit des getters vers les DAO.
 */
@Database(
        entities = { Book.class, User.class },
        version = 3,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    // DAO exposés par la DB
    public abstract BookDao bookDao();
    public abstract UserDao userDao();

    private static volatile AppDatabase INSTANCE;

    // Migrations
    // Migration 1 -> 2 : ajout de la colonne 'read' à la table books.
    // NOT NULL + DEFAULT 0 pour ne pas casser les lignes existantes.
    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE books ADD COLUMN read INTEGER NOT NULL DEFAULT 0");
        }
    };

    // Migration 2 -> 3 : création table users
    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "email TEXT, " +
                    "password TEXT, " +
                    "displayName TEXT)");
        }
    };

    /**
     * Fournit l'instance unique de la base (pattern Singleton) liée au contexte d'application.
     * - Utilise double-checked locking pour éviter la synchro systématique tout en restant thread-safe.
     * - databaseBuilder crée physiquement la base nommée "books.db" dans le répertoire de l'app.
     * - addMigrations enregistre les migrations nécessaires pour passer d'une version à l'autre
     * sans perte de données. Si vous changez le schéma, incrémentez 'version' et ajoutez la
     * migration correspondante.
     * - addCallback(onCreate) s'exécute uniquement à la première création de la base pour pré-remplir
     * des données (ici via DbPrepopulate.insertDefaults(...)).
     */
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "books.db"
                            )
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                            .addCallback(new Callback() {
                                @Override public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    DbPrepopulate.insertDefaults(context.getApplicationContext());
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
