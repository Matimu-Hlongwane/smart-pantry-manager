package com.example.smartpantrymanagerjava.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanagerjava.R;
import com.example.smartpantrymanagerjava.model.MatchResult;
import com.example.smartpantrymanagerjava.model.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    public interface OnRecipeClickListener {
        void onRecipeClick(MatchResult matchResult);
    }

    private final Context context;
    private List<MatchResult> matchResults;
    private final OnRecipeClickListener listener;

    public RecipeAdapter(Context context, OnRecipeClickListener listener) {
        this.context = context;
        this.matchResults = new ArrayList<>();
        this.listener = listener;
    }

    public void setMatchResults(List<MatchResult> matchResults) {
        this.matchResults = matchResults != null ? matchResults : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        MatchResult result = matchResults.get(position);
        Recipe recipe = result.getRecipe();

        holder.tvTitle.setText(recipe.getTitle());
        holder.tvDescription.setText(recipe.getDescription());
        holder.tvCategory.setText(recipe.getCategory());

        String timeStr = String.format(Locale.US, "Total: %d min", recipe.getTotalTimeMinutes());
        holder.tvTime.setText(timeStr);

        if (result.isCanCook()) {
            holder.tvBadge.setText(R.string.badge_ready);
            holder.tvBadge.setBackgroundResource(R.drawable.bg_badge_success);
            holder.tvBadge.setTextColor(ContextCompat.getColor(context, R.color.can_cook_green));
        } else {
            String badgeText;
            if (!result.getMissingIngredientNames().isEmpty()) {
                badgeText = String.format(Locale.US, "Missing %d item(s)", result.getMissingIngredientNames().size());
            } else {
                badgeText = String.format(Locale.US, "Insufficient %d item(s)", result.getInsufficientIngredientDetails().size());
            }
            holder.tvBadge.setText(badgeText);
            holder.tvBadge.setBackgroundResource(R.drawable.bg_badge_warning);
            holder.tvBadge.setTextColor(ContextCompat.getColor(context, R.color.missing_red));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRecipeClick(result);
            }
        });
    }

    @Override
    public int getItemCount() {
        return matchResults.size();
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvTime, tvCategory, tvBadge;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_recipe_title);
            tvDescription = itemView.findViewById(R.id.tv_recipe_description);
            tvTime = itemView.findViewById(R.id.tv_recipe_time);
            tvCategory = itemView.findViewById(R.id.tv_recipe_category);
            tvBadge = itemView.findViewById(R.id.tv_match_badge);
        }
    }
}
