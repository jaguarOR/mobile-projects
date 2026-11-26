# Memorama con sonidos

Juego de memoria para Android (Java). El tablero tiene 12 cartas boca abajo (6 pares de animales). Tocas dos cartas: si son iguales se quedan descubiertas en verde, y si no, se vuelven a tapar después de un momento. Ganas cuando encuentras los 6 pares. Cada acción tiene su sonido.

| Acción | Sonido | Mensaje |
|---|---|---|
| Voltear la primera carta | `voltear.wav` ("tic") | — |
| Encontrar un par | `acierto.wav` (dos notas que suben) | ¡Par encontrado! |
| Fallar | `error.wav` (dos notas graves que bajan) | No son iguales |
| Encontrar el último par | `victoria.wav` (arpegio) | ¡Ganaste en N intentos! |

En pantalla también aparecen el nombre del alumno, el marcador (intentos y pares) y un botón **Reiniciar**.

## Estructura

```
Memorama/
├── app/src/main/java/com/example/memorama/
│   ├── Memorama.java        ← reglas del juego (Java puro, sin Android)
│   └── MainActivity.java    ← pantalla: botones, colores, sonidos
├── app/src/main/res/
│   ├── layout/activity_main.xml   ← título, nombre, marcador, tablero vacío, botón
│   └── raw/*.wav                  ← los 4 sonidos
├── app/src/test/.../MemoramaTest.java  ← 6 pruebas JUnit de las reglas
└── herramientas/generar_sonidos.py     ← crea los .wav por síntesis
```

La idea principal es **separar las reglas de la pantalla**: `Memorama.java` no sabe nada de botones ni de sonidos, solo de cartas. Por eso se puede probar con JUnit sin teléfono, igual que `Operaciones.java` en la Calculadora.

## Cómo funciona (para explicarlo en clase)

### 1. Las cartas son un arreglo de números

```
posición:  0  1  2  3  4  5  6  7  8  9 10 11
figura:    3  0  5  1  3  2  0  4  1  5  2  4   ← revuelto con Collections.shuffle
```

Cada número del 0 al 5 aparece dos veces, y la pantalla lo traduce a un emoji: `FIGURAS[3]` = 🦁. Además hay dos arreglos de `boolean`: `visibles[]` (la carta está boca arriba) y `emparejadas[]` (la carta ya forma parte de un par encontrado).

### 2. Un turno = dos toques

`voltear(pos)` regresa qué pasó:

| Resultado | Cuándo | Qué hace la pantalla |
|---|---|---|
| `INVALIDA` | la carta ya estaba volteada o hay dos esperando a taparse | nada |
| `PRIMERA` | es la primera carta del turno | sonido "tic" |
| `PAR` | la segunda carta tiene la misma figura | sonido de acierto, las dos se pintan de verde |
| `NO_PAR` | la segunda carta es distinta | sonido de error y, 900 ms después, `ocultarNoPares()` |

Mientras las dos cartas distintas siguen visibles, `voltear` regresa `INVALIDA`; así el usuario no puede destapar una tercera carta.

### 3. La espera con `Handler`

```java
handler.postDelayed(() -> {
    juego.ocultarNoPares();
    actualizarPantalla();
}, ESPERA_MS);
```

`postDelayed` ejecuta el código después de 900 ms sin congelar la app (no se usa `Thread.sleep`). Al reiniciar o cerrar la app se cancela con `removeCallbacksAndMessages(null)`.

### 4. El tablero se crea con código

En `crearTablero()` dos ciclos `for` crean 4 renglones (`LinearLayout` horizontal) con 3 `Button` cada uno. Cada botón recibe un ancho de peso `1f`, así que los 3 se reparten el ancho de la pantalla. Es el mismo patrón de arreglo de botones de la Calculadora: `final int pos` guarda la posición que usa el `OnClickListener`.

### 5. `actualizarPantalla()` dibuja según el estado

Para cada carta: si está **emparejada**, muestra el emoji con fondo verde; si está **visible**, el emoji con fondo blanco; si no, un `?` con fondo azul. Toda la pantalla se vuelve a pintar después de cada toque, así la vista siempre coincide con el estado del juego.

### 6. Sonidos con `MediaPlayer`

`reproducirSonido()` es el mismo método de la app de instrumentos (clase del 17 nov): libera el reproductor anterior con `release()`, crea uno nuevo con `MediaPlayer.create(this, R.raw.acierto)` y lo libera al terminar (`setOnCompletionListener`) y en `onDestroy()`.

Los sonidos son originales: `herramientas/generar_sonidos.py` los calcula con senos y caída exponencial (solo biblioteca estándar de Python). Para regenerarlos:

```bash
python3 herramientas/generar_sonidos.py app/src/main/res/raw
```

## Compilar en la nube con GitHub Actions (sin Android Studio)

El archivo [`.github/workflows/memorama.yml`](../.github/workflows/memorama.yml) le pide a GitHub una máquina Ubuntu que ya trae el SDK de Android. Esa máquina:

1. descarga el código (`actions/checkout`);
2. instala Java 21 (`actions/setup-java`);
3. prepara Gradle con caché (`gradle/actions/setup-gradle`), así la segunda compilación es más rápida;
4. escribe `local.properties` con la ruta del SDK y el nombre del alumno;
5. corre las pruebas JUnit (si alguna falla, se detiene y no genera el APK);
6. compila el APK con `./gradlew assembleDebug`;
7. sube el APK como *artifact* llamado `memorama-apk` (se guarda 7 días).

Se ejecuta solo cuando haces `git push` con cambios en `Memorama/`, o a mano desde **Actions → Memorama APK → Run workflow**.

### Poner tu nombre sin escribirlo en el código

El repositorio es público, así que el nombre no va en el código. Se guarda como **secreto** de GitHub:

1. En GitHub: **Settings → Secrets and variables → Actions → New repository secret**.
2. Nombre: `ALUMNO_NOMBRE`. Valor: tu nombre completo.
3. Vuelve a ejecutar el workflow.

Si no creas el secreto, la app muestra "Tu nombre". Aviso: en un repositorio público, cualquier persona con cuenta de GitHub puede descargar el artifact, y el APK lleva el nombre dentro. Por eso solo se guarda 7 días. Si no quieres eso, compila el APK de la entrega en tu computadora (siguiente sección).

### Descargar e instalar el APK en el teléfono

1. En GitHub: **Actions →** la ejecución con palomita verde **→ Artifacts → memorama-apk** (descarga un `.zip`).
2. Descomprime el `.zip`: adentro está `app-debug.apk`.
3. Pásalo al teléfono (cable, Drive o Telegram), ábrelo y permite "Instalar apps de origen desconocido".
   O, con el teléfono conectado por USB: `adb install -r app-debug.apk`.

## Compilar en la computadora (opcional)

No abre Android Studio, solo Gradle desde la terminal (la memoria está limitada a 1 GB en `gradle.properties`):

```bash
source ../entorno.sh
./gradlew testDebugUnitTest   # 6 pruebas
./gradlew assembleDebug       # APK en app/build/outputs/apk/debug/app-debug.apk
./gradlew installDebug        # instala en el teléfono conectado por USB
```

El nombre se lee de `local.properties` (no se sube a git): `alumno.nombre=Tu Nombre`.

## Posibles mejoras

- Elegir la dificultad: 4, 6 u 8 pares.
- Cronómetro y mejor marca guardada con `SharedPreferences`.
- Un sonido distinto para cada animal.
