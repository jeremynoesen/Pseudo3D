package jndev.pseudo3d.renderer;

import jndev.pseudo3d.object.Object;
import jndev.pseudo3d.scene.Scene;
import jndev.pseudo3d.util.Vector;

import javax.swing.*;
import java.awt.*;

/**
 * simple scene renderer, will turn a scene into a jpanel with graphics. depth scaling is uniform based on z distance
 * from camera
 *
 * @author JNDev (Jeremaster101)
 */
public class Renderer {
    
    /**
     * render a scene to an existing jpanel
     *
     * @param scene scene to render
     * @param panel jpanel to render to
     */
    public static void render(Scene scene, JPanel panel, Graphics graphics) {
        if (scene.getBackground() == null) panel.setBackground(Color.WHITE);
        else panel.setBackground(scene.getBackground());
        //draw scene background color
        
        scene.getObjects().sort((o1, o2) -> (int) (o1.getPosition().getZ() - o2.getPosition().getZ()));
        //sort objects by z position so objects can be drawn in front of others
        
        for (Object object : scene.getObjects()) {
            
            Camera camera = scene.getCamera();
            Vector objPos = object.getPosition();
            Vector camPos = camera.getPosition();
            double fov = camera.getFieldOfView() / 2.0;
            
            if (objPos.getZ() >= camPos.getZ())
                break; //prevent rendering objects behind camera
            
            if (object.getSprite() == null || camPos.getZ() - objPos.getZ() > camera.getViewDistance())
                continue; //don't render objects with no sprite or further than view distance
            
            double scale = camera.getSensorSize() / (2 * (camPos.getZ() - objPos.getZ()) *
                    (Math.sin(Math.toRadians(fov)) / Math.sin(Math.toRadians(90 - fov))));
            //scale objects based on fov angle and distance from camera
            
            if (Double.compare(scale, 0) == 0)
                continue; //don't render objects that are too small
            
            Image image = object.getSprite();
            int widthScaled = (int) (image.getWidth(null) * scale);
            int heightScaled = (int) (image.getHeight(null) * scale);
            double x = ((objPos.getX() - camPos.getX()) * scale) + (panel.getWidth() / 2.0);
            double y = ((objPos.getY() - camPos.getY()) * scale) + (panel.getHeight() / 2.0);
            //scale image dimensions and coordinates
            
            int minX = (int) (x - (widthScaled / 2.0));
            int maxX = (int) (x + (widthScaled / 2.0));
            int minY = (int) ((panel.getHeight() - y) - (heightScaled / 2.0));
            int maxY = (int) ((panel.getHeight() - y) + (heightScaled / 2.0));
            //image bounds after scaling
            
            if (((minX >= 0 && minX <= panel.getWidth()) || (maxX >= 0 && maxX <= panel.getWidth())) &&
                    ((minY >= 0 && minY <= panel.getHeight()) || (maxY >= 0 && maxY <= panel.getHeight()))) {
                //check if image is within panel boundary
                
                graphics.drawImage(image, minX, minY, widthScaled, heightScaled, panel);
                //draw image to panel
            }
        }
    }
}