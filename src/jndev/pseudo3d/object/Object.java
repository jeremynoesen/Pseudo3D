package jndev.pseudo3d.object;

import jndev.pseudo3d.scene.Scene;
import jndev.pseudo3d.object.physics.Collision;
import jndev.pseudo3d.util.Box;

import java.awt.*;
import java.util.ArrayList;

/**
 * object for games, has physics and various methods to get object status
 *
 * @author JNDev (Jeremaster101)
 */
public class Object extends Collision {
    
    /**
     * scene this object is in
     */
    private Scene scene;
    
    /**
     * image that represents the object when rendered
     */
    private Image sprite;
    
    /**
     * constructs a game object
     */
    public Object() {
        super();
        scene = null;
        sprite = null;
    }
    
    /**
     * copy constructor for objects
     *
     * @param object object to copy
     */
    public Object(Object object) {
        super(object);
        scene = object.getScene();
        sprite = object.getSprite();
    }
    
    /**
     * get all nearby objects in a cuboid region with x radius xRadius, y radius yRadius, and z radius zRadius
     *
     * @param xRadius radius x
     * @param yRadius radius y
     * @param zRadius radius z
     * @return list of objects nearby
     */
    public ArrayList<Object> getNearbyObjects(double xRadius, double yRadius, double zRadius) {
        ArrayList<Object> nearby = new ArrayList<>();
        for (Object object : scene.getObjects()) {
            if (object == this) continue;
            Box area = new Box(xRadius * 2, yRadius * 2, zRadius  * 2, getPosition());
            if (object.overlaps(area))
                nearby.add(object);
        }
        return nearby;
    }
    
    /**
     * get the board this object is currently on
     *
     * @return board this object is on
     */
    @Override
    public Scene getScene() {
        return scene;
    }
    
    /**
     * set the board the object is on
     *
     * @param scene board to place object on
     */
    @Override
    public void setScene(Scene scene) {
        this.scene = scene;
        super.setScene(scene);
    }
    
    /**
     * get the sprite assigned to this object
     *
     * @return image sprite of this object
     */
    public Image getSprite() {
        return sprite;
    }
    
    /**
     * set the sprite for the object
     *
     * @param sprite new image to set as the sprite
     */
    public void setSprite(Image sprite) {
        this.sprite = sprite;
    }
}
