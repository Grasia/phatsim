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
package phat.structures.houses.commands;

import com.jme3.app.Application;
import phat.commands.PHATCommParam;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandAnn;
import phat.commands.PHATCommandListener;
import phat.structures.houses.HouseAppState;
import phat.structures.houses.HouseFactory;

/**
 *
 * @author pablo
 */
@PHATCommandAnn(name="CreateHouse", type="env", debug = false)
public class CreateHouseCommand extends PHATCommand {
    String id;
    HouseFactory.HouseType houseType;

    public CreateHouseCommand() {
    }

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

    public void setId(String id) {
        this.id = id;
    }

    @PHATCommParam(mandatory=true, order=1)
    public void setHouseType(HouseFactory.HouseType houseType) {
        this.houseType = houseType;
    }
}
