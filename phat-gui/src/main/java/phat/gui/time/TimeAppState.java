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
package phat.gui.time;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector2f;
import phat.gui.GUIMainMenuAppState;
import phat.world.WorldAppState;
import tonegod.gui.controls.menuing.Menu;
import tonegod.gui.controls.text.Label;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;
import tonegod.gui.core.SubScreen;
import tonegod.gui.effects.Effect;

/**
 *
 * @author pablo
 */
public class TimeAppState extends AbstractAppState {

    Screen screen;
    Window window;
    Label timeLabel;
    SimpleApplication app;
    WorldAppState worldAppState;

    public TimeAppState(Screen screen) {
        this.screen = screen;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        this.app = (SimpleApplication) app;

        worldAppState = this.app.getStateManager().getState(WorldAppState.class);

        screen.parseLayout("Interface/TimeDate.gui.xml", 
                this.app.getStateManager().getState(GUIMainMenuAppState.class));

        // Here we can grab pointers to the loaded elements
        window = (Window) screen.getElementById("TimeWindow");
        timeLabel = (Label) screen.getElementById("TimeLabel");
        
        window.setPosition(screen.getWidth()-window.getWidth(), 0f);
    }

    @Override
    public void update(float tpf) {
        if (worldAppState != null) {
            timeLabel.setText(worldAppState.getCalendar().toString());
        }
    }

    @Override
    public void cleanup() {
        super.cleanup();

        // We can alter the effect to destroy our inventory window
        // when we unload the AppState
        Effect hide = new Effect(Effect.EffectType.FadeOut, Effect.EffectEvent.Hide, 0.25f);
        hide.setDestroyOnHide(true);

        screen.removeElement(window);

        for (Element e : screen.getElements()) {
            System.out.println("Element = " + e.getName());
        }
        // Now our UI component scene fades out when the AppState in unloaded.
    }
}
