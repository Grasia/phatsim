/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.config;

import phat.body.BodiesAppState.BodyType;
import phat.commands.PHATCommand;

/**
 *
 * @author sala26
 */
public interface BodyConfigurator {
	public String createBody(BodyType type, String id);
	public void setInSpace(String bodyId, String house, String spaceId);
	public void runCommand(PHATCommand command);
}
