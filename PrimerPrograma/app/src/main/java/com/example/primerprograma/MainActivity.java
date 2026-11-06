package com.example.primerprograma;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

// Tarea "Primer programa Android": al presionar el boton se escribe el nombre en un TextView.
public class MainActivity extends Activity {
    static final String NOMBRE = BuildConfig.ALUMNO;

    LinearLayout principal;
    Button btnNombre;
    TextView txtNombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        principal = new LinearLayout(this);
        principal.setOrientation(LinearLayout.VERTICAL);
        principal.setBackgroundColor(Color.GRAY);
        principal.setPadding(20, 40, 20, 20);

        btnNombre = new Button(this);
        btnNombre.setText("Mostrar mi nombre");
        btnNombre.setTextSize(20);

        txtNombre = new TextView(this);
        txtNombre.setTextSize(28);
        txtNombre.setTextColor(Color.WHITE);
        txtNombre.setGravity(Gravity.CENTER);
        txtNombre.setPadding(20, 60, 20, 20);

        btnNombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtNombre.setText(NOMBRE);
            }
        });

        principal.addView(btnNombre);
        principal.addView(txtNombre);
        setContentView(principal);
    }
}
