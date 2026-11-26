package com.example.memorama;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

/**
 * Pantalla de creditos con los nombres de los autores.
 * Se abre desde el boton "Créditos" de MainActivity.
 */
public class CreditosActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creditos);

        // finish() cierra esta pantalla y regresa al juego (que sigue como estaba)
        Button btnRegresar = findViewById(R.id.btnRegresar);
        btnRegresar.setOnClickListener(v -> finish());
    }
}
