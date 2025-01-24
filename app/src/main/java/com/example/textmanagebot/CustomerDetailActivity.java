package com.example.textmanagebot;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.example.textmanagebot.database.AppDatabase;
import com.example.textmanagebot.database.User;

public class CustomerDetailActivity extends AppCompatActivity {

    private TextView tvName, tvProcessStatus;
    private Spinner spinnerStatus;
    private Button btnUpdateStatus;
    private AppDatabase db;
    private String customerEmail, customerPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvName = findViewById(R.id.tv_name);
        tvProcessStatus = findViewById(R.id.tv_process_status);
        spinnerStatus = findViewById(R.id.spinner_status);
        btnUpdateStatus = findViewById(R.id.btn_update_status);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "tax_filing_db").build();

        customerEmail = getIntent().getStringExtra("email");
        customerPassword = getIntent().getStringExtra("password");
        // Set up the spinner with process status options
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"AWAITED", "FAILEDTOREACH", "ONBOARDED", "INPROCESS", "COMPLETED", "DENIED"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);

        new Thread(() -> {
            User customer = db.userDao().getUserByCredentials(customerEmail,customerPassword);
            runOnUiThread(() -> {
                tvName.setText(customer.name);
                tvProcessStatus.setText(customer.processStatus);
                int statusIndex = adapter.getPosition(customer.processStatus);
                spinnerStatus.setSelection(statusIndex);
            });
        }).start();

        btnUpdateStatus.setOnClickListener(v -> {
            String newStatus = spinnerStatus.getSelectedItem().toString();

            new Thread(() -> {
                User customer = db.userDao().getUserByCredentials(customerEmail,customerPassword);
                customer.processStatus = newStatus;
                db.userDao().update(customer);
                Intent intent = new Intent(CustomerDetailActivity.this, HomeActivity.class);
                intent.putExtra("email", customer.email);
                intent.putExtra("password", customer.password);
                startActivity(intent);
            }).start();
        });
    }
}