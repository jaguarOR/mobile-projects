"""Genera los 4 sonidos del memorama por síntesis (solo biblioteca estándar).

    voltear.wav   - "tic" corto al voltear la primera carta
    acierto.wav   - dos notas que suben (par encontrado)
    error.wav     - dos notas graves que bajan (no son iguales)
    victoria.wav  - arpegio de Do mayor (ganaste)

Los archivos .wav se usan directamente en res/raw (MediaPlayer los reproduce).

Uso:  python3 herramientas/generar_sonidos.py app/src/main/res/raw
"""
import math
import os
import struct
import sys
import wave

FS = 22050  # muestras por segundo (suficiente para efectos cortos y pesa poco)


def nota(frecuencia, duracion, caida=6.0):
    """Seno con su segundo armónico y caída exponencial (suena a 'campanita')."""
    muestras = []
    for i in range(int(FS * duracion)):
        t = i / FS
        ataque = min(1.0, t / 0.005)  # rampa de 5 ms para evitar el 'clic'
        s = math.sin(2 * math.pi * frecuencia * t) + 0.3 * math.sin(4 * math.pi * frecuencia * t)
        muestras.append(s * ataque * math.exp(-t * caida))
    return muestras


def secuencia(notas, separacion, duracion_total, caida=6.0):
    """Toca varias notas, una cada 'separacion' segundos, mezcladas en una pista."""
    total = [0.0] * int(FS * duracion_total)
    for k, f in enumerate(notas):
        inicio = int(k * separacion * FS)
        for i, m in enumerate(nota(f, duracion_total, caida)):
            if inicio + i < len(total):
                total[inicio + i] += m
    return total


def guardar(nombre, muestras, carpeta):
    pico = max(abs(m) for m in muestras) or 1.0
    escala = 0.8 * 32767 / pico
    ruta = os.path.join(carpeta, nombre + ".wav")
    with wave.open(ruta, "wb") as w:
        w.setnchannels(1)
        w.setsampwidth(2)
        w.setframerate(FS)
        w.writeframes(b"".join(struct.pack("<h", int(m * escala)) for m in muestras))
    print("ok", ruta)


if __name__ == "__main__":
    carpeta = sys.argv[1] if len(sys.argv) > 1 else "."
    os.makedirs(carpeta, exist_ok=True)
    guardar("voltear", nota(1046.5, 0.12, caida=30), carpeta)                    # C6 muy corto
    guardar("acierto", secuencia([659.25, 880.0], 0.10, 0.45), carpeta)          # E5 -> A5
    guardar("error", secuencia([196.0, 164.8], 0.15, 0.45, caida=8), carpeta)    # G3 -> E3
    guardar("victoria", secuencia([523.25, 659.25, 783.99, 1046.5], 0.13, 1.2, caida=3), carpeta)
