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
package phat.mason;

import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.system.JmeContext;
import phat.mason.agents.ActorCollisionListener;
import phat.mason.agents.Agent;
import phat.mason.agents.PatientAgent;
import phat.mason.agents.PhysicsActor;
import phat.mason.agents.PhysicsActorImpl;
import phat.mason.agents.RelativeAgent;
import phat.mason.space.Util;
import phat.mason.utils.AlarmFrame;
import sim.engine.SimState;
import sim.field.continuous.Continuous3D;
import sim.util.Double3D;

/**
 *
 * @author pablo
 */
public class PHATSimState extends SimState {

    String houseID;
    String houseUrl;
    boolean visualization = true;
    boolean initialized = false;
    Continuous3D world;
    MASONAppState masonAppState;
    PHATApplication app;

    public static void main(String[] args) {
        PHATSimState sim = new PHATSimState(System.currentTimeMillis());
        sim.setHouseID("House1");
        sim.setHouseUrl("Scenes/Structures/Houses/House3room2bath/House3room2bath.j3o");
        sim.setVisualization(true);

        sim.start();
    }

    public PHATSimState(long seed) {
        super(seed);
    }

    public void setVisualization(boolean enabled) {
        visualization = enabled;
    }

    @Override
    public void start() {
        System.out.println(getClass().getSimpleName() + " start()...");
        super.start();

        masonAppState = new MASONAppState(this);

        app = new PHATApplication(masonAppState);
        app.setShowSettings(false);
        app.setPauseOnLostFocus(false);
        app.setDisplayFps(false);
        app.setDisplayStatView(false);

        /*
         File video = new File("video.avi");
         File audio = new File("audio.wav");
        
         try {
         Capture.captureVideo(app, video);
         Capture.captureAudio(app, audio);
         } catch (IOException ex) {
         Logger.getLogger(PHATSimState.class.getName()).log(Level.SEVERE, null, ex);
         }*/

        if (visualization) {
            app.start();
        } else {
            app.start(JmeContext.Type.Headless);
        }

        System.out.println(getClass().getSimpleName() + " ...start()");
    }

    public void init(MASONAppState masonAppState) {

        masonAppState.getTimeAdapter().setTimePerStep(1f);

        world = masonAppState.getHouseAdapter().getWorld();

        masonAppState.getHouseAdapter().getHouse().createPointLight("Kitchen", ColorRGBA.White, 5f);
        masonAppState.getHouseAdapter().getHouse().createPointLight("BathRoom1", ColorRGBA.White, 0.1f);
        masonAppState.getHouseAdapter().getHouse().createPointLight("BedRoom1", ColorRGBA.White, 5f);

        Double3D loc = Util.get(masonAppState.getHouseAdapter().getHouse().getCoordenates("Kitchen", "Hob"));
        PhysicsActor pa = masonAppState.getActorAdapter().createRelative("Relative", loc);
        pa.showName(true);
        Agent relativeAgent = new RelativeAgent(pa, this);

        masonAppState.setMic((Node) ((PhysicsActorImpl) pa).getSpatial());

        schedule.scheduleRepeating(relativeAgent);

        loc = Util.get(masonAppState.getHouseAdapter().getHouse().getCoordenates("BedRoom1", "Center"));
        pa = masonAppState.getActorAdapter().createPatient("Patient", loc);
        pa.showName(true);
        Agent patientAgent = new PatientAgent(pa, this);

        schedule.scheduleRepeating(patientAgent);

        masonAppState.createAudioPlot("Normalized sound perceived by " + relativeAgent.getName());

        masonAppState.getActorAdapter().createAudioSmartphone("Smartphone3", "emulator-5558");

        initialized = true;
    }

    public void step(SimState simState) {
        System.out.println("Step = " + schedule.getSteps());
        boolean result = schedule.step(simState);
        if (schedule.getSteps() == 60 * 5 + 20) {
            AlarmFrame f = new AlarmFrame();
            f.setVisible(true);
        }
        System.out.println(result);
    }

    public void resumePHAT() {
        app.setEnabled(true);
    }

    public void pausePHAT() {
        app.setEnabled(false);
    }

    public void register(ActorCollisionListener actorCollisionListener) {
        getMasonAppState().getActorAdapter().register(actorCollisionListener);
    }

    /**
     * Checks the finish condition
     *
     * @return
     */
    public boolean isFinish() {
        return schedule.getSteps() > 5000;
    }

    @Override
    public void finish() {
        super.finish();
        app.stop();
    }

    public String getHouseID() {
        return houseID;
    }

    public void setHouseID(String houseID) {
        this.houseID = houseID;
    }

    public Continuous3D getWorld() {
        return world;
    }

    public MASONAppState getMasonAppState() {
        return masonAppState;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public PHATApplication getApp() {
        return app;
    }

    public void setApp(PHATApplication app) {
        this.app = app;
    }

    public boolean isVisualization() {
        return visualization;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public void setWorld(Continuous3D world) {
        this.world = world;
    }

    public void setMasonAppState(MASONAppState masonAppState) {
        this.masonAppState = masonAppState;
    }

    public String getHouseUrl() {
        return houseUrl;
    }

    public void setHouseUrl(String houseUrl) {
        this.houseUrl = houseUrl;
    }
}
