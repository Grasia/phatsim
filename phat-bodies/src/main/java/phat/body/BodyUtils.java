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
package phat.body;

import com.jme3.scene.Node;

/**
 *
 * @author pablo
 */
public class BodyUtils {
    
    public enum BodyPosture {Standing, Sitting, Lying, Falling}
    public static String BodyPostureKey = "BodyPosture";
    
    public static boolean isBodyPosture(Node body, BodyPosture posture) {
        return posture.name().equals(body.getUserData(BodyUtils.BodyPostureKey));
    }
    
    public static void setBodyPosture(Node body, BodyPosture posture) {
        body.setUserData(BodyUtils.BodyPostureKey, posture.name());
    }
    
    public static BodyPosture getBodyPosture(Node body) {
        String bodyPosture = body.getUserData(BodyPostureKey);
        if(bodyPosture != null) {
            return BodyPosture.valueOf(bodyPosture);
        }
        return null;
    }
}
