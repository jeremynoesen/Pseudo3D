package jeremynoesen.pseudo3d.scene;

import javafx.scene.paint.Color;
import jeremynoesen.pseudo3d.scene.entity.Entity;
import jeremynoesen.pseudo3d.scene.renderer.Camera;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * scene to place entities, a camera, and code injections to modify them
 *
 * @author Jeremy Noesen
 */
public class Scene {
    
    /**
     * all entities in the scene
     */
    private CopyOnWriteArrayList<Entity> entities;
    
    /**
     * camera for the scene to determine where to render from
     */
    private Camera camera;
    
    /**
     * background color of scene;
     */
    private Color background;
    
    /**
     * runnable code fragments to run every time the scene ticks
     */
    private final Set<Runnable> runnables;
    
    /**
     * create a new scene
     */
    public Scene() {
        entities = new CopyOnWriteArrayList<>();
        camera = new Camera();
        background = Color.WHITE;
        runnables = new HashSet<>();
    }
    
    /**
     * create a new scene with pre-defined entities, camera, and background color
     *
     * @param entities   entities in scene
     * @param camera     scene camera
     * @param background background color
     * @param runnables  runnables to be injected into game loop
     */
    public Scene(CopyOnWriteArrayList<Entity> entities, Camera camera, Color background, Set<Runnable> runnables) {
        this.entities = entities;
        this.camera = camera;
        this.background = background;
        this.runnables = runnables;
    }
    
    /**
     * copy constructor for scene
     *
     * @param scene scene to copy
     */
    public Scene(Scene scene) {
        entities = new CopyOnWriteArrayList<>();
        for (Entity entity : scene.entities) {
            entities.add(new Entity(entity));
        }
        camera = new Camera(scene.camera);
        background = scene.background;
        runnables = scene.runnables;
    }
    
    /**
     * tick all entities in the scene, updating all motion first, and then all collisions take place. also run any
     * runnables added to the scene
     */
    public void tick() {
        runnables.forEach(Runnable::run);
        for (Entity entity : entities) {
            if (entity.isKinematic()) entity.tickMotion();
        }
        for (Entity entity : entities) {
            if (entity.isKinematic()) entity.tickCollisions();
        }
    }
    
    /**
     * get all the entities in this scene
     *
     * @return list of all entities in this scene
     */
    public CopyOnWriteArrayList<Entity> getEntities() {
        return entities;
    }
    
    /**
     * add an entity to this scene
     *
     * @param entity entity to add
     */
    public void addEntity(Entity entity) {
        entities.add(entity);
        entity.setScene(this);
    }
    
    /**
     * remove an entity from this scene
     *
     * @param entity entity to remove
     */
    public void removeEntity(Entity entity) {
        if (entities.contains(entity)) {
            entities.remove(entity);
            entity.setScene(null);
        }
    }
    
    /**
     * set the entities in this scene
     *
     * @param entities list of entities
     */
    public void setEntities(CopyOnWriteArrayList<Entity> entities) {
        this.entities = entities;
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
    public void setCamera(Camera camera) {
        this.camera = camera;
    }
    
    /**
     * get the background color for the scene
     *
     * @return background color of scene
     */
    public Color getBackground() {
        return background;
    }
    
    /**
     * set a color to show when the scene is rendered, like a sky color
     *
     * @param background color to set as background
     */
    public void setBackground(Color background) {
        this.background = background;
    }
    
    /**
     * add a runnable to the game loop to execute code that is not usually in the game loop. these execute at the same
     * frequency as physics updates and only when the scene is active
     *
     * @param runnable runnable
     */
    public void addRunnable(Runnable runnable) {
        runnables.add(runnable);
    }
    
    /**
     * remove a runnable from the game loop
     *
     * @param runnable runnable
     */
    public void removeRunnable(Runnable runnable) {
        runnables.remove(runnable);
    }
    
    /**
     * get all runnables for the scene to be injected into the game loop
     *
     * @return all runnables for the scene
     */
    public Set<Runnable> getRunnables() {
        return runnables;
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
        Scene that = (Scene) o;
        return Objects.equals(entities, that.entities) &&
                camera.equals(that.camera) &&
                Objects.equals(background, that.background);
    }
}
