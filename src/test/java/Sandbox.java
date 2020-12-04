import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import jndev.pseudo3d.Pseudo3D;
import jndev.pseudo3d.input.Keyboard;
import jndev.pseudo3d.loader.ImageLoader;
import jndev.pseudo3d.scene.Scene;
import jndev.pseudo3d.scene.Camera;
import jndev.pseudo3d.scene.entity.Entity;
import jndev.pseudo3d.scene.entity.Sprite;
import jndev.pseudo3d.scene.util.Vector;

import java.io.File;

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
        ImageLoader.load(new File("src/test/resources/images/"));
        Scene scene = new Scene();
        Entity entity = new Entity();
        Sprite imageSprite = new Sprite(ImageLoader.get("src/test/resources/images/player/front.png"));
        entity.setSprite(imageSprite);
        entity.getBoundingBox().setWidth((float) entity.getSprite().getImage().getWidth());
        entity.getBoundingBox().setHeight((float) entity.getSprite().getImage().getHeight());
        entity.getBoundingBox().setDepth((float) entity.getSprite().getImage().getWidth());
        entity.setPosition(new Vector(entity.getBoundingBox().getWidth() * 10 - 450, 0,
                -entity.getBoundingBox().getWidth()));
        entity.setKinematic(true);
        scene.addEntity(entity);
        
        Entity entity1 = new Entity(entity);
        entity1.setPushable(true);
        entity.setPushable(true);
        entity1.setMass(4f);
        scene.addEntity(entity1);
        
        Entity copy;
        for (int j = 1; j < 22; j++) {
            for (int i = 1; i <= 6; i++) {
                copy = new Entity(entity);
                copy.setSprite(new Sprite(ImageLoader.get("src/test/resources/images/floor.png")));
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
        backdrop.setPosition(new Vector(0,0, -300));
        backdrop.setSprite(new Sprite(ImageLoader.get("src/test/resources/images/background.png")));
        scene.addEntity(backdrop);
        
        Camera camera = new Camera();
        camera.setFieldOfView((float) Math.toRadians(72));
        camera.setScenePosition(new Vector(0, 0, -100));
        camera.setSensorSize(1000);
        
        scene.setCamera(camera);
        scene.setBackground(Color.DARKGRAY);
        
        Pseudo3D.init(1000, 1000, false, "Sandbox");
        Pseudo3D.setActiveScene(scene);
        Pseudo3D.launch();
        
        scene.addRunnable(() -> {
            camera.setRenderPosition(new Vector((float) Pseudo3D.getCanvas().getWidth() / 2.0f,
                    (float) Pseudo3D.getCanvas().getHeight() / 2.0f));
//            physicsObject.setVelocity(
//                    Mouse.getPosition().setY(Mouse.getPosition().multiply(-1).getY())
//                            .subtract(camera.getRenderPosition().multiply(new Vector(1, -1)))
//                            .subtract(physicsObject.getPosition().setZ(0)).multiply(0.1f));
            
            if (Keyboard.isPressed(KeyCode.W) && camera.getFieldOfView() > 0) {
                entity.setVelocity(entity.getVelocity().setZ(-1));
                entity.setSprite(new Sprite(ImageLoader.get("src/test/resources/images/player/back.png")));
            }
            
            if (Keyboard.isPressed(KeyCode.S) && camera.getFieldOfView() > 0) {
                entity.setVelocity(entity.getVelocity().setZ(1));
                entity.setSprite(new Sprite(ImageLoader.get("src/test/resources/images/player/front.png")));
            }
            
            if ((Keyboard.isPressed(KeyCode.W) && Keyboard.isPressed(KeyCode.S)) ||
                    camera.getFieldOfView() == 0) {
                entity.setVelocity(entity.getVelocity().setZ(0));
            }
            
            if (Keyboard.isPressed(KeyCode.A)) {
                entity.setVelocity(entity.getVelocity().setX(-1));
                entity.setSprite(new Sprite(ImageLoader.get("src/test/resources/images/player/left.png")));
            }
            
            if (Keyboard.isPressed(KeyCode.D)) {
                entity.setVelocity(entity.getVelocity().setX(1));
                entity.setSprite(new Sprite(ImageLoader.get("src/test/resources/images/player/right.png")));
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
        });
    }
}
