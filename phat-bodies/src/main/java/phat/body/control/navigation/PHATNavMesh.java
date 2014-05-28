/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
