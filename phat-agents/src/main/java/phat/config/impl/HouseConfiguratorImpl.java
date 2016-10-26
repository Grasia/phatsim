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
package phat.config.impl;

import java.io.File;
import phat.config.HouseConfigurator;
import phat.structures.houses.HouseAppState;
import phat.structures.houses.HouseFactory;
import phat.structures.houses.commands.CreateHouseCommand;
import phat.structures.houses.commands.DebugShowHouseNavMeshCommand;
import phat.util.video.RecordVideoCommand;

/**
 *
 * @author sala26
 */
public class HouseConfiguratorImpl implements HouseConfigurator {
    HouseAppState houseAppState;
    
    public HouseConfiguratorImpl(HouseAppState houseAppState) {
        this.houseAppState = houseAppState;
        
    }
    
    public HouseAppState getHousedAppState() {
        return houseAppState;
    }
    
    @Override
    public void addHouseType(String houseId, HouseFactory.HouseType type) {
        this.houseAppState.runCommand(new CreateHouseCommand(houseId, type));
    }
    
    @Override
    public void setDebugNavMesh(boolean enabled) {
        this.houseAppState.runCommand(new DebugShowHouseNavMeshCommand(enabled));
    }
    
    @Override
    public void recordVideo(String fileName) {
        this.houseAppState.runCommand(new RecordVideoCommand(new File(fileName)));
    }
}
