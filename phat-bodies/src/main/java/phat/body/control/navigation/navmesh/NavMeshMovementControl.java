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
package phat.body.control.navigation.navmesh;

import com.jme3.ai.navmesh.Cell;
import com.jme3.ai.navmesh.NavMesh;
import phat.body.control.navigation.AutonomousMovementControl;
import com.jme3.ai.navmesh.NavMeshPathfinder;
import com.jme3.ai.navmesh.Path.Waypoint;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;
import java.util.List;
import phat.body.control.navigation.AutonomousControlListener;
import phat.body.control.navigation.PathViewer;
import phat.body.control.navigation.StraightMovementControl;
import phat.body.control.physics.PHATCharacterControl;
import phat.util.SpatialUtils;

/**
 *
 * Clase que desplaza a un personaje desde donde se encuentre hasta el punto del
 * escenario indicado.
 *
 * @author pablo
 */
public class NavMeshMovementControl extends AbstractControl implements AutonomousMovementControl {

    private float minDistaceToTarget = 0.5f;
    private Vector3f targetLocation;
    private NavMeshPathfinder pathFinder;
    private boolean showPath = true;
    private PathViewer pathViewer;
    //Vector3f navMeshOffset;
    AutonomousControlListener listener;

    public void setListener(AutonomousControlListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean aimAt(Vector3f location) {
        return true;
    }

    @Override
    public Vector3f getAimDirection() {
        return null;
    }

    public float getDistanceToTarget(Vector3f l1, Vector3f l2) {
        Vector3f loc = l1.clone();
        Vector3f target = l2.clone();
        if (Math.abs(target.getY() - loc.getY()) < 2f) {
            return target.setY(0f).distance(loc.setY(0f));
        }
        return target.distance(loc);
    }

    @Override
    public boolean moveTo(Vector3f targetLocation) {
        if (targetLocation == null) {
            listener = null;
            finishControl();
            return true;
        }
        this.targetLocation = new Vector3f(targetLocation);
        PHATCharacterControl cc = spatial.getControl(PHATCharacterControl.class);
        Vector3f characterLocation = cc.getLocation();
        float distance = getDistanceToTarget(characterLocation, targetLocation);

        Geometry navMeshGeo = getGeoNavMesh();
        //navMeshOffset = navMeshGeo.getWorldTranslation();
        NavMesh navMesh = new NavMesh(navMeshGeo.getMesh());
        Cell closestCellToTarget = navMesh.findClosestCell(targetLocation/*.subtract(navMeshOffset)*/);
        //this.targetLocation.set(closestCellToTarget.getCenter());
        pathFinder = new NavMeshPathfinder(navMesh);
        //finalTargetLocation.set(location);            
        pathFinder.setEntityRadius(cc.getRadius());
        pathFinder.clearPath();
        pathFinder.setPosition(characterLocation/*.subtract(navMeshOffset)*/);
        pathFinder.warp(characterLocation/*.subtract(navMeshOffset)*/);

        if (pathFinder.computePath(closestCellToTarget.getCenter())) {
            return true;
        }

        this.targetLocation = null;
        return false;
    }

    public void setTargetLocation(Vector3f targetLocation) {
        this.targetLocation = targetLocation;
        moveTo(targetLocation);
    }

    @Override
    public Vector3f getTargetLocation() {
        return this.targetLocation;
    }

    private Node getWorldNode() {
        Node current = (Node) spatial;
        while (current != null && !current.getName().equals("World")) {
            current = current.getParent();
        }
        return current;
    }

    private Geometry getGeoNavMesh() {
        List<Spatial> houses = SpatialUtils.getSpatialsByRole(SpatialUtils.getRootNode(spatial), "House");
        for (Spatial house : houses) {
            //if (SpatialUtils.contains(house, ((Node) spatial).getChild(0))) {
                Node le = (Node) ((Node) house).getChild("LogicalEntities");
                return (Geometry) le.getChild("NavMesh");
            //}
        }
        return null;
    }

    @Override
    public void setSpatial(Spatial spatial) {
        if (spatial == null) {
            StraightMovementControl smc = this.spatial.getControl(StraightMovementControl.class);
            if (smc != null) {
                this.spatial.removeControl(smc);
            }
            if (pathViewer != null) {
                pathViewer.hidePath();
                pathViewer = null;
            }
        }
        super.setSpatial(spatial);
    }

    @Override
    public boolean isMoving() {
        return spatial.getControl(StraightMovementControl.class) != null;
    }

    @Override
    public Vector3f getLocation() {
        if (spatial != null) {
            PHATCharacterControl cc = getCharacterControl();
            if (cc != null) {
                return cc.getLocation();
            } else {
                return spatial.getWorldTranslation();
            }
        }
        return null;
    }

    private Vector3f goToNextWaypoint() {
        if (pathFinder.isAtGoalWaypoint()) {
            return targetLocation/*.add(navMeshOffset)*/;
        } else {
            pathFinder.goToNextWaypoint();
            Waypoint wp = pathFinder.getNextWaypoint();
            if (wp != null) {
                return wp.getPosition()/*.add(navMeshOffset)*/;
            }
        }
        return null;
    }

    private void moveToNewPoint(Vector3f destiny, float minDist) {
        StraightMovementControl straightMovementControl =
                new StraightMovementControl(0.1f);
        spatial.addControl(straightMovementControl);
        straightMovementControl.moveTo(destiny);
    }

    private boolean isWaypointReached() {
        return spatial.getControl(StraightMovementControl.class) == null;
    }

    @Override
    public void render(RenderManager rm, ViewPort vp) {
    }

    @Override
    public Control cloneForSpatial(Spatial spatial) {
        NavMeshMovementControl acc = new NavMeshMovementControl();
        acc.setSpatial(spatial);
        return acc;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
    }

    @Override
    public void read(JmeImporter im) throws IOException {
    }

    private PHATCharacterControl getCharacterControl() {
        return spatial.getControl(PHATCharacterControl.class);
    }

    private boolean destinyReached() {
        return getDistanceToTarget() < minDistaceToTarget;
    }

    private void finishControl() {
        if (pathViewer != null) {
            pathViewer.hidePath();
            pathViewer = null;
        }
        targetLocation = null;
        StraightMovementControl smc = spatial.getControl(StraightMovementControl.class);
        if (smc != null) {
            smc.stop();
        }
        if (listener != null) {
            listener.destinationReached(getLocation());
        }
    }

    @Override
    protected void controlUpdate(float tpf) {
        PHATCharacterControl cc = getCharacterControl();
        if (cc != null && cc.isEnabled()) {
            if (targetLocation != null) {
                if (pathViewer == null && showPath) {
                    pathViewer = new PathViewer(pathFinder.getPath(), spatial.getParent(), Vector3f.ZERO/*navMeshOffset*/);
                    pathViewer.showPath();
                }
                if (destinyReached()) {
                    finishControl();
                }
                if (isWaypointReached()) {
                    Vector3f nextPoint = goToNextWaypoint();
                    if (nextPoint == null) {
                        finishControl();
                    } else {
                        float dist = getCharacterControl().getRadius();
                        if (nextPoint.equals(targetLocation)) {
                            dist = minDistaceToTarget;
                        }
                        moveToNewPoint(nextPoint, dist);
                    }
                }
            }
        } else if (targetLocation != null) {
            finishControl();
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    @Override
    public float getSpeed() {
        return spatial.getUserData("Speed");
    }

    /**
     * @TODO redifine the distance method in order to include all distances
     * between waypoint.
     */
    @Override
    public float getDistanceToTarget() {
        return getDistanceToTarget(getLocation(), getTargetLocation());
    }

    @Override
    public void setMinDistance(float minDistance) {
        minDistaceToTarget = minDistance;
    }

    public boolean isShowPath() {
        return showPath;
    }

    public void setShowPath(boolean showPath) {
        this.showPath = showPath;
    }
}
