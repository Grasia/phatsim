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
package phat.facilitator;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Represent the natural environment of the simulation, i.e., the ground, the
 * sky, the time and the light.
 *
 * @author pablo
 */
public class EnvironmetFacilitatorAppState extends AbstractAppState {
    
    List<EFConsumer> consumers = new ArrayList<EFConsumer>();
    List<EFProducer> producers = new ArrayList<EFProducer>();
    
    @Override
    public void initialize(AppStateManager stateManager, Application application) {
        super.initialize(stateManager, application);
        
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        
        for(EFProducer producer: producers) {
            
        }
    }

    public void register(EFConsumer consumer) {
        if(!consumers.contains(consumer)) {
            consumers.add(consumer);
        }
    }
    
    public void unregister(EFConsumer consumer) {
        consumers.remove(consumer);
    }
    
    public void register(EFProducer producer) {
        if(!producers.contains(producer)) {
            producers.add(producer);
        }
    }
    
    public void unregister(EFProducer producer) {
        producers.remove(producer);
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
    }
}