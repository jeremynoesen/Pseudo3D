package jndev.pseudo3d.scene;

import jndev.pseudo3d.physics.AABBPhysics;
import jndev.pseudo3d.object.PhysicsObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

/**
 * scene to place objects on as well as a camera to render them
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
     * create a new scene
     */
    public Scene() {
        objects = new ArrayList<>();
        camera = new Camera();
        background = Color.WHITE;
    }
    
    /**
     * create a new scene with pre-defined objects, camera, and background color
     *
     * @param objects    objects in scene
     * @param camera     scene camera
     * @param background background color
     */
    public Scene(ArrayList<Renderable> objects, Camera camera, Color background) {
        this.objects = objects;
        this.camera = camera;
        this.background = background;
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
    }
    
    /**
     * tick the scene once and every object in it. also sort all objects by z location from high to low
     */
    public void tick() {
        for (int i = 0; i < objects.size(); i++) {
            if (objects.get(i) instanceof AABBPhysics) ((AABBPhysics) objects.get(i)).tick();
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
        if (object instanceof PhysicsObject) ((PhysicsObject) object).setScene(this);
    }
    
    /**
     * remove an object from this scene
     *
     * @param object object to remove
     */
    public void removeObject(Renderable object) {
        if (objects.contains(object)) {
            objects.remove(object);
            if (object instanceof PhysicsObject) ((PhysicsObject) object).setScene(null);
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
