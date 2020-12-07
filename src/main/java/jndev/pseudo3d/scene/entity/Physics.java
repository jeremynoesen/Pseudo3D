package jndev.pseudo3d.scene.entity;

import jndev.pseudo3d.scene.Scene;
import jndev.pseudo3d.scene.util.Box;
import jndev.pseudo3d.scene.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * axis-aligned bounding box entity physics
 *
 * @author JNDev (Jeremaster101)
 */
public abstract class Physics {
    
    /**
     * gravity applied to the entity (pixels / tick ^ 2))
     */
    private Vector gravity;
    
    /**
     * position of entity (pixels)
     */
    private Vector position;
    
    /**
     * velocity of entity, or rate of change of position (pixels / tick)
     */
    private Vector velocity;
    
    /**
     * acceleration of entity, or rate of change of velocity (pixels / tick ^ 2)
     */
    private Vector acceleration;
    
    /**
     * +/- terminal velocity of gravity acceleration of entity (pixels / tick)
     */
    private Vector terminalVelocity;
    
    /**
     * +/- drag applied to the entity, used to slow down per direction. for simplicity, this is an acceleration vector
     * (pixels / tick ^ 2)
     */
    private Vector drag;
    
    /**
     * +/- friction applied to colliding entities. for simplicity, this is an acceleration vector (pixels / tick ^ 2)
     */
    private Vector friction;
    
    /**
     * scene the entity is colliding in
     */
    private Scene scene;
    
    /**
     * if an entity can collide with others
     */
    private boolean collidable;
    
    /**
     * list of entities this one is colliding with per side
     */
    private final HashMap<Box.Side, ArrayList<Physics>> collidingObjects;
    
    /**
     * entity's collision status
     */
    private boolean colliding;
    
    /**
     * entity's overlapping status
     */
    private boolean overlapping;
    
    /**
     * whether this entity can move or not and feel motion of other entitys. this is best used for ground entitys.
     * setting this to false will disallow this entity from having any updates to motion or collisions, which can
     * prevent you from getting the data from its collisions. these entities can remain collidable.
     */
    private boolean kinematic;
    
    /**
     * whether this entity can be pushed by other entities
     */
    private boolean pushable;
    
    /**
     * mass of an entity used for calculations, such as momentum conservation
     */
    private float mass;
    
    /**
     * bounding box for collisions
     */
    private Box box;
    
    /**
     * create a new aabb entity with default values
     */
    public Physics() {
        gravity = new Vector(0, -0.1f, 0);
        position = new Vector();
        velocity = new Vector();
        acceleration = new Vector();
        terminalVelocity = new Vector(10, 10, 10);
        drag = new Vector(0.0000001f, 0.0000001f, 0.0000001f);
        friction = new Vector(0.05f, 0.05f, 0.05f);
        scene = null;
        collidable = true;
        colliding = false;
        overlapping = false;
        kinematic = true;
        pushable = true;
        mass = 1.0f;
        box = new Box();
        collidingObjects = new HashMap<>();
        for (Box.Side s : Box.Side.values()) collidingObjects.put(s, new ArrayList<>());
    }
    
    /**
     * copy constructor for aabb entities
     *
     * @param physics aabb entity to copy
     */
    public Physics(Physics physics) {
        gravity = physics.gravity;
        position = physics.position;
        velocity = physics.velocity;
        terminalVelocity = physics.terminalVelocity;
        acceleration = physics.acceleration;
        drag = physics.drag;
        friction = physics.friction;
        scene = physics.scene;
        collidable = physics.collidable;
        colliding = physics.colliding;
        overlapping = physics.overlapping;
        box = new Box(physics.box);
        mass = physics.mass;
        kinematic = physics.kinematic;
        pushable = physics.pushable;
        collidingObjects = new HashMap<>();
        for (Box.Side s : Box.Side.values()) collidingObjects.put(s, new ArrayList<>());
    }
    
