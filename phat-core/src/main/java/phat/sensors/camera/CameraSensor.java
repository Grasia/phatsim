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
package phat.sensors.camera;

import com.jme3.post.SceneProcessor;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.texture.FrameBuffer;
import com.jme3.util.BufferUtils;
import com.jme3.util.Screenshots;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import phat.sensors.Sensor;
import phat.util.ConvertBgraToAbgr;
import phat.util.PHATImageUtils;

/**
 *
 * @author Pablo
 */
public class CameraSensor extends Sensor implements SceneProcessor {

    boolean initialized = false;
    RenderManager renderManager;
    ViewPort viewPort;
    ByteBuffer outBuf;
    int width;
    int height;
    BufferedImage rawFrame;

    public CameraSensor(String id) {
        super(id);
    }

    @Override
    public void initialize(RenderManager rm, ViewPort vp) {
        this.renderManager = rm;
        this.viewPort = vp;
        reshape(vp, vp.getCamera().getWidth(), vp.getCamera().getHeight());
        initialized = true;
    }

    @Override
    public void reshape(ViewPort vp, int w, int h) {
        outBuf = BufferUtils.createByteBuffer(w * h * 4);
        rawFrame = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
        width = w;
        height = h;
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if(enabled) {
            if(viewPort != null) {
                viewPort.addProcessor(this);
            }
        } else {
            if(viewPort != null) {
                viewPort.removeProcessor(this);
            }
        }
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }
    float cfps = 0f;

    @Override
    public void preFrame(float fps) {
        this.cfps = fps;
    }

    @Override
    public void postQueue(RenderQueue rq) {
    }

    private void process1(FrameBuffer fb) {
        Camera curCamera = viewPort.getCamera();
        int viewX = (int) (curCamera.getViewPortLeft() * curCamera.getWidth());
        int viewY = (int) (curCamera.getViewPortBottom() * curCamera.getHeight());
        int viewWidth = (int) ((curCamera.getViewPortRight() - curCamera.getViewPortLeft()) * curCamera.getWidth());
        int viewHeight = (int) ((curCamera.getViewPortTop() - curCamera.getViewPortBottom()) * curCamera.getHeight());

        renderManager.getRenderer().setViewPort(0, 0, width, height);
        renderManager.getRenderer().readFrameBuffer(fb, outBuf);
        renderManager.getRenderer().setViewPort(viewX, viewY, viewWidth, viewHeight);
    }

    private void process2(FrameBuffer fb) {
        Camera cam = renderManager.getCurrentCamera();

        Camera curCamera = viewPort.getCamera();
        float viewX = curCamera.getViewPortLeft();
        float viewY = curCamera.getViewPortBottom();
        float viewWidth = curCamera.getViewPortRight();
        float viewHeight = curCamera.getViewPortTop();

        float xFactor = (float) cam.getWidth() / (float) curCamera.getWidth();
        float yFactor = (float) cam.getHeight() / (float) curCamera.getHeight();

        viewPort.getCamera().setViewPort(0, 1 * xFactor, 0, 1 * yFactor);
        renderManager.getRenderer().readFrameBuffer(fb, outBuf);
        viewPort.getCamera().setViewPort(viewX, viewWidth, viewY, viewHeight);
    }

    private void process3(FrameBuffer fb) {
        Camera curCamera = viewPort.getCamera();
        int viewX = (int) (curCamera.getViewPortLeft() * curCamera.getWidth());
        int viewY = (int) (curCamera.getViewPortBottom() * curCamera.getHeight());
        int viewWidth = (int) ((curCamera.getViewPortRight() - curCamera.getViewPortLeft()) * curCamera.getWidth());
        int viewHeight = (int) ((curCamera.getViewPortTop() - curCamera.getViewPortBottom()) * curCamera.getHeight());

        renderManager.getRenderer().setViewPort(320, 640, 0, 240);
        renderManager.getRenderer().readFrameBuffer(fb, outBuf);
        renderManager.getRenderer().setViewPort(0, 0, width, height);
    }

    public void easy(FrameBuffer fb) {
        renderManager.getRenderer().readFrameBuffer(fb, outBuf);
        //Screenshots.convertScreenShot(outBuf, rawFrame);
        ConvertBgraToAbgr.convert(outBuf, rawFrame);
        //PHATImageUtils.getScreenShotBGRA(outBuf, rawFrame);
    }

    @Override
    public void postFrame(FrameBuffer fb) {
        if (enabled) {
            easy(fb);

            //process2(fb);

            //Screenshots.convertScreenShot(outBuf, rawFrame);

            CameraSensorData csd = new CameraSensorData(cfps, rawFrame, width, height, 0);
            notifyListeners(csd);
        }
    }

    @Override
    public void cleanup() {
    }

    @Override
    protected void controlUpdate(float fps) {
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
