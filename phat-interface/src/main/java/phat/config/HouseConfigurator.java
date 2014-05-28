/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.config;

import phat.structures.houses.HouseFactory;

/**
 *
 * @author sala26
 */
public interface HouseConfigurator {
    public void addHouseType(String houseId, HouseFactory.HouseType type);
    public void setDebugNavMesh(boolean enabled);
}
