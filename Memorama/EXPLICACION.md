# Explicación del código del Memorama

Guía para explicar la app en clase: qué hace cada archivo y cada función importante, en el orden en que conviene presentarlos.

Autores: **OSCAR DANIEL GARCIA SAAVEDRA** y **OLIVERA REYES EZEQUIEL**.

## 0. Idea general (para empezar la explicación)

La app está dividida en tres partes:

| Parte | Archivo | Qué hace |
|---|---|---|
| **Vista** (lo que se ve) | `activity_main.xml`, `activity_creditos.xml` | Acomoda textos y botones en la pantalla |
| **Controladores** (responden al usuario) | `MainActivity.java`, `CreditosActivity.java` | Reciben los toques, le preguntan al modelo qué pasó, pintan las cartas y tocan los sonidos |
| **Modelo** (las reglas) | `Memorama.java` | Sabe dónde está cada figura, qué cartas están volteadas y cuándo se gana. No usa nada de Android |

Frase para el profesor: *"La Activity no decide si dos cartas son pareja; se lo pregunta a la clase Memorama. Así las reglas se pueden probar con JUnit sin el teléfono."*

```
 Usuario toca una carta
        │
        ▼
 MainActivity.tocarCarta(pos)  ──►  Memorama.voltear(pos)  ──►  regresa PRIMERA / PAR / NO_PAR / INVALIDA
        │                                                              │
        ◄──────────────────────────────────────────────────────────────┘
        │
        ├─ reproducirSonido(...)      (MediaPlayer)
        ├─ txtMensaje.setText(...)    (mensaje)
        └─ actualizarPantalla()       (pinta las 12 cartas según el modelo)
```

---

## 1. `AndroidManifest.xml`: las pantallas de la app

```xml
<activity android:name=".MainActivity" android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
<activity android:name=".CreditosActivity" android:label="Créditos" android:exported="false" />
```

- Toda `Activity` tiene que estar registrada aquí; si falta, la app se cierra al abrirla.
- `MAIN` + `LAUNCHER` indican que `MainActivity` es la pantalla que abre el ícono de la app.
- `CreditosActivity` tiene `exported="false"`: solo se abre desde dentro de la app.
- `screenOrientation="portrait"` fija la pantalla en vertical, para que el tablero no se reacomode al girar el teléfono.

---

## 2. `activity_main.xml`: la pantalla del juego

Es un `LinearLayout` **vertical**: los elementos van uno debajo del otro.

| Elemento | `id` | Para qué |
|---|---|---|
| `TextView` "MEMORAMA" | — | Título fijo |
| `TextView` | `txtAlumno` | "Elaborado por: …"; el texto se pone desde Java |
| `TextView` | `txtMarcador` | "Intentos: 3    Pares: 2/6" |
| `LinearLayout` vertical **vacío** | `tablero` | Aquí Java agrega las 12 cartas |
| `TextView` | `txtMensaje` | "¡Par encontrado!", "No son iguales", "¡Ganaste…!" |
| `LinearLayout` horizontal | — | Pone los dos botones uno junto al otro |
| `Button` | `btnReiniciar` | Revuelve y empieza de nuevo |
| `Button` | `btnCreditos` | Abre la pantalla de créditos |

Atributos que conviene saber explicar:

- `android:id="@+id/txtMarcador"`: el `+` crea el id; en Java se usa como `R.id.txtMarcador`.
- `layout_width="match_parent"`: ocupa todo el ancho del padre. `wrap_content`: solo lo que mide su contenido.
- `android:gravity="center_horizontal"`: centra horizontalmente los hijos.
- `padding` (espacio por dentro) y `layout_margin…` (espacio por fuera), en `dp`. El texto se mide en `sp` para respetar el tamaño de letra del teléfono.

**¿Por qué las cartas no están en el XML?** Serían 12 botones casi iguales. Es más corto crearlas en un ciclo `for` desde Java (como el arreglo de botones de la Calculadora), y así cambiar el tamaño del tablero es cambiar un número.

## 3. `activity_creditos.xml`: la pantalla de créditos

Otro `LinearLayout` vertical con `gravity="center"` (todo centrado): título "CRÉDITOS", el nombre de la app, "Desarrollado por:", los dos nombres en negritas y el botón `btnRegresar`. Toda la pantalla es fija; solo el botón tiene `id`, porque es lo único que Java necesita tocar.

---

## 4. `MainActivity.java`: el controlador del juego

### Constantes y atributos

```java
static final int PARES = 6;        // 6 pares = 12 cartas
static final int COLUMNAS = 3;     // 4 filas x 3 columnas
static final int ESPERA_MS = 900;  // tiempo que se ven dos cartas que no son pareja
static final String[] FIGURAS = {"🐶", "🐱", "🐸", "🦁", "🐵", "🐼"};

Memorama juego;                    // el modelo (las reglas)
Button[] cartas = new Button[12];  // los botones del tablero
MediaPlayer reproductor;           // reproduce los sonidos
Handler handler;                   // para ejecutar algo "después"
```

Las figuras son emojis (texto), así que no se necesitan imágenes. El modelo guarda números del 0 al 5 y `FIGURAS[numero]` los convierte en el dibujo.

