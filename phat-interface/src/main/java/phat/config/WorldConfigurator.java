/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.config;

import phat.world.WorldAppState;

/**
 *
 * @author sala26
 */
public interface WorldConfigurator {
    public void setTime(int year, int month, int dayOfMonth, 
            int hour, int minute, int second);
    
    public void setTimeVisible(boolean visible);
    
    public boolean isTimeVisible();
    
    public void setLandType(WorldAppState.LandType landType);
}
