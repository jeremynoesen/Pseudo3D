package jeremynoesen.pseudo3d.scene.entity;

import jeremynoesen.pseudo3d.scene.Scene;
import jeremynoesen.pseudo3d.scene.util.Box;

import java.util.ArrayList;
import java.util.Objects;

/**
 * entity to be placed in a scene, includes physics and a sprite
 *
 * @author Jeremy Noesen
 */
public class Entity extends Physics {
    
    /**
     * image that represents the entity when rendered
     */
    private Sprite sprite;
    
    /**
     * constructs a new default entity
     */
    public Entity() {
        super();
        sprite = null;
    }
    
    /**
     * copy constructor for entities
     *
     * @param entity entity to copy
     */
    public Entity(Entity entity) {
        super(entity);
        sprite = new Sprite(entity.sprite);
    }
    
    /**
     * get all nearby entities in a cuboid region with x radius xRadius, y radius yRadius, and z radius zRadius
     *
     * @param xRadius radius x
     * @param yRadius radius y
     * @param zRadius radius z
     * @return list of entities nearby
     */
    public ArrayList<Entity> getNearbyObjects(float xRadius, float yRadius, float zRadius) {
        ArrayList<Entity> nearby = new ArrayList<>();
        if (getScene() == null) return nearby;
        for (Entity entity : getScene().getEntities()) {
            if (entity == this) continue;
            Box area = new Box(xRadius * 2, yRadius * 2, zRadius * 2, getPosition());
            if (entity.overlaps(area))
                nearby.add(entity);
        }
        return nearby;
    }
    
    /**
     * get the sprite assigned to this entity
     *
     * @return image sprite of this entity
     */
    public Sprite getSprite() {
        return sprite;
    }
    
    /**
     * set the sprite for the entity
     *
     * @param sprite new image to set as the sprite
     */
    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }
    
    /**
     * set the scene the entity is in
     *
     * @param scene scene to place entity in
     */
    @Override
    public void setScene(Scene scene) {
        super.setScene(scene);
        if (scene != null && !scene.getEntities().contains(this)) scene.addEntity(this);
    }
    
    /**
     * check if this entity is identical to another
     *
     * @param o entity to check
     * @return true if this entity is identical to the other entity
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Entity that = (Entity) o;
        return Objects.equals(sprite, that.sprite) &&
                super.equals(that);
    }
}
