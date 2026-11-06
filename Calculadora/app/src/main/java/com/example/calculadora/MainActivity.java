package com.example.calculadora;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

// Tarea "Calculadora Android": ejercicio de la clase del 15 de octubre con las 4 operaciones basicas
// y el nombre del alumno en la parte de arriba.
public class MainActivity extends Activity {
    static final String NOMBRE = BuildConfig.ALUMNO;

    EditText pantalla;
    int tamLetra = 32;

    // 0-9 digitos, 10 ".", 11 "+", 12 "=", 13 "-", 14 "*", 15 "/", 16 "C"
    Button[] botones = new Button[17];

    Double num1;
    char operador;
    boolean nuevoNumero = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout principal = new LinearLayout(this);
        principal.setOrientation(LinearLayout.VERTICAL);
        principal.setPadding(20, 60, 20, 30);
        principal.setBackgroundColor(Color.GRAY);

        TextView titulo = new TextView(this);
        titulo.setText(NOMBRE);
        titulo.setTextSize(24);
        titulo.setTextColor(Color.WHITE);
        titulo.setGravity(Gravity.CENTER);
        titulo.setPadding(0, 0, 0, 20);
        principal.addView(titulo);

        pantalla = new EditText(this);
        pantalla.setBackgroundColor(Color.WHITE);
        pantalla.setTextSize(tamLetra);
        pantalla.setGravity(Gravity.END);
        pantalla.setShowSoftInputOnFocus(false); // solo se escribe con los botones
        principal.addView(pantalla);

        LinearLayout l1 = fila(Color.BLACK);
        LinearLayout l2 = fila(Color.RED);
        LinearLayout l3 = fila(Color.GREEN);
        LinearLayout l4 = fila(Color.BLUE);
        LinearLayout l5 = fila(Color.DKGRAY);

        OnClickListener escribir = new OnClickListener() {
            public void onClick(View v) {
                if (nuevoNumero) {
                    pantalla.setText("");
                    nuevoNumero = false;
                }
                String textoBoton = ((Button) v).getText().toString();
                String actual = pantalla.getText().toString();
                if (textoBoton.equals(".") && actual.contains(".")) {
                    return;
                }
                if (textoBoton.equals(".") && actual.isEmpty()) {
                    textoBoton = "0.";
                }
                pantalla.setText(actual + textoBoton);
            }
        };

        for (int i = 0; i <= 10; i++) {
            botones[i] = boton(i == 10 ? "." : String.valueOf(i));
            botones[i].setOnClickListener(escribir);
        }

        botones[11] = botonOperador("+", '+');
        botones[13] = botonOperador("-", '-');
        botones[14] = botonOperador("*", '*');
        botones[15] = botonOperador("/", '/');

        botones[12] = boton("=");
        botones[12].setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Double num2 = leerPantalla();
                if (num1 == null || num2 == null) {
                    return;
                }
                try {
                    double res = Operaciones.calcular(num1, operador, num2);
                    pantalla.setText(Operaciones.formatear(res));
                } catch (ArithmeticException e) {
                    pantalla.setText("Error");
                }
                num1 = null;
                nuevoNumero = true;
            }
        });

        botones[16] = boton("C");
        botones[16].setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                pantalla.setText("");
                num1 = null;
                nuevoNumero = false;
            }
        });

        // Distribucion como en clase: 7 8 9 / 4 5 6 / 1 2 3 / . 0, con los operadores a la derecha
        for (int j = 0; j <= 9; j++) {
            switch (j) {
                case 1:
                case 2:
                case 3:
                    l3.addView(botones[j]);
                    break;
                case 4:
                case 5:
                case 6:
                    l2.addView(botones[j]);
                    break;
                case 7:
                case 8:
                case 9:
                    l1.addView(botones[j]);
                    break;
                case 0:
                    l4.addView(botones[10]);
                    l4.addView(botones[j]);
                    break;
            }
        }
        l1.addView(botones[11]);
        l2.addView(botones[13]);
        l3.addView(botones[14]);
        l4.addView(botones[12]);
        l4.addView(botones[15]);
        l5.addView(botones[16]);

        principal.addView(l1);
        principal.addView(l2);
        principal.addView(l3);
        principal.addView(l4);
        principal.addView(l5);
        setContentView(principal);
    }

    LinearLayout fila(int color) {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.HORIZONTAL);
        l.setBackgroundColor(color);
        return l;
    }

    // Peso 1 para que los botones de cada fila ocupen el mismo ancho
    Button boton(String texto) {
        Button b = new Button(this);
        b.setText(texto);
        b.setTextSize(tamLetra);
        b.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        return b;
    }

    Button botonOperador(String texto, final char op) {
        Button b = boton(texto);
        b.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Double valor = leerPantalla();
                if (valor == null) {
                    return;
                }
                num1 = valor;
                operador = op;
                pantalla.setText("");
                nuevoNumero = false;
            }
        });
        return b;
    }

    // null si la pantalla esta vacia o no contiene un numero (p. ej. "Error")
    Double leerPantalla() {
        try {
            return Double.parseDouble(pantalla.getText().toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
