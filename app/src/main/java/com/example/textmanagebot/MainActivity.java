package com.example.textmanagebot;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.example.textmanagebot.database.AppDatabase;
import com.example.textmanagebot.database.User;

public class MainActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPhone, etAddress, etPassword;
    private Spinner roleSpinner;
    private Button btnRegister, btnLogin;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        etAddress = findViewById(R.id.et_address);
        etPassword = findViewById(R.id.et_password);
        roleSpinner = findViewById(R.id.role_spinner);
        btnRegister = findViewById(R.id.btn_register);
        btnLogin = findViewById(R.id.btn_loginReg);
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "tax_filing_db").build();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.roles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        btnRegister.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String email = etEmail.getText().toString();
            String phone = etPhone.getText().toString();
            String address = etAddress.getText().toString();
            String password = etPassword.getText().toString();
            String role = roleSpinner.getSelectedItem().toString();

            // Create a new User object
            User newUser = new User(name, email, phone, address, password, role, "AWAITED");

            // Save the user to Room DB
            new Thread(() -> db.userDao().insert(newUser)).start();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}