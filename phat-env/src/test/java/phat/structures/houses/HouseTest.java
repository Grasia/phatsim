/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.structures.houses;

import com.jme3.math.Vector3f;
import com.jme3.system.JmeContext;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Pablo
 */
public class HouseTest {

    private static TestHouse app;
    private static final int MAX_INITIALIZATION_TIMEOUT = 40;
    private static List<Vector3f> points;

    public HouseTest() {
    }

    @BeforeClass
    public static void setUpClass() {

        app = new TestHouse();
        app.setDisplayFps(false);
        app.setShowSettings(false);
        app.setPauseOnLostFocus(false);

        // Start the simulation in Headless mode. 
        // This means that all input and audio/visual output will be ignored.
        // app.start(JmeContext.Type.Headless);
        app.start(JmeContext.Type.Headless);
        int counter = 0;
        while (!app.isInitialized()) {
            try {
                counter++;
                if (counter > MAX_INITIALIZATION_TIMEOUT) {
                    System.exit(-1); //synchronization has failed
                }
                Thread.sleep(200);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

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
    public void testComputeRigthPath() {
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            Logger.getLogger(HouseTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
