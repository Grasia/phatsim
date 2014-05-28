/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.mason;

import com.aurellem.capture.IsoTimer;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.audio.AudioRenderer;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.input.InputManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl;
import com.jme3.scene.shape.Quad;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.jce.provider.symmetric.Camellia;
import phat.agents.actors.ActorFactory;
import phat.audio.AudioFactory;
import phat.audio.listeners.AudioSourceWaveFileWriter;
import phat.audio.listeners.PCSpeaker;
import phat.audio.listeners.XYRMSAudioChart;
import phat.devices.smartphone.SmartPhoneFactory;
import phat.mason.agents.ActorAdapter;
import phat.mason.space.HouseAdapter;
import phat.sensors.microphone.MicrophoneControl;
import phat.structures.houses.House;
import phat.util.Debug;
import phat.util.SpatialFactory;

/**
 *
 * @author Pablo
 */
public class MASONAppState extends AbstractAppState implements PhysicsCollisionListener {

    private PHATApplication app;
    private AppStateManager stateManager;
    private Node rootNode;
    private AssetManager assetManager;
    private InputManager inputManager;
    private ViewPort viewPort;
    private AudioRenderer audioRenderer;
    private BulletAppState physics;
    private PHATSimState simState;
    private HouseAdapter houseAdapter;
    private ActorAdapter actorAdapter;
    private float defaultTimePerStep = 1f; // 1 second = 1 step
    private TimeAdapter timeAdapter;
    private ConcurrentLinkedQueue<Runnable> collisionActions = new ConcurrentLinkedQueue<Runnable>();
    private MicrophoneControl micControl;

    public MASONAppState(PHATSimState simState) {
        this.simState = simState;
    }

    /**
     * When this AppState is added to the game, the RenderThread initializes the
     * AppState and then calls this method. You can modify the scene graph from
     * here (e.g. attach nodes).
     */
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        System.out.println(getClass().getSimpleName() + " initialize()...");
        super.initialize(stateManager, app);
        this.app = (PHATApplication) app;
        this.stateManager = stateManager;

        this.rootNode = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();
        this.stateManager = this.app.getStateManager();
        this.inputManager = this.app.getInputManager();
        this.viewPort = this.app.getViewPort();
        this.audioRenderer = this.app.getAudioRenderer();
        this.physics = this.stateManager.getState(BulletAppState.class);

        ActorFactory.init(rootNode, assetManager, physics);
        SpatialFactory.init(assetManager, rootNode);
        SmartPhoneFactory.init(physics, assetManager, app.getRenderManager(), 
                app.getCamera(), app.getAudioRenderer());
        //phat.util.Debug.enableDebugGrid(10f, assetManager, rootNode);

        timeAdapter = new TimeAdapter(defaultTimePerStep);

        createHouse(simState.getHouseID(), simState.getHouseUrl());

        actorAdapter = new ActorAdapter(simState);

        physics.getPhysicsSpace().addCollisionListener(this);

        //micControl = new MicrophoneControl("Micro1", 10000, audioRenderer);

        /*Node camNode = new CameraNode(app.getCamera());
         rootNode.attachChild(camNode);
         camNode.addControl(micControl);*/

        //PCSpeaker pcSpeaker = new PCSpeaker();
        //micControl.add(pcSpeaker);


        /*AudioSourceWaveFileWriter aswfw;
         try {
         aswfw = new AudioSourceWaveFileWriter(new File("audio.wav"));
         micControl.add(aswfw);
         } catch (FileNotFoundException ex) {
         Logger.getLogger(MASONAppState.class.getName()).log(Level.SEVERE, null, ex);
         }*/

        simState.init(this);

        //setMessage("Time out alarm:\n Patient spent 5 minutes on the floor!");

        System.out.println(getClass().getSimpleName() + " ...initialize()");

