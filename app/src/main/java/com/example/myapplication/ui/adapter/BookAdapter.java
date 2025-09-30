package com.example.myapplication.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.Book;

import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookViewHolder> {

    public interface OnItemClickListener { void onItemClick(Book book); }
    public interface OnCheckChangedListener { void onCheckedChanged(Book book, boolean checked); }

    private final List<Book> data = new ArrayList<>();
    private final OnItemClickListener clickListener;
    private final OnCheckChangedListener checkListener;

    public BookAdapter(OnItemClickListener clickListener,
                       OnCheckChangedListener checkListener) {
        this.clickListener = clickListener;
        this.checkListener = checkListener;
    }

    public void submitList(List<Book> books) {
        data.clear();
        if (books != null) data.addAll(books);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = data.get(position);
        holder.bind(book, (btn, isChecked) -> {
            book.read = isChecked; // met à jour l'état local
            if (checkListener != null) checkListener.onCheckedChanged(book, isChecked);
        });
        holder.itemView.setOnClickListener(v -> clickListener.onItemClick(book));
    }

    @Override
    public int getItemCount() { return data.size(); }
}
