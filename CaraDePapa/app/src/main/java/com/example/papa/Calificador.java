package com.example.papa;

/**
 * Califica qué tan cerca quedó cada pieza de su posición correcta.
 *
 * Todas las coordenadas están en el sistema de 500 x 500 de la imagen del cuerpo
 * (igual que en el ejemplo de clase: factor = 500 / ancho del escenario), así el
 * resultado no depende del tamaño de la pantalla.
 *
 * No usa clases de Android para poder probarla con JUnit.
 */
public class Calificador {

    // Índices de las piezas (coinciden con la posición en el Spinner menos 1)
    public static final int PELO = 0;
    public static final int OJOS = 1;
    public static final int NARIZ = 2;
    public static final int BOCA = 3;
    public static final int PIES = 4;
    public static final int TOTAL_PIEZAS = 5;

    // Posición correcta (centro de la pieza) medida sobre cuerpo.png (500 x 500).
    // El cuerpo ocupa aprox. x 87-410, y 102-417, centrado en x = 250.
    public static final float[] OBJETIVO_X = {250, 250, 250, 250, 250};
    public static final float[] OBJETIVO_Y = {105, 185, 250, 315, 420};

    // Tolerancias en unidades de la imagen (500 = ancho completo)
    public static final float DISTANCIA_PERFECTA = 30;  // hasta aquí: 10
    public static final float DISTANCIA_MAXIMA = 150;   // desde aquí: 0
    public static final float DISTANCIA_APROXIMADA = 70; // límite de "aproximada"

    /** Distancia euclidiana entre la pieza y su posición correcta. */
    public static float distancia(int pieza, float x, float y) {
        float dx = x - OBJETIVO_X[pieza];
        float dy = y - OBJETIVO_Y[pieza];
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Calificación de 0 a 10 de una pieza según su distancia:
     * 10 si d <= 30, 0 si d >= 150 y lineal entre ambos valores.
     */
    public static float calificacionPieza(float distancia) {
        if (distancia <= DISTANCIA_PERFECTA) {
            return 10f;
        }
        if (distancia >= DISTANCIA_MAXIMA) {
            return 0f;
        }
        float rango = DISTANCIA_MAXIMA - DISTANCIA_PERFECTA;
        return 10f * (DISTANCIA_MAXIMA - distancia) / rango;
    }

    /** Texto corto para mostrar al usuario. */
    public static String estado(float distancia) {
        if (distancia <= DISTANCIA_PERFECTA) {
            return "Correcta";
        }
        if (distancia <= DISTANCIA_APROXIMADA) {
            return "Aproximada";
        }
        return "Incorrecta";
    }

    /**
     * Promedio de las 5 piezas. Una pieza que no se colocó vale 0
     * (se indica con colocada[i] == false).
     */
    public static float calificacionFinal(boolean[] colocada, float[] x, float[] y) {
        float suma = 0;
        for (int i = 0; i < TOTAL_PIEZAS; i++) {
            if (colocada[i]) {
                suma += calificacionPieza(distancia(i, x[i], y[i]));
            }
        }
        return suma / TOTAL_PIEZAS;
    }

    /** Redondea a un decimal para mostrarla (ej. 8.46 -> 8.5). */
    public static float redondear(float valor) {
        return Math.round(valor * 10f) / 10f;
    }
}
