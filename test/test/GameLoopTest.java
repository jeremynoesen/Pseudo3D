package test;

import jndev.pseudo3d.application.Application;
import jndev.pseudo3d.loader.Sprites;
import jndev.pseudo3d.object.Object;
import jndev.pseudo3d.renderer.Camera;
import jndev.pseudo3d.scene.Scene;
import jndev.pseudo3d.util.Vector;

import java.awt.*;
import java.io.File;

public class GameLoopTest {
    
    public static void main(String[] args) {
        Sprites.load(new File("res/sprites/"));
        Scene scene = new Scene();
        Object object = new Object();
        object.setSprite(Sprites.get("front"));
        object.setGravity(1);
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
        
        Application.getGameLoop().addRunnable(() -> {
            System.out.println("test");
        });
    }
}
