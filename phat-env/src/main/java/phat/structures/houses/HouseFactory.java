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
