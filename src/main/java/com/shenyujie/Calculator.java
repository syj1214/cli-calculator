package com.yourname;

import org.apache.commons.lang3.StringUtils;

public class Calculator {

    public double eval(String a, String op, String b) {

        if (StringUtils.isBlank(a) || StringUtils.isBlank(b)) {
            throw new IllegalArgumentException("input is blank");
        }
        if (StringUtils.isBlank(op)) {
            throw new IllegalArgumentException("operator is blank");
        }

        double x;
        double y;
        try {
            x = Double.parseDouble(a.trim());
            y = Double.parseDouble(b.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("operand is not a valid number", ex);
        }

        return switch (op.trim()) {
            case "+" -> x + y;
            case "-" -> x - y;
            case "*" -> x * y;
            case "/" -> divide(x, y);
            default  -> throw new IllegalArgumentException("unknown operator: " + op);
        };
    }

    private double divide(double dividend, double divisor) {
        if (divisor == 0.0) {
            throw new ArithmeticException("division by zero is not allowed");
        }
        return dividend / divisor;
    }
}


