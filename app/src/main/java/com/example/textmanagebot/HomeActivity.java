package com.example.textmanagebot;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.room.Room;

import com.example.textmanagebot.adapter.CustomerAdapter;
import com.example.textmanagebot.database.AppDatabase;
import com.example.textmanagebot.database.User;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CustomerAdapter customerAdapter;
    private AppDatabase db;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recycler_view);
        btnLogout = findViewById(R.id.btn_logout);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "tax_filing_db").build();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        customerAdapter = new CustomerAdapter(null, customer -> {
            Intent intent = new Intent(HomeActivity.this, CustomerDetailActivity.class);
            intent.putExtra("email", customer.email);
            intent.putExtra("password", customer.password);
            startActivity(intent);

        });

        recyclerView.setAdapter(customerAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                User customer = customerAdapter.getCustomerAtPosition(position);

                new AlertDialog.Builder(HomeActivity.this)
                        .setTitle("Delete Customer")
                        .setMessage("Are you sure you want to delete this customer?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            new Thread(() -> {
                                db.userDao().delete(customer);
                            }).start();
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            customerAdapter.notifyItemChanged(position);
                        })
                        .show();
            }

        }).attachToRecyclerView(recyclerView);

        // Observe all customers and update RecyclerView when data changes
        LiveData<List<User>> allCustomers = db.userDao().getAllUsers();
        allCustomers.observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                customerAdapter.customers = users;
                customerAdapter.notifyDataSetChanged();
            }
        });



        btnLogout.setOnClickListener(v -> {
            // Log out and navigate back to the login screen
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}