/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.util;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.control.BillboardControl;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pablo
 */
public class SpatialFactory {
    private static final Logger logger = Logger.getLogger(SpatialFactory.class.getName());
    
    private static AssetManager assetManager;
    private static Node rootNode;
    
    public static void init(AssetManager assetManager, Node rootNode) {
        SpatialFactory.assetManager = assetManager;
        SpatialFactory.rootNode = rootNode;
    }
    
    public static AssetManager getAssetManager() {
        return assetManager;
    }

    public static Node getRootNode() {
        return rootNode;
    }
    
    /**
     * Creates a cube given its dimensions and its color
     * 
     * @param dimensions
     * @param color
     * @return a cube Geometry
     */
    public static Geometry createCube(Vector3f dimensions, ColorRGBA color) {
        checkInit();
        
        Box b = new Box(dimensions.getX(), dimensions.getY(), dimensions.getZ()); // create cube shape at the origin
        Geometry geom = new Geometry("Box", b);  // create cube geometry from the shape
        Material mat = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material
        mat.setColor("Color", color);   // set color of material to blue
        geom.setMaterial(mat);                   // set the cube's material
        return geom;
    }
    
    public static Geometry createArrow(Vector3f dir, float lineWidth, ColorRGBA color) {
        Arrow arrow = new Arrow(dir);
        arrow.setLineWidth(lineWidth); // make arrow thicker
        return createShape("Arrow", arrow, color);
    }
    /**
     * Creates a geometry given its name, its mesh and its color
     * 
     * @param name
     * @param shape
     * @param color
     * @return 
     */
    public static Geometry createShape(String name, Mesh shape, ColorRGBA color) {
        checkInit();
        
        Geometry g = new Geometry(name, shape);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", color);
        g.setMaterial(mat);
        return g;
    }
    
    /**
     * Creates a geometry with the same name of the given node.
     * It adds a controller called BillboardControl that turns the
     * name of the node in order to look at the camera.
     * 
     * Letter's size can be changed using setSize() method, the text with 
     * setText() method and the color using setColor() method.
     * 
     * @param node
     * @return 
     */
    public static BitmapText attachAName(Node node) {
        checkInit();
        
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize()*0.02f);
        ch.setText(node.getName()); // crosshairs
        ch.setColor(new ColorRGBA(1f, 0.8f, 0.3f, 0.8f));        
        
        // controlador para que los objetos miren a la cámara.
        BillboardControl control=new BillboardControl();
        ch.addControl(control);
        node.attachChild(ch);
        return ch;
    }
    
    public static Geometry createImageTextureSurface(String imgPath, float width, float height) {
        Mesh m = new Mesh();

        // Vertex positions in space
        Vector3f [] vertices = new Vector3f[4];
        vertices[0] = new Vector3f(0,0,0);
        vertices[1] = new Vector3f(width,0,0);
        vertices[2] = new Vector3f(0,height,0);
        vertices[3] = new Vector3f(width,height,0);

        // Texture coordinates
        Vector2f [] texCoord = new Vector2f[4];
        texCoord[0] = new Vector2f(0,0);
        texCoord[1] = new Vector2f(1,0);
        texCoord[2] = new Vector2f(0,1);
        texCoord[3] = new Vector2f(1,1);

        // Indexes. We define the order in which mesh should be constructed
        int [] indexes = {2,0,1,1,3,2};

        // Setting buffers
        m.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        m.setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        m.setBuffer(VertexBuffer.Type.Index, 1, BufferUtils.createIntBuffer(indexes));
        m.updateBound();

        // Creating a geometry, and apply a single color material to it
        Geometry geom = new Geometry("OurMesh", m);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture cube1Tex = assetManager.loadTexture(imgPath);
        mat.setTexture("ColorMap", cube1Tex);
        geom.setMaterial(mat);
        
        return geom;
    }
    
    private static void checkInit() {
        if(assetManager == null) {
            logger.log(Level.SEVERE, "Method SpatialFactory.init() hasn't been called");
        }
    }
}
