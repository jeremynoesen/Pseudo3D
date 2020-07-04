package jndev.pseudo3d.physics;

import jndev.pseudo3d.scene.Renderable;
import jndev.pseudo3d.scene.Scene;
import jndev.pseudo3d.util.Box;
import jndev.pseudo3d.util.Side;
import jndev.pseudo3d.util.Vector;

import java.util.*;

/**
 * axis-aligned bounding box object physics
 *
 * @author JNDev (Jeremaster101)
 */
public abstract class AABBPhysics {
    
    /**
     * gravity constant used for physics simulation. (pixels / tick ^ 2))
     */
    private final double GRAVITY = 0.0981;
    
    /**
     * position of object (pixels)
     */
    private Vector position;
    
    /**
     * velocity of object, or rate of change of position (pixels / tick)
     */
    private Vector velocity;
    
    /**
     * acceleration of object, or rate of change of velocity (pixels / tick ^ 2)
     */
    private Vector acceleration;
    
    /**
     * jerk of object, or rate of change of acceleration (pixels / tick ^ 3)
     */
    private Vector jerk;
    
    /**
     * vertical terminal velocity of object (pixels / tick)
     */
    private double terminalVelocity;
    
    /**
     * drag applied to the object, used to slow down per direction. for simplicity, this is a negative acceleration
     * vector (pixels / tick ^ 2)
     */
    private Vector drag;
    
    /**
     * friction applied to colliding objects. for simplicity, this is a negative acceleration vector (pixels / tick
     * ^ 2)
     */
    private Vector friction;
    
    /**
     * value used to scale gravity constant
     */
    private double gravityScale;
    
    /**
     * scene the object is colliding in
     */
    private Scene scene;
    
    /**
     * if an object can collide with others
     */
    private boolean collidable;
    
    /**
     * list of objects this one is colliding with per side
     */
    private final HashMap<Side, ArrayList<AABBPhysics>> collidingObjects;
    
    /**
     * object's collision status
     */
    private boolean colliding;
    
    /**
     * object's overlapping status
     */
    private boolean overlapping;
    
    /**
     * bounding box for collisions
     */
    private Box box;
    
    /**
     * create a new aabb object with default values
     */
    public AABBPhysics() {
        position = new Vector();
        velocity = new Vector();
        acceleration = new Vector();
        jerk = new Vector();
        terminalVelocity = 10;
        drag = new Vector(0.005, 0.005, 0.005);
        friction = new Vector(0.05, 0.05, 0.05);
        gravityScale = 1.0;
        scene = null;
        collidable = true;
        colliding = false;
        overlapping = false;
        box = new Box();
        collidingObjects = new HashMap<>();
        for (Side s : Side.values()) collidingObjects.put(s, new ArrayList<>());
    }
    
    /**
     * copy constructor for aabb objects
     *
     * @param aabbPhysics aabb object to copy
     */
    public AABBPhysics(AABBPhysics aabbPhysics) {
        position = aabbPhysics.position;
        velocity = aabbPhysics.velocity;
        terminalVelocity = aabbPhysics.terminalVelocity;
        acceleration = aabbPhysics.acceleration;
        jerk = aabbPhysics.jerk;
        drag = aabbPhysics.drag;
        friction = aabbPhysics.friction;
        gravityScale = aabbPhysics.gravityScale;
        scene = aabbPhysics.scene;
        collidable = aabbPhysics.collidable;
        colliding = aabbPhysics.colliding;
        overlapping = aabbPhysics.overlapping;
        box = new Box(aabbPhysics.box);
        collidingObjects = new HashMap<>();
        for (Side s : Side.values()) collidingObjects.put(s, new ArrayList<>());
    }
    
    /**
     * calculates the next frame of motion in the x, y, and z axes
     */
    public void tick() {
        calculateMotion();
        checkCollisions();
    }
    
