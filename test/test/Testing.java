package test;

import jndev.pseudo3d.application.Application;
import jndev.pseudo3d.listener.Keyboard;
import jndev.pseudo3d.loader.Sprites;
import jndev.pseudo3d.object.Object;
import jndev.pseudo3d.renderer.Camera;
import jndev.pseudo3d.scene.Scene;
import jndev.pseudo3d.util.Vector;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;

public class Testing {
    
    public static void main(String[] args) {
        Sprites.load(new File("res/sprites/"));
        Scene scene = new Scene();
        Object object = new Object();
        object.setSprite(Sprites.get("front"));
        object.setGravity(0);
        object.setPosition(new Vector(0, 100, -100));
        object.setWidth(10);
        object.setHeight(20);
        object.setDepth(10);
        scene.addObject(object);
    
        Object copy = new Object(object);
        copy.setPosition(new Vector(0, -150, -100));
        copy.setGravity(0);
        scene.addObject(copy);
    
        Camera camera = new Camera();
        camera.setFieldOfView(70);
    
        scene.setCamera(camera);
        scene.setBackground(Color.BLACK);
    
        Application.launch(1000, 1000);
        Application.getGameLoop().setActiveScene(scene);
        Application.getGameLoop().start();
        
        Application.getGameLoop().inject(() -> {
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
    
            if(!Keyboard.isPressed(KeyEvent.VK_W) && !Keyboard.isPressed(KeyEvent.VK_S)) {
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
    
            if(!Keyboard.isPressed(KeyEvent.VK_A) && !Keyboard.isPressed(KeyEvent.VK_D)) {
                object.setVelocity(object.getVelocity().setX(0));
            }
    
            if (Keyboard.isPressed(KeyEvent.VK_UP)) {
                object.setVelocity(object.getVelocity().setY(1));
            }
    
            if (Keyboard.isPressed(KeyEvent.VK_DOWN)) {
                object.setVelocity(object.getVelocity().setY(-1));
            }
    
            if(!Keyboard.isPressed(KeyEvent.VK_UP) && !Keyboard.isPressed(KeyEvent.VK_DOWN)) {
                object.setVelocity(object.getVelocity().setY(0));
            }
        });
    }
}
