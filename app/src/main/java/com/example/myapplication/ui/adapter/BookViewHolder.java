package com.example.myapplication.ui.adapter;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.Book;

/**
 * ViewHolder pour afficher un livre dans le RecyclerView.
 */
public class BookViewHolder extends RecyclerView.ViewHolder {
    private final TextView tvTitle;   // Titre du livre
    private final TextView tvAuthor;  // Auteur du livre
    private final CheckBox cbRead;    // Case à cocher (lu / non lu)
    private final ImageView imgBook;  // Image du livre
    public ImageView imageView;

    public BookViewHolder(@NonNull View itemView) {
        super(itemView);
        // Liaison des vues du layout XML
        tvTitle = itemView.findViewById(R.id.tvTitle);
        tvAuthor = itemView.findViewById(R.id.tvAuthor);
        cbRead  = itemView.findViewById(R.id.cbRead);
        imgBook = itemView.findViewById(R.id.imgBook);
    }

    /**
     * Associe les données d’un livre aux vues.
     */
    public void bind(Book book, CompoundButton.OnCheckedChangeListener listener) {
        tvTitle.setText(book.title);
        tvAuthor.setText(book.author);

        // Charge l’image du livre à partir du nom de ressource
        int resId = itemView.getContext()
                .getResources()
                .getIdentifier(book.imageRes, "drawable", itemView.getContext().getPackageName());
        if (resId != 0) imgBook.setImageResource(resId);
        else imgBook.setImageResource(R.drawable.book_1984);

        // Gère la case à cocher sans duplication d’événements
        cbRead.setOnCheckedChangeListener(null);
        cbRead.setChecked(book.read);
        cbRead.setOnCheckedChangeListener(listener);
    }
}
