package com.example.myapplication.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.AppDatabase;
import com.example.myapplication.data.Book;
import com.example.myapplication.ui.detail.DetailActivity;
import com.example.myapplication.ui.login.LoginActivity;
import com.example.myapplication.ui.adapter.BookAdapter;
import com.example.myapplication.util.ExecutorsProvider;
import com.example.myapplication.util.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_USER_EMAIL = "EXTRA_USER_EMAIL";
    public static final String EXTRA_BOOK_ID   = "EXTRA_BOOK_ID";

    private BookAdapter adapter;
    private SessionManager session;

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

        RecyclerView recycler = findViewById(R.id.recyclerBooks);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BookAdapter(
                book -> {
                    Intent intent = new Intent(this, DetailActivity.class);
                    intent.putExtra(EXTRA_BOOK_ID, book.id);
                    startActivity(intent);
                },
                (book, checked) -> ExecutorsProvider.io().execute(() ->
                        AppDatabase.getInstance(getApplicationContext())
                                .bookDao().setRead(book.id, checked)
                )
        );
        recycler.setAdapter(adapter);

        reloadBooks();

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        if (fab != null) fab.setOnClickListener(v -> showAddDialog());
    }

    private void reloadBooks() {
        ExecutorsProvider.io().execute(() -> {
            List<Book> books = AppDatabase.getInstance(getApplicationContext())
                    .bookDao().getAll();
            runOnUiThread(() -> adapter.submitList(books));
        });
    }

    private void showAddDialog() {
        final EditText etTitle = new EditText(this);  etTitle.setHint("Titre");
        final EditText etAuthor = new EditText(this); etAuthor.setHint("Auteur");
        final EditText etDesc = new EditText(this);   etDesc.setHint("Description");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (getResources().getDisplayMetrics().density * 16);
        layout.setPadding(pad, pad, pad, 0);
        layout.addView(etTitle);
        layout.addView(etAuthor);
        layout.addView(etDesc);

        new AlertDialog.Builder(this)
                .setTitle("Ajouter un livre")
                .setView(layout)
                .setPositiveButton("Ajouter", (d, w) -> ExecutorsProvider.io().execute(() -> {
                    String t = etTitle.getText().toString().trim();
                    String a = etAuthor.getText().toString().trim();
                    String ds = etDesc.getText().toString().trim();
                    if (!t.isEmpty()) {
                        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                        db.bookDao().insert(new Book(t, a, ds, "book_placeholder"));
                        List<Book> books = db.bookDao().getAll();
                        runOnUiThread(() -> adapter.submitList(books));
                    }
                }))
                .setNegativeButton("Annuler", null)
                .show();
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