    /**
     * update the motion of the entity in 3D space using jerk, acceleration, velocity, and position
     */
    public void tickMotion() {
        float ax = acceleration.getX() + gravity.getX(), ay = acceleration.getY() + gravity.getY(),
                az = acceleration.getZ() + gravity.getZ();
        float vx = velocity.getX(), vy = velocity.getY(), vz = velocity.getZ();
        
        if (vx > -terminalVelocity.getX() && ax < 0)
            vx = Math.max(vx + ax, -terminalVelocity.getX());
        else if (vx < terminalVelocity.getX() && ax > 0)
            vx = Math.min(vx + ax, terminalVelocity.getX());
        
        if (vy > -terminalVelocity.getY() && ay < 0)
            vy = Math.max(vy + ay, -terminalVelocity.getY());
        else if (vy < terminalVelocity.getY() && ay > 0)
            vy = Math.min(vy + ay, terminalVelocity.getY());
        
        if (vz > -terminalVelocity.getZ() && az < 0)
            vz = Math.max(vz + az, -terminalVelocity.getZ());
        else if (vz < terminalVelocity.getZ() && az > 0)
            vz = Math.min(vz + az, terminalVelocity.getZ());
        //apply acceleration and gravity if not exceeding terminal velocity
        
        float fx = 0, fy = 0, fz = 0;
        if (colliding) {
            int xCount = 0, yCount = 0, zCount = 0;
            for (Box.Side side : Box.Side.values()) {
                for (Physics physics : collidingObjects.get(side)) {
                    if ((side == Box.Side.LEFT && vx < 0) || (side == Box.Side.RIGHT && vx > 0)) {
                        //check if colliding and moving towards a side
                        if (physics.pushable && physics.kinematic) {
                            float sum = mass + physics.mass;
                            float diff = mass - physics.mass;
                            float v1 = vx;
                            float v2 = physics.velocity.getX();
                            vx = ((diff / sum) * v1) + ((2 * physics.mass / sum) * v2);
                            physics.velocity = physics.velocity.setX(((-diff / sum) * v2) + ((2 * mass / sum) * v1));
                        } else vx = 0;
                        //calculate conservation of momentum only if object is pushable and kinematic
                        fy += physics.friction.getY();
                        yCount++;
                        fz += physics.friction.getZ();
                        zCount++;
                        //sum frictions in other axes
                    } else if ((side == Box.Side.BOTTOM && vy < 0) || (side == Box.Side.TOP && vy > 0)) {
                        if (physics.pushable && physics.kinematic) {
                            float sum = mass + physics.mass;
                            float diff = mass - physics.mass;
                            float v1 = vy;
                            float v2 = physics.velocity.getY();
                            vy = ((diff / sum) * v1) + ((2 * physics.mass / sum) * v2);
                            physics.velocity = physics.velocity.setY(((-diff / sum) * v2) + ((2 * mass / sum) * v1));
                        } else vy = 0;
                        fx += physics.friction.getX();
                        xCount++;
                        fz += physics.friction.getZ();
                        zCount++;
                    } else if ((side == Box.Side.BACK && vz < 0) || (side == Box.Side.FRONT && vz > 0)) {
                        if (physics.pushable && physics.kinematic) {
                            float sum = mass + physics.mass;
                            float diff = mass - physics.mass;
                            float v1 = vz;
                            float v2 = physics.velocity.getZ();
                            vz = ((diff / sum) * v1) + ((2 * physics.mass / sum) * v2);
                            physics.velocity = physics.velocity.setZ(((-diff / sum) * v2) + ((2 * mass / sum) * v1));
                        } else vz = 0;
                        fx += physics.friction.getX();
                        xCount++;
                        fy += physics.friction.getY();
                        yCount++;
                    }
                }
            }
            //get sum of frictions for each axis, as well as apply conservation of momentum
            
            if (xCount > 0) fx = (fx + friction.getX()) / (xCount + 1);
            if (yCount > 0) fy = (fy + friction.getY()) / (yCount + 1);
            if (zCount > 0) fz = (fz + friction.getZ()) / (zCount + 1);
            //calculate average friction per axis that has friction applied
        }
        //apply friction from colliding entities
        
        if (vx < 0) vx = Math.min(vx + (drag.getX() * box.getSurfaceArea()) + (fx * mass), 0);
        else if (vx > 0) vx = Math.max(vx - (drag.getX() * box.getSurfaceArea()) - (fx * mass), 0);
        if (vy < 0) vy = Math.min(vy + (drag.getY() * box.getSurfaceArea()) + (fy * mass), 0);
        else if (vy > 0) vy = Math.max(vy - (drag.getY() * box.getSurfaceArea()) - (fy * mass), 0);
        if (vz < 0) vz = Math.min(vz + (drag.getZ() * box.getSurfaceArea()) + (fz * mass), 0);
        else if (vz > 0) vz = Math.max(vz - (drag.getZ() * box.getSurfaceArea()) - (fz * mass), 0);
        //modify velocity based on friction and mass, and drag and surface area
        
        velocity = new Vector(vx, vy, vz);
        //set new velocity
        
        setPosition(position.add(velocity));
        //update position based on velocity
    }
    
