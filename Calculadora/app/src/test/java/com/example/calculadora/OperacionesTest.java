package com.example.calculadora;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class OperacionesTest {

    @Test
    public void operacionesBasicas() {
        assertEquals(5.0, Operaciones.calcular(2, '+', 3), 1e-9);
        assertEquals(-1.0, Operaciones.calcular(2, '-', 3), 1e-9);
        assertEquals(6.0, Operaciones.calcular(2, '*', 3), 1e-9);
        assertEquals(2.5, Operaciones.calcular(5, '/', 2), 1e-9);
    }

    @Test
    public void decimales() {
        assertEquals(0.3, Operaciones.calcular(0.1, '+', 0.2), 1e-9);
    }

    @Test(expected = ArithmeticException.class)
    public void divisionEntreCero() {
        Operaciones.calcular(1, '/', 0);
    }

    @Test
    public void formatoSinDecimalesInnecesarios() {
        assertEquals("8", Operaciones.formatear(8.0));
        assertEquals("-3", Operaciones.formatear(-3.0));
        assertEquals("2.5", Operaciones.formatear(2.5));
    }
}
