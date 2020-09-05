import jndev.pseudo3d.application.Game;
import jndev.pseudo3d.listeners.Keyboard;
import jndev.pseudo3d.listeners.Mouse;
import jndev.pseudo3d.loader.ImageLoader;
import jndev.pseudo3d.objects.PhysicsObject;
import jndev.pseudo3d.objects.SpriteObject;
import jndev.pseudo3d.objects.Camera;
import jndev.pseudo3d.scene.Scene;
import jndev.pseudo3d.sprites.AnimatedSprite;
import jndev.pseudo3d.sprites.CameraSprite;
import jndev.pseudo3d.sprites.ImageSprite;
import jndev.pseudo3d.utils.Vector;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

/**
 * sandbox-style testing class for testing various bits of this project
 *
 * @author JNDev (Jeremaster101)
 */
public class Sandbox {
    
    /**
     * run the test scene
     *
     * @param args program arguments
     */
    public static void main(String[] args) {
        ImageLoader.load(new File("test/images/"));
        Scene scene = new Scene();
        PhysicsObject physicsObject = new PhysicsObject();
        ImageSprite imageSprite = new ImageSprite(ImageLoader.get("test/images/player/front.png"));
        imageSprite.setWidth(100);
        physicsObject.setSprite(imageSprite);
//        physicsObject.setSprite(new ColorSprite(50, 50, new Color(255, 0, 0, 50)));
        physicsObject.getBoundingBox().setWidth(physicsObject.getSprite().getImage().getWidth(null));
        physicsObject.getBoundingBox().setHeight(physicsObject.getSprite().getImage().getHeight(null));
        physicsObject.getBoundingBox().setDepth(physicsObject.getSprite().getImage().getWidth(null));
        physicsObject.setPosition(new Vector(physicsObject.getBoundingBox().getWidth() * 10 - 450, 0,
                -physicsObject.getBoundingBox().getWidth()));
        physicsObject.setTerminalVelocity(new Vector(1, 1, 1));
        scene.addObject(physicsObject);
        physicsObject.setGravity(new Vector());
        
        for (int j = 1; j < 22; j++) {
            for (int i = 1; i <= 6; i++) {
                PhysicsObject copy = new PhysicsObject(physicsObject);
                copy.setSprite(new ImageSprite(ImageLoader.get("test/images/floor.png")));
                copy.getBoundingBox().setWidth(copy.getSprite().getImage().getWidth(null));
                copy.getBoundingBox().setHeight(copy.getSprite().getImage().getHeight(null));
                copy.getBoundingBox().setDepth(copy.getSprite().getImage().getWidth(null));
                copy.setPosition(new Vector(copy.getBoundingBox().getWidth() * j - 525, -465,
                        -copy.getBoundingBox().getWidth() * i));
                copy.setGravity(new Vector());
                scene.addObject(copy);
            }
        }
        
        Camera camera = new Camera();
        camera.setFieldOfView(72);
        camera.setScenePosition(new Vector(0, 0, -100));
        camera.setSensorSize(1000);
        
        ArrayList<BufferedImage> images = new ArrayList<>();
        images.add(ImageLoader.get("test/images/player/front.png"));
        images.add(ImageLoader.get("test/images/player/left.png"));
        images.add(ImageLoader.get("test/images/player/right.png"));
        AnimatedSprite as = new AnimatedSprite(images, 3);
        
//        physicsObject.setSprite(as);
        
        SpriteObject spriteObject = new SpriteObject();
//        spriteObject.setSprite(new ImageSprite(ImageLoader.get("res/sprites/background.png")));
        spriteObject.setSprite(new CameraSprite(scene, camera, 500, 500));
        spriteObject.getSprite().setRotation(10);
//        spriteObject.setSprite(as);
        spriteObject.setPosition(new Vector(0, 0, -200));
        scene.addObject(spriteObject);
        
        scene.setCamera(camera);
        scene.setBackground(Color.WHITE);
        
        Game.launch(1000, 1000, false, "Testing");
        Game.getInstance().getLoop().setActiveScene(scene);
        Game.getInstance().getLoop().start();
        
        scene.addRunnable(() -> {
//                physicsObject.setVelocity(
//                        Mouse.getPosition().setY(Mouse.getPosition().scale(-1).getY())
//                                .subtract(new Vector(1280, -540))
//                                .subtract(physicsObject.getPosition().setZ(0)).scale(0.1));
                
            if (Keyboard.isPressed(KeyEvent.VK_W) && camera.getFieldOfView() > 0) {
                physicsObject.setVelocity(physicsObject.getVelocity().setZ(-1));
//                physicsObject.setSprite(new ImageSprite(ImageLoader.get("test/images/player/front.png")));
            }

            if (Keyboard.isPressed(KeyEvent.VK_S) && camera.getFieldOfView() > 0) {
                physicsObject.setVelocity(physicsObject.getVelocity().setZ(1));
//                physicsObject.setSprite(new ImageSprite(ImageLoader.get("test/images/player/front.png")));
            }

            if ((Keyboard.isPressed(KeyEvent.VK_W) && Keyboard.isPressed(KeyEvent.VK_S)) ||
                    camera.getFieldOfView() == 0) {
                physicsObject.setVelocity(physicsObject.getVelocity().setZ(0));
            }

            if (Keyboard.isPressed(KeyEvent.VK_A)) {
                physicsObject.setVelocity(physicsObject.getVelocity().setX(-1));
//                physicsObject.setSprite(new ImageSprite(ImageLoader.get("test/images/player/left.png")));
            }

            if (Keyboard.isPressed(KeyEvent.VK_D)) {
                physicsObject.setVelocity(physicsObject.getVelocity().setX(1));
//                physicsObject.setSprite(new ImageSprite(ImageLoader.get("test/images/player/right.png")));
            }

            if (Keyboard.isPressed(KeyEvent.VK_A) && Keyboard.isPressed(KeyEvent.VK_D)) {
                physicsObject.setVelocity(physicsObject.getVelocity().setX(0));
            }

            if (Keyboard.isPressed(KeyEvent.VK_SPACE)) {
                physicsObject.setVelocity(physicsObject.getVelocity().setY(1));
            }

            if (Keyboard.isPressed(KeyEvent.VK_SHIFT)) {
                physicsObject.setVelocity(physicsObject.getVelocity().setY(-1));
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
    
            if (Keyboard.isPressed(KeyEvent.VK_LEFT)) {
                camera.setRotation(camera.getRotation() + 1);
            }
    
            if (Keyboard.isPressed(KeyEvent.VK_RIGHT)) {
                camera.setRotation(camera.getRotation() - 1);
            }
        });
    }
}
