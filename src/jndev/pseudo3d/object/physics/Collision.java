package jndev.pseudo3d.object.physics;

import jndev.pseudo3d.scene.Scene;
import jndev.pseudo3d.util.Side;

import java.util.HashSet;
import java.util.Set;

/**
 * abstract class to handle collisions for an object
 *
 * @author JNDev (Jeremaster101)
 */
public abstract class Collision extends Motion {
    
    /**
     * board the object is colliding on
     */
    private Scene scene;
    
    /**
     * if an object can collide with others
     */
    private boolean collidable;
    
    /**
     * whether or not an object is colliding
     */
    private boolean colliding;
    
    /**
     * whether or not an object is overlapping
     */
    private boolean overlapping;
    
    /**
     * side the object is colliding or overlapping with
     */
    private Side side;
    
    /**
     * list of objects this one is colliding with
     */
    private final Set<Collision> allColliding;
    
    /**
     * list of objects this one is overlapping
     */
    private final Set<Collision> allOverlapping;
    
    /**
     * initializes all booleans to false and initializes array lists
     */
    protected Collision() {
        super();
        scene = null;
        collidable = true;
        colliding = false;
        overlapping = false;
        side = Side.NONE;
        allColliding = new HashSet<>();
        allOverlapping = new HashSet<>();
    }
    
    /**
     * copy constructor for collision data
     *
     * @param collision collision data to copy
     */
    protected Collision(Collision collision) {
        super(collision);
        scene = collision.getScene();
        collidable = collision.isCollidable();
        colliding = collision.isColliding();
        overlapping = collision.isOverlapping();
        side = collision.getCollidingSide();
        allColliding = new HashSet<>(collision.getCollidingObjects());
        allOverlapping = new HashSet<>(collision.getOverlappingObjects());
    }
    
    /**
     * check for collisions and update position based on collisions
     */
    @Override
    public void tick() {
        checkCollisions();
        super.tick();
    }
    
    /**
     * check if an object has collided with another object
     */
    private void checkCollisions() { //todo object priority so the right object gets its position fixed
        colliding = false;
        overlapping = false;
        allColliding.clear();
        allOverlapping.clear();
        for (Collision object : scene.getObjects()) {
            if (object == this) continue;
            if (overlaps(object)) {
                if (object.isCollidable() && collidable) {
                    colliding = true;
                    allColliding.add(object);
                    double collideDist = getOverlappingDistance();
                    side = getOverlappingSide();
                    switch (side) {
                        case TOP:
                            setPosition(getPosition().setY(getPosition().getY() - collideDist));
                            setVelocity(getVelocity().setY(0));
                            break;
                        case BOTTOM:
                            setPosition(getPosition().setY(getPosition().getY() + collideDist));
                            setVelocity(getVelocity().setY(0));
                            return;
                        case LEFT:
                            setPosition(getPosition().setX(getPosition().getX() + collideDist));
                            setVelocity(getVelocity().setX(0));
                            break;
                        case RIGHT:
                            setPosition(getPosition().setX(getPosition().getX() - collideDist));
                            setVelocity(getVelocity().setX(0));
                            break;
                        case BACK:
                            setPosition(getPosition().setZ(getPosition().getZ() + collideDist));
                            setVelocity(getVelocity().setZ(0));
                            break;
                        case FRONT:
                            setPosition(getPosition().setZ(getPosition().getZ() - collideDist));
                            setVelocity(getVelocity().setZ(0));
                            break;
                    }
                } else if (!object.isCollidable() || !collidable) {
                    overlapping = true;
                    allOverlapping.add(object);
                }
            }
        }
    }
    
    /**
     * set the board this object is on
     *
     * @param scene board for collisions
     */
    public void setScene(Scene scene) {
        this.scene = scene;
    }
    
    /**
     * get the board the collisions are set to occur in
     *
     * @return board for collisions
     */
    public Scene getScene() {
        return scene;
    }
    
    /**
     * check if an object can be collided with
     *
     * @return true if collidable
     */
    public boolean isCollidable() {
        return collidable;
    }
    
    /**
     * enable or disable collisions for the object
     *
     * @param collidable set to true to allow collisions
     */
    public void setCollidable(boolean collidable) {
        this.collidable = collidable;
    }
    
    /**
     * get the objects this object is colliding with. only works with collidable objects
     *
     * @return list of objects this object is colliding with
     */
    public Set<Collision> getCollidingObjects() {
        return allColliding;
    }
    
    /**
     * check if an object overlaps another. only works if other objects are non collidable
     *
     * @return list of objects this object overlaps
     */
    public Set<Collision> getOverlappingObjects() {
        return allOverlapping;
    }
    
    /**
     * check if the object is currently colliding with another object
     *
     * @return true if the object has collided with another object
     */
    public boolean isColliding() {
        return colliding;
    }
    
    /**
     * check if the object is overlapping another object
     *
     * @return true if an object overlaps another
     */
    public boolean isOverlapping() {
        return overlapping;
    }
    
    /**
     * get the side the object is colliding with
     *
     * @return side the object is colliding or overlapping on
     */
    public Side getCollidingSide() {
        return side;
    }
}
