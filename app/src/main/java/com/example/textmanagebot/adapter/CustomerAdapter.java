package com.example.textmanagebot.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.textmanagebot.R;
import com.example.textmanagebot.database.User;

import java.util.ArrayList;
import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    public List<User> customers;
    private final OnItemClickListener listener;

    // Constructor
    public CustomerAdapter(List<User> customers, OnItemClickListener listener) {
        // Initialize the list as empty if it's null
        this.customers = customers != null ? customers : new ArrayList<>();
        filterNonAdminUsers();
        this.listener = listener;
    }

    private void filterNonAdminUsers() {
        List<User> filteredList = new ArrayList<>();
        for (User user : customers) {
            if (user.getRole().equals("Customer")) {
                filteredList.add(user);
            }
        }
        customers = filteredList;
    }

    // ViewHolder class
    public static class CustomerViewHolder extends RecyclerView.ViewHolder {
        TextView name, phone, city, status;

        public CustomerViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_name);
            phone = itemView.findViewById(R.id.tv_phone);
            city = itemView.findViewById(R.id.tv_city);
            status = itemView.findViewById(R.id.tv_status);
        }
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer, parent, false);
        return new CustomerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        User customer = customers.get(position);
        holder.name.setText(customer.getName());
        holder.phone.setText(customer.getPhone());
        holder.city.setText(customer.getAddress());
        holder.status.setText(customer.getProcessStatus());

        // Set status color
        switch (customer.getProcessStatus()) {
            case "AWAITED":
                holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.yellow));
                break;
            case "FAILEDTOREACH":
                holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.red_light));
                break;
            case "ONBOARDED":
                holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.green_light));
                break;
            case "INPROCESS":
                holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.green_mid));
                break;
            case "COMPLETED":
                holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.green_dark));
                break;
            case "DENIED":
                holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.red));
                break;
        }

        // Handle item click
        holder.itemView.setOnClickListener(v -> listener.onItemClick(customer));
    }

    @Override
    public int getItemCount() {
        return customers != null ? customers.size() : 0; // Avoid NullPointerException
    }

    public User getCustomerAtPosition(int position) {
        return customers.get(position);
    }

    // Interface to handle item clicks
    public interface OnItemClickListener {
        void onItemClick(User customer);
    }
}
