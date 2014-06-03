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
