package jndev.pseudo3d.scene;

import jndev.pseudo3d.sceneobject.Camera;
import jndev.pseudo3d.sceneobject.Renderable;
import jndev.pseudo3d.sprite.Sprite;
import jndev.pseudo3d.util.Box;
import jndev.pseudo3d.util.FastMath;
import jndev.pseudo3d.util.Vector;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Comparator;

/**
 * simple scene renderer, will turn a scene into a jpanel with graphics. depth scaling is uniform based on z distance
 * from camera
 *
 * @author JNDev (Jeremaster101)
 */
public class SceneRenderer {
    
    /**
     * comparator used to sort scene objects from lowest to highest z position. this allows the renderer to draw objects
     * in front of others if their position is as such
     */
    private static final Comparator<Renderable> zComparator = (o1, o2) -> {
        float diff = o1.getPosition().getZ() - o2.getPosition().getZ();
        return FastMath.round(diff / (diff == 0 ? 1 : Math.abs(diff)));
    };
    
    /**
     * render a scene frame to graphics
     *
     * @param scene    scene to render
     * @param camera   camera to render with
     * @param graphics graphics to render to
     */
    public static void render(Scene scene, Camera camera, Graphics graphics) {
        Graphics2D g2d = (Graphics2D) graphics;
        
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_SPEED);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
                RenderingHints.VALUE_COLOR_RENDER_SPEED);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
        //set rendering settings for speed
        
        g2d.setColor(scene.getBackground());
        //draw scene background color
        
        float gWidth = (float) g2d.getClipBounds().getWidth();
        float gHeight = (float) g2d.getClipBounds().getHeight();
        //graphics dimensions
        
        g2d.fillRect(0, 0, (int) gWidth, (int) gHeight);
        //clear out last drawn frame
        
        scene.getObjects().sort(zComparator);
        //sort objects by z position so objects can be drawn in front of others
        
        Vector camPos = camera.getScenePosition();
        Vector windowPos = camera.getWindowPosition();
        float fov = camera.getFieldOfView() / 2.0f;
        float sensorSize = camera.getSensorSize();
        float zoom = camera.getZoom();
        float viewDistance = camera.getViewDistance();
        //camera data
        
        for (Renderable object : scene.getObjects()) {
            Vector objPos = object.getPosition();
            float camDist = camPos.getZ() - objPos.getZ();
            //object data
            
            if (camDist >= viewDistance || object.getSprite() == null) continue;
            //don't render objects without a sprite, with a camera sprite, or further than view distance
            
            float scale = (float) (zoom * (sensorSize / (sensorSize + (2.0 *
                    camDist * (Math.sin(fov) / Math.sin((Math.PI / 2.0) - fov))))));
            //scale objects based on fov angle and distance from camera using law of sines and camera sensor size
            
            if (scale < 0) break;
            //stop render if objects have negative scale (too far in front of camera)
            
            Sprite sprite = object.getSprite();
            //get sprite
            
            int widthScaled = FastMath.ceil(sprite.getWidth() * scale);
            int heightScaled = FastMath.ceil(sprite.getHeight() * scale);
            //scale image dimensions
            
            float x = ((objPos.getX() - camPos.getX()) * scale) + windowPos.getX();
            float y = gHeight - (((objPos.getY() - camPos.getY()) * scale) + (gHeight - windowPos.getY()));
            //translate object coordinates
            
            Box screenBox = new Box(gWidth, gHeight,
                    new Vector(gWidth / 2.0f, gHeight / 2.0f));
            Box spriteBox;
            //boxes to represent image and panel bounds
            
            AffineTransform transform = new AffineTransform();
            //create new transform, resetting old one
            
            if (camera.getRotation() != 0 || sprite.getRotation() != 0) {
                //check if there is any rotation
                
                float spriteRotation = sprite.getRotation();
                float cameraRotation = -camera.getRotation();
                //convert to radians
                
                transform.rotate(cameraRotation, windowPos.getX(), windowPos.getY());
                transform.rotate(spriteRotation, x, y);
                //rotate canvas
                
                float sprRotSin = (float) Math.sin(spriteRotation + cameraRotation);
                float sprRotCos = (float) Math.cos(spriteRotation + cameraRotation);
                float camRotSin = (float) Math.sin(cameraRotation);
                float camRotCos = (float) Math.cos(cameraRotation);
                float relX = x - windowPos.getX();
                float relY = y - windowPos.getY();
                //calculations done once to reduce total calculations
                
                float heightRotated = Math.abs(widthScaled * sprRotSin) + Math.abs(heightScaled * sprRotCos);
                float widthRotated = Math.abs(widthScaled * sprRotCos) + Math.abs(heightScaled * sprRotSin);
                //get dimensions of image based on sprite rotation
                
                float yRotated = (relX * camRotSin) + (relY * camRotCos) + windowPos.getY();
                float xRotated = (relX * camRotCos) - (relY * camRotSin) + windowPos.getX();
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
                
                g2d.setTransform(transform);
                g2d.drawImage(sprite.getImage(), (int) (x - (widthScaled / 2.0)),
                        (int) (y - (heightScaled / 2.0)), widthScaled, heightScaled, null);
                //draw image to panel
            }
        }
        g2d.dispose();
        //free up resources used by graphics processing
    }
}