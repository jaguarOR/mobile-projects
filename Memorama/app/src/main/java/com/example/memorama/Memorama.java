package com.example.memorama;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Reglas del memorama, sin nada de Android (por eso se puede probar con JUnit).
 *
 * Las cartas se guardan en un arreglo: cartas[i] es el numero de figura de la
 * posicion i. Cada figura aparece exactamente dos veces.
 *
 * Flujo de un turno:
 *   1. voltear(a) -> PRIMERA   (queda boca arriba esperando a la segunda)
 *   2. voltear(b) -> PAR       (las dos se quedan boca arriba para siempre)
 *              o -> NO_PAR    (se quedan visibles hasta llamar a ocultarNoPares())
 */
public class Memorama {

    /** Lo que paso al tocar una carta. */
    public enum Resultado { INVALIDA, PRIMERA, PAR, NO_PAR }

    private final int[] cartas;          // figura de cada posicion
    private final boolean[] visibles;    // true = boca arriba
    private final boolean[] emparejadas; // true = ya forma parte de un par encontrado

    private int primera = -1;  // posicion de la primera carta del turno (-1 = ninguna)
    private int segunda = -1;  // posicion de la segunda carta cuando NO hubo par
    private int intentos = 0;  // turnos completos (cada vez que se voltean 2 cartas)
    private int pares = 0;     // pares encontrados

    /** Juego nuevo con las cartas revueltas al azar. */
    public Memorama(int numPares, Random azar) {
        this(revolver(numPares, azar));
    }

    /** Juego con un orden fijo de cartas (lo usan las pruebas). */
    public Memorama(int[] cartas) {
        this.cartas = cartas.clone();
        this.visibles = new boolean[cartas.length];
        this.emparejadas = new boolean[cartas.length];
    }

    /** Crea la lista 0,0,1,1,2,2,... y la revuelve. */
    private static int[] revolver(int numPares, Random azar) {
        List<Integer> lista = new ArrayList<>();
        for (int figura = 0; figura < numPares; figura++) {
            lista.add(figura);
            lista.add(figura);
        }
        Collections.shuffle(lista, azar);

        int[] resultado = new int[lista.size()];
        for (int i = 0; i < resultado.length; i++) {
            resultado[i] = lista.get(i);
        }
        return resultado;
    }

    /** Intenta voltear la carta de la posicion pos. */
    public Resultado voltear(int pos) {
        // No se puede: fuera del tablero, ya esta boca arriba,
        // o todavia hay dos cartas sin par esperando a ocultarse.
        if (pos < 0 || pos >= cartas.length || visibles[pos] || segunda != -1) {
            return Resultado.INVALIDA;
        }

        visibles[pos] = true;

        // Primera carta del turno
        if (primera == -1) {
            primera = pos;
            return Resultado.PRIMERA;
        }

        // Segunda carta: se completa un intento
        intentos++;
        if (cartas[primera] == cartas[pos]) {
            emparejadas[primera] = true;
            emparejadas[pos] = true;
            pares++;
            primera = -1;
            return Resultado.PAR;
        }

        segunda = pos;
        return Resultado.NO_PAR;
    }

    /** Vuelve a poner boca abajo las dos cartas que no hicieron par. */
    public void ocultarNoPares() {
        if (segunda == -1) {
            return;
        }
        visibles[primera] = false;
        visibles[segunda] = false;
        primera = -1;
        segunda = -1;
    }

    public boolean terminado() {
        return pares == cartas.length / 2;
    }

    public int getFigura(int pos)          { return cartas[pos]; }
    public boolean esVisible(int pos)      { return visibles[pos]; }
    public boolean estaEmparejada(int pos) { return emparejadas[pos]; }
    public int getIntentos()               { return intentos; }
    public int getPares()                  { return pares; }
    public int getTotalPares()             { return cartas.length / 2; }
    public int getTotalCartas()            { return cartas.length; }
}