### `onCreate(Bundle)`: lo primero que se ejecuta

1. `setContentView(R.layout.activity_main)` carga el XML.
2. `findViewById(...)` conecta cada `TextView` y `Button` del XML con una variable de Java.
3. Pone "Elaborado por: " + `BuildConfig.ALUMNO` (el nombre se define en `app/build.gradle`).
4. Asigna los clics con *lambdas*:
   - `btnReiniciar.setOnClickListener(v -> nuevoJuego());`
   - `btnCreditos.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CreditosActivity.class)));`
5. Llama a `crearTablero()` y a `nuevoJuego()`.

`v -> nuevoJuego()` es la forma corta de escribir `new View.OnClickListener() { public void onClick(View v) { nuevoJuego(); } }`.

### `crearTablero()`: crea las 12 cartas con dos `for`

```java
for (int fila = 0; fila < 4; fila++) {
    LinearLayout renglon = new LinearLayout(this);          // un renglón horizontal
    renglon.setOrientation(LinearLayout.HORIZONTAL);
    tablero.addView(renglon);

    for (int col = 0; col < 3; col++) {
        final int pos = fila * COLUMNAS + col;              // 0..11
        Button carta = new Button(this);
        carta.setOnClickListener(v -> tocarCarta(pos));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, alto, 1f);
        renglon.addView(carta, params);
        cartas[pos] = carta;
    }
}
```

- `pos = fila * 3 + col` convierte (fila, columna) en una posición del 0 al 11.
- `final int pos` es necesario para usar la variable dentro de la lambda; cada botón "recuerda" su propia posición.
- `LayoutParams(0, alto, 1f)`: ancho 0 con **peso 1**, así los 3 botones se reparten el ancho por partes iguales.
- `dp(105)` convierte 105 dp a píxeles según la densidad de la pantalla, para que se vea igual en cualquier teléfono.

### `nuevoJuego()`

```java
handler.removeCallbacksAndMessages(null);  // cancela un "ocultar" pendiente
juego = new Memorama(PARES, new Random()); // cartas revueltas
actualizarPantalla();
```

Crea un modelo nuevo (que revuelve las cartas) y vuelve a pintar. Se cancela el `Handler` por si el usuario reinicia justo cuando dos cartas iban a taparse.

### `tocarCarta(int pos)`: la función más importante

```java
Memorama.Resultado resultado = juego.voltear(pos);
switch (resultado) {
    case INVALIDA: return;                               // no pasa nada
    case PRIMERA:  reproducirSonido(R.raw.voltear); break;
    case PAR:      // acierto, o victoria si ya están todos
    case NO_PAR:   reproducirSonido(R.raw.error);
                   handler.postDelayed(() -> {
                       juego.ocultarNoPares();
                       actualizarPantalla();
                   }, ESPERA_MS);
                   break;
}
actualizarPantalla();
```

- Le pregunta al modelo qué pasó y, según la respuesta, elige sonido y mensaje.
- En `PAR` revisa `juego.terminado()`: si ya están los 6 pares, suena `victoria` y muestra "¡Ganaste en N intentos!".
- **`handler.postDelayed`** ejecuta el código 900 ms después **sin congelar la app**. No se usa `Thread.sleep()` porque bloquearía la pantalla y Android podría marcar la app como "no responde".

### `actualizarPantalla()`: pinta según el modelo

Recorre las 12 cartas y decide cómo se ven:

| Estado en el modelo | Texto | Color |
|---|---|---|
| `estaEmparejada(i)` | el emoji | verde |
| `esVisible(i)` | el emoji | blanco |
| ninguno | `?` | azul |

Después actualiza el marcador. La pantalla se vuelve a pintar completa después de cada cambio, así nunca muestra algo distinto a lo que dice el modelo.

### `reproducirSonido(int sonido)`: `MediaPlayer`

```java
if (reproductor != null) { reproductor.release(); reproductor = null; } // suelta el anterior
reproductor = MediaPlayer.create(this, sonido);                         // p. ej. R.raw.acierto
reproductor.setOnCompletionListener(mp -> { mp.release(); ... });       // libera al terminar
reproductor.start();
```

Es el mismo método de la app de instrumentos (clase del 17 nov). `release()` es importante: cada `MediaPlayer` ocupa memoria y recursos de audio del teléfono, así que se libera al terminar de sonar y en `onDestroy()`.

### `onDestroy()`

Cuando se cierra la app: cancela el `Handler` (para que no intente pintar una pantalla que ya no existe) y libera el `MediaPlayer`.

---

## 5. `CreditosActivity.java`: el controlador de créditos

```java
setContentView(R.layout.activity_creditos);
Button btnRegresar = findViewById(R.id.btnRegresar);
btnRegresar.setOnClickListener(v -> finish());
```

- Se abre desde `MainActivity` con un **`Intent`**: `startActivity(new Intent(MainActivity.this, CreditosActivity.class))`.
- `finish()` cierra la pantalla de créditos y Android regresa a la anterior. **El juego no se pierde**: `MainActivity` seguía abierta debajo, en la pila de actividades. El botón "atrás" del teléfono hace lo mismo.

