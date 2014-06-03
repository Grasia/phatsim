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
package phat.world;

import com.jme3.app.SimpleApplication;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import phat.structures.houses.*;
import com.jme3.system.AppSettings;
import java.util.logging.Logger;

/**
 * App for testing mesh navegation in a small house. Starting point is set by
 * left-clicking on the mouse. Ending point is set by right-clicking on the
 * mouse. Pressing space key an animated character follows the path generated.
 *
 * @author Pablo
 */
public class TestWorld extends SimpleApplication {

    private static final Logger logger = Logger.getLogger(TestHouse.class.getName());
    
    public static void main(String[] args) {
        TestWorld app = new TestWorld();
        
        AppSettings settings = new AppSettings(true);
        settings.setWidth(1280);
        settings.setHeight(720);
        app.setSettings(settings);
        app.setDisplayFps(true);
        app.setShowSettings(true);
        app.setPauseOnLostFocus(false);
        app.start();
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);
    }
    
    @Override
    public void simpleInitApp() {        
        //Debug.enableDebugGrid(30, assetManager, rootNode);
        
        flyCam.setMoveSpeed(10f);
        flyCam.setDragToRotate(true);
        
        WorldAppState worldAppState = new WorldAppState();                
        getStateManager().attach(worldAppState);
        
        worldAppState.setCalendar(2013, 1, 1, 5, 0, 0);
        worldAppState.setVisibleCalendar(true);
        worldAppState.setEnableShadows(true);
        worldAppState.setSpeedFactor(1000);
        worldAppState.setLandType(WorldAppState.LandType.TwoHouses);
        
        cam.setLocation(new Vector3f(-0.40728146f, 7.5530906f, 20.056808f));
        cam.setRotation(new Quaternion(0.0010348918f, 0.9921636f, -0.12466925f, 0.008235897f));
    }
}
