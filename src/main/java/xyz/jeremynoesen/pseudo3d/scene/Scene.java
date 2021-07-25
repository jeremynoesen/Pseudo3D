package xyz.jeremynoesen.pseudo3d.scene;

import javafx.scene.canvas.GraphicsContext;
import xyz.jeremynoesen.pseudo3d.scene.entity.Entity;
import xyz.jeremynoesen.pseudo3d.scene.render.Sprite;
import xyz.jeremynoesen.pseudo3d.scene.render.Camera;
import xyz.jeremynoesen.pseudo3d.scene.render.Renderer;
import xyz.jeremynoesen.pseudo3d.scene.util.Vector;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;

/**
 * Scene to place Entities, a Camera, and Runnables in and modify them
 *
 * @author Jeremy Noesen
 */
public class Scene {

    /**
     * All Entities in the Scene
     */
    private final LinkedList<Entity> entities;

    /**
     * Camera for the Scene to determine where to render from
     */
    private Camera camera;

    /**
     * Background Sprite of the Scene;
     */
    private Sprite background;

    /**
     * Grid scaling for the Scene
     * <p>
     * The grid scale is how many pixels represent a meter per axis
     */
    private Vector gridScale;

    /**
     * Runnable code fragments to run every time the Scene ticks
     */
    private final HashSet<Runnable> tickRunnables;

    /**
     * Runnable code fragments to run every time the Scene renders
     */
    private final HashSet<Runnable> renderRunnables;

    /**
     * Scene Renderer
     */
    private final Renderer renderer;

    /**
     * Delta time for the tick loop
     */
    private float tickDeltaTime;

    /**
     * Delta time for the render loop
     */
    private float renderDeltaTime;

    /**
     * Previous time a tick finished in nanoseconds
     */
    private long lastTick;

    /**
     * Previous time a render finished in nanoseconds
     */
    private long lastRender;

    /**
     * Speed modifier for physics and rendering
     */
    private float speed;

    /**
     * Create a new default Scene
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
     * Create a new Scene with pre-defined Entities, Camera, background Sprite, and grid scale
     *
     * @param entities   Entities to put in the Scene
     * @param camera     Scene Camera
     * @param background Background Sprite
     * @param gridScale  Scene grid scale
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
     * Copy constructor for Scene
     *
     * @param scene Scene to copy
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
        tickDeltaTime = 0;
        renderDeltaTime = 0;
        speed = scene.speed;
    }

    /**
     * Tick all entities in the Scene
     * <p>
     * Ticking will do the following in order: run any Runnables, update motion for all Entities, then update collisions
     * for all Entities
     */
    public void tick() {
        tickDeltaTime = 0;
        if (lastTick > 0) tickDeltaTime = (System.nanoTime() - lastTick) / 1000000000.0f;

        tickRunnables.forEach(Runnable::run);

        for (Entity entity : entities) {
            entity.tickMotion(tickDeltaTime * speed);
        }

        for (Entity entity : entities) {
            entity.tickCollisions();
        }

        lastTick = System.nanoTime();
    }

    /**
     * Render this Scene to the main Canvas, as well as run any Runnables
     *
     * @param graphicsContext GraphicsContext to render to
     */
    public void render(GraphicsContext graphicsContext) {
        renderDeltaTime = 0;
        if (lastRender > 0) renderDeltaTime = (System.nanoTime() - lastRender) / 1000000000.0f;

        renderRunnables.forEach(Runnable::run);
        renderer.render(graphicsContext, renderDeltaTime * speed);

        lastRender = System.nanoTime();
    }

    /**
     * Reset the last ticking and rendering times
     * <p>
     * This should only be called if the game loop is paused
     */
    public void clearDeltaTime() {
        lastRender = 0;
        lastTick = 0;
    }

    /**
     * Get all the Entities in this Scene
     * <p>
     * Modifying this directly will cause problems
     *
     * @return List of all Entities in this Scene
     */
    public LinkedList<Entity> getEntities() {
        return entities;
    }

