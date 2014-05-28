/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.controls.animation;

import com.jme3.math.Vector3f;
import com.jme3.system.JmeContext;
import java.util.ArrayList;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author pablo
 */
public class AnimationTest {
    static AnimationTestApp app;
    
    private String[] saPeopleElder = {"WaveAttention", "LookBehindL",
        "SwimTreadwater", "SittingOnGround", "LeverPole", "LookBehindR",
        "IdleStanding", "SawGround", "t-pose", "ScratchArm",
        "StandUpGround", "Yawn", "RunForward", "SpinSpindle",
        "EatStanding", "DrinkStanding", "SitDownGround", "Wave",
        "Hand2Belly", "Sweeping", "Sweeping1", "Hands2Hips"};
    
    @BeforeClass
    public static void setUpClass() {
        app = AnimationTestApp.startAndWait(false, 30);
    }

    @AfterClass
    public static void tearDownClass() {
        app.stop();
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test for checking computation of paths between all rooms.
     */
    @Test
    public void testAnimationNames() {
        for(String animationName: saPeopleElder) {
            assertTrue("Animation "+animationName+" is in the model.", app.existsAnimation(animationName));
        }
            
    }
}
