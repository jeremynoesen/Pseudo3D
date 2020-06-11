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
    private void checkCollisions() {
        colliding = false;
        overlapping = false;
        allColliding.clear();
        allOverlapping.clear();
        
        for (Collision object : scene.getObjects()) { //loop through all objects in scene
            
            if (object == this) continue; //ignore self
            
            if (overlaps(object)) { //check for an overlap
                
                if (object.isCollidable() && collidable) { //if this and other object can collide
    
                    //do the collision calculations
                    doCollision(object, getOverlappingDistance(), getOverlappingSide());
                    
                } else if (!object.isCollidable() || !collidable) {
                    overlapping = true;
                    allOverlapping.add(object); //set overlapping if can't collide
                }
            }
        }
    }
    
    /**
     * fix the position of this object instantly
     *
     * @param object      object colliding with this object
     * @param collideDist distance of overlap
     * @param side        side of overlap
     */
    private void doCollision(Collision object, double collideDist, Side side) {
        colliding = true;
        allColliding.add(object);
        this.side = side;
        
        switch (side) {
            case BOTTOM:
                // check if this object is the faster object to prevent fixing the wrong object
                if (getVelocity().getY() < 0 && Math.abs(getVelocity().getY()) >= Math.abs(object.getVelocity().getY())) {
                    setPosition(getPosition().setY(getPosition().getY() + collideDist));
                    //fix object position so it is not overlapping
                    setVelocity(getVelocity().setY(0));
                    //set object velocity to 0 in the same direction
                }
                break;
    
            case TOP:
                if (getVelocity().getY() > 0 && Math.abs(getVelocity().getY()) >= Math.abs(object.getVelocity().getY())) {
                    setPosition(getPosition().setY(getPosition().getY() - collideDist));
                    setVelocity(getVelocity().setY(0));
                }
                break;
                
            case LEFT:
                if (getVelocity().getX() < 0 && Math.abs(getVelocity().getX()) >= Math.abs(object.getVelocity().getX())) {
                    setPosition(getPosition().setX(getPosition().getX() + collideDist));
                    setVelocity(getVelocity().setX(0));
                }
                break;
            
            case RIGHT:
                if (getVelocity().getX() > 0 && Math.abs(getVelocity().getX()) >= Math.abs(object.getVelocity().getX())) {
                    setPosition(getPosition().setX(getPosition().getX() - collideDist));
                    setVelocity(getVelocity().setX(0));
                }
                break;
            
            case BACK:
                if (getVelocity().getZ() < 0 && Math.abs(getVelocity().getZ()) >= Math.abs(object.getVelocity().getZ())) {
                    setPosition(getPosition().setZ(getPosition().getZ() + collideDist));
                    setVelocity(getVelocity().setZ(0));
                }
                break;
            
            case FRONT:
                if (getVelocity().getZ() > 0 && Math.abs(getVelocity().getZ()) >= Math.abs(object.getVelocity().getZ())) {
                    setPosition(getPosition().setZ(getPosition().getZ() - collideDist));
                    setVelocity(getVelocity().setZ(0));
                }
                break;
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
