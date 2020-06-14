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
     * sides the object is colliding with
     */
    private final Set<Side> sides;
    
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
        sides = new HashSet<>();
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
        sides = new HashSet<>(collision.getCollidingSides());
        allColliding = new HashSet<>(collision.getCollidingObjects());
        allOverlapping = new HashSet<>(collision.getOverlappingObjects());
    }
    
    /**
     * check for collisions and update position based on collisions
     */
    @Override
    public void tick() {
        super.tick();
        checkCollisions();
    }
    
    /**
     * check if an object has collided with another object
     */
    private void checkCollisions() {
        colliding = false;
        overlapping = false;
        sides.clear();
        allColliding.clear();
        allOverlapping.clear();
        
        for (Collision object : scene.getObjects()) { //loop through all objects in scene
            
            if (object == this) continue; //ignore self
            
            if (overlaps(object)) { //check for an overlap
                
                if (object.isCollidable() && collidable) { //if this and other object can collide
                    
                    //do the collision calculations
                    doCollision(object);
                    
                } else {
                    overlapping = true;
                    allOverlapping.add(object); //set overlapping if can't collide
                }
            }
        }
    }
    
    /**
     * fix the position of this object
     *
     * @param object object colliding with this object
     */
    private void doCollision(Collision object) {
        colliding = true;
        allColliding.add(object);
        //set object to colliding
        
        double left = Math.abs(getMinimum().getX() - object.getMaximum().getX());
        double right = Math.abs(getMaximum().getX() - object.getMinimum().getX());
        double bottom = Math.abs(getMinimum().getY() - object.getMaximum().getY());
        double top = Math.abs(getMaximum().getY() - object.getMinimum().getY());
        double back = Math.abs(getMinimum().getZ() - object.getMaximum().getZ());
        double front = Math.abs(getMaximum().getZ() - object.getMinimum().getZ());
        //get overlap distances
        
        double minx = left <= right ? left : right;
        double miny = top <= bottom ? top : bottom;
        double minz = front <= back ? front : back;
        double minxy = minx <= miny ? minx : miny;
        double min = minxy <= minz ? minxy : minz;
        //find min overlap
    
        Side side = Side.NONE;
    
        if (min == left) {
            side = Side.LEFT;
        } else if (min == right) {
            side = Side.RIGHT;
        } else if (min == top) {
            side = Side.TOP;
        } else if (min == bottom) {
            side = Side.BOTTOM;
        } else if (min == back) {
            side = Side.BACK;
        } else if (min == front) {
            side = Side.FRONT;
        }
        //use min overlap to determine colliding side
    
        sides.add(side);
        //add to list of colliding sides
        
        switch (side) {
            case BOTTOM:
                // check if this object is moving in the direction of the collising side
                if (getVelocity().getY() < 0) {
                    // check if the object is faster
                    if (Math.abs(getVelocity().getY()) >= Math.abs(object.getVelocity().getY())) {
                        setPosition(getPosition().setY(getPosition().getY() + min));
                        //fix object position so it is not overlapping
                        setVelocity(getVelocity().setY(0));
                        //set object velocity to 0 in the same direction
                    } else {
                        setPosition(getPosition().setY(getPosition().getY() - getVelocity().getY()));
                        // if not the faster object, cancel its velocity to prevent drifting
                    }
                }
                break;
            
            case TOP:
                if (getVelocity().getY() > 0) {
                    if (Math.abs(getVelocity().getY()) >= Math.abs(object.getVelocity().getY())) {
                        setPosition(getPosition().setY(getPosition().getY() - min));
                        setVelocity(getVelocity().setY(0));
                    } else {
                        setPosition(getPosition().setY(getPosition().getY() - getVelocity().getY()));
                    }
                }
                break;
            
            case LEFT:
                if (getVelocity().getX() < 0) {
                    if (Math.abs(getVelocity().getX()) >= Math.abs(object.getVelocity().getX())) {
                        setPosition(getPosition().setX(getPosition().getX() + min));
                        setVelocity(getVelocity().setX(0));
                    } else {
                        setPosition(getPosition().setX(getPosition().getX() - getVelocity().getX()));
                    }
                }
                break;
            
            case RIGHT:
                if (getVelocity().getX() > 0) {
                    if (Math.abs(getVelocity().getX()) >= Math.abs(object.getVelocity().getX())) {
                        setPosition(getPosition().setX(getPosition().getX() - min));
                        setVelocity(getVelocity().setX(0));
                    } else {
                        setPosition(getPosition().setX(getPosition().getX() - getVelocity().getX()));
                    }
                }
                break;
            
            case BACK:
                if (getVelocity().getZ() < 0) {
                    if (Math.abs(getVelocity().getZ()) >= Math.abs(object.getVelocity().getZ())) {
                        setPosition(getPosition().setZ(getPosition().getZ() + min));
                        setVelocity(getVelocity().setZ(0));
                    } else {
                        setPosition(getPosition().setZ(getPosition().getZ() - getVelocity().getZ()));
                    }
                }
                break;
            
            case FRONT:
                if (getVelocity().getZ() > 0) {
                    if (Math.abs(getVelocity().getZ()) >= Math.abs(object.getVelocity().getZ())) {
                        setPosition(getPosition().setZ(getPosition().getZ() - min));
                        setVelocity(getVelocity().setZ(0));
                    } else {
                        setPosition(getPosition().setZ(getPosition().getZ() - getVelocity().getZ()));
                    }
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
     * get the sides the object is colliding with
     *
     * @return set of sides the object is colliding on
     */
    public Set<Side> getCollidingSides() {
        return sides;
    }
}
