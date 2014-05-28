package phat.devices.controls;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AWTLoader;
import java.awt.image.BufferedImage;
import java.io.IOException;
import phat.mobile.adm.AndroidVirtualDevice;

/**
 *
 * @author pablo
 */
public class ScreenAVDControl extends AbstractControl {

    float frecuency = 1f;
    float cont = 0f;
    AndroidVirtualDevice avd;
    Node device;
    Texture texture;
    AWTLoader awtLoader;
    final BufferedImage[] buf = new BufferedImage[2];
    int index = -1;
    int imagCont = 0;
    Thread imageCapture;

    public ScreenAVDControl(Node device, AndroidVirtualDevice avd) {
        this.device = device;
        this.avd = avd;
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);

        if (spatial != null) {
            device = (Node) spatial;
            awtLoader = new AWTLoader();
            imageCapture = new Thread() {
                @Override
                public void run() {
                    while (true) {
                        long t1 = System.currentTimeMillis();
                        BufferedImage bi = avd.takeSnapshot();
                        long t2 = System.currentTimeMillis();
                        System.out.println("takeSnapshot Time = " + (t2 - t1));
                        synchronized (buf) {
                            if (index == -1) {
                                index = 0;
                                buf[0] = bi;
                            } else {
                                index = (index + 1) % 2;
                                buf[index] = bi;
                            }
                            imagCont++;
                        }
                    }
                }
            };
            imageCapture.start();
        } else {
            imageCapture.interrupt();
        }
    }

    @Override
    protected void controlUpdate(float fps) {
        cont += fps;
        if (cont > frecuency) {
            synchronized (buf) {
                if (imagCont > 0) {
                    Geometry geo = (Geometry) device.getChild(0);
                    if (texture == null) {
                        texture = new Texture2D(buf[index].getWidth(), buf[index].getHeight(), Image.Format.Depth24);
                        texture.setImage(awtLoader.load(buf[index], true));
                        geo.getMaterial().setTexture("ColorMap", texture);
                    } else {
                        texture.setImage(awtLoader.load(buf[index], true));
                        geo.updateGeometricState();
                    }
                    imagCont--;
                }
            }
            cont = 0f;
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    @Override
    public Control cloneForSpatial(Spatial sptl) {
        return new ScreenAVDControl(device, avd);
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);

    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
    }

    public float getFrecuency() {
        return frecuency;
    }

    public void setFrecuency(float frecuency) {
        this.frecuency = frecuency;
    }
}