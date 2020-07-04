import jndev.pseudo3d.application.Game;
import jndev.pseudo3d.listener.Keyboard;
import jndev.pseudo3d.loader.Sprite;
import jndev.pseudo3d.object.Object;
import jndev.pseudo3d.scene.Camera;
import jndev.pseudo3d.scene.Scene;
import jndev.pseudo3d.util.Vector;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;

/**
 * testing class for testing various bits of this project
 *
 * @author JNDev (Jeremaster101)
 */
public class Testing {
    
    /**
     * run the test scene
     *
     * @param args program arguments
     */
    public static void main(String[] args) {
        Sprite.load(new File("res/sprites/"));
        Scene scene = new Scene();
        Object object = new Object();
        object.setSprite(Sprite.get("res/sprites/player/front.png"));
        object.setGravityScale(1);
        object.getBoundingBox().setWidth(object.getSprite().getWidth(null));
        object.getBoundingBox().setHeight(object.getSprite().getHeight(null));
        object.getBoundingBox().setDepth(object.getSprite().getWidth(null));
        object.setPosition(new Vector(object.getBoundingBox().getWidth() * 10 - 450, 0, -object.getBoundingBox().getWidth()));
        object.setDrag(new Vector(0.05, 0.05, 0.05));
        scene.addObject(object);
        
        for (int j = 1; j < 20; j++) {
            for (int i = 1; i <= 10; i++) {
                Object copy = new Object(object);
                copy.setPosition(new Vector(object.getBoundingBox().getWidth() * j - 450, -400, -object.getBoundingBox().getWidth() * i));
                copy.setGravityScale(0);
                copy.setFriction(new Vector(0.07, 0.07, 0.07));
                scene.addObject(copy);
            }
        }
        
        Camera camera = new Camera();
        camera.setFieldOfView(90);
        camera.setSensorSize(1000);
        camera.setPosition(new Vector(0, 0, -100));
        
        scene.setCamera(camera);
        scene.setBackground(Color.WHITE);
        
        Game.launch(1000, 1000);
        Game.getLoop().setActiveScene(scene);
        Game.getLoop().start();
        
        Game.getLoop().inject(() -> {
            if (Game.getLoop().getActiveScene().equals(scene)) {
                if (Keyboard.isPressed(KeyEvent.VK_W) && camera.getFieldOfView() > 0) {
                    object.setVelocity(object.getVelocity().setZ(-1));
                    object.setSprite(Sprite.get("res/sprites/player/front.png"));
                }
                
                if (Keyboard.isPressed(KeyEvent.VK_S) && camera.getFieldOfView() > 0) {
                    object.setVelocity(object.getVelocity().setZ(1));
                    object.setSprite(Sprite.get("res/sprites/player/front.png"));
                }
                
//                if ((!Keyboard.isPressed(KeyEvent.VK_W) && !Keyboard.isPressed(KeyEvent.VK_S)) ||
//                        camera.getFieldOfView() == 0) {
//                    object.setVelocity(object.getVelocity().setZ(0));
//                }
                
                if (Keyboard.isPressed(KeyEvent.VK_A)) {
                    object.setVelocity(object.getVelocity().setX(-1));
                    object.setSprite(Sprite.get("res/sprites/player/left.png"));
                }
                
                if (Keyboard.isPressed(KeyEvent.VK_D)) {
                    object.setVelocity(object.getVelocity().setX(1));
                    object.setSprite(Sprite.get("res/sprites/player/right.png"));
                }
                
//                if (!Keyboard.isPressed(KeyEvent.VK_A) && !Keyboard.isPressed(KeyEvent.VK_D)) {
//                    object.setVelocity(object.getVelocity().setX(0));
//                }
                
                if (Keyboard.isPressed(KeyEvent.VK_SPACE)) {
                    object.setVelocity(object.getVelocity().setY(1));
                }
                
                if (Keyboard.isPressed(KeyEvent.VK_SHIFT)) {
                    object.setVelocity(object.getVelocity().setY(-1));
                }
                
//                if (!Keyboard.isPressed(KeyEvent.VK_SPACE) && !Keyboard.isPressed(KeyEvent.VK_SHIFT) &&
//                        object.getGravityScale() == 0) {
//                    object.setVelocity(object.getVelocity().setY(0));
//                }
                
                if (Keyboard.isPressed(KeyEvent.VK_UP)) {
                    camera.setFieldOfView(Math.min(camera.getFieldOfView() + 1, 178.0));
                }
                
                if (Keyboard.isPressed(KeyEvent.VK_DOWN)) {
                    camera.setFieldOfView(Math.max(camera.getFieldOfView() - 1, 0));
                }
            }
        });
    }
}
