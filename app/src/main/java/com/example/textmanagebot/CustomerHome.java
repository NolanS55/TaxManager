package com.example.textmanagebot;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.example.textmanagebot.database.AppDatabase;
import com.example.textmanagebot.database.User;

import org.w3c.dom.Text;

public class CustomerHome extends AppCompatActivity {
    private EditText editTextName, editTextPhone, editTextCity, editTextPassword;
    private TextView textViewStatusValue;
    private Button buttonSave;
    private AppDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextCity = findViewById(R.id.editTextCity);
        editTextPassword = findViewById(R.id.editTextPassword);
        textViewStatusValue = findViewById(R.id.textViewStatusValue);
        buttonSave = findViewById(R.id.buttonSave);

        Intent curr = getIntent();
        String email = curr.getStringExtra("email");
        String password = curr.getStringExtra("password");

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "tax_filing_db").build();



        new Thread(() -> {
            User user = db.userDao().getUserByCredentials(email, password);
            editTextName.setText(user.getName());
            editTextPhone.setText(user.getPhone());
            editTextCity.setText(user.getAddress());
            editTextPassword.setText(user.getPassword());

        }).start();

        buttonSave.setOnClickListener(v -> {
            new Thread(() -> {
                User user = db.userDao().getUserByCredentials(email,password);
                String updatedName = editTextName.getText().toString();
                String updatedPhone = editTextPhone.getText().toString();
                String updatedCity = editTextCity.getText().toString();
                String updatedPassword = editTextPassword.getText().toString();

                user.setName(updatedName);
                user.setPhone(updatedPhone);
                user.setAddress(updatedCity);
                user.setPassword(updatedPassword);

                db.userDao().update(user);

                Intent intent = new Intent(CustomerHome.this, LoginActivity.class);
                startActivity(intent);
            }).start();
        });
    }
}