package com.example.smartpantrymanagerjava.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanagerjava.R;
import com.example.smartpantrymanagerjava.model.PantryItem;
import com.example.smartpantrymanagerjava.util.DateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    public interface OnPantryItemClickListener {
        void onEditClick(PantryItem item);
        void onDeleteClick(PantryItem item);
    }

    private final Context context;
    private List<PantryItem> items;
    private final OnPantryItemClickListener listener;

    public PantryAdapter(Context context, OnPantryItemClickListener listener) {
        this.context = context;
        this.items = new ArrayList<>();
        this.listener = listener;
    }

    public void setItems(List<PantryItem> items) {
        this.items = items != null ? items : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem item = items.get(position);
        holder.tvName.setText(item.getName());

        String qtyStr = String.format(Locale.US, "Quantity: %.1f %s", item.getQuantity(), item.getUnit());
        holder.tvQuantity.setText(qtyStr);

        String expiryStr = item.getExpiryDate();
        if (expiryStr != null && !expiryStr.trim().isEmpty()) {
            holder.tvExpiry.setVisibility(View.VISIBLE);
            if (DateUtils.isExpired(expiryStr)) {
                holder.tvExpiry.setText(String.format("EXPIRED (%s)", expiryStr));
                holder.tvExpiry.setTextColor(ContextCompat.getColor(context, R.color.missing_red));
            } else if (DateUtils.isExpiringSoon(expiryStr, 3)) {
                holder.tvExpiry.setText(String.format("Expiring Soon (%s)", expiryStr));
                holder.tvExpiry.setTextColor(ContextCompat.getColor(context, R.color.expiry_orange));
            } else {
                holder.tvExpiry.setText(String.format("Expires: %s", expiryStr));
                holder.tvExpiry.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
            }
        } else {
            holder.tvExpiry.setVisibility(View.GONE);
        }

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(item);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class PantryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQuantity, tvExpiry;
        ImageButton btnEdit, btnDelete;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_pantry_name);
            tvQuantity = itemView.findViewById(R.id.tv_pantry_quantity);
            tvExpiry = itemView.findViewById(R.id.tv_pantry_expiry);
            btnEdit = itemView.findViewById(R.id.btn_pantry_edit);
            btnDelete = itemView.findViewById(R.id.btn_pantry_delete);
        }
    }
}
