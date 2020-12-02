import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import jndev.pseudo3d.application.Pseudo3D;
import jndev.pseudo3d.listener.Keyboard;
import jndev.pseudo3d.listener.Mouse;
import jndev.pseudo3d.loader.ImageLoader;
import jndev.pseudo3d.scene.Scene;
import jndev.pseudo3d.sceneobject.Camera;
import jndev.pseudo3d.sceneobject.PhysicsObject;
import jndev.pseudo3d.sprite.ImageSprite;
import jndev.pseudo3d.util.Vector;

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
        PhysicsObject physicsObject = new PhysicsObject();
        ImageSprite imageSprite = new ImageSprite(ImageLoader.get("src/test/resources/images/player/front.png"));
        physicsObject.setSprite(imageSprite);
        physicsObject.getBoundingBox().setWidth((float) physicsObject.getSprite().getImage().getWidth());
        physicsObject.getBoundingBox().setHeight((float) physicsObject.getSprite().getImage().getHeight());
        physicsObject.getBoundingBox().setDepth((float) physicsObject.getSprite().getImage().getWidth());
        physicsObject.setPosition(new Vector(physicsObject.getBoundingBox().getWidth() * 10 - 450, 0,
                -physicsObject.getBoundingBox().getWidth()));
//        physicsObject.setTerminalVelocity(new Vector(100, 100, 100));
        physicsObject.setKinematic(true);
//        physicsObject.setGravity(new Vector());
        scene.addObject(physicsObject);
        
        PhysicsObject physicsObject1 = new PhysicsObject(physicsObject);
        physicsObject1.setPushable(true);
        physicsObject.setPushable(true);
        physicsObject1.setMass(4f);
        scene.addObject(physicsObject1);
        
        PhysicsObject copy;
        for (int j = 1; j < 22; j++) {
            for (int i = 1; i <= 6; i++) {
                copy = new PhysicsObject(physicsObject);
                copy.setSprite(new ImageSprite(ImageLoader.get("src/test/resources/images/floor.png")));
                copy.getBoundingBox().setWidth((float) copy.getSprite().getImage().getWidth());
                copy.getBoundingBox().setHeight((float) copy.getSprite().getImage().getHeight());
                copy.getBoundingBox().setDepth((float) copy.getSprite().getImage().getWidth());
                copy.setPosition(new Vector(copy.getBoundingBox().getWidth() * j - 525, -465,
                        -copy.getBoundingBox().getWidth() * i));
                copy.setKinematic(false);
                scene.addObject(copy);
            }
        }
        
        Camera camera = new Camera();
        camera.setFieldOfView((float) Math.toRadians(72));
        camera.setScenePosition(new Vector(0, 0, -100));
        camera.setSensorSize(1000);
//        camera.setWindowPosition(new Vector());


//        Camera second = new Camera(camera);
//        second.setRenderPosition(new Vector(250, 250));
//        SpriteObject spriteObject = new SpriteObject(new CameraSprite(scene, second, 500, 500), new Vector(0, 0, -300));
//        scene.addObject(spriteObject);

//        ArrayList<BufferedImage> images = new ArrayList<>();
//        images.add(ImageLoader.get("test/images/player/front.png"));
//        images.add(ImageLoader.get("test/images/player/left.png"));
//        images.add(ImageLoader.get("test/images/player/right.png"));
//        AnimatedSprite as = new AnimatedSprite(images, 3);

//        physicsObject.setSprite(as);

//        SpriteObject spriteObject = new SpriteObject();
//        spriteObject.setSprite(new ImageSprite(ImageLoader.get("res/sprites/background.png")));
//        spriteObject.setSprite(new CameraSprite(scene, camera, 500, 500));
//        spriteObject.getSprite().setRotation(10);
//        spriteObject.setSprite(as);
//        spriteObject.setPosition(new Vector(0, 0, -200));
//        scene.addObject(spriteObject);
        
        scene.setCamera(camera);
        scene.setBackground(Color.DARKGRAY);

//        Pseudo3D.getStage().setWidth(1000); //todo reimplement
//        Pseudo3D.getCanvas().setWidth(1000);
//        Pseudo3D.getStage().setHeight(1000);
//        Pseudo3D.getCanvas().setHeight(1000);
//        Pseudo3D.getStage().setTitle("Sandbox");
        Pseudo3D.getGameLoop().setActiveScene(scene);
        Pseudo3D.getGameLoop().setRenderFrequency(75);
        Pseudo3D.getGameLoop().setTickFrequency(120);
        Pseudo3D.getGameLoop().start();
        Pseudo3D.launch();
        
        scene.addRunnable(() -> {
            camera.setRenderPosition(new Vector((float) Pseudo3D.getCanvas().getWidth() / 2.0f,
                    (float) Pseudo3D.getCanvas().getHeight() / 2.0f));
//            physicsObject.setVelocity(
//                    Mouse.getPosition().setY(Mouse.getPosition().multiply(-1).getY())
//                            .subtract(camera.getRenderPosition().multiply(new Vector(1, -1)))
//                            .subtract(physicsObject.getPosition().setZ(0)).multiply(0.1f));
            
            if (Keyboard.isPressed(KeyCode.W) && camera.getFieldOfView() > 0) {
                physicsObject.setVelocity(physicsObject.getVelocity().setZ(-1));
                physicsObject.setSprite(new ImageSprite(ImageLoader.get("src/test/resources/images/player/back.png")));
            }
            
            if (Keyboard.isPressed(KeyCode.S) && camera.getFieldOfView() > 0) {
                physicsObject.setVelocity(physicsObject.getVelocity().setZ(1));
                physicsObject.setSprite(new ImageSprite(ImageLoader.get("src/test/resources/images/player/front.png")));
            }
            
            if ((Keyboard.isPressed(KeyCode.W) && Keyboard.isPressed(KeyCode.S)) ||
                    camera.getFieldOfView() == 0) {
                physicsObject.setVelocity(physicsObject.getVelocity().setZ(0));
            }
            
            if (Keyboard.isPressed(KeyCode.A)) {
                physicsObject.setVelocity(physicsObject.getVelocity().setX(-1));
                physicsObject.setSprite(new ImageSprite(ImageLoader.get("src/test/resources/images/player/left.png")));
            }
            
            if (Keyboard.isPressed(KeyCode.D)) {
                physicsObject.setVelocity(physicsObject.getVelocity().setX(1));
                physicsObject.setSprite(new ImageSprite(ImageLoader.get("src/test/resources/images/player/right.png")));
            }
            
            if (Keyboard.isPressed(KeyCode.A) && Keyboard.isPressed(KeyCode.D)) {
                physicsObject.setVelocity(physicsObject.getVelocity().setX(0));
            }
            
            if (Keyboard.isPressed(KeyCode.SPACE)) {
                physicsObject.setVelocity(physicsObject.getVelocity().setY(1));
            }
            
            if (Keyboard.isPressed(KeyCode.SHIFT)) {
                physicsObject.setVelocity(physicsObject.getVelocity().setY(-1));
            }
            
            if (Keyboard.isPressed(KeyCode.SPACE) && Keyboard.isPressed(KeyCode.SHIFT)) {
                physicsObject.setVelocity(physicsObject.getVelocity().setY(0));
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
            
            if (Keyboard.isPressed(KeyCode.LEFT)) {
                camera.setRotation(camera.getRotation() - 0.01f);
            }
        });
    }
}