    /**
     * Add an Entity to this Scene
     *
     * @param entity Entity to add
     * @return this Scene
     */
    public Scene addEntity(Entity entity) {
        entities.add(entity);
        entity.setScene(this);
        return this;
    }

    /**
     * Remove an Entity from this Scene
     *
     * @param entity Entity to remove
     * @return this Scene
     */
    public Scene removeEntity(Entity entity) {
        if (entities.contains(entity)) {
            entities.remove(entity);
            entity.setScene(null);
        }
        return this;
    }

    /**
     * Get the Camera for this Scene
     *
     * @return Scene's Camera
     */
    public Camera getCamera() {
        return camera;
    }

    /**
     * Give this Scene a different Camera
     *
     * @param camera New Camera
     * @return this Scene
     */
    public Scene setCamera(Camera camera) {
        this.camera = camera;
        return this;
    }

    /**
     * Get the background Sprite of the Scene
     *
     * @return Background Sprite of Scene
     */
    public Sprite getBackground() {
        return background;
    }

    /**
     * Set the background Sprite for the Scene
     *
     * @param background Background Sprite
     * @return this Scene
     */
    public Scene setBackground(Sprite background) {
        this.background = background;
        return this;
    }

    /**
     * Add a Runnable to the tick loop for this Scene
     *
     * @param runnable Runnable
     * @return this Scene
     */
    public Scene addTickRunnable(Runnable runnable) {
        tickRunnables.add(runnable);
        return this;
    }

    /**
     * Remove a Runnable from the tick loop for the Scene
     *
     * @param runnable Runnable
     * @return this Scene
     */
    public Scene removeTickRunnable(Runnable runnable) {
        tickRunnables.remove(runnable);
        return this;
    }

    /**
     * Get all tick Runnables for the Scene
     *
     * @return All tick Runnables for the Scene
     */
    public HashSet<Runnable> getTickRunnables() {
        return tickRunnables;
    }

    /**
     * Add a Runnable to the render loop for this Scene
     *
     * @param runnable Runnable
     * @return this Scene
     */
    public Scene addRenderRunnable(Runnable runnable) {
        renderRunnables.add(runnable);
        return this;
    }

    /**
     * Remove a Runnable from the render loop for the Scene
     *
     * @param runnable Runnable
     * @return this Scene
     */
    public Scene removeRenderRunnable(Runnable runnable) {
        renderRunnables.remove(runnable);
        return this;
    }

    /**
     * Get all render Runnables for the Scene
     *
     * @return All render Runnables for the Scene
     */
    public HashSet<Runnable> getRenderRunnables() {
        return renderRunnables;
    }

    /**
     * Get the Scene grid scale
     *
     * @return Grid scale for the Scene
     */
    public Vector getGridScale() {
        return gridScale;
    }

    /**
     * Set a new grid scale for the Scene
     *
     * @param gridScale Grid scales
     * @return this Scene
     */
    public Scene setGridScale(Vector gridScale) {
        this.gridScale = gridScale;
        return this;
    }

    /**
     * Get the speed modifier of the Scene
     *
     * @return Speed modifier
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * Set the speed modifier for the Scene
     *
     * @param speed Speed modifier
     * @return this Scene
     */
    public Scene setSpeed(float speed) {
        this.speed = speed;
        return this;
    }

    /**
     * Get the delta time for the previous iteration of the tick loop
     *
     * @return Delta time in seconds
     */
    public float getTickDeltaTime() {
        return tickDeltaTime;
    }

    /**
     * Get the delta time for the previous iteration of the render loop
     *
     * @return Delta time in seconds
     */
    public float getRenderDeltaTime() {
        return renderDeltaTime;
    }

    /**
     * Check if a Scene is identical to this Scene
     *
     * @param o Scene to check
     * @return True if the Scene is equal to this Scene
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
