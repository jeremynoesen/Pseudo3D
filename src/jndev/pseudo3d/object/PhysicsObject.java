package jndev.pseudo3d.object;

import jndev.pseudo3d.physics.AABBPhysics;
import jndev.pseudo3d.scene.Renderable;
import jndev.pseudo3d.scene.Scene;
import jndev.pseudo3d.util.Box;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

/**
 * object for games, has physics and various methods to get object status
 *
 * @author JNDev (Jeremaster101)
 */
public class PhysicsObject extends AABBPhysics implements Renderable {
    
    /**
     * image that represents the object when rendered
     */
    private Image sprite;
    
    /**
     * constructs a game object
     */
    public PhysicsObject() {
        super();
        sprite = null;
    }
    
    /**
     * copy constructor for objects
     *
     * @param physicsObject object to copy
     */
    public PhysicsObject(PhysicsObject physicsObject) {
        super(physicsObject);
        sprite = physicsObject.sprite;
    }
    
    /**
     * get all nearby objects in a cuboid region with x radius xRadius, y radius yRadius, and z radius zRadius
     *
     * @param xRadius radius x
     * @param yRadius radius y
     * @param zRadius radius z
     * @return list of objects nearby
     */
    public ArrayList<PhysicsObject> getNearbyObjects(double xRadius, double yRadius, double zRadius) {
        ArrayList<PhysicsObject> nearby = new ArrayList<>();
        if (getScene() == null) return nearby;
        for (Renderable object : getScene().getObjects()) {
            if (object == this) continue;
            if (object instanceof PhysicsObject) {
                PhysicsObject o = (PhysicsObject) object;
                Box area = new Box(xRadius * 2, yRadius * 2, zRadius * 2, getPosition());
                if (o.getBoundingBox().overlaps(area))
                    nearby.add(o);
            }
        }
        return nearby;
    }
    
    /**
     * get the sprite assigned to this object
     *
     * @return image sprite of this object
     */
    @Override
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
    
    /**
     * set the scene the object is in
     *
     * @param scene scene to place object in
     */
    @Override
    public void setScene(Scene scene) {
        super.setScene(scene);
        if (scene != null && !scene.getObjects().contains(this)) scene.addObject(this);
    }
    
    /**
     * check if this object is identical to another
     *
     * @param o object to check
     * @return true if this object is identical to the other object
     */
    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PhysicsObject physicsObject = (PhysicsObject) o;
        return Objects.equals(sprite, physicsObject.sprite);
    }
}
