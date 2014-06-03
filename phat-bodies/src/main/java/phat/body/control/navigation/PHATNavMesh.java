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
package phat.body.control.navigation;

import com.jme3.ai.navmesh.Cell;
import com.jme3.ai.navmesh.Cell.ClassifyResult;
import com.jme3.ai.navmesh.Line2D;
import com.jme3.ai.navmesh.NavMesh;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;

/**
 *
 * @author sala26
 */
public class PHATNavMesh extends NavMesh {

    public PHATNavMesh(Mesh mesh) {
        super(mesh);
    }

    @Override
    public Cell findClosestCell(Vector3f point) {
        Cell result = null;
        float minDist = Float.MAX_VALUE;
        for (int i = 0; i < getNumCells(); i++) {
            Cell cell = getCell(i);
            float dist = cell.getCenter().distance(point);
            if (dist < minDist) {
                result = cell;
                minDist = dist;
            }
        }
        return result;
    }
}
