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
package phat.devices.commands;

import com.jme3.app.Application;
import com.jme3.asset.TextureKey;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.texture.Texture;
import java.util.logging.Level;
import phat.commands.PHATCommParam;
import phat.commands.PHATCommandAnn;
import phat.commands.PHATCommandListener;
import phat.devices.DevicesAppState;
import phat.devices.smartphone.SmartPhoneFactory;
import static phat.devices.smartphone.SmartPhoneFactory.assetManager;
import phat.util.Debug;

/**
 *
 * @author pablo
 */
@PHATCommandAnn(name = "CreateScreen", type = "device", debug = false)
public class CreateScreenCommand extends PHATDeviceCommand {

    private String screenId;
    private String image;
    private float width;
    private float height;
    private Vector3f location = new Vector3f();
    private Vector3f direction = new Vector3f();
    private boolean attachCoordinateAxes = false;

    public CreateScreenCommand() {
    }

    public CreateScreenCommand(String smartphoneId) {
        this(smartphoneId, null);
    }

    public CreateScreenCommand(String smartphoneId, PHATCommandListener listener) {
        super(listener);
        this.screenId = smartphoneId;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    @Override
    public void runCommand(Application app) {
        DevicesAppState devicesAppState = app.getStateManager().getState(DevicesAppState.class);
        
        Node screenNode = new Node();
        Geometry screen = SmartPhoneFactory.createDisplayGeometry(screenId, width*0.9f, height*0.9f);
        screenNode.attachChild(screen);
        
        //assetManager.registerLocator("https://jmonkeyengine.googlecode.com/svn/BookSamples/assets/Textures/", UrlLocator.class);
        TextureKey key = new TextureKey(image, false);
        key.setGenerateMips(true);
        Texture tex = assetManager.loadTexture(key);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", tex);
        screen.setMaterial(mat);

        /*float aspect = tex.getImage().getWidth() / (float) tex.getImage().getHeight();
        screen.setLocalScale(new Vector3f(aspect * 1.5f, 1.5f, 1));
        screen.center();*/
        
        screenNode.setLocalTranslation(location);
        screenNode.getLocalRotation().lookAt(direction, Vector3f.UNIT_Y);
        
        if (attachCoordinateAxes) {
            Debug.attachCoordinateAxes(Vector3f.ZERO, 0.5f, SmartPhoneFactory.assetManager, screenNode);
        }
        
        devicesAppState.addDevice(screenId, screenNode);
        
        setState(State.Success);
    }

    @Override
    public void interruptCommand(Application app) {
        setState(State.Interrupted);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + screenId + ")";
    }

    public CreateScreenCommand setAttachCoordinateAxes(boolean attachCoordinateAxes) {
        this.attachCoordinateAxes = attachCoordinateAxes;
        return this;
    }
    
    public String getScreenId() {
        return screenId;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Vector3f getLocation() {
        return location;
    }

    public void setLocation(Vector3f location) {
        this.location.set(location);
    }
    
    public void setLocation(float x, float y, float z) {
        this.location.set(x,y,z);
    }

    public Vector3f getDirection() {
        return direction;
    }

    public void setDirection(Vector3f direction) {
        this.direction.set(direction);
    }
    
    public void setDirection(float x, float y, float z) {
        this.direction.set(x,y,z);
    }

    @PHATCommParam(mandatory = true, order = 1)
    public void setScreenId(String screenId) {
        this.screenId = screenId;
    }

    @PHATCommParam(mandatory = true, order = 2)
    public void setImage(String image) {
        this.image = image;
    }

    @PHATCommParam(mandatory = true, order = 3)
    public void setWidth(float width) {
        this.width = width;
    }

    @PHATCommParam(mandatory = true, order = 4)
    public void setHeight(float height) {
        this.height = height;
    }
}
