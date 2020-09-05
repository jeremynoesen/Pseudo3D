package jndev.pseudo3d.scene;

import jndev.pseudo3d.objects.Camera;
import jndev.pseudo3d.objects.Renderable;
import jndev.pseudo3d.sprites.Sprite;
import jndev.pseudo3d.utils.Box;
import jndev.pseudo3d.utils.Vector;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * simple scene renderer, will turn a scene into a jpanel with graphics. depth scaling is uniform based on z distance
 * from camera
 *
 * @author JNDev (Jeremaster101)
 */
public class Renderer {
    
    /**
     * render a scene frame to graphics
     *
     * @param scene    scene to render
     * @param camera   camera to render with
     * @param graphics graphics to render to
     */
    public static void render(Scene scene, Camera camera, Graphics graphics) {
        if (scene == null || camera == null || graphics == null) return;
        //don't attempt rendering if one of the parameters is null
        
        graphics.setColor(scene.getBackground());
        //draw scene background color
        
        double gWidth = graphics.getClipBounds().getWidth();
        double gHeight = graphics.getClipBounds().getHeight();
        //graphics dimensions
        
        graphics.fillRect(0, 0, (int) Math.ceil(gWidth), (int) Math.ceil(gHeight));
        //clear out last drawn frame
        
        scene.getObjects().sort((o1, o2) -> (int) (o1.getPosition().getZ() - o2.getPosition().getZ()));
        //sort objects by z position so objects can be drawn in front of others
        
        Vector camPos = camera.getScenePosition();
        Vector windowPos = camera.getWindowPosition() != null ?
                camera.getWindowPosition() : new Vector(gWidth / 2.0, gHeight / 2.0);
        double fov = Math.toRadians(camera.getFieldOfView() / 2.0);
        double sensorSize = camera.getSensorSize();
        double zoom = camera.getZoom();
        double viewDistance = camera.getViewDistance();
        //camera data
        
        for (int i = 0; i < scene.getObjects().size(); i++) {
            Renderable object = scene.getObjects().get(i);
            Vector objPos = object.getPosition();
            Sprite sprite = object.getSprite();
            //object data
            
            if (object.getSprite() == null || camPos.getZ() - objPos.getZ() >= viewDistance) continue;
            //don't render objects without a sprite, with a camera sprite, or further than view distance
            
            double scale = Math.abs(zoom) * (sensorSize / (sensorSize + (2.0 *
                    (camPos.getZ() - objPos.getZ()) * (Math.sin(fov) / Math.sin((Math.PI / 2.0) - fov)))));
            //scale objects based on fov angle and distance from camera using law of sines and camera sensor size
            
            if (Double.compare(scale, 0) == 0) continue;
                //do not render objects that are too small
            else if (Double.compare(scale, 0) < 0) break;
            //stop render if objects have negative scale (too far in front of camera)
            
            int widthScaled = (int) Math.ceil(sprite.getWidth() * scale);
            int heightScaled = (int) Math.ceil(sprite.getHeight() * scale);
            double x = ((objPos.getX() - camPos.getX()) * scale) + windowPos.getX();
            double y = ((objPos.getY() - camPos.getY()) * scale) + (gHeight - windowPos.getY());
            //scale image dimensions and coordinates
            
            Box screenBox = new Box(gWidth, gHeight, new Vector(gWidth / 2.0, gHeight / 2.0));
            Box spriteBox = new Box(widthScaled, heightScaled, new Vector(x, y));
            //boxes to represent image and panel bounds
            
            if (spriteBox.overlaps(screenBox)) {
                //check if any part of image is visible in panel
                
                if(Double.compare(sprite.getRotation(), 0) == 0) {
                    //check if image is not rotated
                    
                    graphics.drawImage(sprite.getImage(), (int) (x - (widthScaled / 2.0)),
                            (int) ((gHeight - y) - (heightScaled / 2.0)),
                            widthScaled, heightScaled, null);
                    //draw image to panel
                    
                } else {
                    Graphics2D graphics2D = (Graphics2D) graphics;
                    AffineTransform rotated = AffineTransform.getRotateInstance(Math.toRadians(sprite.getRotation()), x, y);
                    AffineTransform original = graphics2D.getTransform();
                    //get transforms
    
                    graphics2D.setTransform(rotated);
                    graphics.drawImage(sprite.getImage(), (int) (x - (widthScaled / 2.0)),
                            (int) ((gHeight - y) - (heightScaled / 2.0)),
                            widthScaled, heightScaled, null);
                    graphics2D.setTransform(original);
                    //draw rotated image to panel
                }
            }
        }
        graphics.dispose();
        //free up resources used by graphics processing
    }
}