        //Debug.enableDebugGrid(10, assetManager, rootNode);
    }

    public void createAudioPlot(String person) {
        if (micControl != null) {
            XYRMSAudioChart chart = new XYRMSAudioChart(person);
            micControl.add(chart);
            chart.showWindow();
        }
    }

    public void setCameraAsListener() {
        Node camFollower = new Node();
        // means that the Camera's transform is "copied" to the Transform of the Spatial.
        CameraControl cc = new CameraControl(app.getCamera(), CameraControl.ControlDirection.CameraToSpatial);
        camFollower.addControl(cc);
        rootNode.attachChild(camFollower);
        camFollower.addControl(micControl);
    }

    @Override
    public void update(float tpf) {
        // first, process collision events
        Vector<Runnable> currentActions = new Vector<Runnable>(collisionActions);
        collisionActions.removeAll(currentActions);
        for (Runnable run : currentActions) {
            run.run();
        };

        if (simState.isFinish()) {
            return;
        }

        if (timeAdapter.isNewStep(tpf)) {
            System.out.println("step = " + tpf);
            simState.step(simState);
        }

    }

    @Override
    public void cleanup() {
        super.cleanup();
    }

    public HouseAdapter createHouse(String houseId, String houseUrl) {
        House house = new House(houseId, houseUrl);
        house.build(rootNode, assetManager, physics.getPhysicsSpace());

        //physics.getPhysicsSpace().removeAll(((Node)house.getNode().getChild("PhysicalEntities")).getChild("Geometries"));

        houseAdapter = new HouseAdapter(physics.getPhysicsSpace(), house);
        houseAdapter.createSpace();
        //changeLocation(house.getNode(), "Obstacle", new Vector3f(5.5658574f, 0.01f, 5.8f));
        changeLocation(rootNode, "Obstacle", new Vector3f(8f, 0.01f, 8f));

        house.setHighPhysicsPrecision();
        return houseAdapter;
    }

    public void changeLocation(Spatial root, final String name, final Vector3f location) {
        SceneGraphVisitor visitor = new SceneGraphVisitor() {
            @Override
            public void visit(Spatial spat) {
                System.out.println("changeLocation = " + spat.getName());
                if (spat.getName() != null && spat.getName().equals(name)) {
                    RigidBodyControl rbc = spat.getControl(RigidBodyControl.class);
                    if (rbc != null) {
                        rbc.setPhysicsLocation(location);
                    }
                }
            }
        };
        root.depthFirstTraversal(visitor);
    }

    @Override
    public void collision(final PhysicsCollisionEvent pce) {
        Spatial obj1 = pce.getNodeA();
        Spatial obj2 = pce.getNodeB();
        if (obj1 != null && obj2 != null) {
            final String obj1n = pce.getNodeA().getName();
            final String obj2n = pce.getNodeB().getName();
            Runnable collisionAction = new Runnable() {
                public void run() {
                    actorAdapter.collision(obj1n, obj2n);
                }
            };
            collisionActions.add(collisionAction);
        }
    }

    public HouseAdapter getHouseAdapter() {
        return houseAdapter;
    }

    public TimeAdapter getTimeAdapter() {
        return timeAdapter;
    }

    public ActorAdapter getActorAdapter() {
        return actorAdapter;
    }

    public void setMic(Node node) {
        //node.addControl(micControl);
    }

    public void setMessage(String text) {
        int width = app.getAppSettings().getWidth();
        int height = app.getAppSettings().getHeight();
        BitmapFont fnt = assetManager.loadFont("Interface/Fonts/Default.fnt");
        float fontSize = fnt.getPreferredSize() * 2;
        BitmapText txt = new BitmapText(fnt, false);
        txt.setColor(ColorRGBA.Red);
        txt.setBox(new Rectangle(0, 0, (int) (width / 1.5), (int) (height / 1.5)));
        txt.setSize(fontSize);
        txt.setText(text);
        txt.setLocalTranslation(100, height / 2 + fontSize, 0);
        app.getGuiNode().attachChild(txt);
    }
}
