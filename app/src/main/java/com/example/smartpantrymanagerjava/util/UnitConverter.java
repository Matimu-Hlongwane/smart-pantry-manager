package com.example.smartpantrymanagerjava.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles unit normalizations and conversions for weight, volume, and discrete units.
 */
public class UnitConverter {

    public enum Dimension {
        WEIGHT,
        VOLUME,
        COUNT,
        UNKNOWN
    }

    private static final Map<String, Double> WEIGHT_FACTORS = new HashMap<>();
    private static final Map<String, Double> VOLUME_FACTORS = new HashMap<>();

    static {
        // Base weight unit = Gram (g)
        WEIGHT_FACTORS.put("g", 1.0);
        WEIGHT_FACTORS.put("gram", 1.0);
        WEIGHT_FACTORS.put("grams", 1.0);
        WEIGHT_FACTORS.put("kg", 1000.0);
        WEIGHT_FACTORS.put("kilogram", 1000.0);
        WEIGHT_FACTORS.put("kilograms", 1000.0);
        WEIGHT_FACTORS.put("mg", 0.001);
        WEIGHT_FACTORS.put("milligram", 0.001);
        WEIGHT_FACTORS.put("milligrams", 0.001);
        WEIGHT_FACTORS.put("oz", 28.3495);
        WEIGHT_FACTORS.put("ounce", 28.3495);
        WEIGHT_FACTORS.put("ounces", 28.3495);
        WEIGHT_FACTORS.put("lb", 453.592);
        WEIGHT_FACTORS.put("lbs", 453.592);
        WEIGHT_FACTORS.put("pound", 453.592);
        WEIGHT_FACTORS.put("pounds", 453.592);

        // Base volume unit = Milliliter (ml)
        VOLUME_FACTORS.put("ml", 1.0);
        VOLUME_FACTORS.put("milliliter", 1.0);
        VOLUME_FACTORS.put("milliliters", 1.0);
        VOLUME_FACTORS.put("millilitre", 1.0);
        VOLUME_FACTORS.put("millilitres", 1.0);
        VOLUME_FACTORS.put("l", 1000.0);
        VOLUME_FACTORS.put("liter", 1000.0);
        VOLUME_FACTORS.put("liters", 1000.0);
        VOLUME_FACTORS.put("litre", 1000.0);
        VOLUME_FACTORS.put("litres", 1000.0);
        VOLUME_FACTORS.put("tsp", 5.0);
        VOLUME_FACTORS.put("teaspoon", 5.0);
        VOLUME_FACTORS.put("teaspoons", 5.0);
        VOLUME_FACTORS.put("tbsp", 15.0);
        VOLUME_FACTORS.put("tablespoon", 15.0);
        VOLUME_FACTORS.put("tablespoons", 15.0);
        VOLUME_FACTORS.put("cup", 240.0);
        VOLUME_FACTORS.put("cups", 240.0);
        VOLUME_FACTORS.put("fl oz", 29.5735);
        VOLUME_FACTORS.put("floz", 29.5735);
    }

    public static String normalizeUnit(String unit) {
        if (unit == null) return "";
        String cleaned = unit.trim().toLowerCase();

        if (WEIGHT_FACTORS.containsKey(cleaned)) {
            if (cleaned.startsWith("k")) return "kg";
            if (cleaned.startsWith("m")) return "mg";
            if (cleaned.startsWith("o")) return "oz";
            if (cleaned.startsWith("l") || cleaned.startsWith("p")) return "lb";
            return "g";
        }

        if (VOLUME_FACTORS.containsKey(cleaned)) {
            if (cleaned.equals("l") || cleaned.startsWith("lit")) return "l";
            if (cleaned.startsWith("tsp") || cleaned.startsWith("teaspoon")) return "tsp";
            if (cleaned.startsWith("tbsp") || cleaned.startsWith("tablespoon")) return "tbsp";
            if (cleaned.startsWith("cup")) return "cup";
            if (cleaned.contains("fl")) return "fl oz";
            return "ml";
        }

        if (cleaned.equals("pc") || cleaned.equals("pcs") || cleaned.equals("piece") || cleaned.equals("pieces")
                || cleaned.equals("unit") || cleaned.equals("units") || cleaned.equals("item") || cleaned.equals("items")) {
            return "pcs";
        }

        if (cleaned.contains("clove") || cleaned.contains("slice") || cleaned.contains("head") || cleaned.contains("pinch")) {
            return cleaned;
        }

        return cleaned;
    }

