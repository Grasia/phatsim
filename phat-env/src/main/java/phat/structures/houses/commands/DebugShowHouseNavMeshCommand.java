/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.structures.houses.commands;

import com.jme3.app.Application;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.structures.houses.HouseAppState;
import phat.structures.houses.HouseFactory;

/**
 *
 * @author pablo
 */
public class DebugShowHouseNavMeshCommand extends PHATCommand {
    boolean enable;
    
    public DebugShowHouseNavMeshCommand(boolean enable) {
        this(enable, null);        
    }
    
    public DebugShowHouseNavMeshCommand(boolean enable, PHATCommandListener l) {
        super(l);
        this.enable = enable;
    }

    @Override
    public void runCommand(Application app) {
        HouseAppState houseAppState = app.getStateManager().getState(HouseAppState.class);
        
        if(houseAppState != null) {
            houseAppState.setShowNavMesh(enable);
            setState(State.Success);
            return;
        }
        setState(State.Fail);
    }

    @Override
    public void interruptCommand(Application app) {
        setState(State.Fail);
    }
}
