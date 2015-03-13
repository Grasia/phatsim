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
package phat.gui.screenshot;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.MagFilter;
import com.jme3.texture.Texture.MinFilter;
import com.jme3.texture.Texture2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import phat.gui.GUIMainMenuAppState;
import phat.util.PHATScreenshotAppState;
import phat.world.WorldAppState;
import tonegod.gui.controls.text.Label;
import tonegod.gui.controls.text.TextField;
import tonegod.gui.controls.windows.Panel;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;
import tonegod.gui.effects.Effect;

/**
 *
 * @author pablo
 */
public class GUIScreenShotAppState extends AbstractAppState {

    Screen screen;
    Window window;
    TextField fileNameTextField;
    TextField filePathTextField;
    Label imagePanel;
    SimpleApplication app;
    PHATScreenshotAppState phatScreenshotAppState;

    public GUIScreenShotAppState(Screen screen) {
        this.screen = screen;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        this.app = (SimpleApplication) app;

        this.app.setDisplayFps(false);
        this.app.setDisplayStatView(false);

        phatScreenshotAppState = this.app.getStateManager().getState(PHATScreenshotAppState.class);
        if (phatScreenshotAppState == null) {
            phatScreenshotAppState = new PHATScreenshotAppState();
            this.app.getStateManager().attach(phatScreenshotAppState);
        }
        phatScreenshotAppState.takeScreenshot();
    }

    @Override
    public void update(float tpf) {
        if (phatScreenshotAppState.isImageReady() && imagePanel == null) {
            screen.parseLayout("Interface/ScreenshotWindow.gui.xml",
                    this);

            // Here we can grab pointers to the loaded elements
            window = (Window) screen.getElementById("ScreenshotWindow");
            fileNameTextField = (TextField) screen.getElementById("FileNameTextField");
            fileNameTextField.setText(phatScreenshotAppState.getFileName());
            filePathTextField = (TextField) screen.getElementById("FilePathTextField");
            System.out.println("FilePath = " + phatScreenshotAppState.getFilePath());
            filePathTextField.setText(phatScreenshotAppState.getFilePath());
            imagePanel = (Label) screen.getElementById("ScreenshotImage");
            //imagePanel = new Label(screen, new Vector2f(100f, 180f), new Vector2f(200f, 200f));

            window.setPosition((screen.getWidth() - window.getWidth()) / 2f, (screen.getHeight() - window.getHeight()) / 2f);

            Image image = phatScreenshotAppState.getImage();

            Texture texture = new Texture2D(image);
            texture.setMagFilter(Texture.MagFilter.Nearest);
            Material mat = new Material(app.getAssetManager(),
                    "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setTexture("ColorMap", texture);

            float factor = (float) phatScreenshotAppState.getImage().getWidth()
                    / phatScreenshotAppState.getImage().getHeight();
            System.out.println("iWidth" + phatScreenshotAppState.getImage().getWidth());
            System.out.println("iHeight" + phatScreenshotAppState.getImage().getHeight());
            System.out.println("factor = " + factor);
            System.out.println("width = " + imagePanel.getDimensions().x);
            System.out.println("height = " + imagePanel.getDimensions().y);
            imagePanel.setDimensions(imagePanel.getDimensions().x * factor, imagePanel.getDimensions().y);
            //mat.setColor("Color", ColorRGBA.White);
            imagePanel.setMaterial(mat);

            imagePanel.setPosition((window.getWidth() - imagePanel.getWidth()) / 2f, imagePanel.getPosition().y);
        }
    }

    public void saveScreenshot(MouseButtonEvent evt, boolean isToggle) {
        phatScreenshotAppState.setFileName(fileNameTextField.getText());
        phatScreenshotAppState.setFilePath(filePathTextField.getText());
        phatScreenshotAppState.saveScreenshot();
        app.getStateManager().detach(this);
    }

    @Override
    public void cleanup() {
        super.cleanup();

        // We can alter the effect to destroy our inventory window
        // when we unload the AppState
        Effect hide = new Effect(Effect.EffectType.FadeOut, Effect.EffectEvent.Hide, 0.25f);
        hide.setDestroyOnHide(true);

        screen.removeElement(window);

        app.getStateManager().attach(new GUIMainMenuAppState(screen));
        // Now our UI component scene fades out when the AppState in unloaded.
    }
}
