package phat.agents.automaton.uses;

import com.jme3.scene.Spatial;

import phat.agents.Agent;
import phat.agents.automaton.Automaton;
import phat.util.SpatialFactory;
import phat.util.SpatialUtils;

public class UseObjectAutomatonFactory {

    public static Automaton getAutomaton(Agent agent, String objectId) {
        Spatial objectSpatial = SpatialUtils.getSpatialById(
                SpatialFactory.getRootNode(), objectId);
        if (objectSpatial == null) {
            return null;
        }

        String role = objectSpatial.getUserData("ROLE");
        if (role == null) {
            return null;
        }

        if (role.equals("Shower")) {
            return new HaveAShowerAutomaton(agent, objectId);
        } else if(role.equals("WC")) {
            return new UseWCAutomaton(agent, objectId);
        } else if(role.equals("Basin") || role.equals("Sink")) {
            return new UseCommonObjectAutomaton(agent, objectId);
        } else if(role.equals("Doorbell")) {
            return new UseDoorbellAutomaton(agent, objectId);
        } else if(role.equals("TV")) {
            return new SwitchTVAutomaton(agent, objectId, true);
        }

        return null;
    }
}
