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
package phat.app;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;

/**
 *
 * @author pablo
 */
public class PHATApplication extends SimpleApplication {

    PHATInitAppListener initializer;
    PHATFinalizeAppListener finalizer;
    private boolean initialized = false;

    public PHATApplication(PHATInitAppListener initializer, AppState... states) {
        super(states);
        this.initializer = initializer;
        setShowSettings(false);
        setPauseOnLostFocus(false);
    }

    public PHATApplication(PHATInitAppListener initializer) {
        super();
        this.initializer = initializer;
        setShowSettings(false);
        setPauseOnLostFocus(false);
    }

    @Override
    public void simpleInitApp() {
        initializer.init(this);
        initialized = true;
        flyCam.setDragToRotate(true);
    }

    @Override
    public void update() {
        super.update();

        if (speed == 0) {
            timer.update();

            if (inputEnabled) {
                inputManager.update(timer.getTimePerFrame());
            }

            float tpf = timer.getTimePerFrame() * 1f;
            
            guiNode.updateLogicalState(tpf);
            guiNode.updateGeometricState();

            // render states
            stateManager.render(renderManager);
            renderManager.render(tpf, context.isRenderable());
            simpleRender(renderManager);
            stateManager.postRender();

        }
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setFinalizer(PHATFinalizeAppListener finalizer) {
        this.finalizer = finalizer;
    }

    @Override
    public void finalize() throws Throwable {
        super.finalize();
        if (finalizer != null) {
            finalizer.finalize(this);
        }
    }

    public float getSimSpeed() {
        return super.speed;
    }

    public void setSimSpeed(float speed) {
        super.speed = speed;
    }
}
