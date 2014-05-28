/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.config.impl;

import phat.config.WorldConfigurator;
import phat.world.WorldAppState;

/**
 *
 * @author sala26
 */
public class WorldConfiguratorImpl implements WorldConfigurator {
    WorldAppState worldAppState;
    
    public WorldConfiguratorImpl(WorldAppState worldAppState) {
        this.worldAppState = worldAppState;
    }
    
    public WorldAppState getWorldAppState() {
        return worldAppState;
    }
    
    @Override
    public void setTime(int year, int month, int dayOfMonth, 
            int hour, int minute, int second) {
        this.worldAppState.setCalendar(year, month, dayOfMonth, hour, minute, second);
    }
    
    @Override
    public void setTimeVisible(boolean visible) {
        this.worldAppState.setVisibleCalendar(visible);
    }
    
    @Override
    public boolean isTimeVisible() {
        return this.worldAppState.isVisibleCalendar();        
    }
    
    public void setLandType(WorldAppState.LandType landType) {
        this.worldAppState.setLandType(landType);
    }
}