    /**
     * check if a entity has collided with this entity
     */
    public void tickCollisions() {
        colliding = false;
        overlapping = false;
        collidingObjects.values().forEach(ArrayList::clear);
        //reset all collision data
        
        for (Entity entity : scene.getEntities()) {
            //loop through all renderable entitys in scene
            
            if (entity != this) {
                //check that this is not itself
                
                if (box.overlaps(entity.getBoundingBox())) {
                    //check for an overlap
                    if (entity.isCollidable() && collidable) {
                        //if this and other entity can collide
                        collideWith(entity);
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
     * fix the position of this entity to make a collision occur
     *
     * @param physics entity colliding with this entity
     */
    private void collideWith(Physics physics) {
        float[] overlaps = new float[6];
        overlaps[0] = Math.abs(box.getMinimum().getX() - physics.getBoundingBox().getMaximum().getX()); //left
        overlaps[1] = Math.abs(box.getMaximum().getX() - physics.getBoundingBox().getMinimum().getX()); //right
        overlaps[2] = Math.abs(box.getMinimum().getY() - physics.getBoundingBox().getMaximum().getY()); //bottom
        overlaps[3] = Math.abs(box.getMaximum().getY() - physics.getBoundingBox().getMinimum().getY()); //top
        overlaps[4] = Math.abs(box.getMinimum().getZ() - physics.getBoundingBox().getMaximum().getZ()); //back
        overlaps[5] = Math.abs(box.getMaximum().getZ() - physics.getBoundingBox().getMinimum().getZ()); //front
        //get overlap distances
        
        byte zeros = 0;
        byte axis = 1;
        byte dir = -1;
        float distance = overlaps[0];
        for (int i = 0; i < 6; i++) {
            if (overlaps[i] < distance) {
                distance = overlaps[i];
                dir = (byte) -Math.pow(-1, i);
                if (i <= 1) {
                    axis = 1;
                } else if (i <= 3) {
                    axis = 2;
                } else {
                    axis = 3;
                }
            }
            //find min overlap, direction, and axis of collision
            
            if (overlaps[i] == 0) zeros++;
            //check for 0 distance overlaps
        }
        
        if (zeros > 1) return;
        //if entity has more than one 0 overlaps, it is technically not touching, so stop collision
        
        colliding = true;
        //set entity to colliding
        
        if (axis == 1) {
            // check if collision is on this axis (1 = x, 2 = y, 3 = z)
            if (velocity.getX() * dir > 0) {
                // check if entity is moving in proper direction on the axis
                distance *= Math.abs(velocity.getX()) / (Math.abs(velocity.getX()) + Math.abs(physics.velocity.getX()));
                // scale distance based on entity velocities to improve collision accuracy
                setPosition(position.setX(position.getX() - (distance * dir)));
                // fix entity position so it is not overlapping
            }
            collidingObjects.get(dir == -1 ? Box.Side.LEFT : Box.Side.RIGHT).add(physics);
            //add to colliding entities for the colliding side
        } else if (axis == 2) {
            if (velocity.getY() * dir > 0) {
                distance *= Math.abs(velocity.getY()) / (Math.abs(velocity.getY()) + Math.abs(physics.velocity.getY()));
                setPosition(position.setY(position.getY() - (distance * dir)));
            }
            collidingObjects.get(dir == -1 ? Box.Side.BOTTOM : Box.Side.TOP).add(physics);
        } else {
            if (velocity.getZ() * dir > 0) {
                distance *= Math.abs(velocity.getZ()) / (Math.abs(velocity.getZ()) + Math.abs(physics.velocity.getZ()));
                setPosition(position.setZ(position.getZ() - (distance * dir)));
            }
            collidingObjects.get(dir == -1 ? Box.Side.BACK : Box.Side.FRONT).add(physics);
        }
    }
    
    /**
     * get the position vector of the entity
     *
     * @return position vector of entity
     */
    public Vector getPosition() {
        return position;
    }
    
    /**
     * set the position of the entity
     *
     * @param position position vector
     */
    public void setPosition(Vector position) {
        this.position = position;
        box.setPosition(position);
    }
    
    /**
     * get the velocity vector of the entity
     *
     * @return velocity vector of an entity
     */
    public Vector getVelocity() {
        return velocity;
    }
    
    /**
     * set the velocity of the entity
     *
     * @param velocity velocity vector
     */
    public void setVelocity(Vector velocity) {
        this.velocity = velocity;
    }
    
    /**
     * get the acceleration vector of the entity
     *
     * @return acceleration vector of an entity
     */
    public Vector getAcceleration() {
        return acceleration;
    }
    
    /**
     * set the acceleration of the entity
     *
     * @param acceleration acceleration vector
     */
    public void setAcceleration(Vector acceleration) {
        this.acceleration = acceleration;
    }
    
    /**
     * get the gravity applied to the entity
     *
     * @return gravity vector
     */
    public Vector getGravity() {
        return gravity;
    }
    
    /**
     * set the gravity applied to the entity
     *
     * @param gravity gravity vector
     */
    public void setGravity(Vector gravity) {
        this.gravity = gravity;
    }
    
    /**
     * get terminal velocity for this entity
     *
     * @return terminal velocity
     */
    public Vector getTerminalVelocity() {
        return terminalVelocity;
    }
    
    /**
     * set the terminal velocity for this entity
     *
     * @param terminalVelocity terminal velocity
     */
    public void setTerminalVelocity(Vector terminalVelocity) {
        this.terminalVelocity = terminalVelocity;
    }
    
    /**
     * get the drag of the entity
     *
     * @return drag vector of an entity
     */
    public Vector getDrag() {
        return drag;
    }
    
    /**
     * set the drag for the entity
     *
     * @param drag drag of entity
     */
    public void setDrag(Vector drag) {
        this.drag = drag;
    }
    
    /**
     * get the friction of the entity
     *
     * @return friction vector
     */
    public Vector getFriction() {
        return friction;
    }
    
    /**
     * set the friction of the entity
     *
     * @param friction friction vector
     */
    public void setFriction(Vector friction) {
        this.friction = friction;
    }
    
    /**
     * set the scene this entity is in
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
     * check if an entity can be collided with
     *
     * @return true if collidable
     */
    public boolean isCollidable() {
        return collidable;
    }
    
    /**
     * enable or disable collisions for the entity
     *
     * @param collidable true to allow collisions
     */
    public void setCollidable(boolean collidable) {
        this.collidable = collidable;
    }
    
    /**
     * check if the entity is currently colliding with another entity
     *
     * @return true if the entity is colliding with another entity
     */
    public boolean isColliding() {
        return colliding;
    }
    
    /**
     * check if an entity collides with this entity
     *
     * @param physics entity to check if colliding with
     * @return true if this entity collides with the other entity
     */
    public boolean collidesWith(Physics physics) {
        
        for (ArrayList<Physics> list : collidingObjects.values()) {
            if (list.contains(physics)) return true;
        }
        
        return false;
    }
    
    /**
     * check if this entity is colliding on the specified side
     *
     * @param side side of the entity
     * @return true if the entity is colliding on the side
     */
    public boolean collidesOn(Box.Side side) {
        return !collidingObjects.get(side).isEmpty();
    }
    
    /**
     * check if an entity collides with this one on a specific side
     *
     * @param physics object to check if colliding with
     * @param side    side of object
     * @return true if the object is colliding with the other object on the soecified side
     */
    public boolean collidesWithOn(Physics physics, Box.Side side) {
        return collidingObjects.get(side).contains(physics);
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
     * check if the object is kinematic
     *
     * @return true if object is kinematic
     */
    public boolean isKinematic() {
        return kinematic;
    }
    
    /**
     * set an object to be kinematic or not
     *
     * @param kinematic true to allow object motion self collision checks
     */
    public void setKinematic(boolean kinematic) {
        this.kinematic = kinematic;
    }
    
    /**
     * check if the object is pushable
     *
     * @return true if an object is pushable
     */
    public boolean isPushable() {
        return pushable;
    }
    
    /**
     * set the pushablility status of the object
     *
     * @param pushable true to allow pushing
     */
    public void setPushable(boolean pushable) {
        this.pushable = pushable;
    }
    
    /**
     * get the mass of the object
     *
     * @return mass of the object
     */
    public float getMass() {
        return mass;
    }
    
    /**
     * set the mass of the object
     *
     * @param mass new mass for object
     */
    public void setMass(float mass) {
        this.mass = mass;
    }
    
    /**
     * check if another set of aabbphysics data is equal to this one
     *
     * @param o object to check for equality
     * @return true if aabbphysics data is equivalent to this
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Physics that = (Physics) o;
        return collidable == that.collidable &&
                colliding == that.colliding &&
                overlapping == that.overlapping &&
                kinematic == that.kinematic &&
                Objects.equals(gravity, that.gravity) &&
                Objects.equals(position, that.position) &&
                Objects.equals(velocity, that.velocity) &&
                Objects.equals(acceleration, that.acceleration) &&
                Objects.equals(drag, that.drag) &&
                Objects.equals(friction, that.friction) &&
                Objects.equals(scene, that.scene) &&
                Objects.equals(collidingObjects, that.collidingObjects) &&
                Objects.equals(box, that.box) &&
                Objects.equals(terminalVelocity, that.terminalVelocity);
    }
}
