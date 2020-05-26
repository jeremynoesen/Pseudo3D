package jndev.pseudo3d.scene;

import jndev.pseudo3d.object.Object;

import javax.swing.*;
import java.awt.*;

public class Renderer {
    
    public static JPanel render(Scene scene) {
        
        JPanel panel = new JPanel();
        
        render(scene, panel);
        
        return panel;
        
    }
    
    public static void render(Scene scene, JPanel panel) {
        
        Graphics graphics = panel.getGraphics();
        panel.repaint();
        
        for (Object object : scene.getObjects()) {
    
            if(object.getPosition().getZ() <= scene.getCamera().getPosition().getZ()) break; //prevent rendering objects behind camera
            
            Image image = object.getSprite();
    
            double scale = 0; //todo get scale (get window size over opposite side length of triangle times 2)
            
            if (Double.compare(scale, 0) == 0) continue; //don't render objects that are too small
            
            Image scaledImage = image.getScaledInstance((int) (image.getWidth(null) * scale),
                    (int) (image.getHeight(null) * scale), Image.SCALE_FAST);
            
            double x = 0; //todo get window x value
            double y = 0; //todo get window y value
            graphics.drawImage(scaledImage, (int) x, (int) y, null);
            
        }
        
        Toolkit.getDefaultToolkit().sync();
        
    }
}
