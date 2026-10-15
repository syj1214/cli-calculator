package com.yourname;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Calculator unit test suite")
class CalculatorTest {

    private Calculator calculator;

    @BeforeEach
    void setUp() {

        calculator = new Calculator();
    }

    @Nested
    @DisplayName("Arithmetic operations")
    class ArithmeticOperations {

        @Test
        @DisplayName("TC-01 addition: 2 + 2 = 4")
        void additionWorks() {
            assertEquals(4.0, calculator.eval("2", "+", "2"));
        }

        @Test
        @DisplayName("TC-02 subtraction: 10 - 3 = 7")
        void subtractionWorks() {
            assertEquals(7.0, calculator.eval("10", "-", "3"));
        }

        @Test
        @DisplayName("TC-03 multiplication: 6 * 7 = 42")
        void multiplicationWorks() {
            assertEquals(42.0, calculator.eval("6", "*", "7"));
        }

        @Test
        @DisplayName("TC-04 division: 10 / 5 = 2")
        void divisionWorks() {
            assertEquals(2.0, calculator.eval("10", "/", "5"));
        }
    }

    @Nested
    @DisplayName("Boundary and decimal values")
    class BoundaryValues {

        @Test
        @DisplayName("TC-05 decimal division: 10 / 4 = 2.5")
        void decimalDivisionWorks() {
            assertEquals(2.5, calculator.eval("10", "/", "4"));
        }

        @Test
        @DisplayName("TC-06 repeating decimal: 1 / 3 keeps the fractional part within 1e-9")
        void repeatingDecimalIsApproximated() {
            assertEquals(1.0 / 3.0, calculator.eval("1", "/", "3"), 1e-9);
        }

        @Test
        @DisplayName("TC-07 zero operand: 0 * 99 = 0")
        void zeroOperandGivesZero() {
            assertEquals(0.0, calculator.eval("0", "*", "99"));
        }

        @Test
        @DisplayName("TC-08 negative operands: -5 + 3 = -2 and -4 * -2 = 8")
        void negativeOperandsAreHandled() {
            assertAll(
                    () -> assertEquals(-2.0, calculator.eval("-5", "+", "3")),
                    () -> assertEquals(8.0, calculator.eval("-4", "*", "-2"))
            );
        }
    }

    @Test
    @DisplayName("TC-09 division by zero throws ArithmeticException with a clear message")
    void divideByZeroThrows() {
        ArithmeticException ex = assertThrows(ArithmeticException.class,
                () -> calculator.eval("5", "/", "0"));
        assertTrue(ex.getMessage().contains("division by zero"),
                "the message should explain the reason");
    }

    @Test
    @DisplayName("TC-10 blank operand throws IllegalArgumentException")
    void blankInputThrows() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> calculator.eval("", "+", "2"));
        assertTrue(ex.getMessage().contains("blank"));
    }

    @Test
    @DisplayName("TC-11 unknown operator is rejected")
    void unknownOperatorThrows() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> calculator.eval("2", "^", "3"));
        assertTrue(ex.getMessage().contains("unknown operator"));
    }

    @Test
    @DisplayName("TC-12 non numeric operand is rejected")
    void nonNumericInputThrows() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> calculator.eval("abc", "+", "2"));
        assertTrue(ex.getMessage().contains("valid number"));
    }

    @ParameterizedTest(name = "eval({0},{1},{2}) = {3}")
    @CsvSource({
            "8,  +, 2,  10",
            "8,  -, 2,   6",
            "8,  *, 2,  16",
            "8,  /, 2,   4"
    })
    @DisplayName("TC-P parameterised four-operator cross-check")
    void parameterisedArithmetic(String a, String op, String b, double expected) {
        assertEquals(expected, calculator.eval(a, op, b), 1e-9);
    }
}
