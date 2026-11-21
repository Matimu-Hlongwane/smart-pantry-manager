package com.example.smartpantrymanagerjava;

import com.example.smartpantrymanagerjava.util.UnitConverter;

import org.junit.Test;

import static org.junit.Assert.*;

public class UnitConverterTest {

    @Test
    public void testWeightConversions() {
        assertEquals(1000.0, UnitConverter.convert(1.0, "kg", "g"), 0.001);
        assertEquals(1.5, UnitConverter.convert(1500.0, "g", "kg"), 0.001);
        assertEquals(500.0, UnitConverter.convert(0.5, "kilograms", "grams"), 0.001);
    }

    @Test
    public void testVolumeConversions() {
        assertEquals(1000.0, UnitConverter.convert(1.0, "l", "ml"), 0.001);
        assertEquals(2.0, UnitConverter.convert(2000.0, "ml", "litres"), 0.001);
        assertEquals(15.0, UnitConverter.convert(1.0, "tbsp", "ml"), 0.001);
        assertEquals(5.0, UnitConverter.convert(1.0, "tsp", "ml"), 0.001);
        assertEquals(240.0, UnitConverter.convert(1.0, "cup", "ml"), 0.001);
    }

    @Test
    public void testCountConversions() {
        assertEquals(3.0, UnitConverter.convert(3.0, "pcs", "pcs"), 0.001);
        assertEquals(2.0, UnitConverter.convert(2.0, "pieces", "unit"), 0.001);
    }

    @Test
    public void testIncompatibleUnitConversionReturnsNaN() {
        assertTrue(Double.isNaN(UnitConverter.convert(500.0, "g", "ml")));
        assertTrue(Double.isNaN(UnitConverter.convert(1.0, "kg", "pcs")));
        assertTrue(Double.isNaN(UnitConverter.convert(2.0, "l", "g")));
    }

    @Test
    public void testAreCompatible() {
        assertTrue(UnitConverter.areCompatible("kg", "g"));
        assertTrue(UnitConverter.areCompatible("ml", "l"));
        assertTrue(UnitConverter.areCompatible("tbsp", "cup"));
        assertFalse(UnitConverter.areCompatible("kg", "ml"));
        assertFalse(UnitConverter.areCompatible("pcs", "g"));
    }
}