    public static Dimension getDimension(String unit) {
        String norm = normalizeUnit(unit);
        String cleaned = unit != null ? unit.trim().toLowerCase() : "";

        if (WEIGHT_FACTORS.containsKey(cleaned) || WEIGHT_FACTORS.containsKey(norm)) {
            return Dimension.WEIGHT;
        }
        if (VOLUME_FACTORS.containsKey(cleaned) || VOLUME_FACTORS.containsKey(norm)) {
            return Dimension.VOLUME;
        }
        if (norm.equals("pcs") || cleaned.equals("pcs") || cleaned.equals("pc") || cleaned.equals("piece") || cleaned.equals("pieces")
                || cleaned.contains("clove") || cleaned.contains("slice") || cleaned.contains("head") || cleaned.contains("pinch")) {
            return Dimension.COUNT;
        }
        return Dimension.UNKNOWN;
    }

    /**
     * Converts a given value from sourceUnit to targetUnit.
     * Returns Double.NaN if conversion is impossible (incompatible dimensions).
     */
    public static double convert(double value, String sourceUnit, String targetUnit) {
        if (sourceUnit == null || targetUnit == null) {
            return Double.NaN;
        }

        String srcClean = sourceUnit.trim().toLowerCase();
        String tgtClean = targetUnit.trim().toLowerCase();

        if (srcClean.equals(tgtClean)) {
            return value;
        }

        String srcNorm = normalizeUnit(sourceUnit);
        String tgtNorm = normalizeUnit(targetUnit);

        if (srcNorm.equals(tgtNorm)) {
            return value;
        }

        Dimension srcDim = getDimension(sourceUnit);
        Dimension tgtDim = getDimension(targetUnit);

        if (srcDim != tgtDim || srcDim == Dimension.UNKNOWN) {
            return Double.NaN;
        }

        if (srcDim == Dimension.WEIGHT) {
            Double srcFactor = WEIGHT_FACTORS.get(srcClean);
            if (srcFactor == null) srcFactor = WEIGHT_FACTORS.get(srcNorm);
            Double tgtFactor = WEIGHT_FACTORS.get(tgtClean);
            if (tgtFactor == null) tgtFactor = WEIGHT_FACTORS.get(tgtNorm);

            if (srcFactor != null && tgtFactor != null) {
                double baseValue = value * srcFactor; // in grams
                return baseValue / tgtFactor;
            }
        } else if (srcDim == Dimension.VOLUME) {
            Double srcFactor = VOLUME_FACTORS.get(srcClean);
            if (srcFactor == null) srcFactor = VOLUME_FACTORS.get(srcNorm);
            Double tgtFactor = VOLUME_FACTORS.get(tgtClean);
            if (tgtFactor == null) tgtFactor = VOLUME_FACTORS.get(tgtNorm);

            if (srcFactor != null && tgtFactor != null) {
                double baseValue = value * srcFactor; // in ml
                return baseValue / tgtFactor;
            }
        } else if (srcDim == Dimension.COUNT) {
            return value;
        }

        return Double.NaN;
    }

    /**
     * Checks whether two units can be legally converted.
     */
    public static boolean areCompatible(String unit1, String unit2) {
        if (unit1 == null || unit2 == null) return false;
        if (unit1.equalsIgnoreCase(unit2)) return true;
        Dimension d1 = getDimension(unit1);
        Dimension d2 = getDimension(unit2);
        return d1 != Dimension.UNKNOWN && d1 == d2;
    }
}
