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
package phat;

import com.jme3.app.SimpleApplication;
import phat.server.json_rpc.JsonRpcAppState;

/**
 *
 * @author sala26
 */
public class JSONPHATInterface extends PHATInterface {

     JsonRpcAppState jsonAppState;
    
    public JSONPHATInterface(PHATInitializer initializer) {
        super(initializer);
    }

    public JSONPHATInterface(PHATInitializer initializer, ArgumentProcessor ap) {
        this(initializer);
    }

    @Override
    public void init(SimpleApplication app) {
        super.init(app);
        
        jsonAppState = new JsonRpcAppState();
        app.getStateManager().attach(jsonAppState);

    }
    
}
