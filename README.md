# mobile-projects

Aplicaciones Android en Java de la materia Programación Móvil 1 (Ingeniería en Computación, UNAM FES Aragón), por **Ezequiel Olivera Reyes**. Las primeras apps construyen la interfaz en código con `Activity` y `LinearLayout`; desde `CaraDePapa` se usan layouts XML, como en los ejemplos de clase.

| Proyecto | Qué hace |
|---|---|
| [`PrimerPrograma/`](PrimerPrograma/) | Un botón que escribe el nombre del autor en un `TextView` |
| [`Calculadora/`](Calculadora/) | Suma, resta, multiplicación y división con punto decimal, botón C y "Error" al dividir entre cero. La lógica está en `Operaciones.java` y tiene pruebas JUnit |
| [`Galeria/`](Galeria/) | Galería de 4 imágenes con título sobre cada imagen, botones Anterior/Siguiente, contador y pantalla de créditos |
| [`CaraDePapa/`](CaraDePapa/) | Sr. Cara de Papa: arrastrar pelo, ojos, nariz, boca y pies al cuerpo (drag and drop con `Spinner`) y calificar de 0 a 10 qué tan cerca quedó cada pieza de su posición correcta. La lógica está en `Calificador.java` y tiene pruebas JUnit |
| [`SonidosInstrumentos/`](SonidosInstrumentos/) | Reproductor de sonidos con `MediaPlayer`: 4 instrumentos (piano, campana, tambor, guitarra) en `ImageButton` y el nombre del autor en la pantalla principal. Los sonidos se generan por síntesis con `herramientas/generar_sonidos.py` y las imágenes son vectores propios |
| [`Memorama/`](Memorama/) | **Proyecto final.** Memorama de 6 pares con sonidos (voltear, acierto, error, victoria), marcador de intentos y botón Reiniciar. La lógica está en `Memorama.java` y tiene pruebas JUnit. Se compila en la nube con GitHub Actions ([`memorama.yml`](.github/workflows/memorama.yml)); ver su README |

## Compilar

Requisitos: JDK 21 y Android SDK (compileSdk 35, minSdk 24). Versiones: AGP 8.7.3 · Gradle 8.11.1.

```bash
cd Calculadora
./gradlew assembleDebug        # APK en app/build/outputs/apk/debug/app-debug.apk
./gradlew testDebugUnitTest    # pruebas (Calculadora y CaraDePapa)
./gradlew installDebug         # instala en un teléfono conectado por USB
```

## Autor

**Ezequiel Olivera Reyes**, Ingeniería en Computación, UNAM FES Aragón.

Todas las apps muestran el nombre del autor en pantalla. Para compilar una copia con otro nombre, agrega esta línea al `local.properties` del proyecto (ese archivo no se sube al repositorio):

```properties
alumno.nombre=Otro Nombre
```
