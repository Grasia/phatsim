/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.structures.houses;

/**
 *
 * @author pablo
 */
public class HouseFactory {

    /**
     *
     */
    public enum HouseType {

        House3room2bath, Duplex
    }

    public static House createHouse(String houseId, HouseType type) {
        House house = null;

        switch (type) {
            case House3room2bath:
                house = new House(houseId, "Scenes/Structures/Houses/House3room2bath/House3room2bath.j3o");
                break;
            case Duplex:
                house = new House(houseId, "Scenes/Structures/Houses/Duplex_A_20110505ifc/Duplex_A_20110505.ifc.j3o");
                break;
        }
        return house;
    }
}
