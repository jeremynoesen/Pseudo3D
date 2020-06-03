package jndev.pseudo3d.renderer;

import jndev.pseudo3d.object.Object;
import jndev.pseudo3d.scene.Scene;
import jndev.pseudo3d.util.Vector;

import javax.swing.*;
import java.awt.*;

/**
 * simple scene renderer, will turn a scene into a jpanel with graphics. depth scaling is uniform based on z distance
 * from camera
 */
public class SimpleRenderer {
    
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
        
        if (scene.getBackground() == null) graphics.setColor(Color.WHITE);
        else graphics.setColor(scene.getBackground());
        
        for (Object object : scene.getObjects()) {
            
            Camera camera = scene.getCamera();
            Vector objPos = object.getPosition();
            Vector camPos = camera.getPosition();
            
            if (objPos.getZ() >= camPos.getZ())
                break; //prevent rendering objects behind camera
            
            double fov = camera.getFieldOfView();
            double size = camera.getSensorSize();
            double viewDist = camera.getViewDistance();
            
            double oppositeSide = (objPos.getZ() - camPos.getZ()) *
                    (Math.sin(Math.toRadians(fov)) / Math.sin(Math.toRadians(90 - fov)));
            double scale = (2 * oppositeSide) / size;
            
            if (Double.compare(scale, 0) == 0 || object.getSprite() == null || objPos.getZ() < viewDist)
                continue; //don't render objects that are too small, without a sprite, or are past the view distance
            
            Image image = object.getSprite();
            int imgWidth = image.getWidth(null);
            int imgHeight = image.getHeight(null);
            
            Image scaledImage = image.getScaledInstance((int) (imgWidth * scale),
                    (int) (imgHeight * scale), Image.SCALE_FAST);
            int imgWidthScaled = scaledImage.getWidth(null);
            int imgHeightScaled = scaledImage.getHeight(null);
            
            double x = ((objPos.getX() - camPos.getX()) * scale) + (panel.getWidth() / 2.0);
            double y = ((objPos.getY() - camPos.getY()) * scale) + (panel.getHeight() / 2.0);
            
            graphics.drawImage(scaledImage, (int) (x - (imgWidthScaled / 2.0)),
                    (int) ((panel.getHeight() - y) - (imgHeightScaled / 2.0)), null);
            
        }
        
        Toolkit.getDefaultToolkit().sync();
        
    }
}
