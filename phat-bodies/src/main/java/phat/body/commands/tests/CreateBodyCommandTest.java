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
package phat.body.commands.tests;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.system.AppSettings;

import java.util.logging.Logger;

import phat.app.PHATApplication;
import phat.app.PHATInitAppListener;
import phat.body.BodiesAppState;
import phat.body.commands.RandomWalkingCommand;
import phat.body.commands.SetBodyColorCommand;
import phat.body.commands.SetBodyInCoordenatesCommand;
import phat.body.commands.SetSpeedDisplacemenetCommand;
import phat.structures.houses.TestHouse;
import phat.util.SpatialFactory;
import phat.world.WorldAppState;

/**
 *
 * @author pablo
 */
public class CreateBodyCommandTest implements PHATInitAppListener {

    private static final Logger logger = Logger.getLogger(TestHouse.class.getName());
    BodiesAppState bodiesAppState;
    WorldAppState worldAppState;

    public static void main(String[] args) {
        CreateBodyCommandTest test = new CreateBodyCommandTest();
        PHATApplication phat = new PHATApplication(test);
        phat.setDisplayFps(true);
        phat.setDisplayStatView(true);
        AppSettings settings = new AppSettings(true);
        settings.setWidth(1024);
        settings.setHeight(720);
        phat.setSettings(settings);
        phat.start();
    }

    @Override
    public void init(SimpleApplication app) {
        SpatialFactory.init(app.getAssetManager(), app.getRootNode());

        AppStateManager stateManager = app.getStateManager();

        app.getFlyByCamera().setMoveSpeed(10f);

        app.getCamera().setLocation(new Vector3f(4.034334f, 3.8802402f, 6.621415f));
        app.getCamera().setRotation(new Quaternion(-7.4161455E-4f, 0.97616464f, -0.21700443f, -0.0033340578f));

        app.getFlyByCamera().setDragToRotate(true);

        worldAppState = new WorldAppState();
        app.getStateManager().attach(worldAppState);
        worldAppState.setCalendar(2013, 1, 1, 12, 0, 0);
        worldAppState.setLandType(WorldAppState.LandType.Basic);
        worldAppState.setTerrainColor(ColorRGBA.Black);

        bodiesAppState = new BodiesAppState();
        stateManager.attach(bodiesAppState);

        /*FilterPostProcessor fpp=new FilterPostProcessor(app.getAssetManager());
        BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
        fpp.addFilter(bloom);
        app.getViewPort().addProcessor(fpp);*/

        int bodyNum = 100;
        float step = 0.7f;
        float offset = -step * bodyNum / 2f;

        for (int i = 0; i < bodyNum; i++) {
            String bodyId = "Body-" + i;
            bodiesAppState.createBody(BodiesAppState.BodyType.ElderLP, bodyId);
            bodiesAppState.runCommand(new SetBodyInCoordenatesCommand(bodyId,
                    Vector3f.ZERO.add(/*FastMath.rand.nextInt(100) - 50*/offset+=0.7f, 0f, /*FastMath.rand.nextInt(100) - 50*/0f)));
            bodiesAppState.runCommand(new RandomWalkingCommand(bodyId, true));
            bodiesAppState.runCommand(new SetSpeedDisplacemenetCommand(bodyId, 1.5f));
            //bodiesAppState.runCommand(new SetBodyColorCommand(bodyId, ColorRGBA.Green));
            //bodiesAppState.runCommand(new DebugSkeletonCommand(bodyId, Boolean.TRUE));
        }
    }
}