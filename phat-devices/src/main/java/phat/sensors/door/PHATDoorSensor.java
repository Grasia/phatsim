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
package phat.sensors.door;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import phat.commands.PHATCommand;
import phat.controls.FridgeDoorControl;
import phat.sensors.Sensor;
import phat.sensors.SensorListener;
import phat.util.SpatialUtils;
import phat.world.PHATCalendar;

/**
 *
 * @author pablo
 */
public class PHATDoorSensor extends Sensor {

    DoorData doorData;
    boolean debug = false;
    Node debugNode = new Node();
    PHATCalendar calendar;
    private FridgeDoorControl control;

    public PHATDoorSensor(String id, PHATCalendar calendar) {
        super(id);
        this.calendar = calendar;
        doorData = new DoorData(0, false);
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);

        if (spatial == null) {
            return;
        }
        doorData.opened = false;
    }

    private void getControl() {
        Node fridge = spatial.getParent();
        if (fridge.getChild("Hinge") != null) {
            control = fridge.getChild("Hinge").getControl(FridgeDoorControl.class);
        }
    }

    @Override
    protected void controlUpdate(float f) {
        if (control == null) {
            getControl();
        }
        if (control.getState().equals(FridgeDoorControl.STATE.OPENED)) {
            if (!doorData.opened) {
                doorData.opened = true;
                doorData.timestamp = calendar.getTimeInMillis();
                notifyListeners();
            }
        } else {
            if (doorData.opened) {
                doorData.opened = false;
                doorData.timestamp = calendar.getTimeInMillis();
                notifyListeners();
            }
        }
    }
    
    public DoorData getDoorData() {
        return doorData;
    }

    private void notifyListeners() {
        for (SensorListener al : listeners) {
            al.update(this, doorData);
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
