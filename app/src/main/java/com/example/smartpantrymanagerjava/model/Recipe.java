package com.example.smartpantrymanagerjava.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Model representing a recipe with detailed cooking instructions and required ingredients.
 */
public class Recipe {
    private long id;
    private String title;
    private String description;
    private String category;
    private int prepTimeMinutes;
    private int cookTimeMinutes;
    private String instructions;
    private List<RecipeIngredient> ingredients;

    public Recipe() {
        this.ingredients = new ArrayList<>();
    }

    public Recipe(long id, String title, String description, String category,
                  int prepTimeMinutes, int cookTimeMinutes, String instructions,
                  List<RecipeIngredient> ingredients) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.prepTimeMinutes = prepTimeMinutes;
        this.cookTimeMinutes = cookTimeMinutes;
        this.instructions = instructions;
        this.ingredients = ingredients != null ? ingredients : new ArrayList<>();
    }

    public Recipe(String title, String description, String category,
                  int prepTimeMinutes, int cookTimeMinutes, String instructions,
                  List<RecipeIngredient> ingredients) {
        this(-1, title, description, category, prepTimeMinutes, cookTimeMinutes, instructions, ingredients);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getPrepTimeMinutes() {
        return prepTimeMinutes;
    }

    public void setPrepTimeMinutes(int prepTimeMinutes) {
        this.prepTimeMinutes = prepTimeMinutes;
    }

    public int getCookTimeMinutes() {
        return cookTimeMinutes;
    }

    public void setCookTimeMinutes(int cookTimeMinutes) {
        this.cookTimeMinutes = cookTimeMinutes;
    }

    public int getTotalTimeMinutes() {
        return prepTimeMinutes + cookTimeMinutes;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<RecipeIngredient> ingredients) {
        this.ingredients = ingredients != null ? ingredients : new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recipe recipe = (Recipe) o;
        return id == recipe.id &&
                prepTimeMinutes == recipe.prepTimeMinutes &&
                cookTimeMinutes == recipe.cookTimeMinutes &&
                Objects.equals(title, recipe.title) &&
                Objects.equals(description, recipe.description) &&
                Objects.equals(category, recipe.category) &&
                Objects.equals(instructions, recipe.instructions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, category, prepTimeMinutes, cookTimeMinutes, instructions);
    }
}
