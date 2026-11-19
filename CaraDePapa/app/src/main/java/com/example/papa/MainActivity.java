package com.example.papa;

import android.app.Activity;
import android.content.ClipData;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Sr. Cara de Papa: basado en el ejemplo de clase (ojos y nariz) y ampliado
 * con pelo, boca y pies. Al presionar "Calificar posiciones" se mide la
 * distancia de cada pieza a su posición correcta y se calcula una
 * calificación de 0 a 10 (ver Calificador.java).
 */
public class MainActivity extends Activity {

    FrameLayout escenario;
    ImageView piezaDisponible;
    Spinner selector;
    Button btnPosiciones, btnReiniciar;
    TextView resultado, alumno;

    // 0 = nada seleccionado; 1..5 = pelo, ojos, nariz, boca, pies
    int piezaActual = 0;
    int tamanoPieza;

    // Una posición por pieza (índice = Calificador.PELO ... Calificador.PIES)
    int[] imagenes = {
            R.drawable.pelo,
            R.drawable.ojos,
            R.drawable.nariz,
            R.drawable.boca,
            R.drawable.pies
    };
    String[] nombres = new String[Calificador.TOTAL_PIEZAS];
    ImageView[] colocadas = new ImageView[Calificador.TOTAL_PIEZAS];
    float[] posX = new float[Calificador.TOTAL_PIEZAS];
    float[] posY = new float[Calificador.TOTAL_PIEZAS];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        escenario = findViewById(R.id.escenario);
        piezaDisponible = findViewById(R.id.piezadisponible);
        selector = findViewById(R.id.selector);
        btnPosiciones = findViewById(R.id.btnposiciones);
        btnReiniciar = findViewById(R.id.btnreiniciar);
        resultado = findViewById(R.id.resultado);
        alumno = findViewById(R.id.alumno);

        alumno.setText("Alumno: " + BuildConfig.ALUMNO);

        tamanoPieza = dp(72);

        nombres[Calificador.PELO] = getString(R.string.oppelo);
        nombres[Calificador.OJOS] = getString(R.string.opojos);
        nombres[Calificador.NARIZ] = getString(R.string.opnariz);
        nombres[Calificador.BOCA] = getString(R.string.opboca);
        nombres[Calificador.PIES] = getString(R.string.oppies);

        String[] opciones = {
                getString(R.string.selecciontexto),
                nombres[Calificador.PELO],
                nombres[Calificador.OJOS],
                nombres[Calificador.NARIZ],
                nombres[Calificador.BOCA],
                nombres[Calificador.PIES]
        };

        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                opciones
        );
        adaptador.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        selector.setAdapter(adaptador);

        selector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                piezaActual = position;
                piezaDisponible.setVisibility(View.GONE);

                if (position > 0) {
                    int indice = position - 1;
                    // Si la pieza ya estaba colocada, se quita para volver a ponerla
                    quitarPieza(indice);
                    piezaDisponible.setImageResource(imagenes[indice]);
                    piezaDisponible.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        piezaDisponible.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (piezaActual == 0) {
                        return false;
                    }
                    ClipData datos = ClipData.newPlainText(
                            "pieza", String.valueOf(piezaActual));
                    View.DragShadowBuilder sombra = new View.DragShadowBuilder(v);
                    v.startDrag(datos, sombra, Integer.valueOf(piezaActual), 0);
                    return true;
                }
                return true;
            }
        });

        escenario.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        return event.getLocalState() instanceof Integer;
                    case DragEvent.ACTION_DROP:
                        int tipo = (Integer) event.getLocalState();
                        colocarPieza(event.getX(), event.getY(), tipo - 1);
                        return true;
                    case DragEvent.ACTION_DRAG_ENTERED:
                    case DragEvent.ACTION_DRAG_LOCATION:
                    case DragEvent.ACTION_DRAG_EXITED:
                    case DragEvent.ACTION_DRAG_ENDED:
                        return true;
                }
                return false;
            }
        });

        btnPosiciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarCalificacion();
            }
        });

        btnReiniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < Calificador.TOTAL_PIEZAS; i++) {
                    quitarPieza(i);
                }
                selector.setSelection(0);
                resultado.setText("");
            }
        });
    }

    // Coloca la pieza (indice 0..4) centrada donde se soltó el dedo
    private void colocarPieza(float x, float y, int indice) {
        ImageView nuevaPieza = new ImageView(this);
        nuevaPieza.setImageResource(imagenes[indice]);
        nuevaPieza.setScaleType(ImageView.ScaleType.FIT_CENTER);

        int ancho = escenario.getWidth();
        int alto = escenario.getHeight();

        // Que la pieza no se salga del escenario
        int izquierda = Math.max(0, Math.min((int) x - tamanoPieza / 2, ancho - tamanoPieza));
        int arriba = Math.max(0, Math.min((int) y - tamanoPieza / 2, alto - tamanoPieza));

        FrameLayout.LayoutParams parametros =
                new FrameLayout.LayoutParams(tamanoPieza, tamanoPieza);
        parametros.leftMargin = izquierda;
        parametros.topMargin = arriba;
        escenario.addView(nuevaPieza, parametros);

        // Convertir de pixeles de la pantalla a coordenadas de la imagen (500 x 500)
        float factorX = 500f / ancho;
        float factorY = 500f / alto;
        float centroX = izquierda + tamanoPieza / 2f;
        float centroY = arriba + tamanoPieza / 2f;

        colocadas[indice] = nuevaPieza;
        posX[indice] = centroX * factorX;
        posY[indice] = centroY * factorY;

        piezaDisponible.setVisibility(View.GONE);
        // Regresar el Spinner a "Seleccionar" para poder elegir otra pieza
        selector.setSelection(0);
    }

    private void quitarPieza(int indice) {
        if (colocadas[indice] != null) {
            escenario.removeView(colocadas[indice]);
            colocadas[indice] = null;
        }
    }

    private void mostrarCalificacion() {
        boolean[] colocada = new boolean[Calificador.TOTAL_PIEZAS];
        StringBuilder texto = new StringBuilder();

        for (int i = 0; i < Calificador.TOTAL_PIEZAS; i++) {
            colocada[i] = colocadas[i] != null;
            texto.append(nombres[i]).append(": ");

            if (colocada[i]) {
                float d = Calificador.distancia(i, posX[i], posY[i]);
                float nota = Calificador.calificacionPieza(d);
                texto.append(Calificador.estado(d))
                        .append("  (").append(Calificador.redondear(nota)).append("/10)\n")
                        .append("   X: ").append(Math.round(posX[i]))
                        .append("  Y: ").append(Math.round(posY[i]))
                        .append("  ->  objetivo (")
                        .append(Math.round(Calificador.OBJETIVO_X[i])).append(", ")
                        .append(Math.round(Calificador.OBJETIVO_Y[i])).append(")")
                        .append("  distancia: ").append(Math.round(d)).append("\n");
            } else {
                texto.append("No colocada  (0/10)\n");
            }
        }

        float finalNota = Calificador.calificacionFinal(colocada, posX, posY);
        texto.append("\nCALIFICACIÓN FINAL: ")
                .append(Calificador.redondear(finalNota)).append(" / 10");
        resultado.setText(texto.toString());
    }

    private int dp(float valor) {
        return (int) (valor * getResources().getDisplayMetrics().density + 0.5f);
    }
}
