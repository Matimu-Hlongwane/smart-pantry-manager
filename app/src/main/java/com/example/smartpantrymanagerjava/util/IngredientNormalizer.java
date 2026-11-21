package com.example.smartpantrymanagerjava.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Normalizes ingredient names for reliable comparison and matching.
 */
public class IngredientNormalizer {

    private static final Map<String, String> SYNONYMS = new HashMap<>();

    static {
        SYNONYMS.put("tomatoes", "tomato");
        SYNONYMS.put("eggs", "egg");
        SYNONYMS.put("onions", "onion");
        SYNONYMS.put("carrots", "carrot");
        SYNONYMS.put("apples", "apple");
        SYNONYMS.put("potatoes", "potato");
        SYNONYMS.put("bananas", "banana");
        SYNONYMS.put("cloves garlic", "garlic");
        SYNONYMS.put("garlic cloves", "garlic");
        SYNONYMS.put("garlic clove", "garlic");
        SYNONYMS.put("scallions", "green onion");
        SYNONYMS.put("spring onion", "green onion");
        SYNONYMS.put("spring onions", "green onion");
    }

    /**
     * Normalizes an ingredient name string by trimming, lowercasing, and resolving plurals/synonyms.
     */
    public static String normalize(String name) {
        if (name == null) {
            return "";
        }
        String cleaned = name.trim().toLowerCase();

        // Check explicit synonym map
        if (SYNONYMS.containsKey(cleaned)) {
            return SYNONYMS.get(cleaned);
        }

        // Basic plural handling
        if (cleaned.endsWith("es") && cleaned.length() > 3) {
            String singular = cleaned.substring(0, cleaned.length() - 2);
            if (SYNONYMS.containsKey(singular)) {
                return SYNONYMS.get(singular);
            }
        } else if (cleaned.endsWith("s") && !cleaned.endsWith("ss") && cleaned.length() > 2) {
            String singular = cleaned.substring(0, cleaned.length() - 1);
            if (SYNONYMS.containsKey(singular)) {
                return SYNONYMS.get(singular);
            }
            return singular;
        }

        return cleaned;
    }
}
