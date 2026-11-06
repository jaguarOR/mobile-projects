package com.example.calculadora;

// Logica de la calculadora separada de la interfaz para poder probarla con JUnit.
public class Operaciones {

    public static double calcular(double num1, char operador, double num2) {
        switch (operador) {
            case '+':
                return num1 + num2;
            case '-':
                return num1 - num2;
            case '*':
                return num1 * num2;
            case '/':
                if (num2 == 0) {
                    throw new ArithmeticException("Division entre cero");
                }
                return num1 / num2;
            default:
                throw new IllegalArgumentException("Operador no valido: " + operador);
        }
    }

    // 8.0 -> "8", 2.5 -> "2.5"
    public static String formatear(double numero) {
        if (numero == Math.rint(numero) && !Double.isInfinite(numero)) {
            return String.valueOf((long) numero);
        }
        return String.valueOf(numero);
    }
}
