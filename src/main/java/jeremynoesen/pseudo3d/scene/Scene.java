package jeremynoesen.pseudo3d.scene;

import jeremynoesen.pseudo3d.scene.entity.Entity;
import jeremynoesen.pseudo3d.scene.entity.Sprite;
import jeremynoesen.pseudo3d.scene.renderer.Camera;
import jeremynoesen.pseudo3d.scene.util.Vector;

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
    private final HashSet<Runnable> injections;
    
    /**
     * list of entities to be added to the main list
     */
    private final HashSet<Entity> entitiesToAdd;
    
    /**
     * list of entities to be removed from the main list
     */
    private final HashSet<Entity> entitiesToRemove;
    
    /**
     * list of code injections to be added to the main list
     */
    private final HashSet<Runnable> injectionsToAdd;
    
    /**
     * list of code injections to be removed from the main list
     */
    private final HashSet<Runnable> injectionsToRemove;
    
    /**
     * create a new scene
     */
    public Scene() {
        entities = new LinkedList<>();
        camera = new Camera();
        background = null;
        injections = new HashSet<>();
        entitiesToAdd = new HashSet<>();
        entitiesToRemove = new HashSet<>();
        injectionsToAdd = new HashSet<>();
        injectionsToRemove = new HashSet<>();
        gridScale = new Vector(32, 32, 32);
    }
    
    /**
     * create a new scene with pre-defined entities, camera, and background color
     *
     * @param entities   entities in scene
     * @param camera     scene camera
     * @param background background sprite
     * @param injections code to be injected into game loop
     * @param gridScale  scene grid scale
     */
    public Scene(LinkedList<Entity> entities, Camera camera, Sprite background, HashSet<Runnable> injections,
                 Vector gridScale) {
        this.entities = entities;
        this.camera = camera;
        this.background = background;
        this.injections = injections;
        this.gridScale = gridScale;
        entitiesToAdd = new HashSet<>();
        entitiesToRemove = new HashSet<>();
        injectionsToAdd = new HashSet<>();
        injectionsToRemove = new HashSet<>();
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
        injections = scene.injections;
        gridScale = scene.gridScale;
        entitiesToAdd = scene.entitiesToAdd;
        entitiesToRemove = scene.entitiesToRemove;
        injectionsToAdd = scene.injectionsToAdd;
        injectionsToRemove = scene.injectionsToRemove;
    }
    
    /**
     * tick all entities in the scene, updating all motion first, and then all collisions take place. also run any code
     * injections added to the scene
     */
    public void tick() {
        injections.forEach(Runnable::run);
        // run all loop injections
        
        for (Entity entity : entitiesToRemove) {
            entities.remove(entity);
            entity.setScene(null);
        }
        entitiesToRemove.clear();
        // remove any entities queued for removal
        
        for (Entity entity : entitiesToAdd) {
            entities.add(entity);
            entity.setScene(this);
        }
        entitiesToAdd.clear();
        // add any entities queued to be added
        
        injections.removeAll(injectionsToRemove);
        injectionsToRemove.clear();
        // remove any loop injections queued to be removed
        
        injections.addAll(injectionsToAdd);
        injectionsToAdd.clear();
        // add any loop injections queued to be added
        
        for (Entity entity : entities) {
            if (entity.isOnScreen() || entity.canUpdateOffScreen()) entity.tickMotion();
        }
        // tick all entities' motion
        
        for (Entity entity : entities) {
            if (entity.isOnScreen() || entity.canUpdateOffScreen()) entity.tickCollisions();
        }
        // tick all entities' collisions
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
        entitiesToAdd.add(entity);
        return this;
    }
    
    /**
     * remove an entity from this scene
     *
     * @param entity entity to remove
     */
    public Scene removeEntity(Entity entity) {
        if (entities.contains(entity))
            entitiesToRemove.add(entity);
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
     * add a code injection to the game loop for this scene
     *
     * @param injection code injection
     */
    public Scene addLoopInjection(Runnable injection) {
        injectionsToAdd.add(injection);
        return this;
    }
    
    /**
     * remove a code injection from the game loop for the scene
     *
     * @param injection code injection
     */
    public Scene removeLoopInjection(Runnable injection) {
        injectionsToRemove.add(injection);
        return this;
    }
    
    /**
     * get all code injections for the scene. modifying this directly will cause problems
     *
     * @return all code injections for the scene
     */
    public HashSet<Runnable> getLoopInjections() {
        return injections;
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
