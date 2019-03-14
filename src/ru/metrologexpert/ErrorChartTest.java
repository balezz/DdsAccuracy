package ru.metrologexpert;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorChartTest {

    private ErrorChart errorChart;

    @BeforeEach
    void setUp() {
        errorChart = new ErrorChart();
    }

    @AfterEach
    void tearDown() {
        errorChart = null;
    }

    @Test
    void powTwo() {
        assertEquals(8, errorChart.twoPow(3));
        assertEquals(0, errorChart.twoPow(-3));
        assertEquals(1, errorChart.twoPow(0));
    }

    @Test
    void writeFile() {

    }
}