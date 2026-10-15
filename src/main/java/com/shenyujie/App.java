package com.yourname;

public class App {

    public static void main(String[] args) {

        if (args.length != 3) {
            System.err.println("Usage: App <number> <+|-|*|/> <number>");
            System.err.println("Example: App 2 + 2");
            System.exit(1);
        }

        Calculator calculator = new Calculator();
        try {
            double result = calculator.eval(args[0], args[1], args[2]);
            System.out.println(result);
        } catch (IllegalArgumentException | ArithmeticException ex) {
            System.err.println("Error: " + ex.getMessage());
            System.exit(2);
        }
    }
}
