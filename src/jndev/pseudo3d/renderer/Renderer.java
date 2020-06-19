package jndev.pseudo3d.renderer;

import jndev.pseudo3d.object.Object;
import jndev.pseudo3d.scene.Scene;
import jndev.pseudo3d.util.Box;
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
        
        for (int i = 0; i < scene.getObjects().size(); i++) {
            Object object = scene.getObjects().get(i);
            Camera camera = scene.getCamera();
            Vector objPos = object.getPosition();
            Vector camPos = camera.getPosition();
            double fov = Math.toRadians(camera.getFieldOfView() / 2.0);
            
            if (object.getSprite() == null || camPos.getZ() - objPos.getZ() >= camera.getViewDistance())
                continue; //don't render objects with no sprite or further than view distance
            
            double scale = camera.getSensorSize() / (camera.getSensorSize() + (2 * (camPos.getZ() - objPos.getZ()) *
                    (Math.sin(fov) / Math.sin((Math.PI / 2.0) - fov))));
            //scale objects based on fov angle and distance from camera
            
            if (scale <= 0)
                continue; //don't render objects that are too small
            
            Image image = object.getSprite();
            int widthScaled = (int) (image.getWidth(null) * scale);
            int heightScaled = (int) (image.getHeight(null) * scale);
            double x = ((objPos.getX() - camPos.getX()) * scale) + (panel.getWidth() / 2.0);
            double y = ((objPos.getY() - camPos.getY()) * scale) + (panel.getHeight() / 2.0);
            //scale image dimensions and coordinates
            
            Box screen = new Box(panel.getWidth(), panel.getHeight(), 0,
                    new Vector(panel.getWidth() / 2.0, panel.getHeight() / 2.0, 0));
            Box sprite = new Box(widthScaled, heightScaled, 0,
                    new Vector(x, y, 0));
            //boxes to represent image and panel bounds
            
            if (sprite.overlaps(screen)) {
                //check if any part of image is within panel bounds
                
                graphics.drawImage(image, (int) (x - (widthScaled / 2.0)),
                        (int) ((panel.getHeight() - y) - (heightScaled / 2.0)),
                        widthScaled, heightScaled, panel);
                //draw image to panel
            }
        }
    }
}