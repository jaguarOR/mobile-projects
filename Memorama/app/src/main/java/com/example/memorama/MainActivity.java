package com.example.memorama;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Random;

/**
 * Pantalla del memorama: dibuja las cartas, responde a los toques y reproduce sonidos.
 * Las reglas del juego estan en Memorama.java.
 */
public class MainActivity extends Activity {

    static final int PARES = 6;      // 6 pares = 12 cartas
    static final int COLUMNAS = 3;   // tablero de 4 filas x 3 columnas
    static final int ESPERA_MS = 900; // tiempo que se ven dos cartas sin par

    // Figura de cada par (emoji, asi no se necesitan imagenes)
    static final String[] FIGURAS = {"🐶", "🐱", "🐸", "🦁", "🐵", "🐼"};

    static final int COLOR_REVERSO = Color.rgb(63, 81, 181);   // azul
    static final int COLOR_FRENTE = Color.WHITE;
    static final int COLOR_PAR = Color.rgb(165, 214, 167);     // verde claro

    Memorama juego;
    Button[] cartas = new Button[PARES * 2];
    TextView txtMarcador, txtMensaje;

    MediaPlayer reproductor;
    Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Nombre del alumno (se lee de local.properties al compilar)
        TextView txtAlumno = findViewById(R.id.txtAlumno);
        txtAlumno.setText("Elaborado por: " + BuildConfig.ALUMNO);

        txtMarcador = findViewById(R.id.txtMarcador);
        txtMensaje = findViewById(R.id.txtMensaje);

        Button btnReiniciar = findViewById(R.id.btnReiniciar);
        btnReiniciar.setOnClickListener(v -> nuevoJuego());

        // Abre la pantalla de creditos con un Intent (como en la Galeria)
        Button btnCreditos = findViewById(R.id.btnCreditos);
        btnCreditos.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, CreditosActivity.class)));

        crearTablero();
        nuevoJuego();
    }

    /** Crea los 12 botones en filas de 3, como el arreglo de botones de la calculadora. */
    private void crearTablero() {
        LinearLayout tablero = findViewById(R.id.tablero);
        int alto = dp(105);
        int margen = dp(4);

        for (int fila = 0; fila < cartas.length / COLUMNAS; fila++) {
            LinearLayout renglon = new LinearLayout(this);
            renglon.setOrientation(LinearLayout.HORIZONTAL);
            tablero.addView(renglon);

            for (int col = 0; col < COLUMNAS; col++) {
                final int pos = fila * COLUMNAS + col;

                Button carta = new Button(this);
                carta.setTextSize(36);
                carta.setTextColor(Color.WHITE);
                carta.setPadding(0, 0, 0, 0);
                carta.setOnClickListener(v -> tocarCarta(pos));

                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(0, alto, 1f); // ancho repartido
                params.setMargins(margen, margen, margen, margen);
                renglon.addView(carta, params);

                cartas[pos] = carta;
            }
        }
    }

    /** Revuelve las cartas y reinicia el marcador. */
    private void nuevoJuego() {
        handler.removeCallbacksAndMessages(null); // cancela un "ocultar" pendiente
        juego = new Memorama(PARES, new Random());
        txtMensaje.setText("Encuentra los pares");
        actualizarPantalla();
    }

    /** Se llama cada vez que el usuario toca una carta. */
    private void tocarCarta(int pos) {
        Memorama.Resultado resultado = juego.voltear(pos);

        switch (resultado) {
            case INVALIDA:
                return; // carta ya volteada o hay dos esperando: no pasa nada

            case PRIMERA:
                reproducirSonido(R.raw.voltear);
                break;

            case PAR:
                if (juego.terminado()) {
                    reproducirSonido(R.raw.victoria);
                    txtMensaje.setText("¡Ganaste en " + juego.getIntentos() + " intentos!");
                } else {
                    reproducirSonido(R.raw.acierto);
                    txtMensaje.setText("¡Par encontrado!");
                }
                break;

            case NO_PAR:
                reproducirSonido(R.raw.error);
                txtMensaje.setText("No son iguales");
                // Esperar un momento para que se vean y luego voltearlas
                handler.postDelayed(() -> {
                    juego.ocultarNoPares();
                    actualizarPantalla();
                }, ESPERA_MS);
                break;
        }
        actualizarPantalla();
    }

    /** Pinta cada carta segun su estado en el juego. */
    private void actualizarPantalla() {
        for (int i = 0; i < cartas.length; i++) {
            if (juego.estaEmparejada(i)) {
                cartas[i].setText(FIGURAS[juego.getFigura(i)]);
                cartas[i].setBackgroundColor(COLOR_PAR);
            } else if (juego.esVisible(i)) {
                cartas[i].setText(FIGURAS[juego.getFigura(i)]);
                cartas[i].setBackgroundColor(COLOR_FRENTE);
            } else {
                cartas[i].setText("?");
                cartas[i].setBackgroundColor(COLOR_REVERSO);
            }
        }
        txtMarcador.setText("Intentos: " + juego.getIntentos()
                + "    Pares: " + juego.getPares() + "/" + juego.getTotalPares());
    }

    // Metodo para reproducir sonidos (igual que en la app de instrumentos)
    private void reproducirSonido(int sonido) {
        // Detener sonido anterior
        if (reproductor != null) {
            reproductor.release();
            reproductor = null;
        }

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

    /** Convierte dp (unidad independiente de la pantalla) a pixeles. */
    private int dp(int valor) {
        return (int) (valor * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        if (reproductor != null) {
            reproductor.release();
            reproductor = null;
        }
    }
}
