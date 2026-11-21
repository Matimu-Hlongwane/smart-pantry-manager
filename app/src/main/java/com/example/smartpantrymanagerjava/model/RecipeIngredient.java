package com.example.smartpantrymanagerjava.model;

import java.util.Objects;

/**
 * Represents a required ingredient for a recipe.
 */
public class RecipeIngredient {
    private long id;
    private long recipeId;
    private String name;
    private double quantity;
    private String unit;

    public RecipeIngredient() {
    }

    public RecipeIngredient(long id, long recipeId, String name, double quantity, String unit) {
        this.id = id;
        this.recipeId = recipeId;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public RecipeIngredient(String name, double quantity, String unit) {
        this(-1, -1, name, quantity, unit);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(long recipeId) {
        this.recipeId = recipeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecipeIngredient item = (RecipeIngredient) o;
        return id == item.id &&
                recipeId == item.recipeId &&
                Double.compare(item.quantity, quantity) == 0 &&
                Objects.equals(name, item.name) &&
                Objects.equals(unit, item.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, recipeId, name, quantity, unit);
    }

    @Override
    public String toString() {
        return "RecipeIngredient{" +
                "id=" + id +
                ", recipeId=" + recipeId +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", unit='" + unit + '\'' +
                '}';
    }
}
