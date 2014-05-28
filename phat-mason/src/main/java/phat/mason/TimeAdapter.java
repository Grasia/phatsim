/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.mason;

/**
 *
 * @author Pablo
 */
public class TimeAdapter {
    private float timePerStep;

    private float internalTimer;
    
    public TimeAdapter(float timePerStep) {
        this.timePerStep = timePerStep;
        internalTimer = 0f;
    }

    public boolean isNewStep(float tpf) {
        internalTimer += tpf;
        if(internalTimer >= timePerStep) {
            internalTimer -= timePerStep;
            return true;
        }
        return false;
    }
    
    public float getTimePerStep() {
        return timePerStep;
    }

    public void setTimePerStep(float timePerStep) {
        this.timePerStep = timePerStep;
    }
}
