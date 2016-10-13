package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResults;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.scene.Node;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;

/**
 * @author Jakob Schoser
 */
public class Main extends SimpleApplication implements ActionListener {
    
    private Node headset = new Node("headset");
    private Nifty nifty;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        setUpAtmosphere();
        setUpCam();
        setUpGui();
        loadHeadset();
        setUpInput();
    }

    @Override
    public void simpleUpdate(float tpf) {
        headset.rotate(0,FastMath.QUARTER_PI*tpf,0);
    }

    private void setUpAtmosphere() {
        viewPort.setBackgroundColor(ColorRGBA.LightGray);
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(4,-2,-2));
        rootNode.addLight(sun);
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White.mult(0.4f));
        rootNode.addLight(ambient);
    }
    
    private void setUpCam() {
        flyCam.setEnabled(false);
        cam.setLocation(new Vector3f(0,4,8));
        cam.lookAt(new Vector3f(0,1.1f,0), Vector3f.UNIT_Y);
    }
    
    private void setUpGui() {
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, 
                inputManager, audioRenderer, guiViewPort);
        nifty = niftyDisplay.getNifty();
        nifty.fromXml("Interface/TextBox.xml", "start");
        guiViewPort.addProcessor(niftyDisplay);
    }
    
    private void loadHeadset() {
        Node model = (Node) assetManager.loadModel("Scenes/CardboardBuild.j3o");
        model.move(0,0,1);        
        headset.attachChild(model);
        headset.move(1.2f,-0.5f,0);
        rootNode.attachChild(headset);
    }
    
    private void setUpInput() {
        inputManager.addMapping("leftClick", new MouseButtonTrigger(MouseInput.
                BUTTON_LEFT));
        inputManager.addListener(this,"leftClick");
    }

    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("leftClick") && isPressed) {
            CollisionResults results = new CollisionResults();
            Vector3f click3D = cam.getWorldCoordinates(inputManager.
                    getCursorPosition(),0);
            Vector3f dir = cam.getWorldCoordinates(inputManager.
                    getCursorPosition(),1).subtract(click3D);
            Ray ray = new Ray(cam.getLocation(),dir);
            headset.collideWith(ray,results);
            if (results.size() > 0) {
                Element niftyElement = nifty.getCurrentScreen().
                        findElementByName("infoText");
                niftyElement.getRenderer(TextRenderer.class).setText(results.
                        getClosestCollision().getGeometry().getName());
            }
        }
    }
}
