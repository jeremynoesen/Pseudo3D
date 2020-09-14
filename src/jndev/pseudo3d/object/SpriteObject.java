package jndev.pseudo3d.object;

import jndev.pseudo3d.sprite.Sprite;
import jndev.pseudo3d.util.Vector;

import java.util.Objects;

/**
 * sprite placed in 3D space with no physics
 *
 * @author JNDev (Jeremaster101)
 */
public class SpriteObject implements Renderable {
    
    /**
     * sprite for object
     */
    private Sprite sprite;
    
    /**
     * position of object in 3D space
     */
    private Vector position;
    
    /**
     * create a new sprite object
     */
    public SpriteObject() {
        sprite = null;
        position = new Vector();
    }
    
    /**
     * create as new sprite object with pre-defined sprite, position, and scene
     *
     * @param sprite   sprite image of object
     * @param position position of object
     */
    public SpriteObject(Sprite sprite, Vector position) {
        this.sprite = sprite;
        this.position = position;
    }
    
    /**
     * copyu constructor for sprite objects
     *
     * @param spriteObject sprite object to copy
     */
    public SpriteObject(SpriteObject spriteObject) {
        sprite = spriteObject.sprite;
        position = spriteObject.position;
    }
    
    /**
     * get the sprite image for the object
     *
     * @return sprite image
     */
    @Override
    public Sprite getSprite() {
        return sprite;
    }
    
    /**
     * set sprite for object
     *
     * @param sprite image to set as sprite
     */
    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }
    
    /**
     * get the position of the object
     *
     * @return object position
     */
    @Override
    public Vector getPosition() {
        return position;
    }
    
    /**
     * set the position of the object
     *
     * @param position position vector
     */
    public void setPosition(Vector position) {
        this.position = position;
    }
    
    /**
     * check that this sprite object is similar to another
     *
     * @param o object to check
     * @return true if the objects are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpriteObject that = (SpriteObject) o;
        return Objects.equals(sprite, that.sprite) &&
                Objects.equals(position, that.position);
    }
}
