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
package phat.gui;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import phat.app.PHATApplication;
import phat.gui.logging.LoggingViewerAppState;
import phat.gui.screenshot.GUIScreenShotAppState;
import phat.gui.time.TimeAppState;
import phat.util.PHATScreenshotAppState;
import tonegod.gui.controls.buttons.Button;
import tonegod.gui.controls.menuing.Menu;
import tonegod.gui.controls.text.TextField;
import tonegod.gui.controls.windows.DialogBox;
import tonegod.gui.core.Screen;
import tonegod.gui.effects.Effect;

/**
 *
 * @author pablo
 */
public class GUIMainMenuAppState extends AbstractAppState {

    Screen screen;
    Menu mainMenu;
    Menu viewMenu;
    Menu viewInfoMenu;
    Menu viewDebugMenu;
    Menu toolsMenu;
    Button menuButton;
    Button playPauseButton;
    Button speedDownButton;
    TextField speedLabel;
    Button speedUpButton;
    PHATApplication app;
    String path = "/home/pablo/Models/";
    
    public GUIMainMenuAppState(Screen screen) {
        // Store a pointer to the screen
        this.screen = screen;
        // Call the xml parser to load your new components
        screen.parseLayout("Interface/MainMenu.gui.xml", this);

        // Here we can grab pointers to the loaded elements
        mainMenu = (Menu) screen.getElementById("MainMenu");
        toolsMenu = (Menu) screen.getElementById("ToolsMenu");
        viewMenu = (Menu) screen.getElementById("ViewMenu");
        viewInfoMenu = (Menu) screen.getElementById("ViewInfoMenu");
        viewDebugMenu = (Menu) screen.getElementById("ViewDebugMenu");
        menuButton = (Button) screen.getElementById("MenuButton");
        playPauseButton = (Button) screen.getElementById("PlayPauseButton");
        speedDownButton = (Button) screen.getElementById("SpeedDownButton");
        speedLabel = (TextField) screen.getElementById("SpeedLabel");
        speedUpButton = (Button) screen.getElementById("SpeedUpButton");
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        this.app = (PHATApplication) app;
        this.app.setSimSpeed(1f);

        this.app.setDisplayFps(false);
        this.app.setDisplayStatView(false);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        Vector2f pos = screen.getMouseXY();

        if (pos.y > screen.getHeight() - 50f) {
            if (!menuButton.getIsVisible() && !mainMenu.getIsVisible()) {
                menuButton.showWithEffect();
                playPauseButton.showWithEffect();
                speedDownButton.showWithEffect();
                speedLabel.showWithEffect();
                speedUpButton.showWithEffect();
            }
        } else if (menuButton.getIsVisible()) {
            menuButton.hideWithEffect();
            playPauseButton.hideWithEffect();
            speedDownButton.hideWithEffect();
            speedLabel.hideWithEffect();
            speedUpButton.hideWithEffect();
        }
    }

    @Override
    public void cleanup() {
        super.cleanup();

        // We can alter the effect to destroy our inventory window
        // when we unload the AppState
        Effect hide = new Effect(Effect.EffectType.FadeOut, Effect.EffectEvent.Hide, 0.25f);
        hide.setDestroyOnHide(true);

        screen.removeElement(menuButton);
        screen.removeElement(viewDebugMenu);
        screen.removeElement(viewInfoMenu);
        screen.removeElement(viewMenu);
        screen.removeElement(toolsMenu);
        screen.removeElement(mainMenu);
        screen.removeElement(playPauseButton);
        screen.removeElement(speedDownButton);
        screen.removeElement(speedLabel);
        screen.removeElement(speedUpButton);
    }

    public void showMenu(MouseButtonEvent evt, boolean isToggle) {
        //mainMenu.showMenu(null, evt.getX(), evt.get - mainMenu.getHeight());
        System.out.println("showMenu!!!");
        mainMenu.showWithEffect();
    }

