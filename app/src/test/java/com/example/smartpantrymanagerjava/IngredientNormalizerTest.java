package com.example.smartpantrymanagerjava;

import com.example.smartpantrymanagerjava.util.IngredientNormalizer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IngredientNormalizerTest {

    @Test
    public void testNormalization() {
        assertEquals("tomato", IngredientNormalizer.normalize("Tomatoes"));
        assertEquals("egg", IngredientNormalizer.normalize("  Eggs  "));
        assertEquals("garlic", IngredientNormalizer.normalize("Cloves Garlic"));
        assertEquals("green onion", IngredientNormalizer.normalize("Spring Onions"));
        assertEquals("chicken breast", IngredientNormalizer.normalize("Chicken Breast"));
        assertEquals("", IngredientNormalizer.normalize(null));
    }
}