    /**
     * update the motion of the object in 3D space using jerk, acceleration, velocity, and position
     */
    private void calculateMotion() {
        acceleration = new Vector(acceleration.getX() + jerk.getX(),
                acceleration.getY() + jerk.getY(),
                acceleration.getZ() + jerk.getZ());
        //update acceleration based on jerk
        
        double vx = velocity.getX() + acceleration.getX();
        double vy = velocity.getY() + acceleration.getY() - (GRAVITY * gravityScale - drag.getY());
        double vz = velocity.getZ() + acceleration.getZ();
        //update velocity based on acceleration and gravity
        
        double fx = 0;
        double fy = 0;
        double fz = 0;
        
        //get friction to apply
        if((collidesOn(Side.LEFT) && vx < 0) || (collidesOn(Side.RIGHT) && vx > 0)) {
            //check if colliding and moving towards side
            
            collidingObjects.get(Side.LEFT).sort((o1, o2) -> (int) (o2.getFriction().getY() - o1.getFriction().getY()));
            collidingObjects.get(Side.RIGHT).sort((o1, o2) -> (int) (o2.getFriction().getY() - o1.getFriction().getY()));
            //sort objects per side by friction for specific axis
            
            double fyl = !collidingObjects.get(Side.LEFT).isEmpty() ? collidingObjects.get(Side.LEFT).get(0).getFriction().getY() : 0;
            double fyr = !collidingObjects.get(Side.RIGHT).isEmpty() ? collidingObjects.get(Side.RIGHT).get(0).getFriction().getY() : 0;
            fy = Math.max(fy, Math.max(fyl, fyr));
            //get max friction
            
            collidingObjects.get(Side.LEFT).sort((o1, o2) -> (int) (o2.getFriction().getZ() - o1.getFriction().getZ()));
            collidingObjects.get(Side.RIGHT).sort((o1, o2) -> (int) (o2.getFriction().getZ() - o1.getFriction().getZ()));
    
            double fzl = !collidingObjects.get(Side.LEFT).isEmpty() ? collidingObjects.get(Side.LEFT).get(0).getFriction().getZ() : 0;
            double fzr = !collidingObjects.get(Side.RIGHT).isEmpty() ? collidingObjects.get(Side.RIGHT).get(0).getFriction().getZ() : 0;
            fz = Math.max(fz, Math.max(fzl, fzr));
        }
    
        if((collidesOn(Side.BOTTOM) && vy < 0) || (collidesOn(Side.TOP) && vy > 0)) {
            collidingObjects.get(Side.BOTTOM).sort((o1, o2) -> (int) (o2.getFriction().getX() - o1.getFriction().getX()));
            collidingObjects.get(Side.TOP).sort((o1, o2) -> (int) (o2.getFriction().getX() - o1.getFriction().getX()));
    
            double fxb = !collidingObjects.get(Side.BOTTOM).isEmpty() ? collidingObjects.get(Side.BOTTOM).get(0).getFriction().getX() : 0;
            double fxt = !collidingObjects.get(Side.TOP).isEmpty() ? collidingObjects.get(Side.TOP).get(0).getFriction().getX() : 0;
            fx = Math.max(fx, Math.max(fxb, fxt));
        
            collidingObjects.get(Side.BOTTOM).sort((o1, o2) -> (int) (o2.getFriction().getZ() - o1.getFriction().getZ()));
            collidingObjects.get(Side.TOP).sort((o1, o2) -> (int) (o2.getFriction().getZ() - o1.getFriction().getZ()));
    
            double fzb = !collidingObjects.get(Side.BOTTOM).isEmpty() ? collidingObjects.get(Side.BOTTOM).get(0).getFriction().getZ() : 0;
            double fzt = !collidingObjects.get(Side.TOP).isEmpty() ? collidingObjects.get(Side.TOP).get(0).getFriction().getZ() : 0;
            fz = Math.max(fz, Math.max(fzb, fzt));
        }
    
        if((collidesOn(Side.BACK) && vz < 0) || (collidesOn(Side.FRONT) && vz > 0)) {
            collidingObjects.get(Side.BACK).sort((o1, o2) -> (int) (o2.getFriction().getX() - o1.getFriction().getX()));
            collidingObjects.get(Side.FRONT).sort((o1, o2) -> (int) (o2.getFriction().getX() - o1.getFriction().getX()));
        
            double fxb = !collidingObjects.get(Side.BACK).isEmpty() ? collidingObjects.get(Side.BACK).get(0).getFriction().getX() : 0;
            double fxf = !collidingObjects.get(Side.FRONT).isEmpty() ? collidingObjects.get(Side.FRONT).get(0).getFriction().getX() : 0;
            fx = Math.max(fx, Math.max(fxb, fxf));
        
            collidingObjects.get(Side.BACK).sort((o1, o2) -> (int) (o2.getFriction().getY() - o1.getFriction().getY()));
            collidingObjects.get(Side.FRONT).sort((o1, o2) -> (int) (o2.getFriction().getY() - o1.getFriction().getY()));
        
            double fyb = !collidingObjects.get(Side.BACK).isEmpty() ? collidingObjects.get(Side.BACK).get(0).getFriction().getY() : 0;
            double fyf = !collidingObjects.get(Side.FRONT).isEmpty() ? collidingObjects.get(Side.FRONT).get(0).getFriction().getY() : 0;
            fy = Math.max(fy, Math.max(fyb, fyf));
        }
        
        if (vx < 0) vx = collidesOn(Side.LEFT) ? 0 : Math.min(vx + drag.getX() + fx, 0);
        if (vx > 0) vx = collidesOn(Side.RIGHT) ? 0 : Math.max(vx - drag.getX() - fx, 0);
        if (vy < 0) {
            if (vy < -terminalVelocity)
                vy = collidesOn(Side.BOTTOM) ? 0 : Math.min(vy + drag.getY() + fy, -terminalVelocity);
            else
                vy = collidesOn(Side.BOTTOM) ? 0 : vy + fy;
        }
        if (vy > 0) vy = collidesOn(Side.TOP) ? 0 : Math.max(vy - drag.getY() - fy, 0);
        if (vz < 0) vz = collidesOn(Side.BACK) ? 0 : Math.min(vz + drag.getZ() + fz, 0);
        if (vz > 0) vz = collidesOn(Side.FRONT) ? 0 : Math.max(vz - drag.getZ() - fz, 0);
        //modify velocity based on friction, drag, terminal velocity, and collision status
        
        velocity = new Vector(vx, vy, vz);
        //set new velocity
        
        position = new Vector(position.getX() + velocity.getX(),
                position.getY() + velocity.getY(),
                position.getZ() + velocity.getZ());
        //update position based on velocity
        
        box.setPosition(position);
        //update bounding box position
    }
    
