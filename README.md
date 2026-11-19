# mobile-projects

Aplicaciones Android en Java de la materia Programación Móvil 1 (Ingeniería en Computación, UNAM). Las primeras apps construyen la interfaz en código con `Activity` y `LinearLayout`; desde `CaraDePapa` se usan layouts XML, como en los ejemplos de clase.

| Proyecto | Qué hace |
|---|---|
| [`PrimerPrograma/`](PrimerPrograma/) | Un botón que escribe el nombre del autor en un `TextView` |
| [`Calculadora/`](Calculadora/) | Suma, resta, multiplicación y división con punto decimal, botón C y "Error" al dividir entre cero. La lógica está en `Operaciones.java` y tiene pruebas JUnit |
| [`Galeria/`](Galeria/) | Galería de 4 imágenes con título sobre cada imagen, botones Anterior/Siguiente, contador y pantalla de créditos |
| [`CaraDePapa/`](CaraDePapa/) | Sr. Cara de Papa: arrastrar pelo, ojos, nariz, boca y pies al cuerpo (drag and drop con `Spinner`) y calificar de 0 a 10 qué tan cerca quedó cada pieza de su posición correcta. La lógica está en `Calificador.java` y tiene pruebas JUnit |
| [`SonidosInstrumentos/`](SonidosInstrumentos/) | Reproductor de sonidos con `MediaPlayer`: 4 instrumentos (piano, campana, tambor, guitarra) en `ImageButton` y el nombre del autor en la pantalla principal. Los sonidos se generan por síntesis con `herramientas/generar_sonidos.py` y las imágenes son vectores propios |

## Compilar

Requisitos: JDK 21 y Android SDK (compileSdk 35, minSdk 24). Versiones: AGP 8.7.3 · Gradle 8.11.1.

```bash
cd Calculadora
./gradlew assembleDebug        # APK en app/build/outputs/apk/debug/app-debug.apk
./gradlew testDebugUnitTest    # pruebas (Calculadora y CaraDePapa)
./gradlew installDebug         # instala en un teléfono conectado por USB
```

## Nombre del autor

El nombre que muestran las apps no está en el código: se lee de `local.properties` (que no se sube al repositorio) al compilar. Agrega esta línea en el `local.properties` de cada proyecto:

```properties
alumno.nombre=Tu Nombre
```

Si falta, las apps muestran "Tu nombre".
