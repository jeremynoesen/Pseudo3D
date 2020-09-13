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
        
        Graphics2D g2d = (Graphics2D) graphics;
        
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_SPEED);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
                RenderingHints.VALUE_COLOR_RENDER_SPEED);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
        //set rendering settings for speed
        
        graphics.setColor(scene.getBackground());
        //draw scene background color
        
        double gWidth = graphics.getClipBounds().getWidth();
        double gHeight = graphics.getClipBounds().getHeight();
        //graphics dimensions
        
        graphics.fillRect(0, 0, (int) gWidth, (int) gHeight);
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
            double camDist = camPos.getZ() - objPos.getZ();
            //object data
            
            if (camDist >= viewDistance || object.getSprite() == null) continue;
            //don't render objects without a sprite, with a camera sprite, or further than view distance
            
            double scale = Math.abs(zoom) * (sensorSize / (sensorSize + (2.0 *
                    camDist * (Math.sin(fov) / Math.sin((Math.PI / 2.0) - fov)))));
            //scale objects based on fov angle and distance from camera using law of sines and camera sensor size
            
            if (scale < 0) break;
            //stop render if objects have negative scale (too far in front of camera)
            
            Sprite sprite = object.getSprite();
            //get sprite
            
            int widthScaled = (int) Math.ceil(sprite.getWidth() * scale);
            int heightScaled = (int) Math.ceil(sprite.getHeight() * scale);
            //scale image dimensions
            
            if (widthScaled == 1 && heightScaled == 1) continue;
            //don't draw sprites that are too small
            
            double x = ((objPos.getX() - camPos.getX()) * scale) + windowPos.getX();
            double y = gHeight - (((objPos.getY() - camPos.getY()) * scale) + (gHeight - windowPos.getY()));
            //translate object coordinates
            
            double spriteRotation = Math.toRadians(sprite.getRotation());
            double cameraRotation = Math.toRadians(-camera.getRotation());
            //get rotations
            
            Box screenBox = new Box(gWidth, gHeight,
                    new Vector(gWidth / 2.0, gHeight / 2.0));
            Box spriteBox;
            //boxes to represent image and panel bounds
            
            if (cameraRotation != 0 || spriteRotation != 0) {
                //check if there is any rotation
                
                double sprRotSin = Math.sin(spriteRotation + cameraRotation);
                double sprRotCos = Math.cos(spriteRotation + cameraRotation);
                double camRotSin = Math.sin(cameraRotation);
                double camRotCos = Math.cos(cameraRotation);
                double relX = x - windowPos.getX();
                double relY = y - windowPos.getY();
                //calculations done once to reduce total calculations
                
                double heightRotated = Math.abs(widthScaled * sprRotSin) + Math.abs(heightScaled * sprRotCos);
                double widthRotated = Math.abs(widthScaled * sprRotCos) + Math.abs(heightScaled * sprRotSin);
                //get dimensions of image based on sprite rotation
                
                double yRotated = (relX * camRotSin) + (relY * camRotCos) + windowPos.getY();
                double xRotated = (relX * camRotCos) - (relY * camRotSin) + windowPos.getX();
                //get position of image based on camera rotation
                
                spriteBox = new Box(widthRotated, heightRotated,
                        new Vector(xRotated, yRotated));
                //set box data
            } else {
                spriteBox = new Box(widthScaled, heightScaled, new Vector(x, y));
                //set box data
            }
            
            if (spriteBox.overlaps(screenBox)) {
                //check if any part of image is visible in panel
                
                AffineTransform transform = new AffineTransform();
                //create new transform, resetting old one
                
                if (cameraRotation != 0)
                    transform = AffineTransform.getRotateInstance(
                            cameraRotation, windowPos.getX(), windowPos.getY());
                //rotate canvas if camera is rotated
                
                if (spriteRotation != 0) transform.rotate(spriteRotation, x, y);
                //rotate canvas about center of sprite if sprite is rotated
                
                g2d.setTransform(transform);
                graphics.drawImage(sprite.getImage(), (int) (x - (widthScaled / 2.0)),
                        (int) (y - (heightScaled / 2.0)), widthScaled, heightScaled, null);
                //draw image to panel
            }
        }
        graphics.dispose();
        g2d.dispose();
        //free up resources used by graphics processing
    }
}