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
package phat;

import phat.app.PHATApplication;
import phat.util.PHATUtils;

/**
 *
 * @author pablo
 */
public class ArgumentProcessor {
    String[] args;

    public ArgumentProcessor(String[] args) {
        this.args = args;
        
        System.out.println("\n\n\n\n\n----------------");
        System.out.println("Arguments:");
        System.out.println("----------------");
        for(String s: args) {
            System.out.println(s);
        }
        System.out.println("----------------\n\n\n\n\n");
    }
    
    public void initialize(PHATInterface inter) {
        if(PHATUtils.contains(args, "-ml")) {
            inter.setMultiListener(true);
        }
        if(PHATUtils.contains(args, "-fps")) {
            inter.setDisplayFPS(true);
        }
        if(PHATUtils.contains(args, "-sv")) {
            inter.setStatView(true);
        }
    }
}
