/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
