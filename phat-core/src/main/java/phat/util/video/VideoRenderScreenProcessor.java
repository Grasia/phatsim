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
package phat.util.video;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import com.jme3.post.SceneProcessor;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.Renderer;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.texture.FrameBuffer;
import com.jme3.util.BufferUtils;

/**
 *
 * @author Rafael Pax
 *
 */
public class VideoRenderScreenProcessor implements SceneProcessor {

    private RenderManager renderManager;
    private ViewPort viewPort;
    private int width;
    private int height;
    private VideoSequenceWriter videoWriter;
    private File output;
    private boolean initialized;
    private AtomicBoolean writing;

    private FrameBuffer originalFB;

    public VideoRenderScreenProcessor(File output) {
        this.output = output;
        this.initialized = false;
    }

    @Override
    public void initialize(RenderManager rm, ViewPort vp) {
        this.renderManager = rm;
        this.width = vp.getCamera().getWidth();
        this.height = vp.getCamera().getHeight();
        this.viewPort = vp;
        this.writing = new AtomicBoolean(true);
        this.videoWriter = new JavaCVWriter(width, height);
        videoWriter.start(output);
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                VideoRenderScreenProcessor.this.cleanup();
            }
        }));
        this.reshape(vp, vp.getCamera().getWidth(), vp.getCamera().getHeight());
        this.initialized = true;

    }

    @Override
    public void reshape(ViewPort vp, int w, int h) {
        this.width = w;
        this.height = h;
        this.videoWriter.setWidth(width);
        this.videoWriter.setHeight(height);
    }

    @Override
    public boolean isInitialized() {
        return this.initialized;
    }

    @Override
    public void preFrame(float tpf) {

    }

    @Override
    public void postQueue(RenderQueue rq) {

    }

    @Override
    public void postFrame(FrameBuffer out) {
        if (!this.initialized) {
            return;
        }
        if (!this.isLastProcessor()) {
            return;
        }
        //
        final Renderer renderer = this.renderManager.getRenderer();

        ByteBuffer cpuBuff = getSharedBuffer(this.width * this.height * 4);

        renderer.readFrameBuffer(out, cpuBuff);
        try {
            this.videoWriter.writePicture(cpuBuff);
        } catch (RuntimeException e) {
            System.err.println(
                    "Problematic viewPort: " + this.viewPort.getName());
            throw e;
        }
    }

    public boolean isLastProcessor() {
        final List<SceneProcessor> processors = this.viewPort.getProcessors();
        return processors.get(processors.size() - 1) == this;
    }

    @Override
    public void cleanup() {
        if (this.writing.getAndSet(false)) {
            if (this.videoWriter != null) {
                this.videoWriter.stop();
            }
        }
    }

    private static ByteBuffer getSharedBuffer(int size) {
        SharedCPUByteBuffer current = SharedCPUByteBuffer.cpuByteBufferTL.get();
        if (current.byteBuffer != null) {
            current.byteBuffer.clear();
        }
        current.byteBuffer = BufferUtils.ensureLargeEnough(current.byteBuffer,
                size);
        return current.byteBuffer;
    }

    private static class SharedCPUByteBuffer {

        private ByteBuffer byteBuffer;
        private static ThreadLocal<SharedCPUByteBuffer> cpuByteBufferTL;

        static {
            cpuByteBufferTL = ThreadLocal
                    .withInitial(new Supplier<SharedCPUByteBuffer>() {
                        @Override
                        public SharedCPUByteBuffer get() {
                            return new SharedCPUByteBuffer();
                        }
                    });
        }
    }

    public ViewPort getViewPort() {
        return this.viewPort;
    }

    public FrameBuffer getOriginalFB() {
        return this.originalFB;
    }

}
