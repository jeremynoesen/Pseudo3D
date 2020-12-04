import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import jndev.pseudo3d.Pseudo3D;
import jndev.pseudo3d.input.Keyboard;
import jndev.pseudo3d.scene.renderer.Camera;
import jndev.pseudo3d.scene.Scene;
import jndev.pseudo3d.scene.entity.Entity;
import jndev.pseudo3d.scene.entity.Sprite;
import jndev.pseudo3d.scene.util.Vector;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;

/**
 * sandbox-style testing class for testing various bits of this project
 *
 * @author JNDev (Jeremaster101)
 */
public class Sandbox {
    
    /**
     * run the sandbox test scene
     *
     * @param args program arguments
     */
    public static void main(String[] args) throws FileNotFoundException {
        Sprite playerFront = new Sprite(new Image(new FileInputStream("src/test/resources/images/player/front.png")));
        Sprite playerBack = new Sprite(new Image(new FileInputStream("src/test/resources/images/player/back.png")));
        Sprite playerLeft = new Sprite(new Image(new FileInputStream("src/test/resources/images/player/left.png")));
        Sprite playerRight = new Sprite(new Image(new FileInputStream("src/test/resources/images/player/right.png")));
        Sprite floor = new Sprite(new Image(new FileInputStream("src/test/resources/images/floor.png")));
        Sprite background = new Sprite(new Image(new FileInputStream("src/test/resources/images/background.png")));
        
        Scene scene = new Scene();
        Entity entity = new Entity();
        entity.setSprite(playerFront);
        entity.getBoundingBox().setWidth((float) entity.getSprite().getImage().getWidth());
        entity.getBoundingBox().setHeight((float) entity.getSprite().getImage().getHeight());
        entity.getBoundingBox().setDepth((float) entity.getSprite().getImage().getWidth());
        entity.setPosition(new Vector(entity.getBoundingBox().getWidth() * 10 - 450, 0,
                -entity.getBoundingBox().getWidth()));
        entity.setMass(1f);
        scene.addEntity(entity);
        
        Entity entity1 = new Entity(entity);
        entity1.setMass(4f);
        scene.addEntity(entity1);
        
        for (int j = 1; j < 22; j++) {
            for (int i = 1; i <= 6; i++) {
                Entity copy = new Entity();
                copy.setSprite(floor);
                copy.getBoundingBox().setWidth((float) copy.getSprite().getImage().getWidth());
                copy.getBoundingBox().setHeight((float) copy.getSprite().getImage().getHeight());
                copy.getBoundingBox().setDepth((float) copy.getSprite().getImage().getWidth());
                copy.setPosition(new Vector(copy.getBoundingBox().getWidth() * j - 525, -465,
                        -copy.getBoundingBox().getWidth() * i));
                copy.setKinematic(false);
                scene.addEntity(copy);
            }
        }
        
        Entity backdrop = new Entity();
        backdrop.setKinematic(false);
        backdrop.setCollidable(false);
        backdrop.setPosition(new Vector(0, 0, -300));
        backdrop.setSprite(background);
        scene.addEntity(backdrop);
        
        Camera camera = new Camera();
        camera.setFieldOfView((float) Math.toRadians(72));
        camera.setPosition(new Vector(0, 0, -100));
        camera.setSensorSize(1000);
        
        scene.setCamera(camera);
        scene.setBackground(Color.DARKGRAY);
        
        Pseudo3D.init(1000, 1000, false, "Sandbox");
        Pseudo3D.setActiveScene(scene);
        Pseudo3D.launch();
        
        scene.addRunnable(() -> {
//            entity.setVelocity(
//                    Mouse.getPosition().setY(Mouse.getPosition().multiply(-1).getY())
//                            .subtract(camera.getRenderPosition().multiply(new Vector(1, -1)))
//                            .subtract(entity.getPosition().setZ(0)).multiply(0.1f));
            
            if (Keyboard.isPressed(KeyCode.W) && camera.getFieldOfView() > 0) {
                entity.setVelocity(entity.getVelocity().setZ(-1));
                entity.setSprite(playerBack);
            }
            
            if (Keyboard.isPressed(KeyCode.S) && camera.getFieldOfView() > 0) {
                entity.setVelocity(entity.getVelocity().setZ(1));
                entity.setSprite(playerFront);
            }
            
            if ((Keyboard.isPressed(KeyCode.W) && Keyboard.isPressed(KeyCode.S)) ||
                    camera.getFieldOfView() == 0) {
                entity.setVelocity(entity.getVelocity().setZ(0));
            }
            
            if (Keyboard.isPressed(KeyCode.A)) {
                entity.setVelocity(entity.getVelocity().setX(-1));
                entity.setSprite(playerLeft);
            }
            
            if (Keyboard.isPressed(KeyCode.D)) {
                entity.setVelocity(entity.getVelocity().setX(1));
                entity.setSprite(playerRight);
            }
            
            if (Keyboard.isPressed(KeyCode.A) && Keyboard.isPressed(KeyCode.D)) {
                entity.setVelocity(entity.getVelocity().setX(0));
            }
            
            if (Keyboard.isPressed(KeyCode.SPACE)) {
                entity.setVelocity(entity.getVelocity().setY(1));
            }
            
            if (Keyboard.isPressed(KeyCode.SHIFT)) {
                entity.setVelocity(entity.getVelocity().setY(-1));
            }
            
            if (Keyboard.isPressed(KeyCode.SPACE) && Keyboard.isPressed(KeyCode.SHIFT)) {
                entity.setVelocity(entity.getVelocity().setY(0));
            }
            
            if (Keyboard.isPressed(KeyCode.UP)) {
                camera.setFieldOfView(camera.getFieldOfView() + 0.1f);
            }
            
            if (Keyboard.isPressed(KeyCode.DOWN)) {
                camera.setFieldOfView(Math.max(camera.getFieldOfView() - 0.1f, 0));
            }
            
            if (Keyboard.isPressed(KeyCode.LEFT)) {
                camera.setRotation(camera.getRotation() + 0.01f);
            }
            
            if (Keyboard.isPressed(KeyCode.RIGHT)) {
                camera.setRotation(camera.getRotation() - 0.01f);
            }
            
            if (Keyboard.isPressed(KeyCode.F)) {
                Random random = new Random();
                camera.setOffset(new Vector(random.nextInt() % 4 - 2, random.nextInt() % 4 - 2));
                camera.setRotation(((random.nextInt() % 4) - 2) * 0.001f);
            }
        });
    }
}
