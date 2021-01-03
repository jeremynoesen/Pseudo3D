import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import jeremynoesen.pseudo3d.Pseudo3D;
import jeremynoesen.pseudo3d.input.Keyboard;
import jeremynoesen.pseudo3d.scene.Scene;
import jeremynoesen.pseudo3d.scene.entity.Entity;
import jeremynoesen.pseudo3d.scene.entity.Sprite;
import jeremynoesen.pseudo3d.scene.renderer.Camera;
import jeremynoesen.pseudo3d.scene.util.Vector;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;

/**
 * sandbox-style testing class for testing various bits of this project
 *
 * @author Jeremy Noesen
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
        
        Entity player = new Entity();
        player.setSprite(playerFront);
        player.setDimensions(playerFront.getWidth(), playerFront.getHeight(), playerFront.getWidth());
        player.setPosition(new Vector(0, 0, -150));
        player.setMass(1f);
        scene.addEntity(player);
        
        Entity dummy = new Entity(player);
        dummy.setMass(4f);
        scene.addEntity(dummy);
        
        for (int j = 1; j < 18; j++) {
            for (int i = 1; i <= 5; i++) {
                Entity block = new Entity();
                block.setSprite(floor);
                block.setDimensions(floor.getWidth(), floor.getHeight(), floor.getWidth());
                block.setPosition(new Vector(block.getWidth() * j - 425, -226, -block.getWidth() * i - 52));
                block.setKinematic(false);
                scene.addEntity(block);
            }
        }
        
        Entity backdrop = new Entity();
        backdrop.setDimensions(1000, 1000, 0);
        backdrop.setKinematic(false);
        backdrop.setPosition(new Vector(0, 0, -300));
        background.setDimensions(1000, 1000);
        backdrop.setSprite(background);
        scene.addEntity(backdrop);
        
        Camera camera = new Camera();
        camera.setFieldOfView((float) Math.toRadians(40));
        camera.setPosition(new Vector(0, 0, -100));
        camera.setSensorSize(500);
        
        scene.setCamera(camera);
        scene.setBackground(Color.DARKGRAY);
        
        Pseudo3D.init(500, 500, 60, 120, true, "Sandbox");
        Pseudo3D.setActiveScene(scene);
        Pseudo3D.launch();
        
        scene.addRunnable(() -> {
            
            if (Keyboard.isPressed(KeyCode.W) && camera.getFieldOfView() > 0) {
                player.setVelocity(player.getVelocity().setZ(-1));
                player.setSprite(playerBack);
            }
            
            if (Keyboard.isPressed(KeyCode.S) && camera.getFieldOfView() > 0) {
                player.setVelocity(player.getVelocity().setZ(1));
                player.setSprite(playerFront);
            }
            
            if ((Keyboard.isPressed(KeyCode.W) && Keyboard.isPressed(KeyCode.S)) ||
                    camera.getFieldOfView() == 0) {
                player.setVelocity(player.getVelocity().setZ(0));
            }
            
            if (Keyboard.isPressed(KeyCode.A)) {
                player.setVelocity(player.getVelocity().setX(-1));
                player.setSprite(playerLeft);
            }
            
            if (Keyboard.isPressed(KeyCode.D)) {
                player.setVelocity(player.getVelocity().setX(1));
                player.setSprite(playerRight);
            }
            
            if (Keyboard.isPressed(KeyCode.A) && Keyboard.isPressed(KeyCode.D)) {
                player.setVelocity(player.getVelocity().setX(0));
            }
            
            if (Keyboard.isPressed(KeyCode.SPACE)) {
                player.setVelocity(player.getVelocity().setY(1));
            }
            
            if (Keyboard.isPressed(KeyCode.SHIFT)) {
                player.setVelocity(player.getVelocity().setY(-1));
            }
            
            if (Keyboard.isPressed(KeyCode.SPACE) && Keyboard.isPressed(KeyCode.SHIFT)) {
                player.setVelocity(player.getVelocity().setY(0));
            }
            
            if (Keyboard.isPressed(KeyCode.UP)) {
                camera.setFieldOfView(camera.getFieldOfView() + 0.01f);
            }
            
            if (Keyboard.isPressed(KeyCode.DOWN)) {
                camera.setFieldOfView(Math.max(camera.getFieldOfView() - 0.01f, 0));
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
            
            if (Keyboard.isPressed(KeyCode.R)) {
                camera.setOffset(new Vector());
                camera.setRotation(0);
                player.setPosition(new Vector(0, 0, -150));
                dummy.setPosition(new Vector(0, 0, -150));
                camera.setFieldOfView((float) Math.toRadians(40));
            }
        });
    }
}