    public void playPauseSim(MouseButtonEvent evt, boolean isToggle) {
        if (playPauseButton.getText().equals("Pause")) {
            playPauseButton.setText("Resume");
            this.app.setSimSpeed(0f);
        } else {
            playPauseButton.setText("Pause");
            this.app.setSimSpeed(Float.parseFloat(speedLabel.getText()));
        }
    }

    public void onSpeedDown(MouseButtonEvent evt, boolean isToggle) {
        float speed = Float.parseFloat(speedLabel.getText());
        if (speed >= 0.5f) {
            speed /= 2;
            speedLabel.setText(String.valueOf(speed));
            onSpeedChange();
        }
    }

    public void onSpeedUp(MouseButtonEvent evt, boolean isToggle) {
        float speed = Float.parseFloat(speedLabel.getText());
        if (speed < 128f) {
            speed *= 2;
            speedLabel.setText(String.valueOf(speed));
            onSpeedChange();
        }
    }

    public void onSpeedChange() {
        if (playPauseButton.getText().equals("Pause")) {
            this.app.setSimSpeed(Float.parseFloat(speedLabel.getText()));
        }
    }

    public void viewInfoMenuClick(int index, Object value, boolean isToggled) {
        switch (index) {
            case 0:
                TimeAppState timeAppState = app.getStateManager().getState(TimeAppState.class);
                if (timeAppState == null) {
                    timeAppState = new TimeAppState(screen);
                    app.getStateManager().attach(timeAppState);
                } else {
                    app.getStateManager().detach(timeAppState);
                }
                break;
        }
    }

    /*
     * DEBUG OPTIONS
     */
    public void viewDebugMenuClick(int index, Object value, boolean isToggled) {
        System.out.println(index + ":" + value + ":" + isToggled);
        switch (index) {
            case 0:
                app.setDisplayFps(isToggled);
                break;
            case 1:
                app.setDisplayStatView(isToggled);
                break;
        }
    }

    /*
     * TOOLS OPTIONS
     */
    public void toolsMenuClick(int index, Object value, boolean isToggled) {
        System.out.println(index + ":" + value + ":" + isToggled);
        switch (index) {
            case 0:
                // Call the xml parser to load your new components
                //screen.parseLayout("Interface/MainMenu.gui.xml", this);
                //screenShotState.takeScreenshot();
                app.getStateManager().detach(this);
                takeScreenshot();
                break;
            case 1:
                LoggingViewerAppState log = app.getStateManager().getState(LoggingViewerAppState.class);
                if(log == null) {
                    log = new LoggingViewerAppState();
                    app.getStateManager().attach(this);
                }
                if(log.isShown()) {
                    log.hide();
                } else {
                    log.show();
                }
                break;
        }
    }
    
    private void takeScreenshot() {
        this.app.getStateManager().attach(new GUIScreenShotAppState(screen));
    }

    public void menuClicked(int index, Object value, boolean isToggled) {
        switch (index) {
            case 2:
                System.out.println("Quit!");
                float wHeight = 150f;
                float wWidth = 300f;
                DialogBox dialog = new DialogBox(screen, "QuitDialog",
                        new Vector2f((screen.getWidth() - wWidth) / 2f, (screen.getHeight() - wHeight) / 2f), new Vector2f(wWidth, wHeight)) {
                    @Override
                    public void onButtonCancelPressed(MouseButtonEvent mbe, boolean bln) {
                        screen.removeElement(this);
                    }

                    @Override
                    public void onButtonOkPressed(MouseButtonEvent mbe, boolean bln) {
                        quit();
                    }
                };
                dialog.setWindowTitle("Permission!");
                dialog.setMsg("Are you sure?");
                screen.addElement(dialog);
                dialog.hide();
                dialog.showWithEffect();
                break;
        }
    }

    private void quit() {
        this.app.stop();
    }
}
