package com.example.smartpantrymanagerjava.model;

import java.util.Objects;

/**
 * Model representing an ingredient stored in the user's pantry.
 */
public class PantryItem {
    private long id;
    private String name;
    private double quantity;
    private String unit;
    private String expiryDate; // Format: YYYY-MM-DD (optional)

    public PantryItem() {
    }

    public PantryItem(long id, String name, double quantity, String unit, String expiryDate) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }

    public PantryItem(String name, double quantity, String unit, String expiryDate) {
        this(-1, name, quantity, unit, expiryDate);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PantryItem item = (PantryItem) o;
        return id == item.id &&
                Double.compare(item.quantity, quantity) == 0 &&
                Objects.equals(name, item.name) &&
                Objects.equals(unit, item.unit) &&
                Objects.equals(expiryDate, item.expiryDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, quantity, unit, expiryDate);
    }

    @Override
    public String toString() {
        return "PantryItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", unit='" + unit + '\'' +
                ", expiryDate='" + expiryDate + '\'' +
                '}';
    }
}
