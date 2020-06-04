package jndev.pseudo3d.renderer;

import jndev.pseudo3d.loader.Sprites;
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
     * render a scene to an existing jpanel
     *
     * @param scene scene to render
     * @param panel jpanel to render to
     */
    public static void render(Scene scene, JPanel panel, Graphics graphics) {
        
        panel.repaint();
        
        if (scene.getBackground() == null) graphics.setColor(Color.WHITE);
        else graphics.setColor(scene.getBackground());
        //draw scene background color
        
        for (Object object : scene.getObjects()) {
            
            Camera camera = scene.getCamera();
            Vector objPos = object.getPosition();
            Vector camPos = camera.getPosition();
            double fov = camera.getFieldOfView() / 2.0;
            double size = camera.getSensorSize();
            double viewDist = camera.getViewDistance();
            
            if (objPos.getZ() >= camPos.getZ())
                break; //prevent rendering objects behind camera
            
            if (object.getSprite() == null || camPos.getZ() - objPos.getZ() > viewDist)
                continue; //don't render objects with no sprite or further than view distance
            
            double scale = (2 * (camPos.getZ() - objPos.getZ()) *
                    (Math.sin(Math.toRadians(90 - fov)) / Math.sin(Math.toRadians(fov)))) / size;
            //scale objects based on fov angle and distance from camera
            
            if (Double.compare(scale, 0) == 0)
                continue; //don't render objects that are too small
            
            Image image = object.getSprite();
            int widthScaled = (int) (image.getWidth(null) * scale);
            int heightScaled = (int) (image.getHeight(null) * scale);
            double x = ((objPos.getX() - camPos.getX()) * scale) + (panel.getWidth() / 2.0);
            double y = ((objPos.getY() - camPos.getY()) * scale) + (panel.getHeight() / 2.0);
            //scale image dimensions and coordinates
            
            graphics.drawImage(image, (int) (x - (widthScaled / 2.0)),
                    (int) ((panel.getHeight() - y) - (heightScaled / 2.0)),
                    widthScaled, heightScaled, panel);
            //draw image to panel
            
        }
        
        Toolkit.getDefaultToolkit().sync();
        
    }
}
