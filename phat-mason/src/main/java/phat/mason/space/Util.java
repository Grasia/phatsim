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
package phat.mason.space;

import com.jme3.bullet.control.PhysicsControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.LineArray;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import sim.util.Double3D;

/**
 *
 * @author Pablo
 */
public class Util {

    public static Vector3f get(Double3D vector) {
        Vector3f result = new Vector3f((float) vector.getX(), (float) vector.getY(), (float) vector.getZ());
        return result;
    }

    public static Double3D get(Vector3f vector) {
        Double3D result = new Double3D(vector.getX(), vector.getY(), vector.getZ());
        return result;
    }

    public static BranchGroup createAxis(float minX, float maxX, float minY, float maxY, float minZ, float maxZ) {
        Color3f red = new Color3f(1.0f, 0.0f, 0.0f);
        Color3f green = new Color3f(0.0f, 1.0f, 0.0f);
        Color3f blue = new Color3f(0.0f, 0.0f, 1.0f);

        BranchGroup axisBG;
        axisBG = new BranchGroup();

        // create line for X axis
        Point3f x1 = new Point3f(minX, 0.0f, 0.0f);
        Point3f x2 = new Point3f(maxX, 0.0f, 0.0f);
        LineArray axisXLines = new LineArray(10, LineArray.COORDINATES | LineArray.COLOR_3);
        axisBG.addChild(new Shape3D(axisXLines));

        axisXLines.setCoordinate(0, x1);
        axisXLines.setCoordinate(1, x2);
        axisXLines.setCoordinate(2, x2);
        axisXLines.setCoordinate(3, new Point3f(maxX - 0.1f, 0.1f, 0.1f));
        axisXLines.setCoordinate(4, x2);
        axisXLines.setCoordinate(5, new Point3f(maxX - 0.1f, -0.1f, 0.1f));
        axisXLines.setCoordinate(6, x2);
        axisXLines.setCoordinate(7, new Point3f(maxX - 0.1f, 0.1f, -0.1f));
        axisXLines.setCoordinate(8, x2);
        axisXLines.setCoordinate(9, new Point3f(maxX - 0.1f, -0.1f, -0.1f));
        
        Color3f colorsX[] = new Color3f[10];
        for (int v = 0; v < 10; v++) {
            colorsX[v] = green;
        }
        axisXLines.setColors(0, colorsX);
        
        // create line for Y axis
        Point3f y1 = new Point3f(0.0f, minY, 0.0f);
        Point3f y2 = new Point3f(0.0f, maxY, 0.0f);
        LineArray axisYLines = new LineArray(10, LineArray.COORDINATES
                | LineArray.COLOR_3);
        axisBG.addChild(new Shape3D(axisYLines));

        axisYLines.setCoordinate(0, y1);
        axisYLines.setCoordinate(1, y2);
        axisYLines.setCoordinate(2, y2);
        axisYLines.setCoordinate(3, new Point3f(0.1f, maxY - 0.1f, 0.1f));
        axisYLines.setCoordinate(4, y2);
        axisYLines.setCoordinate(5, new Point3f(-0.1f, maxY - 0.1f, 0.1f));
        axisYLines.setCoordinate(6, y2);
        axisYLines.setCoordinate(7, new Point3f(0.1f, maxY - 0.1f, -0.1f));
        axisYLines.setCoordinate(8, y2);
        axisYLines.setCoordinate(9, new Point3f(-0.1f, maxY - 0.1f, -0.1f));

        Color3f colorsY[] = new Color3f[10];
        for (int v = 0; v < 10; v++) {
            colorsY[v] = red;
        }
        axisYLines.setColors(0, colorsY);

        // create line for Z axis
        Point3f z1 = new Point3f(0.0f, 0.0f, minZ);
        Point3f z2 = new Point3f(0.0f, 0.0f, maxZ);

        LineArray axisZLines = new LineArray(10, LineArray.COORDINATES
                | LineArray.COLOR_3);
        axisBG.addChild(new Shape3D(axisZLines));

        axisZLines.setCoordinate(0, z1);
        axisZLines.setCoordinate(1, z2);
        axisZLines.setCoordinate(2, z2);
        axisZLines.setCoordinate(3, new Point3f(0.1f, 0.1f, maxZ - 0.1f));
        axisZLines.setCoordinate(4, z2);
        axisZLines.setCoordinate(5, new Point3f(-0.1f, 0.1f, maxZ - 0.1f));
        axisZLines.setCoordinate(6, z2);
        axisZLines.setCoordinate(7, new Point3f(0.1f, -0.1f, maxZ - 0.1f));
        axisZLines.setCoordinate(8, z2);
        axisZLines.setCoordinate(9, new Point3f(-0.1f, -0.1f, maxZ - 0.1f));

        Color3f colorsZ[] = new Color3f[10];

        for (int v = 0; v < 10; v++) {
            colorsZ[v] = blue;
        }

        axisZLines.setColors(0, colorsZ);

        return axisBG;
    }

