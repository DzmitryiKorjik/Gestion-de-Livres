package com.example.myapplication.ui.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.AppDatabase;
import com.example.myapplication.data.Book;
import com.example.myapplication.ui.adapter.BookAdapter;
import com.example.myapplication.ui.detail.DetailActivity;
import com.example.myapplication.ui.login.LoginActivity;
import com.example.myapplication.util.ExecutorsProvider;
import com.example.myapplication.util.ImageUtils;    // <-- util Base64 (bitmapToBase64/base64ToBitmap)
import com.example.myapplication.util.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_BOOK_ID = "EXTRA_BOOK_ID";

    private BookAdapter adapter;
    private SessionManager session;

    // Image choisie pour l’ajout (encodée en Base64)
    private String pendingImageBase64 = null;

    // Sélecteur d'image (galerie)
    private final ActivityResultLauncher<String> pickImage =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    Bitmap bmp = loadBitmapFromUri(uri);
                    if (bmp != null) {
                        pendingImageBase64 = ImageUtils.bitmapToBase64(bmp);
                    }
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionManager(this);

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Sous-titre = email connecté (depuis la session)
        String email = session.getLoggedInEmail();
        if (getSupportActionBar() != null && email != null) {
            getSupportActionBar().setSubtitle(email);
        }

        // RecyclerView + Adapter
        RecyclerView recycler = findViewById(R.id.recyclerBooks);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BookAdapter(
                book -> {
                    Intent intent = new Intent(this, DetailActivity.class);
                    intent.putExtra(EXTRA_BOOK_ID, book.id);
                    startActivity(intent);
                },
                (book, checked) -> ExecutorsProvider.io().execute(() -> {
                    AppDatabase.getInstance(getApplicationContext())
                            .bookDao().setRead(book.id, checked);
                    // Recharger la liste après la MAJ
                    List<Book> books = AppDatabase.getInstance(getApplicationContext())
                            .bookDao().getAll();
                    runOnUiThread(() -> adapter.submitList(books));
                })
        );
        recycler.setAdapter(adapter);

        reloadBooks();

        // FAB ajout
        FloatingActionButton fab = findViewById(R.id.fabAdd);
        if (fab != null) fab.setOnClickListener(v -> showAddDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Si DetailActivity modifie un livre, on reflète les changements
        reloadBooks();
    }

    private void reloadBooks() {
        ExecutorsProvider.io().execute(() -> {
            List<Book> books = AppDatabase.getInstance(getApplicationContext())
                    .bookDao().getAll();
            runOnUiThread(() -> adapter.submitList(books));
        });
    }

    private void showAddDialog() {
        final EditText etTitle  = new EditText(this);  etTitle.setHint("Titre");
        final EditText etAuthor = new EditText(this);  etAuthor.setHint("Auteur");
        final EditText etDesc   = new EditText(this);  etDesc.setHint("Description");

        // Bouton pour choisir une image
        Button btnPick = new Button(this);
        btnPick.setText("Choisir une image");
        btnPick.setOnClickListener(v -> pickImage.launch("image/*"));

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (getResources().getDisplayMetrics().density * 16);
        layout.setPadding(pad, pad, pad, 0);
        layout.addView(etTitle);
        layout.addView(etAuthor);
        layout.addView(etDesc);
        layout.addView(btnPick);

        // reset l’image en attente à chaque ouverture du dialogue
        pendingImageBase64 = null;

        new AlertDialog.Builder(this)
                .setTitle("Ajouter un livre")
                .setView(layout)
                .setPositiveButton("Ajouter", (d, w) -> ExecutorsProvider.io().execute(() -> {
                    String t  = etTitle.getText().toString().trim();
                    String a  = etAuthor.getText().toString().trim();
                    String ds = etDesc.getText().toString().trim();
                    if (!t.isEmpty()) {
                        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                        Book book;
                        if (pendingImageBase64 != null) {
                            // Constructeur pratique si tu l’as ajouté (Book.withBase64)
                            // Sinon: book = new Book(t, a, ds, null); book.imageBase64 = pendingImageBase64;
                            book = Book.withBase64(t, a, ds, pendingImageBase64);
                        } else {
                            book = new Book(t, a, ds, "book_placeholder");
                        }
                        db.bookDao().insert(book);

                        List<Book> books = db.bookDao().getAll();
                        runOnUiThread(() -> adapter.submitList(books));
                    }
                }))
                .setNegativeButton("Annuler", null)
                .show();
    }

    private Bitmap loadBitmapFromUri(Uri uri) {
        try {
            if (Build.VERSION.SDK_INT >= 28) {
                ImageDecoder.Source src = ImageDecoder.createSource(getContentResolver(), uri);
                return ImageDecoder.decodeBitmap(src);
            } else {
                @SuppressWarnings("deprecation")
                Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                return bmp;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // ------ Menu (déconnexion) ------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            session.clear();
            Intent i = new Intent(this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