    /**
     * check if a object has collided with this object
     */
    private void checkCollisions() {
        colliding = false;
        overlapping = false;
        for (ArrayList<AABBPhysics> list : collidingObjects.values()) {
            list.clear();
        }
        //reset all collision data
        
        for (int i = 0; i < scene.getObjects().size(); i++) {
            Renderable object = scene.getObjects().get(i);
            //loop through all renderable objects in scene
            
            if (object instanceof AABBPhysics) {
                AABBPhysics aabbPhysics = (AABBPhysics) object;
                //check for AABBPhysics objects
                
                if (aabbPhysics == this) continue;
                //ignore self
                
                if (box.overlaps(aabbPhysics.getBoundingBox())) {
                    //check for an overlap
                    if (aabbPhysics.isCollidable() && collidable) {
                        //if this and other object can collide
                        doCollision(aabbPhysics);
                        //do the collision calculations
                    } else {
                        overlapping = true;
                        //set overlapping
                    }
                }
            }
        }
    }
    
    /**
     * fix the position of this object to make a collision occur
     *
     * @param aabbPhysics object colliding with this object
     */
    private void doCollision(AABBPhysics aabbPhysics) {
        double[] overlaps = new double[6];
        overlaps[0] = Math.abs(box.getMinimum().getX() - aabbPhysics.getBoundingBox().getMaximum().getX()); //left
        overlaps[1] = Math.abs(box.getMaximum().getX() - aabbPhysics.getBoundingBox().getMinimum().getX()); //right
        overlaps[2] = Math.abs(box.getMinimum().getY() - aabbPhysics.getBoundingBox().getMaximum().getY()); //bottom
        overlaps[3] = Math.abs(box.getMaximum().getY() - aabbPhysics.getBoundingBox().getMinimum().getY()); //top
        overlaps[4] = Math.abs(box.getMinimum().getZ() - aabbPhysics.getBoundingBox().getMaximum().getZ()); //back
        overlaps[5] = Math.abs(box.getMaximum().getZ() - aabbPhysics.getBoundingBox().getMinimum().getZ()); //front
        //get overlap distances
        
        int zeros = 0;
        double distance = overlaps[0];
        for (int i = 0; i < 6; i++) {
            if (overlaps[i] < distance) distance = overlaps[i];
            if (Double.compare(overlaps[i], 0) == 0) zeros++;
        }
        //find min overlap and amount of 0 overlaps
        
        if (zeros > 1) return;
        //if object has more than 1 0 overlaps, it is technically not touching, so stop collision
        
        colliding = true;
        //set object to colliding
        
        if (distance == overlaps[0]) {
            // check if this object is moving in the direction of the collising side
            if (velocity.getX() < 0) {
                // check if the object is faster
                if (Math.abs(velocity.getX()) >= Math.abs(aabbPhysics.getVelocity().getX())) {
                    setPosition(position.setX(position.getX() + distance));
                    //fix object position so it is not overlapping
                } else {
                    setPosition(position.setX(position.getX() - velocity.getX()));
                    //if not the faster object, cancel its velocity to prevent drifting
                }
                velocity = velocity.setX(0);
                //set object velocity to 0 in the same direction
            }
            collidingObjects.get(Side.LEFT).add(aabbPhysics);
            //add to colliding objects for the colliding side
            
        } else if (distance == overlaps[1]) {
            if (velocity.getX() > 0) {
                if (Math.abs(velocity.getX()) >= Math.abs(aabbPhysics.getVelocity().getX())) {
                    setPosition(position.setX(position.getX() - distance));
                } else {
                    setPosition(position.setX(position.getX() - velocity.getX()));
                }
                velocity = velocity.setX(0);
            }
            collidingObjects.get(Side.RIGHT).add(aabbPhysics);
            
        } else if (distance == overlaps[2]) {
            if (velocity.getY() < 0) {
                if (Math.abs(velocity.getY()) >= Math.abs(aabbPhysics.getVelocity().getY())) {
                    setPosition(position.setY(position.getY() + distance));
                } else {
                    setPosition(position.setY(position.getY() - velocity.getY()));
                }
                velocity = velocity.setY(0);
            }
            collidingObjects.get(Side.BOTTOM).add(aabbPhysics);
            
        } else if (distance == overlaps[3]) {
            if (velocity.getY() > 0) {
                if (Math.abs(velocity.getY()) >= Math.abs(aabbPhysics.getVelocity().getY())) {
                    setPosition(position.setY(position.getY() - distance));
                } else {
                    setPosition(position.setY(position.getY() - velocity.getY()));
                }
                velocity = velocity.setY(0);
            }
            collidingObjects.get(Side.TOP).add(aabbPhysics);
            
        } else if (distance == overlaps[4]) {
            if (velocity.getZ() < 0) {
                if (Math.abs(velocity.getZ()) >= Math.abs(aabbPhysics.getVelocity().getZ())) {
                    setPosition(position.setZ(position.getZ() + distance));
                } else {
                    setPosition(position.setZ(position.getZ() - velocity.getZ()));
                }
                velocity = velocity.setZ(0);
            }
            collidingObjects.get(Side.BACK).add(aabbPhysics);
            
        } else if (distance == overlaps[5]) {
            if (velocity.getZ() > 0) {
                if (Math.abs(velocity.getZ()) >= Math.abs(aabbPhysics.getVelocity().getZ())) {
                    setPosition(position.setZ(position.getZ() - distance));
                } else {
                    setPosition(position.setZ(position.getZ() - velocity.getZ()));
                }
                velocity = velocity.setZ(0);
            }
            collidingObjects.get(Side.FRONT).add(aabbPhysics);
        }
    }
    
