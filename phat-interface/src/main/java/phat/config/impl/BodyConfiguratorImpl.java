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
package phat.config.impl;

import phat.body.BodiesAppState;
import phat.body.BodiesAppState.BodyType;
import phat.commands.PHATCommand;
import phat.config.BodyConfigurator;

public class BodyConfiguratorImpl implements BodyConfigurator {
	BodiesAppState bodiesAppState;
	
	public BodyConfiguratorImpl(BodiesAppState bodiesAppState) {
		super();
		this.bodiesAppState = bodiesAppState;
	}

	@Override
	public String createBody(BodyType type, String id) {
		return bodiesAppState.createBody(type, id);
	}

	@Override
	public void setInSpace(String bodyId, String house, String spaceId) {
		bodiesAppState.setInSpace(bodyId, house, spaceId);
	}

	public void runCommand(PHATCommand command) {
		bodiesAppState.runCommand(command);
    }
	
	public BodiesAppState getBodiesAppState() {
		return bodiesAppState;
	}
}
