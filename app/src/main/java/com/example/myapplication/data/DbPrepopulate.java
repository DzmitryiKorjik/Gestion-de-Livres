package com.example.myapplication.data;

import android.content.Context;

import com.example.myapplication.util.ExecutorsProvider;

/**
 * Utilitaire de pré-remplissage de la base.
 */
public class DbPrepopulate {
    /**
     * Insère des données par défaut (livres + utilisateur de démo).
     */
    public static void insertDefaults(Context context) {
        ExecutorsProvider.io().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(context);

            // Livres de démo
            BookDao bookDao = db.bookDao();
            if (bookDao.getAll().isEmpty()) {
                bookDao.insert(new Book(
                        "1984",
                        "George Orwell",
                        "Dystopie politique dénonçant les dérives totalitaires...",
                        "book_1984"
                ));

                bookDao.insert(new Book(
                        "L'Étranger",
                        "Albert Camus",
                        "Roman de l'absurde explorant l'aliénation...",
                        "book_etranger"
                ));

                bookDao.insert(new Book(
                        "Le Petit Prince",
                        "Antoine de Saint-Exupéry",
                        "Conte poétique et philosophique sur l'amitié...",
                        "book_petit_prince"
                ));

            }
        });
    }
}
