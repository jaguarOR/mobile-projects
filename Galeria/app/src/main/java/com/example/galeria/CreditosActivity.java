package com.example.galeria;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

// Pantalla de creditos con el nombre del alumno.
public class CreditosActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout principal = new LinearLayout(this);
        principal.setOrientation(LinearLayout.VERTICAL);
        principal.setGravity(Gravity.CENTER);
        principal.setBackgroundColor(Color.DKGRAY);
        principal.setPadding(40, 40, 40, 40);

        principal.addView(texto("Créditos", 30));
        principal.addView(texto("Desarrollado por:", 18));
        principal.addView(texto(BuildConfig.ALUMNO, 26));
        principal.addView(texto("Programación Móvil 1 · Grupo 1807", 16));
        principal.addView(texto("FES Aragón, UNAM", 16));

        Button btnRegresar = new Button(this);
        btnRegresar.setText("Regresar a la galería");
        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        principal.addView(btnRegresar);

        setContentView(principal);
    }

    TextView texto(String contenido, int tam) {
        TextView t = new TextView(this);
        t.setText(contenido);
        t.setTextSize(tam);
        t.setTextColor(Color.WHITE);
        t.setGravity(Gravity.CENTER);
        t.setPadding(0, 15, 0, 15);
        return t;
    }
}
