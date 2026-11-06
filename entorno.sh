# Uso: source ~/ProgramacionMovil1/entorno.sh
# Activa JDK 21 y el SDK de Android solo en esta terminal (el Java 27 del sistema no se toca).
export JAVA_HOME=/home/jaguar/.local/opt/jdk-21.0.13+9
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$JAVA_HOME/bin:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH
