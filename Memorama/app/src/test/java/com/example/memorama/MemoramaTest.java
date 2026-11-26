package com.example.memorama;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Random;
import org.junit.Test;

public class MemoramaTest {

    // Tablero fijo para las pruebas: posiciones 0 y 2 son la figura 0, 1 y 3 la figura 1
    private Memorama juegoFijo() {
        return new Memorama(new int[] {0, 1, 0, 1});
    }

    @Test
    public void cadaFiguraApareceDosVeces() {
        Memorama juego = new Memorama(6, new Random(1));
        assertEquals(12, juego.getTotalCartas());
        int[] cuenta = new int[6];
        for (int i = 0; i < juego.getTotalCartas(); i++) {
            cuenta[juego.getFigura(i)]++;
        }
        int[] esperado = new int[6];
        Arrays.fill(esperado, 2);
        assertTrue(Arrays.equals(esperado, cuenta));
    }

    @Test
    public void encontrarUnPar() {
        Memorama juego = juegoFijo();
        assertEquals(Memorama.Resultado.PRIMERA, juego.voltear(0));
        assertEquals(Memorama.Resultado.PAR, juego.voltear(2));
        assertTrue(juego.estaEmparejada(0));
        assertTrue(juego.estaEmparejada(2));
        assertEquals(1, juego.getPares());
        assertEquals(1, juego.getIntentos());
    }

    @Test
    public void noParSeOcultaDespues() {
        Memorama juego = juegoFijo();
        juego.voltear(0);
        assertEquals(Memorama.Resultado.NO_PAR, juego.voltear(1));
        // Mientras siguen visibles, no se puede voltear una tercera carta
        assertEquals(Memorama.Resultado.INVALIDA, juego.voltear(2));

        juego.ocultarNoPares();
        assertFalse(juego.esVisible(0));
        assertFalse(juego.esVisible(1));
        assertEquals(Memorama.Resultado.PRIMERA, juego.voltear(2));
    }

    @Test
    public void noSePuedeVoltearLaMismaCartaDosVeces() {
        Memorama juego = juegoFijo();
        juego.voltear(0);
        assertEquals(Memorama.Resultado.INVALIDA, juego.voltear(0));
        assertEquals(0, juego.getIntentos());
    }

    @Test
    public void posicionFueraDelTableroEsInvalida() {
        Memorama juego = juegoFijo();
        assertEquals(Memorama.Resultado.INVALIDA, juego.voltear(-1));
        assertEquals(Memorama.Resultado.INVALIDA, juego.voltear(4));
    }

    @Test
    public void ganarElJuego() {
        Memorama juego = juegoFijo();
        juego.voltear(0);
        juego.voltear(1);          // fallo
        juego.ocultarNoPares();
        juego.voltear(0);
        juego.voltear(2);          // par de la figura 0
        assertFalse(juego.terminado());
        juego.voltear(1);
        juego.voltear(3);          // par de la figura 1
        assertTrue(juego.terminado());
        assertEquals(3, juego.getIntentos());
    }
}
