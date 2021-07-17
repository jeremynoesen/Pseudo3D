package xyz.jeremynoesen.pseudo3d.scene.render;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Affine;
import xyz.jeremynoesen.pseudo3d.scene.Scene;
import xyz.jeremynoesen.pseudo3d.scene.entity.Entity;
import xyz.jeremynoesen.pseudo3d.scene.entity.Sprite;
import xyz.jeremynoesen.pseudo3d.scene.util.Box;
import xyz.jeremynoesen.pseudo3d.scene.util.Vector;

import java.util.Comparator;
import java.util.Objects;

/**
 * scene renderer, will turn a scene into a render on a javafx canvas
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
     * scene being rendered by this renderer
     */
    private final Scene scene;
    
    /**
     * reference to scene's camera
     */
    private Camera camera;
    
    /**
     * position on the canvas to render from
     */
    private Vector renderPos;
    
    /**
     * javafx canvas graphics context to render to
     */
    private GraphicsContext graphicsContext;
    
    /**
     * time elapsed in the last render frame
     */
    private float deltaTime;
    
    /**
     * create a new renderer for the specified scene
     *
     * @param scene scene to render
     */
    public Renderer(Scene scene) {
        this.scene = scene;
    }
    
    /**
     * render the next full frame
     *
     * @param graphicsContext graphics context to draw to
     * @param deltaTime       time elapsed in last frame, used for sprite updating
     */
    public void render(GraphicsContext graphicsContext, float deltaTime) {
        this.graphicsContext = graphicsContext;
        this.deltaTime = deltaTime;
        init();
        drawBackground();
        for (Entity entity : scene.getEntities()) {
            drawEntity(entity);
        }
    }
    
    /**
     * initialize a few variables and sort the scene entities before starting with the render
     */
    private void init() {
        scene.getEntities().sort(zComparator);
        //sort entities by z position so entities can be drawn in front of others
        
        graphicsContext.setImageSmoothing(false);
        //set rendering settings for speed
        
        camera = scene.getCamera();
        renderPos = new Vector((float) graphicsContext.getCanvas().getWidth() / 2.0f + camera.getOffset().getX(),
                (float) graphicsContext.getCanvas().getHeight() / 2.0f + camera.getOffset().getY());
        //update required variables
    }
    
    /**
     * draw the background image
     */
    private void drawBackground() {
        if (scene.getBackground() != null) {
            Sprite background = scene.getBackground();
            //check if there is a background sprite, if so, get it
            
            Affine original = graphicsContext.getTransform();
            if (camera.getRotation() != 0 || background.getRotation() != 0) {
                Affine transform = new Affine();
                transform.appendRotation(-camera.getRotation() - background.getRotation(),
                        renderPos.getX(), renderPos.getY());
                graphicsContext.setTransform(transform);
            }
            //rotate the canvas if the camera is rotated
            
            float zoom = camera.getZoom();
            graphicsContext.drawImage(scene.getBackground().getImage(),
                    (renderPos.getX() - (background.getWidth() * scene.getGridScale().getX() * zoom) / 2),
                    (renderPos.getY() - (background.getHeight() * scene.getGridScale().getY() * zoom) / 2),
                    background.getWidth() * zoom * scene.getGridScale().getX(),
                    background.getHeight() * zoom * scene.getGridScale().getY());
            //draw the image
            
            graphicsContext.setTransform(original);
            scene.getBackground().update(deltaTime);
            //update the sprite's frames and put the canvas back
        }
    }
    
    /**
     * draw an entity to the canvas
     *
     * @param entity entity to draw to the canvas
     */
    private void drawEntity(Entity entity) {
        Vector objPos = entity.getPosition().multiply(scene.getGridScale());
        Vector camPos = camera.getPosition().multiply(scene.getGridScale());
        float camDist = camPos.getZ() - objPos.getZ();
        //entity and camera data
        
        if (!entity.isEnabled() || !entity.isVisible() || entity.getSprite() == null ||
                camDist >= camera.getViewDistance() * scene.getGridScale().getZ()) {
            entity.setOnScreen(false);
            return;
        }
        //don't render entities without a sprite or further than view distance
        
        float scale = (float) (camera.getZoom() * (camera.getSensorSize() / (camera.getSensorSize() + (2.0 *
                camDist * (Math.sin(Math.toRadians(camera.getFieldOfView()) / 2.0f) /
                Math.sin((Math.PI / 2.0) - Math.toRadians(camera.getFieldOfView()) / 2.0f))))));
        //scale entities based on fov angle and distance from camera using law of sines and camera sensor size
        
        if (scale <= 0) {
            entity.setOnScreen(false);
            return;
        }
        //don't render if entities are too small
        
        Sprite sprite = entity.getSprite();
        //get entity sprite
        
        int widthScaled = (int) Math.ceil(sprite.getWidth() * scene.getGridScale().getX() * scale);
        int heightScaled = (int) Math.ceil(sprite.getHeight() * scene.getGridScale().getY() * scale);
        //scale image dimensions
        
        short gWidth = (short) graphicsContext.getCanvas().getWidth();
        short gHeight = (short) graphicsContext.getCanvas().getHeight();
        //references to canvas dimensions
        
        float x = ((objPos.getX() - camPos.getX()) * scale) + renderPos.getX();
        float y = gHeight - (((objPos.getY() - camPos.getY()) * scale) + (gHeight - renderPos.getY()));
        //translate entity coordinates
        
        Box screenBox = new Box(gWidth, gHeight,
                new Vector(gWidth / 2.0f, gHeight / 2.0f));
        Box spriteBox;
        //boxes to represent image and panel bounds
        
        Affine original = graphicsContext.getTransform();
        //original affine transform
        
        Affine transform = new Affine();
        //create new transform, resetting old one
        
        if (camera.getRotation() != 0 || sprite.getRotation() != 0) {
            //check if there is any rotation
            
            float spriteRotation = sprite.getRotation();
            float cameraRotation = -camera.getRotation();
            //get rotations
            
            transform.appendRotation(cameraRotation, renderPos.getX(), renderPos.getY());
            transform.appendRotation(spriteRotation, x, y);
            //rotate canvas
    
            spriteRotation = (float) Math.toRadians(spriteRotation);
            cameraRotation = (float) Math.toRadians(cameraRotation);
            //convert to radians
            
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
            
            graphicsContext.setTransform(transform);
            graphicsContext.drawImage(sprite.getImage(), x - (widthScaled / 2.0),
                    y - (heightScaled / 2.0), widthScaled, heightScaled);
            graphicsContext.setTransform(original);
            sprite.update(deltaTime * entity.getSpeed());
            //draw image to panel
            
            entity.setOnScreen(true);
            //update on screen status
        } else {
            entity.setOnScreen(false);
            if (entity.canUpdateOffScreen()) sprite.update(deltaTime * entity.getSpeed());
            //update sprite if allowed
        }
    }
    
    /**
     * check if two renderer objects are equal
     *
     * @param o object to check
     * @return true if equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Renderer renderer = (Renderer) o;
        return Objects.equals(scene, renderer.scene) &&
                Objects.equals(camera, renderer.camera) &&
                Objects.equals(renderPos, renderer.renderPos) &&
                Objects.equals(graphicsContext, renderer.graphicsContext);
    }
}
