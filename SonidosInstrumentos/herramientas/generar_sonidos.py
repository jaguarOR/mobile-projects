"""Genera los 4 sonidos de la app (piano, campana, tambor y guitarra) por síntesis.

Solo usa la biblioteca estándar de Python; crea archivos .wav que luego se
convierten a .ogg con ffmpeg (ver README del proyecto). Así los sonidos son
originales y no tienen problemas de derechos de autor.

Uso:  python3 generar_sonidos.py <carpeta_salida>
"""
import math
import os
import random
import struct
import sys
import wave

FS = 44100  # muestras por segundo


def guardar(nombre, muestras, carpeta):
    pico = max(abs(m) for m in muestras) or 1.0
    escala = 0.89 * 32767 / pico  # normalizar a -1 dB
    ruta = os.path.join(carpeta, nombre + ".wav")
    with wave.open(ruta, "wb") as w:
        w.setnchannels(1)
        w.setsampwidth(2)
        w.setframerate(FS)
        w.writeframes(b"".join(struct.pack("<h", int(m * escala)) for m in muestras))
    print("ok", ruta)


def envolvente_ataque(i, ataque=0.005):
    """Rampa corta al inicio para evitar el 'clic'."""
    t = i / FS
    return min(1.0, t / ataque)


def nota_piano(f, dur):
    """Suma de armónicos con caída exponencial (más rápida en los agudos)."""
    n = int(FS * dur)
    armonicos = [(1, 1.0), (2, 0.55), (3, 0.30), (4, 0.18), (5, 0.10), (6, 0.06)]
    out = []
    for i in range(n):
        t = i / FS
        s = 0.0
        for k, a in armonicos:
            s += a * math.exp(-t * (2.2 + 1.3 * k)) * math.sin(2 * math.pi * f * k * t)
        out.append(s * envolvente_ataque(i))
    return out


def mezclar(pistas, dur):
    """pistas = [(inicio_en_segundos, muestras), ...]"""
    total = [0.0] * int(FS * dur)
    for inicio, muestras in pistas:
        o = int(inicio * FS)
        for i, m in enumerate(muestras):
            if o + i < len(total):
                total[o + i] += m
    return total


def piano():
    # Arpegio de Do mayor: C4 E4 G4 C5
    notas = [261.63, 329.63, 392.00, 523.25]
    pistas = [(0.18 * i, nota_piano(f, 1.6)) for i, f in enumerate(notas)]
    return mezclar(pistas, 2.2)


def campana():
    # Parciales inarmónicos típicos de una campana (relativos a la fundamental)
    f0 = 440.0
    parciales = [(0.5, 0.8, 0.9), (1.0, 1.0, 1.4), (1.19, 0.6, 1.8), (1.56, 0.45, 2.4),
                 (2.0, 0.5, 2.8), (2.51, 0.3, 3.6), (2.66, 0.25, 4.0), (3.01, 0.2, 5.0)]
    n = int(FS * 2.5)
    out = []
    for i in range(n):
        t = i / FS
        s = sum(a * math.exp(-t * c) * math.sin(2 * math.pi * f0 * r * t) for r, a, c in parciales)
        out.append(s * envolvente_ataque(i, 0.002))
    return out


def golpe_tambor(rnd):
    """Seno grave que baja de tono + ruido corto (el parche)."""
    n = int(FS * 0.7)
    out = []
    fase = 0.0
    for i in range(n):
        t = i / FS
        f = 55 + 95 * math.exp(-t * 18)  # de 150 Hz a 55 Hz
        fase += 2 * math.pi * f / FS
        cuerpo = math.exp(-t * 6) * math.sin(fase)
        ruido = 0.35 * math.exp(-t * 40) * (rnd.random() * 2 - 1)
        out.append((cuerpo + ruido) * envolvente_ataque(i, 0.001))
    return out


def tambor():
    rnd = random.Random(7)
    golpes = [0.0, 0.35, 0.55, 0.9]
    return mezclar([(t, golpe_tambor(rnd)) for t in golpes], 1.7)


def cuerda_karplus(f, dur, rnd):
    """Algoritmo Karplus-Strong: ruido en un búfer que se promedia (cuerda pulsada)."""
    largo = int(FS / f)
    bufer = [rnd.random() * 2 - 1 for _ in range(largo)]
    out = []
    for i in range(int(FS * dur)):
        j = i % largo
        siguiente = 0.996 * 0.5 * (bufer[j] + bufer[(j + 1) % largo])
        out.append(bufer[j])
        bufer[j] = siguiente
    return out


def guitarra():
    # Rasgueo de Mi mayor (E2 B2 E3 G#3 B3 E4) con 35 ms entre cuerdas
    rnd = random.Random(3)
    cuerdas = [82.41, 123.47, 164.81, 207.65, 246.94, 329.63]
    pistas = [(0.035 * i, cuerda_karplus(f, 2.2, rnd)) for i, f in enumerate(cuerdas)]
    return mezclar(pistas, 2.5)


if __name__ == "__main__":
    carpeta = sys.argv[1] if len(sys.argv) > 1 else "."
    os.makedirs(carpeta, exist_ok=True)
    guardar("piano", piano(), carpeta)
    guardar("campana", campana(), carpeta)
    guardar("tambor", tambor(), carpeta)
    guardar("guitarra", guitarra(), carpeta)
