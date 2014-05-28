$mvn exec:java -Dexec.mainClass="phat.hais.FromBathRoom1ToHob"
Ejemplo de casa con un paciente y un familiar. Secuencia de aciones:
1. PACIENTE: se gira en el baño y se cae.
2. FAMILIAR: gira a la izquierda como si oyese el ruido.
3. FAMILIAR: va andando rapido hacia el baño.
4. FAMILIAR: se tropieza con la botella que hay en medio del pasillo.


$mvn compile exec:java -Dexec.mainClass="phat.examples.android.AudioAndroid"
Prueba de sonido con Emulador Android.
Pasos necesarios:
1. Crear un emulador Android llamado "Smartphone1" con espacio en la sd.
2. Arrancar el emulador.
$emulator -dpi-device 240 -scale 0.5 -avd Smartphone1
3. El emulador debe tener instalada la aplicación mic-rms-app
$cd phat-android/mic-rms-app
$mvn clean install android:deploy
4. Arrancar la simulacion. (La simualción lanza el emulador si no está arrancado
pero en linux se queda colgado esperando a que se arranque, por tanto conviene
de momento tenerlo arrancado)
$mvn compile exec:java -Dexec.mainClass="phat.examples.android.AudioAndroid"

PARA que funcione mejor la experiencia, conviene tener la aceleración hardware configurada. En linnux, para comprobar si la aceleración va bien, escribir:
./emulator -dpi-device 240 -scale 0.5 -avd Smartphone1 -gpu on -qemu -m 512 -enable-kvm
