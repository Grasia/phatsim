/*
 * Copyright (C) 2016 Pablo Campillo-Sanchez <pabcampi@ucm.es>
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
package phat.server.json_rpc;

import phat.server.*;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;
import phat.body.BodiesAppState;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandAnn;
import phat.devices.DevicesAppState;
import phat.server.json_rpc.commands.CommandList;
import phat.structures.houses.HouseAppState;
import phat.world.WorldAppState;

/**
 *
 * @author pablo
 */
public class JsonRpcAppState extends AbstractAppState {

    boolean listening = true;
    int portNumber = 44123;

    SimpleApplication app;
    AssetManager assetManager;
    BulletAppState bulletAppState;
    PHATServerManager serverManager;
    DevicesAppState devicesAppState;
    BodiesAppState bodiesAppState;
    HouseAppState houseAppState;
    WorldAppState worldAppState;

    JsonToPHATCommandFactory commandFactory;

    ConcurrentLinkedQueue<PHATCommand> runningCommands = new ConcurrentLinkedQueue<>();
    ConcurrentLinkedQueue<PHATCommand> pendingCommands = new ConcurrentLinkedQueue<>();

    @Override
    public void initialize(AppStateManager stateManager, Application application) {
        System.out.println("Inititalize " + getClass().getSimpleName());
        super.initialize(stateManager, application);
        this.app = (SimpleApplication) application;
        this.assetManager = application.getAssetManager();

        worldAppState = app.getStateManager().getState(WorldAppState.class);
        houseAppState = app.getStateManager().getState(HouseAppState.class);
        bulletAppState = app.getStateManager().getState(BulletAppState.class);
        devicesAppState = app.getStateManager().getState(DevicesAppState.class);
        bodiesAppState = app.getStateManager().getState(BodiesAppState.class);

        commandFactory = new JsonToPHATCommandFactory();

        server.start();
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        runningCommands.addAll(pendingCommands);
        pendingCommands.clear();
        for (PHATCommand bc : runningCommands) {
            bc.run(app);
        }
        runningCommands.clear();
    }

    public void runCommand(PHATCommand command) {
        PHATCommandAnn ann = command.getClass().getAnnotation(PHATCommandAnn.class);
        if (ann.type().equals(CommandList.COMMAND_TYPE.body.name())) {
            bodiesAppState.runCommand(command);
        } else if (ann.type().equals(ann.type().equals(CommandList.COMMAND_TYPE.env.name()))) {
            houseAppState.runCommand(command);
        } else {
            pendingCommands.add(command);
        }
    }

    Thread server = new Thread(new Runnable() {
        @Override
        public void run() {
            try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
                while (listening) {
                    Socket client = serverSocket.accept();
                    new PHATSocketDispacherThread(JsonRpcAppState.this, client).start();
                }
            } catch (IOException e) {
                System.err.println("Could not listen on port " + portNumber);
                System.exit(-1);
            }
        }
    });

    public static void main(String[] args) {
        JsonRpcAppState state = new JsonRpcAppState();
        state.commandFactory = new JsonToPHATCommandFactory();
        state.server.start();
    }

    @Override
    public void cleanup() {
        super.cleanup();
        listening = false;
    }

    public JsonToPHATCommandFactory getCommandFactory() {
        return commandFactory;
    }
}
