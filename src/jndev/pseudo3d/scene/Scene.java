package jndev.pseudo3d.scene;

import jndev.pseudo3d.physics.AABBRigidBody;
import jndev.pseudo3d.object.RigidBodyObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * scene to place objects, a camera, and code injections to modify them
 *
 * @author JNDev (Jeremaster101)
 */
public class Scene {
    
    /**
     * all objects in the scene
     */
    private ArrayList<Renderable> objects;
    
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
    private Set<Runnable> runnables;
    
    /**
     * create a new scene
     */
    public Scene() {
        objects = new ArrayList<>();
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
    public Scene(ArrayList<Renderable> objects, Camera camera, Color background, Set<Runnable> runnables) {
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
        objects = new ArrayList<>(scene.objects);
        camera = scene.camera;
        background = scene.background;
        runnables = scene.runnables;
    }
    
    /**
     * tick the scene once and every object in it. also sort all objects by z location from high to low
     */
    public void tick() {
        for (int i = 0; i < objects.size(); i++) {
            if (objects.get(i) instanceof AABBRigidBody) ((AABBRigidBody) objects.get(i)).tick();
        }
    }
    
    /**
     * get all the objects in this scene
     *
     * @return ArrayList of all objects in this scene
     */
    public ArrayList<Renderable> getObjects() {
        return objects;
    }
    
    /**
     * add an object to this scene
     *
     * @param object object to add
     */
    public void addObject(Renderable object) {
        objects.add(object);
        if (object instanceof RigidBodyObject) ((RigidBodyObject) object).setScene(this);
    }
    
    /**
     * remove an object from this scene
     *
     * @param object object to remove
     */
    public void removeObject(Renderable object) {
        if (objects.contains(object)) {
            objects.remove(object);
            if (object instanceof RigidBodyObject) ((RigidBodyObject) object).setScene(null);
        }
    }
    
    /**
     * set the objects in this scene
     *
     * @param objects ArrayList of objects
     */
    public void setObjects(ArrayList<Renderable> objects) {
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
        Scene scene = (Scene) o;
        return Objects.equals(objects, scene.objects) &&
                camera.equals(scene.camera) &&
                Objects.equals(background, scene.background);
    }
}
