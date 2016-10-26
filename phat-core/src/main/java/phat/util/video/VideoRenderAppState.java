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
import java.util.List;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.post.SceneProcessor;
import com.jme3.renderer.ViewPort;
import com.jme3.texture.FrameBuffer;
import com.jme3.util.SafeArrayList;

/**
 *
 * @author Rafael Pax
 *
 */
public class VideoRenderAppState extends AbstractAppState {

    private List<VideoRenderScreenProcessor> processors;

    public VideoRenderAppState() {
        this.processors = new SafeArrayList<VideoRenderScreenProcessor>(
                VideoRenderScreenProcessor.class);
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
    }

    public void recordViewPort(ViewPort vp, File output) {
        this.recordViewPort(vp, output, vp.getOutputFrameBuffer());
    }

    public void stopRecording(ViewPort vp) {
        SceneProcessor toRemove = null;
        for (VideoRenderScreenProcessor p : processors) {
            if (p.getViewPort() == vp) {
                toRemove = p;
                break;
            }
        }
        if (toRemove != null) {
            this.processors.remove(toRemove);
            vp.removeProcessor(toRemove);
        }
    }

    public void recordViewPort(ViewPort vp, File output, FrameBuffer ofb) {
        VideoRenderScreenProcessor processor = new VideoRenderScreenProcessor(
                output);
        vp.addProcessor(processor);
        this.processors.add(processor);

    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        // Ensure every OffScreenProcessor is the last one of each viewport.
        for (VideoRenderScreenProcessor osp : processors) {
            if (osp.isInitialized()) {
                ViewPort vp = osp.getViewPort();
                ensureProcessorIsLast(vp);
            }
        }
    }

    protected void ensureProcessorIsLast(ViewPort vp) {
        final List<SceneProcessor> processors = vp.getProcessors();

        int processorIndex = -1;
        for (int i = 0; i < processors.size(); i++) {
            SceneProcessor processor = processors.get(i);
            if (processor instanceof VideoRenderScreenProcessor) {
                if (processorIndex != -1) {
                    throw new IllegalStateException(
                            "ViewPort cannot have two processors of type "
                            + VideoRenderScreenProcessor.class
                            .getName());
                }
                processorIndex = i;
            }
        }
        if (processorIndex == -1) {
            throw new IllegalStateException(
                    "Viewport does not have any screenProcessor of type "
                    + VideoRenderScreenProcessor.class.getName()
                    + ". Was it removed elsewhere?");
        }
        if (processorIndex > 0) {
            SceneProcessor recorder = processors.remove(processorIndex);
            processors.add(recorder);
        }
    }

    @Override
    public void cleanup() {
        super.cleanup();
        for (VideoRenderScreenProcessor p : processors) {
            p.cleanup();
        }
    }

    @Override
    public void postRender() {
        super.postRender();

    }

}
