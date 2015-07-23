/*
 * Copyright (C) 2014 Pablo Campillo-Sanchez <pabcampi@ucm.es>
 *
 * This software has been developed as part of the 
 * SociAAL project directed by Jorge J. Gomez Sanz
 * (http://grasia.fdi.ucm.es/sociaal)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package phat.devices.sensors.presence;

import phat.sensors.presence.PHATPresenceSensor;
import phat.sensors.presence.PresenceStatePanel;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;

import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;

import phat.app.PHATApplication;
import phat.app.PHATInitAppListener;
import phat.body.BodiesAppState;
import phat.body.commands.RandomWalkingCommand;
import phat.body.commands.SetBodyInCoordenatesCommand;
import phat.body.commands.SetSpeedDisplacemenetCommand;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.devices.DevicesAppState;
import phat.devices.commands.CreatePresenceSensorCommand;
import phat.util.SpatialFactory;
import phat.world.WorldAppState;

/**
 *
 * @author pablo
 */
public class PresenceSensorTest implements PHATInitAppListener, PHATCommandListener {

    private static final Logger logger = Logger.getLogger(PresenceSensorTest.class.getName());
    BodiesAppState bodiesAppState;
    DevicesAppState devicesAppState;
    WorldAppState worldAppState;
    SimpleApplication app;
    
    JFrame sensorMonitor;
    
    public static void main(String[] args) {
        PresenceSensorTest test = new PresenceSensorTest();
        PHATApplication phat = new PHATApplication(test);
        phat.setDisplayFps(true);
        phat.setDisplayStatView(false);
        AppSettings settings = new AppSettings(true);
        settings.setTitle("PHAT");
        settings.setWidth(1280);
        settings.setHeight(960);
        phat.setSettings(settings);
        phat.start();
    }

    @Override
    public void init(SimpleApplication app) {
        SpatialFactory.init(app.getAssetManager(), app.getRootNode());
        this.app = app;
        
        AppStateManager stateManager = app.getStateManager();

        app.getFlyByCamera().setMoveSpeed(10f);

        app.getCamera().setLocation(
                new Vector3f(0.661027f, 16.514854f, 17.928465f));
        app.getCamera().setRotation(
                new Quaternion(0.005797257f, 0.91812664f, -0.39601657f, 0.013443171f));

        BulletAppState bulletAppState = new BulletAppState();
        bulletAppState.setThreadingType(BulletAppState.ThreadingType.PARALLEL);
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().setAccuracy(1 / 60f);
        bulletAppState.setDebugEnabled(true);

        worldAppState = new WorldAppState();
        worldAppState.setLandType(WorldAppState.LandType.Grass);
        app.getStateManager().attach(worldAppState);
        worldAppState.setCalendar(2013, 1, 1, 12, 0, 0);

        //Debug.enableDebugGrid(10, app.getAssetManager(), app.getRootNode());
        
        bodiesAppState = new BodiesAppState();
        stateManager.attach(bodiesAppState);

        bodiesAppState.createBody(BodiesAppState.BodyType.ElderLP, "Patient");
        bodiesAppState.runCommand(new SetBodyInCoordenatesCommand("Patient", Vector3f.ZERO));
        bodiesAppState.runCommand(new RandomWalkingCommand("Patient", true));
        
        bodiesAppState.runCommand(new SetSpeedDisplacemenetCommand("Patient", 1f));
        
        
        // Presence Sensor
        
        /*Geometry sensorBody = SpatialFactory.createCube(
                new Vector3f(0.1f, 0.1f, 0.1f), ColorRGBA.Green);
        */
        
        devicesAppState = new DevicesAppState();
        stateManager.attach(devicesAppState);
        
        createBodyPresenceSensor("PresenceSensor1", 
                new Vector3f(2f, 3f, 3f), 
                new Vector3f(45f, -90f, 0f));
        
        CreatePresenceSensorCommand ps1 = new CreatePresenceSensorCommand("PresenceSensor1", this);
        ps1.setEnableDebug(true);
        devicesAppState.runCommand(ps1);
        
        createBodyPresenceSensor("PresenceSensor2", 
                new Vector3f(-2f, 3f, 10f), 
                new Vector3f(45f, 90f, 0f));
        
        CreatePresenceSensorCommand ps2 = new CreatePresenceSensorCommand("PresenceSensor2", this);
        ps2.setEnableDebug(true);
        devicesAppState.runCommand(ps2);
                        
        sensorMonitor = new JFrame("Sensor Monitoring");
        JPanel content = new JPanel();
        sensorMonitor.setContentPane(content);
        sensorMonitor.setVisible(true); 
    }
    
    void createBodyPresenceSensor(String id, Vector3f pos, Vector3f dir) {
        Node devicePS1 = new Node();
        devicePS1.setUserData("ROLE", "PresenceSensor");
        devicePS1.setUserData("ID", id);
        devicePS1.move(pos);
        devicePS1.rotate(new Quaternion().fromAngles(dir.x, dir.y, dir.z));
        app.getRootNode().attachChild(devicePS1);
    }
        
    public void cleanUp() {
        sensorMonitor.dispose();
    }

    @Override
    public void commandStateChanged(PHATCommand command) {
        if (command instanceof CreatePresenceSensorCommand) {
            CreatePresenceSensorCommand cpsc = (CreatePresenceSensorCommand) command;
            Node psNode = devicesAppState.getDevice(cpsc.getPresenceSensorId());
            if (psNode != null) {
                PHATPresenceSensor psControl = psNode.getControl(PHATPresenceSensor.class);
                if (psControl != null) {
                    PresenceStatePanel psp1 = new PresenceStatePanel();
                    psControl.add(psp1);
                    sensorMonitor.getContentPane().add(psp1);
                    sensorMonitor.pack();
                }
            }
        }
    }
}
