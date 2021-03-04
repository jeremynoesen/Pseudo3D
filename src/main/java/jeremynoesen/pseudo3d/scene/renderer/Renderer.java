package jeremynoesen.pseudo3d.scene.renderer;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Affine;
import jeremynoesen.pseudo3d.scene.Scene;
import jeremynoesen.pseudo3d.scene.entity.Entity;
import jeremynoesen.pseudo3d.scene.entity.Sprite;
import jeremynoesen.pseudo3d.scene.util.Box;
import jeremynoesen.pseudo3d.scene.util.Vector;

import java.util.Comparator;

/**
 * Pseudo3D scene renderer, will turn a scene into a render on a javafx canvas
 *
 * @author Jeremy Noesen
 */
public class Renderer {
    
    /**
     * comparator used to sort scene entities from lowest to highest z position for draw order
     */
    private static final Comparator<Entity> zComparator = (o1, o2) -> {
        float diff = o1.getPosition().getZ() - o2.getPosition().getZ();
        return Math.round(diff / (diff == 0 ? 1 : Math.abs(diff)));
    };
    
    /**
     * render a scene frame to canvas graphics context
     *
     * @param scene  scene to render
     * @param canvas canvas to render to
     */
    public static void render(Scene scene, Canvas canvas) {
        
        GraphicsContext gc = canvas.getGraphicsContext2D();
        //get canvas graphics context
        
        gc.setImageSmoothing(false);
        //set rendering settings for speed
        
        short gWidth = (short) gc.getCanvas().getWidth();
        short gHeight = (short) gc.getCanvas().getHeight();
        //graphics dimensions
        
        scene.getEntities().sort(zComparator);
        //sort entities by z position so entities can be drawn in front of others
        
        Camera camera = scene.getCamera();
        Vector camPos = camera.getPosition().multiply(scene.getGridScale());
        Vector renderPos = new Vector(gWidth / 2.0f + camera.getOffset().getX(),
                gHeight / 2.0f + camera.getOffset().getY());
        float fov = camera.getFieldOfView() / 2.0f;
        float sensorSize = camera.getSensorSize();
        float zoom = camera.getZoom();
        float viewDistance = camera.getViewDistance() * scene.getGridScale().getZ();
        float cameraRotation = -camera.getRotation();
        Affine original = gc.getTransform();
        //camera data
        
        if (scene.getBackground() != null) {
            Sprite background = scene.getBackground();
            if (camera.getRotation() != 0) {
                Affine transform = new Affine();
                transform.appendRotation(Math.toDegrees(-camera.getRotation()),
                        renderPos.getX(), renderPos.getY());
                gc.setTransform(transform);
            }
            gc.drawImage(scene.getBackground().getImage(),
                    (renderPos.getX() - (background.getWidth() * scene.getGridScale().getX() * zoom) / 2),
                    (renderPos.getY() - (background.getHeight() * scene.getGridScale().getY() * zoom) / 2),
                    background.getWidth() * zoom * scene.getGridScale().getX(),
                    background.getHeight() * zoom * scene.getGridScale().getY());
            gc.setTransform(original);
            scene.getBackground().update();
        }
        //draw background, also clears screen where drawn
        
        for (Entity entity : scene.getEntities()) {
            Vector objPos = entity.getPosition().multiply(scene.getGridScale());
            float camDist = camPos.getZ() - objPos.getZ();
            //entity data
            
            if (camDist >= viewDistance * scene.getGridScale().getZ() || entity.getSprite() == null) {
                entity.setOnScreen(false);
                continue;
            }
            //don't render entities without a sprite, with a camera sprite, or further than view distance
            
            float scale = (float) (zoom * (sensorSize / (sensorSize + (2.0 *
                    camDist * (Math.sin(fov) / Math.sin((Math.PI / 2.0) - fov))))));
            //scale entities based on fov angle and distance from camera using law of sines and camera sensor size
            
            if (scale < 0) {
                entity.setOnScreen(false);
                continue;
            }
            //don't render if entities have negative scale (too far in front of camera)
            
            Sprite sprite = entity.getSprite();
            //get sprite
            
            int widthScaled = (int) Math.ceil(sprite.getWidth() * scene.getGridScale().getX() * scale);
            int heightScaled = (int) Math.ceil(sprite.getHeight() * scene.getGridScale().getY() * scale);
            //scale image dimensions
            
            float x = ((objPos.getX() - camPos.getX()) * scale) + renderPos.getX();
            float y = gHeight - (((objPos.getY() - camPos.getY()) * scale) + (gHeight - renderPos.getY()));
            //translate entity coordinates
            
            Box screenBox = new Box(gWidth, gHeight,
                    new Vector(gWidth / 2.0f, gHeight / 2.0f));
            Box spriteBox;
            //boxes to represent image and panel bounds
            
            Affine transform = new Affine();
            //create new transform, resetting old one
            
            if (camera.getRotation() != 0 || sprite.getRotation() != 0) {
                //check if there is any rotation
                
                float spriteRotation = sprite.getRotation();
                //sprite rotation
                
                transform.appendRotation(Math.toDegrees(cameraRotation), renderPos.getX(), renderPos.getY());
                transform.appendRotation(Math.toDegrees(spriteRotation), x, y);
                //rotate canvas
                
                float sprRotSin = (float) Math.sin(spriteRotation + cameraRotation);
                float sprRotCos = (float) Math.cos(spriteRotation + cameraRotation);
                float camRotSin = (float) Math.sin(cameraRotation);
                float camRotCos = (float) Math.cos(cameraRotation);
                float relX = x - renderPos.getX();
                float relY = y - renderPos.getY();
                //calculations done once to reduce total calculations
                
                float heightRotated = Math.abs(widthScaled * sprRotSin) + Math.abs(heightScaled * sprRotCos);
                float widthRotated = Math.abs(widthScaled * sprRotCos) + Math.abs(heightScaled * sprRotSin);
                //get dimensions of image based on sprite rotation
                
                float yRotated = (relX * camRotSin) + (relY * camRotCos) + renderPos.getY();
                float xRotated = (relX * camRotCos) - (relY * camRotSin) + renderPos.getX();
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
                
                gc.setTransform(transform);
                gc.drawImage(sprite.getImage(), x - (widthScaled / 2.0),
                        y - (heightScaled / 2.0), widthScaled, heightScaled);
                gc.setTransform(original);
                sprite.update();
                //draw image to panel
                
                entity.setOnScreen(true);
                //update on screen status
            } else {
                entity.setOnScreen(false);
                if (entity.canUpdateOffScreen()) sprite.update();
                //update sprite if allowed
            }
        }
    }
}