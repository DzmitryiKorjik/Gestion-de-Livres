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
        version = 2,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract BookDao bookDao();
    public abstract UserDao userDao();

    private static volatile AppDatabase INSTANCE;

    // Migration qui met à jour le schéma de la base de 1 -> 2
    // Ajoute la colonne `imageBase64` à la table `books` afin de conserver les images encodées
    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Ajouter une colonne TEXT (valeur par défaut NULL pour les anciennes lignes)
            database.execSQL("ALTER TABLE books ADD COLUMN imageBase64 TEXT");
            // Si vous avez ajouté d'autres colonnes dans la modification du modèle, ajoutez-les ici
            // database.execSQL("ALTER TABLE books ADD COLUMN imageRes TEXT");
        }
    };

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "books.db"
                            )

                            // Enregistre la migration et conserve un fallback destructif en dernier recours
                            .addMigrations(MIGRATION_1_2)
                            .fallbackToDestructiveMigration()

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