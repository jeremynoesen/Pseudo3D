package jeremynoesen.pseudo3d.scene;

import javafx.scene.paint.Color;
import jeremynoesen.pseudo3d.scene.entity.Entity;
import jeremynoesen.pseudo3d.scene.entity.Sprite;
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
     * background sprite of scene;
     */
    private Sprite background;
    
    /**
     * runnable code fragments to run every time the scene ticks
     */
    private final Set<Runnable> injections;
    
    /**
     * create a new scene
     */
    public Scene() {
        entities = new CopyOnWriteArrayList<>();
        camera = new Camera();
        background = null;
        injections = new HashSet<>();
    }
    
    /**
     * create a new scene with pre-defined entities, camera, and background color
     *
     * @param entities   entities in scene
     * @param camera     scene camera
     * @param background background sprite
     * @param injections  code to be injected into game loop
     */
    public Scene(CopyOnWriteArrayList<Entity> entities, Camera camera, Sprite background, Set<Runnable> injections) {
        this.entities = entities;
        this.camera = camera;
        this.background = background;
        this.injections = injections;
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
        injections = scene.injections;
    }
    
    /**
     * tick all entities in the scene, updating all motion first, and then all collisions take place. also run any
     * code injections added to the scene
     */
    public void tick() {
        injections.forEach(Runnable::run);
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
    public void setBackground(Sprite background) {
        this.background = background;
    }
    
    /**
     * add a code injection to the game loop for this scene
     *
     * @param injection code injection
     */
    public void addLoopInjection(Runnable injection) {
        injections.add(injection);
    }
    
    /**
     * remove a code injection from the game loop for the scene
     *
     * @param injection code injection
     */
    public void removeLoopInjection(Runnable injection) {
        injections.remove(injection);
    }
    
    /**
     * get all code injections for the scene
     *
     * @return all code injections for the scene
     */
    public Set<Runnable> getLoopInjections() {
        return injections;
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
                Objects.equals(background, that.background) &&
                Objects.equals(injections, that.injections);
    }
}
