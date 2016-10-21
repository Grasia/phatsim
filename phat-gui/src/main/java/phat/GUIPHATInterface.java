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
import phat.gui.GUIMainMenuAppState;
import phat.gui.logging.LoggingViewerAppState;
import tonegod.gui.core.Screen;

/**
 *
 * @author sala26
 */
public class GUIPHATInterface extends JSONPHATInterface {

    private GUIMainMenuAppState guimainMenu;
    private boolean displayFps = false;
    private boolean statView = false;

    public GUIPHATInterface(PHATInitializer initializer) {
        super(initializer);
        this.initializer = initializer;
    }

    public GUIPHATInterface(PHATInitializer initializer, GUIArgumentProcessor ap) {
        this(initializer);

        ap.initialize(this);
    }

    @Override
    public void init(SimpleApplication app) {
        super.init(app);
        
        Screen screen = new Screen(app, "tonegod/gui/style/def/style_map.gui.xml");
        app.getGuiNode().addControl(screen);
        guimainMenu = new GUIMainMenuAppState(screen);
        guimainMenu.setDisplayFps(displayFps);
        guimainMenu.setStatView(statView);
        app.getStateManager().attach(guimainMenu);

        app.getStateManager().attach(new LoggingViewerAppState());
    }

    public void setDisplayFPS(boolean show) {
        this.displayFps = show;
    }

    public boolean isStatView() {
        return statView;
    }

    public void setStatView(boolean statView) {
        this.statView = statView;
    }
}
