/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.mason.agents;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.util.Collection;
import phat.agents.actors.BasicActor;
import phat.mason.space.Util;
import sim.field.continuous.Continuous3D;
import sim.util.Double3D;

/**
 *
 * @author pablo
 */
public class PhysicsActorImpl extends AbstractControl implements PhysicsActor {

    private BasicActor basicActor;
    private Continuous3D world;
    private Agent agent;

    protected PhysicsActorImpl(BasicActor basicActor, Continuous3D world) {
        this.basicActor = basicActor;
        this.world = world;
    }

    @Override
    protected void controlUpdate(float f) {
        updateMASONWorldLoaction();
    }

    private void updateMASONWorldLoaction() {
        Double3D loc = Util.get(basicActor.getLocation());
        if (loc != null && loc.distance(getLocation()) >= world.discretization) {
            world.setObjectLocation(this, loc);
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    @Override
    public Control cloneForSpatial(Spatial sptl) {
        return new PhysicsActorImpl(basicActor, world);
    }

    @Override
    public String getName() {
        return basicActor.getName();
    }

    @Override
    public Continuous3D world() {
        return world;
    }

    @Override
    public String getRoll() {
        return getName();
    }

    @Override
    public Double3D getLocation() {
        return world.getObjectLocation(this);//Util.get(basicActor.getLocation());
    }

    @Override
    public void moveTo(Double3D loc) {
        basicActor.moveTo(Util.get(loc));
    }
    
    @Override
    public void moveTo(Double3D loc, float distance) {
        basicActor.moveTo(Util.get(loc));
    }

    @Override
    public String currentAnimName() {
        return basicActor.getCurrentAnimationName();
    }


    @Override
    public void showName(boolean showName) {
        basicActor.showName(showName);
    }

    @Override
    public void tripOver() {
        basicActor.tripOver();
    }
    
    @Override
    public void slip() {
        basicActor.slip();
    }

    @Override
    public void standUp() {
        basicActor.standUp();
    }

    @Override
    public void playAnimation(String name) {
        basicActor.setAnimation(name);
    }

    @Override
    public Agent agent() {
        return agent;
    }
    
    @Override
    public void putAgent(Agent agent) {
        this.agent = agent;
    }
    
    @Override
    public String toString() {
        return getName();
    }

    @Override
    public Collection<String> animationName() {
        return basicActor.getAnimationName();
    }

    @Override
    public boolean hasAnimation(String animationName) {
        return basicActor.hasAnimation(animationName);
    }

    @Override
    public void say(String text, float volume) {
        basicActor.say(text, volume);
    }

    @Override
    public String getCurrentAction() {
        return agent.getCurrentAction();
    }

    @Override
    public void stopMoving() {
        basicActor.stopMoving();
    }
}
