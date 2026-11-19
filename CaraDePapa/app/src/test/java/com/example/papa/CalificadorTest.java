package com.example.papa;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CalificadorTest {

    private static final float DELTA = 0.01f;

    @Test
    public void piezaEnSuLugarVale10() {
        float d = Calificador.distancia(Calificador.NARIZ, 250, 250);
        assertEquals(0f, d, DELTA);
        assertEquals(10f, Calificador.calificacionPieza(d), DELTA);
        assertEquals("Correcta", Calificador.estado(d));
    }

    @Test
    public void distanciaEsEuclidiana() {
        // 30 a la derecha y 40 abajo de los ojos (250, 185) -> 50
        assertEquals(50f, Calificador.distancia(Calificador.OJOS, 280, 225), DELTA);
    }

    @Test
    public void calificacionBajaEnFormaLineal() {
        assertEquals(10f, Calificador.calificacionPieza(30), DELTA);
        assertEquals(5f, Calificador.calificacionPieza(90), DELTA);   // a la mitad de 30 y 150
        assertEquals(0f, Calificador.calificacionPieza(150), DELTA);
        assertEquals(0f, Calificador.calificacionPieza(400), DELTA);
    }

    @Test
    public void estados() {
        assertEquals("Aproximada", Calificador.estado(60));
        assertEquals("Incorrecta", Calificador.estado(71));
    }

    @Test
    public void piezaNoColocadaValeCero() {
        boolean[] colocada = {true, true, true, true, false};
        float[] x = {250, 250, 250, 250, 0};
        float[] y = {105, 185, 250, 315, 0};
        // 4 piezas perfectas + 1 sin colocar = 40 / 5 = 8
        assertEquals(8f, Calificador.calificacionFinal(colocada, x, y), DELTA);
    }

    @Test
    public void bocaEnLosPiesReprueba() {
        // La boca puesta donde van los pies queda a 105 -> 10 * (150 - 105) / 120 = 3.75
        float d = Calificador.distancia(Calificador.BOCA, 250, 420);
        assertEquals(105f, d, DELTA);
        assertEquals(3.75f, Calificador.calificacionPieza(d), DELTA);
    }

    @Test
    public void redondeoAUnDecimal() {
        assertEquals(3.8f, Calificador.redondear(3.75f), DELTA);
    }
}