    /**
     * get the position vector of the object
     *
     * @return position vector of object
     */
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
        box.setPosition(position);
    }
    
    /**
     * get the velocity vector of the object
     *
     * @return velocity vector of an object
     */
    public Vector getVelocity() {
        return velocity;
    }
    
    /**
     * set the velocity of the object
     *
     * @param velocity velocity vector
     */
    public void setVelocity(Vector velocity) {
        this.velocity = velocity;
    }
    
    /**
     * get the acceleration vector of the object
     *
     * @return acceleration vector of an object
     */
    public Vector getAcceleration() {
        return acceleration.setY(acceleration.getY() - (GRAVITY * gravityScale));
    }
    
    /**
     * set the acceleration of the object
     *
     * @param acceleration acceleration vector
     */
    public void setAcceleration(Vector acceleration) {
        this.acceleration = acceleration;
    }
    
    /**
     * get the jerk vector of the object
     *
     * @return jerk of the object
     */
    public Vector getJerk() {
        return jerk;
    }
    
    /**
     * set the jerk of the object
     *
     * @param jerk jerk vector
     */
    public void setJerk(Vector jerk) {
        this.jerk = jerk;
    }
    
    /**
     * get the scale gravity. default is 1.0
     *
     * @return gravity scale
     */
    public double getGravityScale() {
        return gravityScale;
    }
    
    /**
     * set the gravity scale
     *
     * @param scale gravity scale
     */
    public void setGravityScale(double scale) {
        gravityScale = scale;
    }
    
    /**
     * set terminal velocity for this object
     *
     * @return terminal velocity
     */
    public double getTerminalVelocity() {
        return terminalVelocity;
    }
    
    /**
     * set the terminal velocity for this object
     *
     * @param terminalVelocity terminal velocity
     */
    public void setTerminalVelocity(double terminalVelocity) {
        this.terminalVelocity = Math.abs(terminalVelocity);
    }
    
    /**
     * get the drag of the object
     *
     * @return drag vector of an object
     */
    public Vector getDrag() {
        return drag;
    }
    
    /**
     * set the drag for the object
     *
     * @param drag drag of object
     */
    public void setDrag(Vector drag) {
        this.drag = drag;
    }
    
    /**
     * get the friction of the object
     *
     * @return friction vector
     */
    public Vector getFriction() {
        return friction;
    }
    
    /**
     * set the friction of the object
     *
     * @param friction friction vector
     */
    public void setFriction(Vector friction) {
        this.friction = friction;
    }
    
    /**
     * set the scene this object is in
     *
     * @param scene scene for collisions
     */
    public void setScene(Scene scene) {
        this.scene = scene;
    }
    
    /**
     * get the scene the collisions are set to occur in
     *
     * @return scene for collisions
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
     * @param collidable true to allow collisions
     */
    public void setCollidable(boolean collidable) {
        this.collidable = collidable;
    }
    
    /**
     * check if the object is currently colliding with another object
     *
     * @return true if the object is colliding with another object
     */
    public boolean isColliding() {
        return colliding;
    }
    
    /**
     * check if an= object collides with this object
     *
     * @param aabbPhysics object to check if colliding with
     * @return true if this object collides with the other object
     */
    public boolean collidesWith(AABBPhysics aabbPhysics) {
        
        for (ArrayList<AABBPhysics> list : collidingObjects.values()) {
            if (list.contains(aabbPhysics)) return true;
        }
        
        return false;
    }
    
    /**
     * check if this object is colliding on the specified side
     *
     * @param side side of the object
     * @return true if the object is colliding on the side
     */
    public boolean collidesOn(Side side) {
        return !collidingObjects.get(side).isEmpty();
    }
    
    /**
     * check if an object collides with this one on a specific side
     *
     * @param aabbPhysics object to check if colliding with
     * @param side        side of object
     * @return true if the object is colliding with the other object on the soecified side
     */
    public boolean collidesWithOn(AABBPhysics aabbPhysics, Side side) {
        return collidingObjects.get(side).contains(aabbPhysics);
    }
    
    /**
     * see if this object is overlapping any object
     *
     * @return true if overlapping
     */
    public boolean isOverlapping() {
        return overlapping;
    }
    
    /**
     * set the bounding box for this object
     *
     * @return object's bounding box
     */
    public Box getBoundingBox() {
        return box;
    }
    
    /**
     * set the object's bounding box centered at the object's current position
     *
     * @param box bounding box to set
     */
    public void setBoundingBox(Box box) {
        Box newBox = new Box(box);
        newBox.setPosition(position);
        this.box = newBox;
    }
    
    /**
     * check if another set of aabbphysics data is equal to this one
     *
     * @param o object to check for equality
     * @return aabbphysics data is equivalent to this
     */
    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AABBPhysics that = (AABBPhysics) o;
        return Double.compare(that.terminalVelocity, terminalVelocity) == 0 &&
                Double.compare(that.gravityScale, gravityScale) == 0 &&
                collidable == that.collidable &&
                colliding == that.colliding &&
                overlapping == that.overlapping &&
                Objects.equals(position, that.position) &&
                Objects.equals(velocity, that.velocity) &&
                Objects.equals(acceleration, that.acceleration) &&
                Objects.equals(jerk, that.jerk) &&
                Objects.equals(drag, that.drag) &&
                Objects.equals(friction, that.friction) &&
                Objects.equals(scene, that.scene) &&
                Objects.equals(collidingObjects, that.collidingObjects) &&
                Objects.equals(box, that.box);
    }
}
