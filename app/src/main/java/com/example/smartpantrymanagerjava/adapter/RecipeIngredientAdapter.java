package com.example.smartpantrymanagerjava.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanagerjava.R;
import com.example.smartpantrymanagerjava.model.MatchResult;
import com.example.smartpantrymanagerjava.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

public class RecipeIngredientAdapter extends RecyclerView.Adapter<RecipeIngredientAdapter.IngredientViewHolder> {

    private final Context context;
    private List<MatchResult.RecipeIngredientStatus> statuses;

    public RecipeIngredientAdapter(Context context) {
        this.context = context;
        this.statuses = new ArrayList<>();
    }

    public void setStatuses(List<MatchResult.RecipeIngredientStatus> statuses) {
        this.statuses = statuses != null ? statuses : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe_ingredient, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        MatchResult.RecipeIngredientStatus status = statuses.get(position);
        RecipeIngredient ing = status.getRequiredIngredient();

        holder.tvName.setText(ing.getName());
        holder.tvStatusMsg.setText(status.getMessage());

        if (status.isSufficient()) {
            holder.ivStatus.setImageResource(R.drawable.ic_check_circle);
            holder.tvStatusMsg.setTextColor(ContextCompat.getColor(context, R.color.can_cook_green));
        } else {
            holder.ivStatus.setImageResource(R.drawable.ic_warning);
            holder.tvStatusMsg.setTextColor(ContextCompat.getColor(context, R.color.missing_red));
        }
    }

    @Override
    public int getItemCount() {
        return statuses.size();
    }

    static class IngredientViewHolder extends RecyclerView.ViewHolder {
        ImageView ivStatus;
        TextView tvName, tvStatusMsg;

        IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            ivStatus = itemView.findViewById(R.id.iv_ingredient_status);
            tvName = itemView.findViewById(R.id.tv_ingredient_name);
            tvStatusMsg = itemView.findViewById(R.id.tv_ingredient_status_msg);
        }
    }
}
