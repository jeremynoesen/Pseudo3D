package xyz.jeremynoesen.pseudo3d.scene;

import javafx.scene.canvas.GraphicsContext;
import xyz.jeremynoesen.pseudo3d.scene.entity.Entity;
import xyz.jeremynoesen.pseudo3d.scene.entity.Sprite;
import xyz.jeremynoesen.pseudo3d.scene.render.Camera;
import xyz.jeremynoesen.pseudo3d.scene.render.Renderer;
import xyz.jeremynoesen.pseudo3d.scene.util.Vector;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;

/**
 * scene to place entities, a camera, and code injections to modify them
 *
 * @author Jeremy Noesen
 */
public class Scene {
    
    /**
     * all entities in the scene
     */
    private final LinkedList<Entity> entities;
    
    /**
     * camera for the scene to determine where to render from
     */
    private Camera camera;
    
    /**
     * background sprite of scene;
     */
    private Sprite background;
    
    /**
     * grid scaling for scene, how many pixels represent a meter
     */
    private Vector gridScale;
    
    /**
     * runnable code fragments to run every time the scene ticks
     */
    private final HashSet<Runnable> tickRunnables;
    
    /**
     * runnable code fragments to run every time the scene renders
     */
    private final HashSet<Runnable> renderRunnables;
    
    /**
     * scene renderer
     */
    private final Renderer renderer;
    
    /**
     * last time a tick finished in nanoseconds
     */
    private long lastTick;
    
    /**
     * last time a render finished in nanoseconds
     */
    private long lastRender;
    
    /**
     * speed modifier for physics and rendering
     */
    private float speed;
    
    /**
     * create a new scene
     */
    public Scene() {
        entities = new LinkedList<>();
        camera = new Camera();
        background = null;
        tickRunnables = new HashSet<>();
        renderRunnables = new HashSet<>();
        gridScale = new Vector(32, 32, 32);
        renderer = new Renderer(this);
        speed = 1;
    }
    
    /**
     * create a new scene with pre-defined entities, camera, and background color
     *
     * @param entities   entities in scene
     * @param camera     scene camera
     * @param background background sprite
     * @param gridScale  scene grid scale
     */
    public Scene(LinkedList<Entity> entities, Camera camera, Sprite background, Vector gridScale) {
        this.entities = entities;
        this.camera = camera;
        this.background = background;
        tickRunnables = new HashSet<>();
        renderRunnables = new HashSet<>();
        this.gridScale = gridScale;
        this.renderer = new Renderer(this);
        speed = 1;
    }
    
    /**
     * copy constructor for scene
     *
     * @param scene scene to copy
     */
    public Scene(Scene scene) {
        entities = new LinkedList<>();
        for (Entity entity : scene.entities) {
            entities.add(new Entity(entity));
        }
        camera = new Camera(scene.camera);
        background = scene.background;
        tickRunnables = scene.tickRunnables;
        renderRunnables = scene.renderRunnables;
        gridScale = scene.gridScale;
        renderer = new Renderer(this);
        lastRender = 0;
        lastTick = 0;
        speed = scene.speed;
    }
    
    /**
     * tick all entities in the scene, updating all motion first, and then all collisions take place. also run any tick
     * injections
     */
    public void tick() {
        float deltaTime = 0;
        if (lastTick > 0) deltaTime = (System.nanoTime() - lastTick) / 1000000000.0f;
        //delta time for ticking
        
        tickRunnables.forEach(Runnable::run);
        //run all tick loop injections
        
        for (Entity entity : entities) {
            entity.tickMotion(deltaTime * speed);
        }
        //tick all entities motion
        
        for (Entity entity : entities) {
            entity.tickCollisions();
        }
        //tick all entities collisions
        
        lastTick = System.nanoTime();
    }
    
    /**
     * render this scene to the main canvas, as well as run any render injections
     *
     * @param graphicsContext graphics context to render to
     */
    public void render(GraphicsContext graphicsContext) {
        float deltaTime = 0;
        if (lastRender > 0) deltaTime = (System.nanoTime() - lastRender) / 1000000000.0f;
        //delta time for rendering
        
        renderRunnables.forEach(Runnable::run);
        //run all render loop injections
        
        renderer.render(graphicsContext, deltaTime * speed);
        //render frame
        
        lastRender = System.nanoTime();
    }
    
