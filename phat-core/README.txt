1. Testing camera sensor in a freefall scenario. Pictures taken by the camera are
displayed in a JFrame using CameraSensorListenerFrame.

mvn exec:java -Dexec.mainClass="phat.sensors.camera.CameraSensorAppTest"

2. It is the same scenario as before but an accelerometer sensor is attched to
the falling box. Also, sensor accelerations (x,y,z) are shown in a plot thanks 
to XYAccelerationsChar.

mvn exec:java -Dexec.mainClass="phat.sensors.accelerometer.CameraAndAccelerometerControlAppTest"

