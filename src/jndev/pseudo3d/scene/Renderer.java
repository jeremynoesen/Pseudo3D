package jndev.pseudo3d.scene;

import jndev.pseudo3d.object.Object;

import javax.swing.*;
import java.awt.*;

/**
 * scene renderer, will turn a scene into a jpanel with graphics
 */
public class Renderer {
    
    /**
     * render a scene to a new jpanel
     *
     * @param scene scene to render
     * @return new jpanel with rendered scene
     */
    public static JPanel render(Scene scene) {
        
        JPanel panel = new JPanel();
        
        render(scene, panel);
        
        return panel;
        
    }
    
    /**
     * render a scene to an existing jpanel
     *
     * @param scene scene to render
     * @param panel jpanel to render to
     */
    public static void render(Scene scene, JPanel panel) {
        
        Graphics graphics = panel.getGraphics();
        panel.repaint();
        
        for (Object object : scene.getObjects()) {
            
            if (object.getPosition().getZ() <= scene.getCamera().getPosition().getZ())
                break; //prevent rendering objects behind camera
            
            Image image = object.getSprite();
            
            double oppositeSide = (object.getPosition().getZ() - scene.getCamera().getPosition().getZ()) *
                    (Math.sin(Math.toRadians(scene.getCamera().getFieldOfView())) / Math.sin(Math.toRadians(90 - scene.getCamera().getFieldOfView())));
            double scale = (2 * oppositeSide) / scene.getCamera().getSensorSize();
            
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
