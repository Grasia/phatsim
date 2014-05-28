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
public class CreateHouseCommand extends PHATCommand {
    String id;
    HouseFactory.HouseType houseType;

    public CreateHouseCommand(String id, HouseFactory.HouseType houseType) {
        this(id, houseType, null);
    }
    
    public CreateHouseCommand(String id, HouseFactory.HouseType houseType, PHATCommandListener l) {
        super(l);
        this.id = id;
        this.houseType = houseType;
    }

    @Override
    public void runCommand(Application app) {
        HouseAppState houseAppState = app.getStateManager().getState(HouseAppState.class);
        
        if(houseAppState != null) {
            houseAppState.addHouse(id, houseType);
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
