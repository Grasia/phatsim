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
package phat.util;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.ScreenshotAppState;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.post.SceneProcessor;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.Renderer;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.system.JmeSystem;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image;
import com.jme3.util.BufferUtils;
import com.jme3.util.Screenshots;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pablo
 */
public class PHATScreenshotAppState extends AbstractAppState implements ActionListener, SceneProcessor {

    private static final Logger logger = Logger.getLogger(ScreenshotAppState.class.getName());
    private String filePath = null;
    private String fileName = null;
    private boolean capture = false;
    private Renderer renderer;
    private RenderManager rm;
    private ByteBuffer outBuf;
    private ByteBuffer imgBuf;
    private int width, height;
    private Image image;

    /**
     * Using this constructor, the screenshot files will be written sequentially
     * to the system default storage folder.
     */
    public PHATScreenshotAppState() {
        this.filePath = JmeSystem.getStorageFolder() + File.separator;
        this.fileName = "screenshot";
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        if (!super.isInitialized()) {
            InputManager inputManager = app.getInputManager();
            inputManager.addMapping("ScreenShot", new KeyTrigger(KeyInput.KEY_SYSRQ));
            inputManager.addListener(this, "ScreenShot");

            List<ViewPort> vps = app.getRenderManager().getPostViews();
            ViewPort last = vps.get(vps.size() - 1);
            last.addProcessor(this);
        }

        super.initialize(stateManager, app);
    }

    @Override
    public void onAction(String name, boolean value, float tpf) {
        if (value) {
            capture = true;
        }
    }

    public void takeScreenshot() {
        image = null;
        capture = true;
    }

    @Override
    public void initialize(RenderManager rm, ViewPort vp) {
        renderer = rm.getRenderer();
        this.rm = rm;
        reshape(vp, vp.getCamera().getWidth(), vp.getCamera().getHeight());
    }

    @Override
    public boolean isInitialized() {
        return super.isInitialized() && renderer != null;
    }

    @Override
    public void reshape(ViewPort vp, int w, int h) {
        outBuf = BufferUtils.createByteBuffer(w * h * 4);
        imgBuf = BufferUtils.createByteBuffer(w * h * 4);
        width = w;
        height = h;
    }

    @Override
    public void preFrame(float tpf) {
    }

    @Override
    public void postQueue(RenderQueue rq) {
    }

    @Override
    public void postFrame(FrameBuffer out) {
        if (capture) {
            capture = false;

            Camera curCamera = rm.getCurrentCamera();
            int viewX = (int) (curCamera.getViewPortLeft() * curCamera.getWidth());
            int viewY = (int) (curCamera.getViewPortBottom() * curCamera.getHeight());
            int viewWidth = (int) ((curCamera.getViewPortRight() - curCamera.getViewPortLeft()) * curCamera.getWidth());
            int viewHeight = (int) ((curCamera.getViewPortTop() - curCamera.getViewPortBottom()) * curCamera.getHeight());

            renderer.setViewPort(0, 0, width, height);
            renderer.readFrameBuffer(out, outBuf);
            renderer.setViewPort(viewX, viewY, viewWidth, viewHeight);

            for (int i = 0; i < width*height*4; i+=4) {
                byte r = outBuf.get(i);
                byte g = outBuf.get(i + 1);
                byte b = outBuf.get(i + 2);
                byte a = outBuf.get(i + 3);
                
                imgBuf.put(i, b); // r
                imgBuf.put(i + 1, g); // g
                imgBuf.put(i + 2, r); // b
                imgBuf.put(i + 3, a); // a
            }
            
            image = new Image(Image.Format.RGBA8, width, height, imgBuf);
        }
    }

    public boolean isImageReady() {
        return image != null;
    }

    public Image getImage() {
        return image;
    }

    public void saveScreenshot() {
        File file = new File(getFilePath() + getFileName() + ".png").getAbsoluteFile();
        logger.log(Level.INFO, "Saving ScreenShot to: {0}", file.getAbsolutePath());

        OutputStream outStream = null;
        try {
            outStream = new FileOutputStream(file);
            JmeSystem.writeImageFile(outStream, "png", outBuf, width, height);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Error while saving screenshot", ex);
        } finally {
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, "Error while saving screenshot", ex);
                }
            }
        }
    }

    /**
     * Set the file path to store the screenshot. Include the seperator at the
     * end of the path. Use an emptry string to use the application folder. Use
     * NULL to use the system default storage folder.
     *
     * @param file File path to use to store the screenshot. Include the
     * seperator at the end of the path.
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
        if (!this.filePath.endsWith(File.separator)) {
            this.filePath += File.separator;
        }
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return this.fileName;
    }
}
