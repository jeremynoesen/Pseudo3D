package jndev.pseudo3d.scene;

import jndev.pseudo3d.physics.AABBPhysics;
import jndev.pseudo3d.sceneobject.Camera;
import jndev.pseudo3d.sceneobject.PhysicsObject;
import jndev.pseudo3d.sceneobject.Renderable;

import java.awt.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * scene to place objects, a camera, and code injections to modify them
 *
 * @author JNDev (Jeremaster101)
 */
public class Scene {
    
    /**
     * all objects in the scene
     */
    private CopyOnWriteArrayList<Renderable> objects;
    
    /**
     * camera for the scene to determine where to render from
     */
    private Camera camera;
    
    /**
     * background color of scene;
     */
    private Color background;
    
    /**
     * runnables to be inserted into the game loop for the scene
     */
    private final Set<Runnable> runnables;
    
    /**
     * create a new scene
     */
    public Scene() {
        objects = new CopyOnWriteArrayList<>();
        camera = new Camera();
        background = Color.WHITE;
        runnables = new HashSet<>();
    }
    
    /**
     * create a new scene with pre-defined objects, camera, and background color
     *
     * @param objects    objects in scene
     * @param camera     scene camera
     * @param background background color
     * @param runnables  runnables to be injected into game loop
     */
    public Scene(CopyOnWriteArrayList<Renderable> objects, Camera camera, Color background, Set<Runnable> runnables) {
        this.objects = objects;
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
        objects = new CopyOnWriteArrayList<>(scene.objects);
        camera = scene.camera;
        background = scene.background;
        runnables = scene.runnables;
    }
    
    /**
     * tick all physics objects in the scene, updating all motion first, and then all collisions take place
     */
    public void tick() {
        for (Renderable object : objects) {
            if (object instanceof AABBPhysics aabbPhysics) aabbPhysics.tickMotion();
        }
        for (Renderable object : objects) {
            if (object instanceof AABBPhysics aabbPhysics) aabbPhysics.tickCollisions();
        }
    }
    
    /**
     * get all the objects in this scene
     *
     * @return list of all objects in this scene
     */
    public CopyOnWriteArrayList<Renderable> getObjects() {
        return objects;
    }
    
    /**
     * add an object to this scene
     *
     * @param object object to add
     */
    public void addObject(Renderable object) {
        objects.add(object);
        if (object instanceof PhysicsObject physicsObject) physicsObject.setScene(this);
    }
    
    /**
     * remove an object from this scene
     *
     * @param object object to remove
     */
    public void removeObject(Renderable object) {
        if (objects.contains(object)) {
            objects.remove(object);
            if (object instanceof PhysicsObject physicsObject) physicsObject.setScene(null);
        }
    }
    
    /**
     * set the objects in this scene
     *
     * @param objects list of objects
     */
    public void setObjects(CopyOnWriteArrayList<Renderable> objects) {
        this.objects = objects;
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
     * @param o object to check
     * @return true if the scene is equal to this scene
     */
    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Scene that = (Scene) o;
        return Objects.equals(objects, that.objects) &&
                camera.equals(that.camera) &&
                Objects.equals(background, that.background);
    }
}