    /**
     * reset the last values for ticking and rendering. should only be called if the game loop is paused
     */
    public void clearDeltaTime() {
        lastRender = 0;
        lastTick = 0;
    }
    
    /**
     * get all the entities in this scene. modifying this directly will cause problems
     *
     * @return list of all entities in this scene
     */
    public LinkedList<Entity> getEntities() {
        return entities;
    }
    
    /**
     * add an entity to this scene
     *
     * @param entity entity to add
     */
    public Scene addEntity(Entity entity) {
        entities.add(entity);
        entity.setScene(this);
        return this;
    }
    
    /**
     * remove an entity from this scene
     *
     * @param entity entity to remove
     */
    public Scene removeEntity(Entity entity) {
        if (entities.contains(entity)) {
            entities.remove(entity);
            entity.setScene(null);
        }
        return this;
    }
    
    /**
     * get the camera for this scene
     *
     * @return scene's camera
     */
    public Camera getCamera() {
        return camera;
    }
    
    /**
     * give this scene a different camera
     *
     * @param camera new camera
     */
    public Scene setCamera(Camera camera) {
        this.camera = camera;
        return this;
    }
    
    /**
     * get the background sprite for the scene
     *
     * @return background sprite of scene
     */
    public Sprite getBackground() {
        return background;
    }
    
    /**
     * set a sprite to show when the scene is rendered
     *
     * @param background sprite to set as background
     */
    public Scene setBackground(Sprite background) {
        this.background = background;
        return this;
    }
    
    /**
     * add a runnable to the tick loop for this scene
     *
     * @param runnable runnable
     */
    public Scene addTickRunnable(Runnable runnable) {
        tickRunnables.add(runnable);
        return this;
    }
    
    /**
     * remove a runnable from the tick loop for the scene
     *
     * @param runnable runnable
     */
    public Scene removeTickRunnable(Runnable runnable) {
        tickRunnables.remove(runnable);
        return this;
    }
    
    /**
     * get all tick runnables for the scene
     *
     * @return all tick runnables for the scene
     */
    public HashSet<Runnable> getTickRunnables() {
        return tickRunnables;
    }
    
    /**
     * add a runnable to the render loop for this scene
     *
     * @param runnable runnable
     */
    public Scene addRenderRunnables(Runnable runnable) {
        renderRunnables.add(runnable);
        return this;
    }
    
    /**
     * remove a runnable from the render loop for the scene
     *
     * @param runnable runnable
     */
    public Scene removeRenderRunnables(Runnable runnable) {
        renderRunnables.remove(runnable);
        return this;
    }
    
    /**
     * get all render runnables for the scene
     *
     * @return all render runnables for the scene
     */
    public HashSet<Runnable> getRenderRunnables() {
        return renderRunnables;
    }
    
    /**
     * get the scene grid scale
     *
     * @return grid scale for the scene
     */
    public Vector getGridScale() {
        return gridScale;
    }
    
    /**
     * set a new grid scale for the scene
     *
     * @param gridScale vector scales
     */
    public Scene setGridScale(Vector gridScale) {
        this.gridScale = gridScale;
        return this;
    }
    
    /**
     * get the speed of the scene
     *
     * @return speed modifier value
     */
    public float getSpeed() {
        return speed;
    }
    
    /**
     * set the speed modifier for the scene
     *
     * @param speed speed modifier value
     */
    public Scene setSpeed(float speed) {
        this.speed = speed;
        return this;
    }
    
    /**
     * check if a scene is identical to this scene
     *
     * @param o entity to check
     * @return true if the scene is equal to this scene
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Scene scene = (Scene) o;
        return Objects.equals(entities, scene.entities) &&
                Objects.equals(camera, scene.camera) &&
                Objects.equals(background, scene.background) &&
                Objects.equals(gridScale, scene.gridScale) &&
                Objects.equals(tickRunnables, scene.tickRunnables) &&
                Objects.equals(renderRunnables, scene.renderRunnables) &&
                Objects.equals(renderer, scene.renderer) &&
                Float.compare(speed, scene.speed) == 0;
    }
}
