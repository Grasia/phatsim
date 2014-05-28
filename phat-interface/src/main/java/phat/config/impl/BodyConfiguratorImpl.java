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
