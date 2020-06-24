package test;

import jndev.pseudo3d.game.Game;
import jndev.pseudo3d.listener.Keyboard;
import jndev.pseudo3d.loader.Sprites;
import jndev.pseudo3d.object.Object;
import jndev.pseudo3d.renderer.Camera;
import jndev.pseudo3d.scene.Scene;
import jndev.pseudo3d.util.Vector;

import java.awt.event.KeyEvent;
import java.io.File;

public class Testing {
    
    public static void main(String[] args) {
        Sprites.load(new File("res/sprites/"));
        Scene scene = new Scene();
        Object object = new Object();
        object.setSprite(Sprites.get("front"));
        object.setGravity(0);
        object.setPosition(new Vector(0, 0, 0));
        object.setWidth(10);
        object.setHeight(20);
        object.setDepth(10);
        scene.addObject(object);
        
        for(int j = 1; j <= 20; j++) {
            for (int i = 1; i <= 10; i++) {
                Object copy = new Object(object);
                copy.setPosition(new Vector(40 * j - 400, -400, -20 * i));
                copy.setGravity(0);
                scene.addObject(copy);
            }
        }
        
        Camera camera = new Camera();
        camera.setFieldOfView(0);
        camera.setSensorSize(1000);
        camera.setPosition(new Vector(0, 0, -100));
        
        scene.setCamera(camera);
        //scene.setBackground(Color.BLACK);
        
        Game.launch(1000, 1000);
        Game.getLoop().setActiveScene(scene);
        Game.getLoop().start();
        
        Game.getLoop().inject(() -> {
            if (Game.getLoop().getActiveScene().equals(scene)) {
                if (Keyboard.isPressed(KeyEvent.VK_W)) {
                    object.setVelocity(object.getVelocity().setZ(-1));
                    object.setSprite(Sprites.get("front"));
                    object.setWidth(object.getSprite().getWidth(null));
                }
                
                if (Keyboard.isPressed(KeyEvent.VK_S)) {
                    object.setVelocity(object.getVelocity().setZ(1));
                    object.setSprite(Sprites.get("front"));
                    object.setWidth(object.getSprite().getWidth(null));
                }
                
                if (!Keyboard.isPressed(KeyEvent.VK_W) && !Keyboard.isPressed(KeyEvent.VK_S)) {
                    object.setVelocity(object.getVelocity().setZ(0));
                }
                
                if (Keyboard.isPressed(KeyEvent.VK_A)) {
                    object.setVelocity(object.getVelocity().setX(-1));
                    object.setSprite(Sprites.get("left"));
                    object.setWidth(object.getSprite().getWidth(null));
                }
                
                if (Keyboard.isPressed(KeyEvent.VK_D)) {
                    object.setVelocity(object.getVelocity().setX(1));
                    object.setSprite(Sprites.get("right"));
                    object.setWidth(object.getSprite().getWidth(null));
                }
                
                if (!Keyboard.isPressed(KeyEvent.VK_A) && !Keyboard.isPressed(KeyEvent.VK_D)) {
                    object.setVelocity(object.getVelocity().setX(0));
                }

                if (Keyboard.isPressed(KeyEvent.VK_SPACE)) {
                    object.setVelocity(object.getVelocity().setY(1));
                }

                if (Keyboard.isPressed(KeyEvent.VK_SHIFT)) {
                    object.setVelocity(object.getVelocity().setY(-1));
                }

                if (!Keyboard.isPressed(KeyEvent.VK_SPACE) && !Keyboard.isPressed(KeyEvent.VK_SHIFT)) {
                    object.setVelocity(object.getVelocity().setY(0));
                }
                
                if (Keyboard.isPressed(KeyEvent.VK_UP)) {
                    camera.setFieldOfView(Math.min(camera.getFieldOfView() + 1, 178.0));
                }
                
                if (Keyboard.isPressed(KeyEvent.VK_DOWN)) {
                    camera.setFieldOfView(Math.max(camera.getFieldOfView() - 1, 0));
                }
                
                System.out.println(object.getPosition());
            }
        });
    }
}
