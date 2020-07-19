import jndev.pseudo3d.application.Game;
import jndev.pseudo3d.listener.Keyboard;
import jndev.pseudo3d.loader.ImageLoader;
import jndev.pseudo3d.object.RigidBodyObject;
import jndev.pseudo3d.object.SpriteObject;
import jndev.pseudo3d.scene.Camera;
import jndev.pseudo3d.scene.Scene;
import jndev.pseudo3d.sprite.ImageSprite;
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
        ImageLoader.load(new File("res/sprites/"));
        Scene scene = new Scene();
        RigidBodyObject physicsObject = new RigidBodyObject();
        physicsObject.setSprite(new ImageSprite(ImageLoader.get("res/sprites/player/front.png")));
//        physicsObject.setSprite(new ColorSprite(50, 50, Color.CYAN));
        physicsObject.getBoundingBox().setWidth(physicsObject.getSprite().getImage().getWidth(null));
        physicsObject.getBoundingBox().setHeight(physicsObject.getSprite().getImage().getHeight(null));
        physicsObject.getBoundingBox().setDepth(physicsObject.getSprite().getImage().getWidth(null));
        physicsObject.setPosition(new Vector(physicsObject.getBoundingBox().getWidth() * 10 - 450, 0, -physicsObject.getBoundingBox().getWidth()));
        physicsObject.setTerminalVelocity(new Vector(1, 1 ,1));
        scene.addObject(physicsObject);
        
        for (int j = 1; j < 22; j++) {
            for (int i = 1; i <= 6; i++) {
                RigidBodyObject copy = new RigidBodyObject(physicsObject);
                copy.setSprite(new ImageSprite(ImageLoader.get("res/sprites/floor.png")));
                copy.getBoundingBox().setWidth(copy.getSprite().getImage().getWidth(null));
                copy.getBoundingBox().setHeight(copy.getSprite().getImage().getHeight(null));
                copy.getBoundingBox().setDepth(copy.getSprite().getImage().getWidth(null));
                copy.setPosition(new Vector(copy.getBoundingBox().getWidth() * j - 525, -465, -copy.getBoundingBox().getWidth() * i));
                copy.setGravity(new Vector());
                scene.addObject(copy);
            }
        }
        
        SpriteObject spriteObject = new SpriteObject();
        spriteObject.setSprite(new ImageSprite(ImageLoader.get("res/sprites/background.png")));
        spriteObject.setPosition(new Vector(0, 0, -325));
        scene.addObject(spriteObject);
        
        Camera camera = new Camera();
        camera.setFieldOfView(72);
        camera.setScenePosition(new Vector(0, 0, -100));
        camera.setSensorSize(1000);
        
        scene.setCamera(camera);
        scene.setBackground(Color.WHITE);
        
        Game.launch(1000, 1000, false);
        Game.getLoop().setActiveScene(scene);
        Game.getLoop().start();
        
        Game.getLoop().inject(() -> {
            if (Game.getLoop().getActiveScene().equals(scene)) {
                
                System.out.println(physicsObject.getVelocity());
                
                if (Keyboard.isPressed(KeyEvent.VK_W) && camera.getFieldOfView() > 0) {
                    physicsObject.setVelocity(physicsObject.getVelocity().setZ(-1));
                    physicsObject.setSprite(new ImageSprite(ImageLoader.get("res/sprites/player/front.png")));
                }
                
                if (Keyboard.isPressed(KeyEvent.VK_S) && camera.getFieldOfView() > 0) {
                    physicsObject.setVelocity(physicsObject.getVelocity().setZ(1));
                    physicsObject.setSprite(new ImageSprite(ImageLoader.get("res/sprites/player/front.png")));
                }

                if ((Keyboard.isPressed(KeyEvent.VK_W) && Keyboard.isPressed(KeyEvent.VK_S)) ||
                        camera.getFieldOfView() == 0) {
                    physicsObject.setVelocity(physicsObject.getVelocity().setZ(0));
                }
                
                if (Keyboard.isPressed(KeyEvent.VK_A)) {
                    physicsObject.setVelocity(physicsObject.getVelocity().setX(-1));
                    physicsObject.setSprite(new ImageSprite(ImageLoader.get("res/sprites/player/left.png")));
                }
                
                if (Keyboard.isPressed(KeyEvent.VK_D)) {
                    physicsObject.setVelocity(physicsObject.getVelocity().setX(1));
                    physicsObject.setSprite(new ImageSprite(ImageLoader.get("res/sprites/player/right.png")));
                }

                if (Keyboard.isPressed(KeyEvent.VK_A) && Keyboard.isPressed(KeyEvent.VK_D)) {
                    physicsObject.setVelocity(physicsObject.getVelocity().setX(0));
                }
                
                if (Keyboard.isPressed(KeyEvent.VK_SPACE)) {
                    physicsObject.setVelocity(physicsObject.getVelocity().setY(1));
                }
                
                if (Keyboard.isPressed(KeyEvent.VK_SHIFT)) {
                    physicsObject.setVelocity(physicsObject.getVelocity().setY(-2));
                }

                if (Keyboard.isPressed(KeyEvent.VK_SPACE) && Keyboard.isPressed(KeyEvent.VK_SHIFT)) {
                    physicsObject.setVelocity(physicsObject.getVelocity().setY(0));
                }
                
                if (Keyboard.isPressed(KeyEvent.VK_UP)) {
                    camera.setFieldOfView(Math.min(camera.getFieldOfView() + 1, 72));
                }
                
                if (Keyboard.isPressed(KeyEvent.VK_DOWN)) {
                    camera.setFieldOfView(Math.max(camera.getFieldOfView() - 1, 0));
                }
            }
        });
    }
}
