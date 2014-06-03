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
package phat.mason.gui;

import com.sun.j3d.utils.geometry.ColorCube;
import java.awt.Color;
import java.awt.Font;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import phat.mason.PHATSimState;
import phat.mason.space.Util;
import sim.display.Console;
import sim.display.Controller;
import sim.display.GUIState;
import sim.display3d.Display3D;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.portrayal3d.Portrayal3D;
import sim.portrayal3d.continuous.ContinuousPortrayal3D;
import sim.portrayal3d.simple.LabelledPortrayal3D;
import sim.portrayal3d.simple.Shape3DPortrayal3D;

/**
 *
 * @author Pablo
 */
public class GUISimState extends GUIState {

    Display3D display;
    JFrame displayFrame;
    ContinuousPortrayal3D yardPortrayal = new ContinuousPortrayal3D();

    public static void main(String[] args) {
        PHATSimState sim = new PHATSimState(System.currentTimeMillis());
        sim.setHouseID("Scenes/Structures/Houses/House3room2bath/House3room2bath.j3o");


        GUIState guiState = new GUISimState(sim);
        PHATConsole c = new PHATConsole(guiState);
        c.setVisible(true);

        //sim.start();        
    }

    public GUISimState(SimState simState) {
        super(simState);
    }

    @Override
    public void start() {
        super.start();

        watingForPHAT();

        setupPortrayals();
    }

    private void watingForPHAT() {
        PHATSimState phatSimState = (PHATSimState) state;
        while (!phatSimState.isInitialized()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                Logger.getLogger(GUISimState.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void setupPortrayals() {
        System.out.println("setup portrayals!");
        PHATSimState phatSimState = (PHATSimState) state;
        System.out.println("world = "+phatSimState.getWorld());
        yardPortrayal.setField(phatSimState.getWorld());
        //ConePortrayal3D lp = new ConePortrayal3D(Color.red, 0.1);
        //Box box = new Box(0.1f, 0.1f, 0.1f, new Appearance());
                
        LabelledPortrayal3D lp;
        lp = new LabelledPortrayal3D(
                new Shape3DPortrayal3D(new ColorCube(0.1)), 
                0.0, 0.0, 0.0,
                Font.getFont(Font.SERIF), 
                null,
                Color.BLACK, 
                false);
        lp.setLabelScale(0.1f);
        //LabelledPortrayal3D lp = new LabelledPortrayal3D(new ConePortrayal3D(Color.red, 0.1), null, Color.BLACK, false);
        //lp.setLabelScale(0.1);
        yardPortrayal.setPortrayalForAll(lp);
        display.setBackdrop(Color.white);
        display.scale(0.2f);        
        
        display.createSceneGraph();
        display.reset();        
    }

    @Override
    public void init(Controller c) {
        System.out.println("init(Controler)");
        
        display = new Display3D(300, 300, this);
        System.out.println("Display = "+display);
        displayFrame = display.createFrame();
        System.out.println("diplayFrame = "+displayFrame);
        displayFrame.setTitle("House");
        boolean result = c.registerFrame(displayFrame); // so the frame appears in the "Display" list
        displayFrame.setVisible(true);
        display.attach(yardPortrayal, "House");
        display.root.addChild(Util.createAxis(0f, 10f, 0f, 3f, 0f, 10f));

        super.init(c);
        
        scheduleAtStart(new Steppable() {
            @Override
            public void step(SimState ss) {
                display.universe.addBranchGraph(Util.createAxis(0f, 10f, 0f, 3f, 0f, 10f));
            }
        });        
        
        new Thread(new RefrestAgent(c)).start();
    }

    class RefrestAgent implements Runnable {

        Controller c;

        public RefrestAgent(Controller c) {
            this.c = c;
        }

        @Override
        public void run() {
            while (displayFrame != null) {
                try {
                    Thread.sleep(1000 / 60);
                    c.refresh();
                } catch (InterruptedException ex) {
                    Logger.getLogger(GUISimState.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @Override
    public void quit() {
        super.quit();
        if (displayFrame != null) {
            displayFrame.dispose();
        }
        displayFrame = null;
        display = null;
    }
}
