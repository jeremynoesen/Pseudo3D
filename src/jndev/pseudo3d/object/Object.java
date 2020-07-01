package jndev.pseudo3d.object;

import jndev.pseudo3d.util.Box;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

/**
 * object for games, has physics and various methods to get object status
 *
 * @author JNDev (Jeremaster101)
 */
public class Object extends AABBPhysics implements Renderable {
    
    /**
     * image that represents the object when rendered
     */
    private Image sprite;
    
    /**
     * constructs a game object
     */
    public Object() {
        super();
        sprite = null;
    }
    
    /**
     * copy constructor for objects
     *
     * @param object object to copy
     */
    public Object(Object object) {
        super(object);
        sprite = object.sprite;
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
        if(getScene() == null) return nearby;
        for (Renderable object : getScene().getObjects()) {
            if (object == this) continue;
            if (object instanceof Object) {
                Object o = (Object) object;
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
    @Override
    public void setSprite(Image sprite) {
        this.sprite = sprite;
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
        Object object = (Object) o;
        return Objects.equals(sprite, object.sprite);
    }
}
