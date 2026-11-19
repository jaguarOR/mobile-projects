package com.example.sonidos;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Reproductor de sonidos basado en el ejemplo de clase (17 de noviembre),
 * con instrumentos musicales en lugar de animales.
 */
public class MainActivity extends Activity {

    ImageButton btnPiano, btnCampana, btnTambor, btnGuitarra;
    TextView txtAlumno;

    MediaPlayer reproductor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Nombre del alumno en la pantalla principal
        txtAlumno = findViewById(R.id.txtAlumno);
        txtAlumno.setText("Elaborado por: " + BuildConfig.ALUMNO);

        // Relacionar los botones
        btnPiano = findViewById(R.id.btnPiano);
        btnCampana = findViewById(R.id.btnCampana);
        btnTambor = findViewById(R.id.btnTambor);
        btnGuitarra = findViewById(R.id.btnGuitarra);

        btnPiano.setOnClickListener(v -> reproducirSonido(R.raw.piano));
        btnCampana.setOnClickListener(v -> reproducirSonido(R.raw.campana));
        btnTambor.setOnClickListener(v -> reproducirSonido(R.raw.tambor));
        btnGuitarra.setOnClickListener(v -> reproducirSonido(R.raw.guitarra));
    }

    // Metodo para reproducir sonidos
    private void reproducirSonido(int sonido) {

        // Detener sonido anterior
        if (reproductor != null) {
            reproductor.release();
            reproductor = null;
        }

        // Crear nuevo reproductor
        reproductor = MediaPlayer.create(this, sonido);

        if (reproductor != null) {
            reproductor.setOnCompletionListener(mp -> {
                mp.release();
                if (reproductor == mp) {
                    reproductor = null;
                }
            });
            reproductor.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (reproductor != null) {
            reproductor.release();
            reproductor = null;
        }
    }
}