---

## 6. `Memorama.java`: el modelo (las reglas)

### Datos

```java
int[] cartas;            // cartas[i] = número de figura en la posición i (cada número aparece 2 veces)
boolean[] visibles;      // true = boca arriba
boolean[] emparejadas;   // true = ya forma parte de un par encontrado
int primera = -1;        // posición de la 1.ª carta del turno (-1 = ninguna)
int segunda = -1;        // posición de la 2.ª carta cuando NO hubo par
int intentos, pares;
```

Ejemplo de tablero revuelto:

```
posición:  0  1  2  3  4  5  6  7  8  9 10 11
figura:    3  0  5  1  3  2  0  4  1  5  2  4
```

### Constructor y `revolver()`

Arma la lista `0,0,1,1,2,2,3,3,4,4,5,5` y la revuelve con `Collections.shuffle(lista, azar)`. Hay un segundo constructor que recibe un arreglo fijo: lo usan las pruebas para saber de antemano dónde está cada figura.

### `voltear(int pos)`: el corazón del juego

1. **Inválida** si `pos` está fuera del tablero, la carta ya está boca arriba o hay dos cartas sin pareja esperando taparse (`segunda != -1`). Esto evita que el usuario destape una tercera carta.
2. La marca visible.
3. Si es la primera del turno, guarda `primera = pos` y regresa **`PRIMERA`**.
4. Si es la segunda: suma un intento y compara `cartas[primera] == cartas[pos]`.
   - Iguales: marca las dos como emparejadas, suma un par y regresa **`PAR`**.
   - Distintas: guarda `segunda = pos` y regresa **`NO_PAR`**.

El resultado es un `enum` (`Resultado`), un tipo con solo esos 4 valores posibles, más claro que regresar números o cadenas.

### `ocultarNoPares()`

Vuelve a poner boca abajo `primera` y `segunda` y las reinicia a `-1`. La llama el `Handler` de `MainActivity` 900 ms después de un `NO_PAR`.

### `terminado()`

`pares == cartas.length / 2`: ganaste cuando encontraste 6 pares de 12 cartas.

---

## 7. `MemoramaTest.java`: pruebas JUnit

| Prueba | Qué verifica |
|---|---|
| `cadaFiguraApareceDosVeces` | Al revolver, cada figura aparece exactamente 2 veces |
| `encontrarUnPar` | Dos cartas iguales → `PAR`, quedan emparejadas, cuenta 1 intento |
| `noParSeOcultaDespues` | Dos distintas → `NO_PAR`; no deja voltear una tercera; `ocultarNoPares()` las tapa |
| `noSePuedeVoltearLaMismaCartaDosVeces` | Tocar la misma carta dos veces no cuenta como intento |
| `posicionFueraDelTableroEsInvalida` | Posiciones −1 y 12 se rechazan |
| `ganarElJuego` | Partida completa en un tablero de 4 cartas: `terminado()` y 3 intentos |

Se corren con `./gradlew testDebugUnitTest`, y también en GitHub Actions antes de compilar el APK.

---

## 8. Recursos y configuración

- **`res/raw/*.wav`**: los 4 sonidos. Se usan como `R.raw.voltear`, `R.raw.acierto`, `R.raw.error`, `R.raw.victoria`. Se generaron con `herramientas/generar_sonidos.py` (senos con caída exponencial), así que son originales.
- **`app/build.gradle`**: `compileSdk 35`, `minSdk 24` (Android 7 o más nuevo) y `buildConfigField "String", "ALUMNO", ...`, que crea la constante `BuildConfig.ALUMNO` con el nombre que se muestra en la pantalla principal.
- **`.github/workflows/memorama.yml`**: en cada `git push`, GitHub instala Java 21, corre las pruebas, compila el APK y lo deja para descargar en *Actions → Artifacts*.

---

## 9. Preguntas que puede hacer el profesor

| Pregunta | Respuesta corta |
|---|---|
| ¿Por qué no `Thread.sleep(900)`? | Bloquea el hilo de la interfaz: la app se congela y Android puede cerrarla. `Handler.postDelayed` programa la tarea sin bloquear |
| ¿Por qué `final int pos`? | La lambda usa la variable después de que el `for` termina; Java exige que no cambie, y así cada botón conserva su posición |
| ¿Qué pasa si toco una tercera carta mientras se ven dos distintas? | `voltear` regresa `INVALIDA` porque `segunda != -1`, y no pasa nada |
| ¿Para qué `release()`? | Libera la memoria y el canal de audio del `MediaPlayer`; sin eso, después de muchos sonidos la app falla |
| ¿Cómo se pasa a la pantalla de créditos? | Con un `Intent` explícito y `startActivity`; se regresa con `finish()` |
| ¿Cómo harías un tablero más grande? | Cambiar `PARES` y agregar emojis a `FIGURAS`; el `for` crea las cartas solo |
| ¿Por qué separar `Memorama.java`? | Las reglas no dependen de Android, así que se prueban con JUnit sin teléfono, y la Activity queda más corta |
