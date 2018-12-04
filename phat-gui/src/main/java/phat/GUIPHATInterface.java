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

import javax.swing.SwingUtilities;

import com.jme3.app.SimpleApplication;

import phat.agents.AgentsAppState;
import phat.gui.GUIMainMenuAppState;
import phat.gui.logging.LoggingViewerAppState;
import phat.gui.logging.PrettyLogViewerAppState;
import tonegod.gui.core.Screen;

/**
 *
 * @author sala26
 */
public class GUIPHATInterface extends JSONPHATInterface {

	private GUIMainMenuAppState guimainMenu;
	private boolean displayFps = false;
	private boolean statView = false;
	private PrettyLogViewerAppState prettyLog = null;

	public GUIPHATInterface(PHATInitializer initializer) {
		super(initializer);
	}

	public GUIPHATInterface(PHATInitializer initializer, GUIArgumentProcessor ap) {
		super(initializer, ap);
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

		prettyLog = app.getStateManager().getState(PrettyLogViewerAppState.class);
		if (prettyLog == null) {
			prettyLog = new PrettyLogViewerAppState();

		}
		app.getStateManager().attach(prettyLog);
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

	public void setPrettyLogView(boolean b) {
		do {
			prettyLog = app.getStateManager().getState(PrettyLogViewerAppState.class);
			try {
				Thread.currentThread().sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while (prettyLog == null);

		while (!prettyLog.isInitialized())
			try {
				Thread.currentThread().sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		while (!prettyLog.isShown()) {

			try {
				Thread.currentThread().sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		prettyLog.show();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				app.getStateManager().getState(AgentsAppState.class).getPHAInterface().getRootJFrame().pack();		
			}
		});
	}

	public void hidePrettyLogger() {
		this.setPrettyLogView(false);
		app.getStateManager().detach(prettyLog);
	}
}
