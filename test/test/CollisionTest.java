package test;

import jndev.pseudo3d.listener.Keyboard;
import jndev.pseudo3d.loader.Sprites;
import jndev.pseudo3d.object.Object;
import jndev.pseudo3d.renderer.Camera;
import jndev.pseudo3d.renderer.SimpleRenderer;
import jndev.pseudo3d.scene.Scene;
import jndev.pseudo3d.util.Vector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class CollisionTest extends JPanel implements ActionListener {
    
    private Scene scene;
    private Object object;
    private Object copy;
    
    public CollisionTest() {
        scene = new Scene();
        object = new Object();
        object.setSprite(Sprites.get("front"));
        object.setGravity(1);
        object.setPosition(new Vector(0, 100, -100));
        object.setWidth(object.getSprite().getWidth(null));
        object.setHeight(object.getSprite().getHeight(null));
        object.setDepth(10);
        scene.addObject(object);
        
        copy = new Object(object);
        copy.setPosition(new Vector(0, -150, -100));
        copy.setGravity(0);
        scene.addObject(copy);
        
        Object copy2 = new Object(copy);
        copy2.setPosition(new Vector(50, -100, -100));
        scene.addObject(copy2);
        
        
        Camera camera = new Camera();
        camera.setFieldOfView(70);
        
        scene.setCamera(camera);
        scene.setBackground(Color.BLACK);
        
        setSize(new Dimension(1200, 1200));
        
        Timer timer = new Timer(10, this);
        timer.start();
        
        setVisible(true);
        requestFocus();
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponents(g);
        SimpleRenderer.render(scene, this, g);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
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

//        if (Keyboard.isPressed(KeyEvent.VK_UP)) {
//            object.setVelocity(object.getVelocity().setY(1));
//        }
//
//        if (Keyboard.isPressed(KeyEvent.VK_DOWN)) {
//            object.setVelocity(object.getVelocity().setY(-1));
//        }
//
//        if(!Keyboard.isPressed(KeyEvent.VK_UP) && !Keyboard.isPressed(KeyEvent.VK_DOWN)) {
//            object.setVelocity(object.getVelocity().setY(0));
//        }
    
        //copy.setVelocity(new Vector(0.0981, 0, 0));
    
    
        scene.tick();
    }
    
}
