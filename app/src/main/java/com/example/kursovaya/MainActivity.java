package com.example.kursovaya;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements BookAdapter.OnDeleteClickListener {

    private Realm realm;
    private RecyclerView recyclerView;
    private BookAdapter bookAdapter;
    private Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        realm = Realm.getDefaultInstance();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddBookDialog();
            }
        });

        loadBooks();
    }

    private void loadBooks() {
        RealmResults<Book> books = realm.where(Book.class).findAll();
        if (bookAdapter == null) {
            bookAdapter = new BookAdapter(realm.copyFromRealm(books), this);
            recyclerView.setAdapter(bookAdapter);
        } else {
            bookAdapter.updateBooks(realm.copyFromRealm(books));
        }
    }

    private void showAddBookDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_book, null);
        builder.setView(dialogView);

        EditText editTitle = dialogView.findViewById(R.id.editTitle);
        EditText editAuthor = dialogView.findViewById(R.id.editAuthor);
        EditText editRackNumber = dialogView.findViewById(R.id.editRackNumber);
        EditText editShelfNumber = dialogView.findViewById(R.id.editShelfNumber);
        EditText editVendorCode = dialogView.findViewById(R.id.editVendorCode);

        builder.setTitle("Добавить книгу")
                .setPositiveButton("Сохранить", null) // Передаем null, чтобы переопределить поведение позже
                .setNegativeButton("Отмена", (dialog, id) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String title = editTitle.getText().toString();
            String author = editAuthor.getText().toString();
            String rackNumberStr = editRackNumber.getText().toString();
            String shelfNumberStr = editShelfNumber.getText().toString();
            String vendorCodeStr = editVendorCode.getText().toString();

            if (TextUtils.isEmpty(title) || TextUtils.isEmpty(author) ||
                    TextUtils.isEmpty(rackNumberStr) || TextUtils.isEmpty(shelfNumberStr) || TextUtils.isEmpty(vendorCodeStr)) {
                Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            } else {
                int rackNumber = Integer.parseInt(rackNumberStr);
                int shelfNumber = Integer.parseInt(shelfNumberStr);
                int vendorCode = Integer.parseInt(vendorCodeStr);

                addBook(title, author, rackNumber, shelfNumber, vendorCode);
                dialog.dismiss();
            }
        });
    }

    private void addBook(String title, String author, int rackNumber, int shelfNumber, int vendorCode) {
        realm.executeTransactionAsync(realm -> {
            Book book = realm.createObject(Book.class, vendorCode);
            book.setTitle(title);
            book.setAuthor(author);
            book.setRackNumber(rackNumber);
            book.setShelfNumber(shelfNumber);
        }, this::loadBooks);
    }

    @Override
    public void onDeleteClick(Book book) {
        realm.executeTransactionAsync(realm -> {
            Book bookToDelete = realm.where(Book.class).equalTo("vendorCode", book.getVendorCode()).findFirst();
            if (bookToDelete != null) {
                bookToDelete.deleteFromRealm();
            }
        }, this::loadBooks);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
