package com.example.calculator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Plain JUnit 5 unit tests for CalculatorService (no Spring context needed).
 */
class CalculatorServiceTest {

    private final CalculatorService calculatorService = new CalculatorService();

    // ---------- add ----------

    @Test
    void addPositiveNumbers() {
        assertEquals(6, calculatorService.add(2, 3));
    }

    @Test
    void addNegativeNumbers() {
        assertEquals(-5, calculatorService.add(-2, -3));
    }

    @Test
    void addDecimals() {
        assertEquals(4.0, calculatorService.add(1.5, 2.5));
    }

    // ---------- subtract ----------

    @Test
    void subtractPositiveNumbers() {
        assertEquals(6, calculatorService.subtract(10, 4));
    }

    @Test
    void subtractGivesNegativeResult() {
        assertEquals(-6, calculatorService.subtract(4, 10));
    }

    // ---------- multiply ----------

    @Test
    void multiplyPositiveNumbers() {
        assertEquals(12, calculatorService.multiply(3, 4));
    }

    @Test
    void multiplyByZero() {
        assertEquals(0, calculatorService.multiply(5, 0));
    }

    @Test
    void multiplyNegativeByPositive() {
        assertEquals(-12, calculatorService.multiply(-3, 4));
    }

    // ---------- divide ----------

    @Test
    void dividePositiveNumbers() {
        assertEquals(2.5, calculatorService.divide(10, 4));
    }

    @Test
    void divideNegativeNumber() {
        assertEquals(-5, calculatorService.divide(-10, 2));
    }

    @Test
    void divideByZeroThrowsException() {
        ArithmeticException ex = assertThrows(ArithmeticException.class,
                () -> calculatorService.divide(5, 0));
        assertEquals("Division by zero is not allowed", ex.getMessage());
    }
}
