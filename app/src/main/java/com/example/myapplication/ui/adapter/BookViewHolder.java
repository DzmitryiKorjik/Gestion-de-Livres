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

public class BookViewHolder extends RecyclerView.ViewHolder {
    private final TextView tvTitle;
    private final TextView tvAuthor;
    private final CheckBox cbRead;
    private final ImageView imgBook;

    public BookViewHolder(@NonNull View itemView) {
        super(itemView);
        tvTitle = itemView.findViewById(R.id.tvTitle);
        tvAuthor = itemView.findViewById(R.id.tvAuthor);
        cbRead  = itemView.findViewById(R.id.cbRead);
        imgBook = itemView.findViewById(R.id.imgBook); // <- dans le constructeur
    }

    public void bind(Book book, CompoundButton.OnCheckedChangeListener listener) {
        tvTitle.setText(book.title);
        tvAuthor.setText(book.author);

        // Charger l'image depuis res/drawable via le nom stocké dans book.imageRes
        int resId = itemView.getContext()
                .getResources()
                .getIdentifier(book.imageRes, "drawable", itemView.getContext().getPackageName());
        if (resId != 0) {
            imgBook.setImageResource(resId);
        } else {
            imgBook.setImageResource(R.drawable.book_1984);
        }

        // gérer la checkbox sans déclencher de callback pendant le bind
        cbRead.setOnCheckedChangeListener(null);
        cbRead.setChecked(book.read);
        cbRead.setOnCheckedChangeListener(listener);
    }
}