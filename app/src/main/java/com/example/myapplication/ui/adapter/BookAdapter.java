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

/**
 * Adaptateur pour afficher une liste de livres dans un RecyclerView.
 * Il gère la création et l’association des vues pour chaque élément de la liste.
 */
public class BookAdapter extends RecyclerView.Adapter<BookViewHolder> {

    /**
     * Interface de rappel pour gérer les clics sur un élément du RecyclerView.
     */
    public interface OnItemClickListener {
        void onItemClick(Book book);
    }

    /**
     * Interface de rappel pour gérer les changements d’état d’un bouton (lecture cochée / non cochée).
     */
    public interface OnCheckChangedListener {
        void onCheckedChanged(Book book, boolean checked);
    }

    // Liste interne des livres affichés dans le RecyclerView
    private final List<Book> data = new ArrayList<>();

    // Listeners pour les interactions utilisateur
    private final OnItemClickListener clickListener;
    private final OnCheckChangedListener checkListener;

    /**
     * Constructeur pour initialiser l’adaptateur avec ses listeners.
     *
     * @param clickListener Listener de clic sur un élément du RecyclerView.
     * @param checkListener Listener pour le changement d’état du bouton "lu/non lu".
     */
    public BookAdapter(OnItemClickListener clickListener,
                       OnCheckChangedListener checkListener) {
        this.clickListener = clickListener;
        this.checkListener = checkListener;
    }

    /**
     * Remplace la liste actuelle des livres par une nouvelle et actualise l’affichage.
     *
     * @param books Nouvelle liste de livres à afficher.
     */
    public void submitList(List<Book> books) {
        data.clear(); // vide la liste actuelle
        if (books != null) data.addAll(books); // ajoute les nouveaux éléments
        notifyDataSetChanged(); // notifie le RecyclerView que les données ont changé
    }

    /**
     * Crée une nouvelle instance de BookViewHolder lorsque nécessaire.
     *
     * @param parent Le parent où la vue sera attachée.
     * @param viewType Type de vue (ici, unique).
     * @return Un nouveau BookViewHolder prêt à être lié à un livre.
     */
    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Gonfle la mise en page XML d’un élément de livre
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    /**
     * Lie un livre spécifique à un ViewHolder donné.
     *
     * @param holder Le ViewHolder contenant les vues à remplir.
     * @param position Position du livre dans la liste.
     */
    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = data.get(position); // récupère l’objet à la position donnée

        // Lie les données du livre à la vue et gère l’état du bouton
        holder.bind(book, (btn, isChecked) -> {
            book.read = isChecked; // met à jour la propriété "read" de l’objet Book
            if (checkListener != null) checkListener.onCheckedChanged(book, isChecked);
        });

        // Gère le clic sur l’ensemble de l’élément
        holder.itemView.setOnClickListener(v -> clickListener.onItemClick(book));
    }

    /**
     * Retourne le nombre total d’éléments à afficher dans la liste.
     *
     * @return Taille de la liste des livres.
     */
    @Override
    public int getItemCount() {
        return data.size();
    }
}
