package com.example.smartpantrymanagerjava;

import com.example.smartpantrymanagerjava.util.DateUtils;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;

public class DateUtilsTest {

    @Test
    public void testDateValidation() {
        assertTrue(DateUtils.isValidDate("2026-12-31"));
        assertTrue(DateUtils.isValidDate(""));
        assertTrue(DateUtils.isValidDate(null));
        assertFalse(DateUtils.isValidDate("invalid-date"));
        assertFalse(DateUtils.isValidDate("2026/12/31"));
    }

    @Test
    public void testIsExpired() {
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -2);
        String pastDate = DateUtils.formatDate(yesterday.getTime());

        assertTrue("Past date should be expired", DateUtils.isExpired(pastDate));

        Calendar future = Calendar.getInstance();
        future.add(Calendar.DAY_OF_YEAR, 10);
        String futureDate = DateUtils.formatDate(future.getTime());

        assertFalse("Future date should not be expired", DateUtils.isExpired(futureDate));
    }

    @Test
    public void testIsExpiringSoon() {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);
        String tomorrowDate = DateUtils.formatDate(tomorrow.getTime());

        assertTrue("Tomorrow should be expiring soon within 3 days", DateUtils.isExpiringSoon(tomorrowDate, 3));

        Calendar farFuture = Calendar.getInstance();
        farFuture.add(Calendar.DAY_OF_YEAR, 30);
        String farFutureDate = DateUtils.formatDate(farFuture.getTime());

        assertFalse("Far future date should not be expiring soon", DateUtils.isExpiringSoon(farFutureDate, 3));
    }
}