    public static BranchGroup createAxis2(float minX, float maxX, float minY, float maxY, float minZ, float maxZ) {
        BranchGroup axisBG;
        axisBG = new BranchGroup();

        // create line for X axis
        LineArray axisXLines = new LineArray(2, LineArray.COORDINATES | LineArray.COLOR_3);
        axisBG.addChild(new Shape3D(axisXLines));

        axisXLines.setCoordinate(0, new Point3f(minX, 0.0f, 0.0f));
        axisXLines.setCoordinate(1, new Point3f(maxX, 0.0f, 0.0f));

        Color3f red = new Color3f(1.0f, 0.0f, 0.0f);
        Color3f green = new Color3f(0.0f, 1.0f, 0.0f);
        Color3f blue = new Color3f(0.0f, 0.0f, 1.0f);

        axisXLines.setColor(0, red);
        axisXLines.setColor(1, red);

        // create line for Y axis
        Point3f y1 = new Point3f(0.0f, minY, 0.0f);
        Point3f y2 = new Point3f(0.0f, maxY, 0.0f);
        LineArray axisYLines = new LineArray(2, LineArray.COORDINATES
                | LineArray.COLOR_3);
        axisBG.addChild(new Shape3D(axisYLines));

        axisYLines.setCoordinate(0, y1);
        axisYLines.setCoordinate(1, y2);
        axisYLines.setCoordinate(2, y2);
        axisYLines.setCoordinate(3, new Point3f(0.1f, maxY, 0.1f));
        axisYLines.setCoordinate(4, y2);
        axisYLines.setCoordinate(5, new Point3f(-0.1f, maxY, 0.1f));
        axisYLines.setCoordinate(6, y2);
        axisYLines.setCoordinate(7, new Point3f(0.1f, maxY, -0.1f));
        axisYLines.setCoordinate(8, y2);
        axisYLines.setCoordinate(9, new Point3f(-0.1f, maxY, -0.1f));

        Color3f colors[] = new Color3f[9];
        colors[0] = new Color3f(0.0f, 1.0f, 1.0f);
        for (int v = 0; v < 9; v++) {
            colors[v] = green;
        }
        axisYLines.setColors(1, colors);

        // create line for Z axis
        Point3f z1 = new Point3f(0.0f, 0.0f, minZ);
        Point3f z2 = new Point3f(0.0f, 0.0f, maxZ);

        LineArray axisZLines = new LineArray(10, LineArray.COORDINATES
                | LineArray.COLOR_3);
        axisBG.addChild(new Shape3D(axisZLines));

        axisZLines.setCoordinate(0, z1);
        axisZLines.setCoordinate(1, z2);
        axisZLines.setCoordinate(2, z2);
        axisZLines.setCoordinate(3, new Point3f(0.1f, 0.1f, maxZ));
        axisZLines.setCoordinate(4, z2);
        axisZLines.setCoordinate(5, new Point3f(-0.1f, 0.1f, maxZ));
        axisZLines.setCoordinate(6, z2);
        axisZLines.setCoordinate(7, new Point3f(0.1f, -0.1f, maxZ));
        axisZLines.setCoordinate(8, z2);
        axisZLines.setCoordinate(9, new Point3f(-0.1f, -0.1f, maxZ));

        colors[0] = new Color3f(0.0f, 1.0f, 1.0f);
        for (int v = 0; v < 9; v++) {
            colors[v] = blue;
        }

        axisZLines.setColors(1, colors);

        return axisBG;
    }
}
