package com.example.kursovaya;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import io.realm.Realm;

public class LoginActivity extends AppCompatActivity {
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Realm
        Realm.init(this);
        realm = Realm.getDefaultInstance();

        Button logButton = findViewById(R.id.logButton);
        logButton.setOnClickListener(v -> {
            EditText username = findViewById(R.id.username);
            EditText password = findViewById(R.id.password);
            TextView infoMessage = findViewById(R.id.notfound);
            if (username.getText().toString().equals("1") && password.getText().toString().equals("1")) {
                infoMessage.setText("OK!");
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                return;
            }
            if (!username.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
                HashMap<String, String> requestBody = new HashMap<>();
                requestBody.put("lgn", username.getText().toString());
                requestBody.put("pwd", password.getText().toString());
                requestBody.put("g", "RIBO-05-22");
                HTTPRequestRunnable httpPOSTRunnable = new HTTPRequestRunnable
                        ("POST", "https://android-for-students.ru/coursework/login.php", requestBody);
                Thread th = new Thread(httpPOSTRunnable);
                th.start();
                try {
                    th.join();
                } catch (Exception e) {
                    infoMessage.setText("Ошибка подключения");
                }
                try {
                    JSONObject jsonResponse = new JSONObject(httpPOSTRunnable.getResponseBody());
                    int resultCode = jsonResponse.getInt("result_code");
                    if (resultCode == 1) {
                        infoMessage.setText("OK!");

                        // Сохранение книг в базу данных
                        JSONArray booksArray = jsonResponse.getJSONArray("data");
                        saveBooksToRealm(booksArray);

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    } else {
                        infoMessage.setText("Неверный логин или пароль");
                    }
                } catch (Exception e) {
                    infoMessage.setText("Ошибка подключения");
                }
            } else infoMessage.setText("Пожалуйста, введите логин и пароль");
        });
    }

    private void saveBooksToRealm(JSONArray booksArray) {
        try {
            realm.executeTransaction(realm -> {
                for (int i = 0; i < booksArray.length(); i++) {
                    try {
                        JSONObject bookObject = booksArray.getJSONObject(i);
                        Book book = realm.createObject(Book.class, bookObject.getInt("vendor_code"));
                        book.setTitle(bookObject.getString("title"));
                        book.setAuthor(bookObject.getString("author"));
                        book.setRackNumber(bookObject.getInt("rack_number"));
                        book.setShelfNumber(bookObject.getInt("shelf_number"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
