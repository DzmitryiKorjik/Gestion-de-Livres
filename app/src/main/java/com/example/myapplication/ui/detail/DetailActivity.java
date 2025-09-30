package com.example.myapplication.ui.detail;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.data.AppDatabase;
import com.example.myapplication.data.Book;
import com.example.myapplication.ui.main.MainActivity;
import com.example.myapplication.util.ExecutorsProvider;
import com.google.android.material.appbar.MaterialToolbar;

public class DetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvAuthor, tvDescription;
    private ImageView imgBook; // <- on garde une référence

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Toolbar avec flèche retour
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Références aux vues
        tvTitle = findViewById(R.id.tvTitle);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvDescription = findViewById(R.id.tvDescription);
        imgBook = findViewById(R.id.imgBookDetail);

        // Récupère l’ID du livre passé en extra
        long id = getIntent().getLongExtra(MainActivity.EXTRA_BOOK_ID, -1);
        if (id == -1) {
            Toast.makeText(this, "Livre introuvable", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Charger les infos du livre depuis Room (thread IO)
        ExecutorsProvider.io().execute(() -> {
            Book book = AppDatabase.getInstance(getApplicationContext())
                    .bookDao().getById(id);

            runOnUiThread(() -> {
                if (book != null) {
                    tvTitle.setText(book.title);
                    tvAuthor.setText(book.author);
                    tvDescription.setText(book.description);

                    // Afficher l'image si fournie (imageRes = nom dans res/drawable)
                    if (book.imageRes != null) {
                        int resId = getResources().getIdentifier(
                                book.imageRes, "drawable", getPackageName());
                        if (resId != 0) {
                            imgBook.setImageResource(resId);
                        } else {
                            // optionnel: placeholder
                            // imgBook.setImageResource(R.drawable.ic_launcher_foreground);
                        }
                    }
                } else {
                    Toast.makeText(this, "Livre introuvable", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });
    }
}
