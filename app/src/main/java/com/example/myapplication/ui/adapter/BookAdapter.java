package com.example.myapplication.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.Book;
import com.example.myapplication.util.ImageUtils;

public class BookAdapter extends ListAdapter<Book, BookAdapter.BookViewHolder> {

    public interface OnBookClick { void onClick(Book book); }
    public interface OnReadToggle { void onToggle(Book book, boolean checked); }

    private final OnBookClick onBookClick;
    private final OnReadToggle onReadToggle;

    public BookAdapter(OnBookClick onBookClick, OnReadToggle onReadToggle) {
        super(DIFF);
        this.onBookClick = onBookClick;
        this.onReadToggle = onReadToggle;
    }

    private static final DiffUtil.ItemCallback<Book> DIFF = new DiffUtil.ItemCallback<Book>() {
        @Override public boolean areItemsTheSame(@NonNull Book a, @NonNull Book b) {
            return a.id == b.id;
        }
        @Override public boolean areContentsTheSame(@NonNull Book a, @NonNull Book b) {
            return a.id == b.id
                    && eq(a.title, b.title)
                    && eq(a.author, b.author)
                    && eq(a.description, b.description)
                    && a.read == b.read
                    && eq(a.imageRes, b.imageRes)
                    && eq(a.imageBase64, b.imageBase64);
        }
        private boolean eq(Object x, Object y) { return x == null ? y == null : x.equals(y); }
    };

    @NonNull @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = getItem(position);

        holder.tvTitle.setText(book.title != null ? book.title : "");
        holder.tvAuthor.setText(book.author != null ? book.author : "");
        holder.cbRead.setOnCheckedChangeListener(null);
        holder.cbRead.setChecked(book.read);

        // Image: seulement si l'ImageView existe dans le layout (évite NPE)
        if (holder.imgBook != null) {
            if (book.imageBase64 != null && !book.imageBase64.isEmpty()) {
                holder.imgBook.setImageBitmap(ImageUtils.base64ToBitmap(book.imageBase64));
            } else {
                String resName = (book.imageRes != null && !book.imageRes.isEmpty())
                        ? book.imageRes : "book_placeholder";
                int resId = holder.imgBook.getContext().getResources()
                        .getIdentifier(resName, "drawable",
                                holder.imgBook.getContext().getPackageName());
                if (resId != 0) {
                    holder.imgBook.setImageResource(resId);
                } else {
                    // Fallback système si ton drawable n’existe pas
                    holder.imgBook.setImageResource(android.R.drawable.ic_menu_report_image);
                }
            }
        }

        holder.itemView.setOnClickListener(v -> { if (onBookClick != null) onBookClick.onClick(book); });
        holder.cbRead.setOnCheckedChangeListener((b, checked) -> {
            if (onReadToggle != null) onReadToggle.onToggle(book, checked);
        });
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        final ImageView imgBook;
        final TextView tvTitle;
        final TextView tvAuthor;
        final CheckBox cbRead;

        BookViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBook = itemView.findViewById(R.id.imgBook); // <- unique ID attendu
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            cbRead = itemView.findViewById(R.id.cbRead);
        }
    }
}
