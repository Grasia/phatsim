/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

    public boolean isInitialized() {
        return initialized;
    }

    public void setFinalizer(PHATFinalizeAppListener finalizer) {
        this.finalizer = finalizer;
    }

    @Override
    public void finalize() throws Throwable {
        super.finalize();
        if(finalizer != null) {
            finalizer.finalize(this);
        }
    }
}
