package com.example.kursovaya;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.realm.Realm;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private List<Book> books;
    private OnDeleteClickListener onDeleteClickListener;

    public BookAdapter(List<Book> books, OnDeleteClickListener onDeleteClickListener) {
        this.books = books;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = books.get(position);
        holder.titleTextView.setText(book.getTitle());
        holder.authorTextView.setText(book.getAuthor());
        holder.rackNumberTextView.setText(String.valueOf(book.getRackNumber()));
        holder.shelfNumberTextView.setText(String.valueOf(book.getShelfNumber()));
        holder.vendorCodeTextView.setText(String.valueOf(book.getVendorCode()));

        holder.deleteButton.setOnClickListener(v -> onDeleteClickListener.onDeleteClick(book));
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public void updateBooks(List<Book> newBooks) {
        this.books = newBooks;
        notifyDataSetChanged();
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Book book);
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView authorTextView;
        TextView rackNumberTextView;
        TextView shelfNumberTextView;
        TextView vendorCodeTextView;
        Button deleteButton;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            authorTextView = itemView.findViewById(R.id.authorTextView);
            rackNumberTextView = itemView.findViewById(R.id.rackNumberTextView);
            shelfNumberTextView = itemView.findViewById(R.id.shelfNumberTextView);
            vendorCodeTextView = itemView.findViewById(R.id.vendorCodeTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
