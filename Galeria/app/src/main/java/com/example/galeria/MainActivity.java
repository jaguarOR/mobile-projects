package com.example.galeria;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

// Tarea "Galeria android": galeria con un titulo arriba de cada imagen y pantalla de creditos.
public class MainActivity extends Activity {

    int[] imagenes = {R.drawable.montana, R.drawable.playa, R.drawable.noche, R.drawable.bosque};
    String[] titulos = {"Montaña", "Playa al atardecer", "Noche estrellada", "Bosque"};
    int actual = 0;

    TextView txtTitulo, txtContador;
    ImageView imagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            actual = savedInstanceState.getInt("actual", 0); // conservar la imagen al girar el telefono
        }

        LinearLayout principal = new LinearLayout(this);
        principal.setOrientation(LinearLayout.VERTICAL);
        principal.setBackgroundColor(Color.BLACK);
        principal.setPadding(20, 40, 20, 20);

        // Titulo arriba de la imagen
        txtTitulo = new TextView(this);
        txtTitulo.setTextSize(26);
        txtTitulo.setTextColor(Color.WHITE);
        txtTitulo.setGravity(Gravity.CENTER);
        txtTitulo.setPadding(0, 20, 0, 20);
        principal.addView(txtTitulo);

        imagen = new ImageView(this);
        imagen.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imagen.setAdjustViewBounds(true);
        principal.addView(imagen, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));

        txtContador = new TextView(this);
        txtContador.setTextColor(Color.LTGRAY);
        txtContador.setGravity(Gravity.CENTER);
        txtContador.setPadding(0, 10, 0, 10);
        principal.addView(txtContador);

        LinearLayout botones = new LinearLayout(this);
        botones.setOrientation(LinearLayout.HORIZONTAL);

        Button btnAnterior = boton("Anterior");
        btnAnterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actual = (actual - 1 + imagenes.length) % imagenes.length;
                mostrar();
            }
        });

        Button btnSiguiente = boton("Siguiente");
        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actual = (actual + 1) % imagenes.length;
                mostrar();
            }
        });

        botones.addView(btnAnterior);
        botones.addView(btnSiguiente);
        principal.addView(botones);

        Button btnCreditos = new Button(this);
        btnCreditos.setText("Créditos");
        btnCreditos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CreditosActivity.class));
            }
        });
        principal.addView(btnCreditos);

        mostrar();
        setContentView(principal);
    }

    void mostrar() {
        txtTitulo.setText(titulos[actual]);
        imagen.setImageResource(imagenes[actual]);
        txtContador.setText((actual + 1) + " / " + imagenes.length);
    }

    Button boton(String texto) {
        Button b = new Button(this);
        b.setText(texto);
        b.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        return b;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("actual", actual);
    }
}
