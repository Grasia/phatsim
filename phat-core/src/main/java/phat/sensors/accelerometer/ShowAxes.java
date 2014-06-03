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
package phat.sensors.accelerometer;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Line;
import phat.util.SpatialFactory;
import phat.sensors.Sensor;
import phat.sensors.SensorData;
import phat.sensors.SensorListener;

/**
 *
 * @author Pablo
 */
public class ShowAxes implements SensorListener {

    Line axeX;
    Line axeY;
    Line axeZ;
    Node parent;
    float scalar;

    public ShowAxes(Node parent) {
        this.parent = parent;
        showAccelerationAxes();
    }

    private void showAccelerationAxes() {
        //Line line = new Line("Axis", new Vector3f[] {new Vector3f(0,0,0), new Vector3f(8,0,0),new Vector3f(0,0,0), new Vector3f(0,8,0),new Vector3f(0,0,0), new Vector3f(0,0,8)}, null, new ColorRGBA[] {ColorRGBA.green, ColorRGBA.green, ColorRGBA.red, ColorRGBA.red, ColorRGBA.blue, ColorRGBA.blue}, null); 
        Vector3f start = parent.getWorldTranslation();
        axeX = createLinesAxe(start, start.mult(new Vector3f(1f + scalar, 1f, 1f)), 0);
        axeY = createLinesAxe(start, start.mult(new Vector3f(1f, 1f + scalar, 1f)), 1);
        axeZ = createLinesAxe(start, start.mult(new Vector3f(1f, 1f, 1f + scalar)), 2);

    }

    private Mesh createAxes(Vector3f start, Vector3f end, int axe) {
        Mesh lineMesh = new Mesh();
        lineMesh.setMode(Mesh.Mode.Lines);
        lineMesh.setBuffer(VertexBuffer.Type.Position, 3,
                new float[]{start.getX(), start.getY(), start.getZ(), end.getX(), end.getY(), end.getZ()});
        lineMesh.setBuffer(VertexBuffer.Type.Index, 2, new short[]{0, 1});
//lineMesh.updateBound();
//lineMesh.updateCounts();

        Geometry lineGeometry = new Geometry("line", lineMesh);
        Material lineMaterial = new Material(SpatialFactory.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        lineMaterial.getAdditionalRenderState().setWireframe(true);
        lineMaterial.getAdditionalRenderState().setDepthTest(false);
        switch (axe) {
            case 0:
                lineMaterial.setColor("Color", ColorRGBA.Red);
                break;
            case 1:
                lineMaterial.setColor("Color", ColorRGBA.Blue);
                break;
            case 2:
                lineMaterial.setColor("Color", ColorRGBA.Green);
                break;
        }

        lineGeometry.setMaterial(lineMaterial);
        SpatialFactory.getRootNode().attachChild(lineGeometry);
        return lineMesh;
    }
    
    private Line createLinesAxe(Vector3f start, Vector3f end, int axe) {
        Line line = new Line(start, end);
        line.setLineWidth(1f);         
        
        Geometry lineGeometry = new Geometry("line"+axe, line);
        Material lineMaterial = new Material(SpatialFactory.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        lineMaterial.getAdditionalRenderState().setWireframe(true);
        lineMaterial.getAdditionalRenderState().setDepthTest(false);
        switch (axe) {
            case 0:
                lineMaterial.setColor("Color", ColorRGBA.Red);
                break;
            case 1:
                lineMaterial.setColor("Color", ColorRGBA.Blue);
                break;
            case 2:
                lineMaterial.setColor("Color", ColorRGBA.Green);
                break;
        }

        lineGeometry.setMaterial(lineMaterial);
        SpatialFactory.getRootNode().attachChild(lineGeometry);
        return line;
    }
    
    private Material generateMaterial(int axe) {
        Material lineMaterial = new Material(SpatialFactory.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        lineMaterial.getAdditionalRenderState().setWireframe(true);
        lineMaterial.getAdditionalRenderState().setDepthTest(false);
        switch (axe) {
            case 0:
                lineMaterial.setColor("Color", ColorRGBA.Red);
                break;
            case 1:
                lineMaterial.setColor("Color", ColorRGBA.Blue);
                break;
            case 2:
                lineMaterial.setColor("Color", ColorRGBA.Green);
                break;
        }
        return lineMaterial;
    }

    public void updateIncrementalAcceleration(float time, float[] accelerations) {
    }

    @Override
    public void update(Sensor source, SensorData sd) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void cleanUp() {
        axeX = null;
        axeY = null;
        axeZ = null;
        parent = null;
    }
}
