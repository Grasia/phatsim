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

import java.util.concurrent.Callable;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.scene.Spatial;

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

        BulletAppState bullet = stateManager.getState(BulletAppState.class);
        
        if (speed == 0) {
            timer.update();
            
            if(bullet != null) {
                bullet.setEnabled(false);
            }
            
            final float tpf = timer.getTimePerFrame();
            
            if (inputEnabled) {
                inputManager.update(tpf);
            }

            guiNode.updateLogicalState(tpf);
            guiNode.updateGeometricState();

            // render states
            stateManager.render(renderManager);
			// render is called in the render thread
			// otherwise, an exception could be called
            this.enqueue(new Callable<Spatial>() {
            	// to avoid "java.lang.IllegalStateException: Scene graph is not properly updated for rendering."
                public Spatial call() throws Exception {
                    renderManager.render(tpf, context.isRenderable());
                    simpleRender(renderManager);
                    stateManager.postRender();
                    return null;
                }
            });
        

        } else if(bullet != null) {
            bullet.setEnabled(true);
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
