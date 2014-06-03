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
package phat.mason.space;

import sim.field.continuous.Continuous3D;
import sim.util.Double3D;

/**
 *
 * @author Pablo
 */
public class PHATContinuous3D extends Continuous3D {
    
    public PHATContinuous3D(Continuous3D copy) {
        super(copy);
    }
    
    public PHATContinuous3D(double discretization, double width, double height, double length) {
        super(discretization, width, height, length);
        
    }
    
    public static void main(String[] args) {
        PHATContinuous3D world = new PHATContinuous3D(1.0, 10.0, 10.0, 10.0);
        
        world.setObjectLocation(new Object(), new Object());        
    }
    
    /**
     *
     * @param object
     * @param location
     * @return
     */
    /*public boolean setObjectLocation(Object object, Double3D location) {
        boolean result = super.setObjectLocation(object, location);
        System.out.println("Result = "+result);
        
        if(result) {
            // change locatio in PHAT physics space
        }
        
        return result;
    }*/
    
    @Override
    public boolean setObjectLocation(Object object, Object location) {
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        boolean result = super.setObjectLocation(object, location);
        System.out.println("Result = "+result);
        
        if(result) {
            // change locatio in PHAT physics space
            
        }
        
        return result;
    }
}
