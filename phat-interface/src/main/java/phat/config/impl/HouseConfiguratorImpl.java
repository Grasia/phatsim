/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.config.impl;

import phat.config.HouseConfigurator;
import phat.config.WorldConfigurator;
import phat.structures.houses.HouseAppState;
import phat.structures.houses.HouseFactory;
import phat.structures.houses.commands.CreateHouseCommand;
import phat.structures.houses.commands.DebugShowHouseNavMeshCommand;
import phat.world.WorldAppState;

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
}
