package xyz.jeremynoesen.pseudo3d.scene.entity;

import xyz.jeremynoesen.pseudo3d.scene.util.Box;
import xyz.jeremynoesen.pseudo3d.scene.util.Vector;

import java.util.*;

/**
 * axis-aligned bounding box physics entity
 *
 * @author Jeremy Noesen
 */
public abstract class Physics extends Box {
    
    /**
     * entities this entity is in a scene with
     */
    private LinkedList<Physics> entities;
    
    /**
     * gravity applied to the entity (meters / second ^ 2)
     */
    private Vector gravity;
    
    /**
     * position of entity (meters)
     */
    private Vector position;
    
    /**
     * velocity of entity, or rate of change of position (meters / second)
     */
    private Vector velocity;
    
    /**
     * +/- terminal velocity of gravity acceleration of entity (meters / second)
     */
    private Vector terminalVelocity;
    
    /**
     * acceleration of entity, or rate of change of velocity (meters / second ^ 2)
     */
    private Vector acceleration;
    
    /**
     * coefficient of drag per axis
     */
    private Vector drag;
    
    /**
     * roughness of entity per axis, used for friction
     */
    private Vector roughness;
    
    /**
     * if an entity is solid, allowing collision
     */
    private boolean solid;
    
    /**
     * list of entities colliding with per side
     */
    private final HashMap<Side, HashSet<Physics>> collidingEntities;
    
    /**
     * list of entities overlapping this one
     */
    private final HashSet<Physics> overlappingEntities;
    
    /**
     * entity's collision status
     */
    private boolean colliding;
    
    /**
     * entity's overlapping status
     */
    private boolean overlapping;
    
    /**
     * whether this entity can have motion or not
     */
    private boolean kinematic;
    
    /**
     * whether this entity can be pushed by other entities
     */
    private boolean pushable[];
    
    /**
     * mass of an entity used for calculations, such as momentum conservation
     */
    private float mass;
    
    /**
     * whether the entity can update or not
     */
    private boolean updatable;
    
    /**
     * create a new aabb entity with default values
     */
    public Physics() {
        super();
        gravity = new Vector(0, -9.81f, 0);
        position = new Vector();
        velocity = new Vector();
        acceleration = new Vector();
        terminalVelocity = new Vector(20, 20, 20);
        drag = new Vector(0.5f, 0.5f, 0.5f);
        roughness = new Vector(5, 5, 5);
        entities = null;
        solid = true;
        colliding = false;
        overlapping = false;
        kinematic = true;
        pushable = new boolean[] {true, true, true};
        updatable = true;
        mass = 1;
        collidingEntities = new HashMap<>();
        overlappingEntities = new HashSet<>();
        for (Side s : Side.values()) collidingEntities.put(s, new HashSet<>());
    }
    
    /**
     * copy constructor for aabb entities
     *
     * @param physics aabb entity to copy
     */
    public Physics(Physics physics) {
        super(physics);
        gravity = physics.gravity;
        position = physics.position;
        velocity = physics.velocity;
        acceleration = physics.acceleration;
        terminalVelocity = physics.terminalVelocity;
        drag = physics.drag;
        roughness = physics.roughness;
        solid = physics.solid;
        colliding = physics.colliding;
        overlapping = physics.overlapping;
        mass = physics.mass;
        kinematic = physics.kinematic;
        pushable = physics.pushable;
        updatable = physics.updatable;
        collidingEntities = new HashMap<>();
        overlappingEntities = new HashSet<>(physics.overlappingEntities);
        entities = physics.entities;
        for (Side s : Side.values()) collidingEntities.put(s, new HashSet<>(physics.collidingEntities.get(s)));
    }
    
    /**
     * update the motion of the entity
     *
     * @param deltaTime time elapsed to use in calculation
     */
    public void tickMotion(float deltaTime) {
        if (!kinematic || !updatable) return;
        
        float ax = acceleration.getX() + gravity.getX(), ay = acceleration.getY() + gravity.getY(),
                az = acceleration.getZ() + gravity.getZ();
        
        float vx = velocity.getX(), vy = velocity.getY(), vz = velocity.getZ();
        
        if (vx > -terminalVelocity.getX() && ax < 0)
            vx = Math.max(vx + (ax * deltaTime), -terminalVelocity.getX());
        else if (vx < terminalVelocity.getX() && ax > 0)
            vx = Math.min(vx + (ax * deltaTime), terminalVelocity.getX());
        
        if (vy > -terminalVelocity.getY() && ay < 0)
            vy = Math.max(vy + (ay * deltaTime), -terminalVelocity.getY());
        else if (vy < terminalVelocity.getY() && ay > 0)
            vy = Math.min(vy + (ay * deltaTime), terminalVelocity.getY());
        
        if (vz > -terminalVelocity.getZ() && az < 0)
            vz = Math.max(vz + (az * deltaTime), -terminalVelocity.getZ());
        else if (vz < terminalVelocity.getZ() && az > 0)
            vz = Math.min(vz + (az * deltaTime), terminalVelocity.getZ());
        //apply acceleration and gravity if not exceeding terminal velocity
        
        float fx = 0, fy = 0, fz = 0;
        if (colliding) {
            int xCount = 0, yCount = 0, zCount = 0;
            for (Side side : Side.values()) {
                for (Physics physics : collidingEntities.get(side)) {
                    if ((side == Side.LEFT && vx < 0) || (side == Side.RIGHT && vx > 0)) {
                        //check if colliding and moving towards a side
                        fy += physics.roughness.getY() * Math.abs(vx);
                        yCount++;
                        fz += physics.roughness.getZ() * Math.abs(vx);
                        zCount++;
                        //sum frictions in other axes
                        if (physics.pushable[0] && physics.kinematic && physics.updatable) {
                            float sum = mass + physics.mass;
                            float diff = mass - physics.mass;
                            float v1 = vx;
                            float v2 = physics.velocity.getX();
                            vx = ((diff / sum) * v1) + ((2 * physics.mass / sum) * v2);
                            physics.velocity = physics.velocity.setX(((-diff / sum) * v2) + ((2 * mass / sum) * v1));
                        } else vx = 0;
                        //calculate conservation of momentum if entity is able to
                    } else if ((side == Side.BOTTOM && vy < 0) || (side == Side.TOP && vy > 0)) {
                        fx += physics.roughness.getX() * Math.abs(vy);
                        xCount++;
                        fz += physics.roughness.getZ() * Math.abs(vy);
                        zCount++;
                        if (physics.pushable[1] && physics.kinematic && physics.updatable) {
                            float sum = mass + physics.mass;
                            float diff = mass - physics.mass;
                            float v1 = vy;
                            float v2 = physics.velocity.getY();
                            vy = ((diff / sum) * v1) + ((2 * physics.mass / sum) * v2);
                            physics.velocity = physics.velocity.setY(((-diff / sum) * v2) + ((2 * mass / sum) * v1));
                        } else vy = 0;
                    } else if ((side == Side.BACK && vz < 0) || (side == Side.FRONT && vz > 0)) {
                        fx += physics.roughness.getX() * Math.abs(vz);
                        xCount++;
                        fy += physics.roughness.getY() * Math.abs(vz);
                        yCount++;
                        if (physics.pushable[2] && physics.kinematic && physics.updatable) {
                            float sum = mass + physics.mass;
                            float diff = mass - physics.mass;
                            float v1 = vz;
                            float v2 = physics.velocity.getZ();
                            vz = ((diff / sum) * v1) + ((2 * physics.mass / sum) * v2);
                            physics.velocity = physics.velocity.setZ(((-diff / sum) * v2) + ((2 * mass / sum) * v1));
                        } else vz = 0;
                    }
                }
            }
            //get sum of frictions for each axis, as well as apply conservation of momentum
            
            if (xCount > 0) fx = ((fx + roughness.getX()) / (xCount + 1)) * mass * deltaTime;
            if (yCount > 0) fy = ((fy + roughness.getY()) / (yCount + 1)) * mass * deltaTime;
            if (zCount > 0) fz = ((fz + roughness.getZ()) / (zCount + 1)) * mass * deltaTime;
            //calculate average friction per axis that has friction applied
        }
        //apply friction from colliding entities
        
        float dx = drag.getX() * getHeight() * getDepth() * deltaTime * Math.abs(vx);
        float dy = drag.getY() * getWidth() * getDepth() * deltaTime * Math.abs(vy);
        float dz = drag.getZ() * getHeight() * getWidth() * deltaTime * Math.abs(vz);
        ///calculate drag
        
        if (vx < 0) vx = Math.min(vx + dx + fx, 0);
        else if (vx > 0) vx = Math.max(vx - dx - fx, 0);
        if (vy < 0) vy = Math.min(vy + dy + fy, 0);
        else if (vy > 0) vy = Math.max(vy - dy - fy, 0);
        if (vz < 0) vz = Math.min(vz + dz + fz, 0);
        else if (vz > 0) vz = Math.max(vz - dz - fz, 0);
        //modify velocity based on friction and mass, and drag and surface area
        
        velocity = new Vector(vx, vy, vz);
        //set new velocity
        
        setPosition(position.add(velocity.multiply(deltaTime)));
        //update position based on velocity
    }
    
    /**
     * check if a entity has collided with this entity
     */
    public void tickCollisions() {
        if (!kinematic || !updatable || entities == null) return;
        
        colliding = false;
        overlapping = false;
        collidingEntities.values().forEach(HashSet::clear);
        overlappingEntities.clear();
        //reset all collision data
        
        for (Physics entity : entities) {
            //loop through all entities in scene
            if (entity != this && entity.updatable) {
                //check that this is not itself, or can't be checked at the moment
                if (super.overlaps(entity)) {
                    //check for an overlap
                    if (entity.isSolid() && solid) {
                        //if this and other entity can collide
                        collideWith(entity);
                        //do the collision calculations
                    } else {
                        overlapWith(entity);
                        //do overlap
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
        overlaps[0] = Math.abs(getMinimum().getX() - physics.getMaximum().getX()); //left
        overlaps[1] = Math.abs(getMaximum().getX() - physics.getMinimum().getX()); //right
        overlaps[2] = Math.abs(getMinimum().getY() - physics.getMaximum().getY()); //bottom
        overlaps[3] = Math.abs(getMaximum().getY() - physics.getMinimum().getY()); //top
        overlaps[4] = Math.abs(getMinimum().getZ() - physics.getMaximum().getZ()); //back
        overlaps[5] = Math.abs(getMaximum().getZ() - physics.getMinimum().getZ()); //front
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
                if (Math.signum(velocity.getX()) == -Math.signum(physics.velocity.getX()))
                    //check that the two entities are moving towards each other
                    distance *= velocity.getX() / (velocity.getX() - physics.velocity.getX());
                // scale distance based on entity velocities to improve collision accuracy
                setPosition(position.setX(position.getX() - (distance * dir)));
                // fix entity position so it is not overlapping
            }
            collidingEntities.get(dir == -1 ? Side.LEFT : Side.RIGHT).add(physics);
            //add to colliding entities for the colliding side
        } else if (axis == 2) {
            if (velocity.getY() * dir > 0) {
                if (Math.signum(velocity.getY()) == -Math.signum(physics.velocity.getY()))
                    distance *= velocity.getY() / (velocity.getY() - physics.velocity.getY());
                setPosition(position.setY(position.getY() - (distance * dir)));
            }
            collidingEntities.get(dir == -1 ? Side.BOTTOM : Side.TOP).add(physics);
        } else {
            if (velocity.getZ() * dir > 0) {
                if (Math.signum(velocity.getZ()) == -Math.signum(physics.velocity.getZ()))
                    distance *= velocity.getZ() / (velocity.getZ() - physics.velocity.getZ());
                setPosition(position.setZ(position.getZ() - (distance * dir)));
            }
            collidingEntities.get(dir == -1 ? Side.BACK : Side.FRONT).add(physics);
        }
    }
    
    /**
     * set this entity as overlapping another
     *
     * @param physics entity to overlap with
     */
    private void overlapWith(Physics physics) {
        overlapping = true;
        overlappingEntities.add(physics);
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
    public Physics setPosition(Vector position) {
        this.position = position;
        super.setPosition(position);
        return this;
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
    public Physics setVelocity(Vector velocity) {
        this.velocity = velocity;
        return this;
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
    public Physics setAcceleration(Vector acceleration) {
        this.acceleration = acceleration;
        return this;
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
    public Physics setGravity(Vector gravity) {
        this.gravity = gravity;
        return this;
    }
    
    /**
     * get terminal velocity for this entity
     *
     * @return terminal velocity vector
     */
    public Vector getTerminalVelocity() {
        return terminalVelocity;
    }
    
    /**
     * set the terminal velocity for this entity to limit maximum velocity due to accelerations
     *
     * @param terminalVelocity terminal velocity vector
     */
    public Physics setTerminalVelocity(Vector terminalVelocity) {
        this.terminalVelocity = terminalVelocity;
        return this;
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
    public Physics setDrag(Vector drag) {
        this.drag = drag;
        return this;
    }
    
    /**
     * get the roughness of the entity
     *
     * @return roughness vector
     */
    public Vector getRoughness() {
        return roughness;
    }
    
    /**
     * set the friction of the entity
     *
     * @param roughness friction vector
     */
    public Physics setRoughness(Vector roughness) {
        this.roughness = roughness;
        return this;
    }
    
    /**
     * check if an entity can be collided with
     *
     * @return true if collidable
     */
    public boolean isSolid() {
        return solid;
    }
    
    /**
     * enable or disable collisions for the entity
     *
     * @param solid true to allow collisions
     */
    public Physics setSolid(boolean solid) {
        this.solid = solid;
        return this;
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
        for (HashSet<Physics> list : collidingEntities.values()) {
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
    public boolean collidesOn(Side side) {
        return !collidingEntities.get(side).isEmpty();
    }
    
    /**
     * check if an entity collides with this one on a specific side
     *
     * @param physics entity to check if colliding with
     * @param side    side of entity
     * @return true if the entity is colliding with the other entity on the specified side
     */
    public boolean collidesWithOn(Physics physics, Side side) {
        return collidingEntities.get(side).contains(physics);
    }
    
    /**
     * get all entities colliding on the specified side
     *
     * @param side side to get colliding entities of
     * @return set of all entities colliding on the side
     */
    public HashSet<Physics> getCollidingEntities(Side side) {
        return collidingEntities.get(side);
    }
    
    /**
     * get a set of all entities colliding with this one
     *
     * @return all colliding entities
     */
    public HashSet<Physics> getCollidingEntities() {
        HashSet<Physics> allEntities = new HashSet<>();
        for (Side side : Side.values()) allEntities.addAll(collidingEntities.get(side));
        return allEntities;
    }
    
    /**
     * see if this entity is overlapping any entity
     *
     * @return true if overlapping
     */
    public boolean isOverlapping() {
        return overlapping;
    }
    
    /**
     * check if this entity overlaps another
     *
     * @param physics entity to check with
     * @return true if this entity overlaps the specified entity
     */
    public boolean overlaps(Physics physics) {
        return overlappingEntities.contains(physics);
    }
    
    /**
     * get the set of all entities overlapping this one
     *
     * @return set of overlapping entities
     */
    public HashSet<Physics> getOverlappingEntities() {
        return overlappingEntities;
    }
    
    /**
     * check if the entity is kinematic
     *
     * @return true if entity is kinematic
     */
    public boolean isKinematic() {
        return kinematic;
    }
    
    /**
     * set an entity to be kinematic or not
     *
     * @param kinematic true to allow entity motion
     */
    public Physics setKinematic(boolean kinematic) {
        this.kinematic = kinematic;
        return this;
    }
    
    /**
     * check if the entity is pushable on any axis
     *
     * @return true if an entity is pushable on any axis
     */
    public boolean isPushable() {
        return pushable[0] || pushable[1] || pushable[2];
    }
    
    /**
     * check if the entity is pushable on the x axis
     *
     * @return true if an entity is pushable on the x axis
     */
    public boolean isPushableX() {
        return pushable[0];
    }
    
    /**
     * check if the entity is pushable on the y axis
     *
     * @return true if an entity is pushable on the y axis
     */
    public boolean isPushableY() {
        return pushable[1];
    }
    
    /**
     * check if the entity is pushable on the z axis
     *
     * @return true if an entity is pushable on the z axis
     */
    public boolean isPushableZ() {
        return pushable[2];
    }
    
    /**
     * set whether the entity can be pushed or not per axis
     *
     * @param x true to allow pushing on the x axis
     * @param y true to allow pushing on the y axis
     * @param z true to allow pushing on the z axis
     */
    public Physics setPushable(boolean x, boolean y, boolean z) {
        pushable[0] = x;
        pushable[1] = y;
        pushable[2] = z;
        return this;
    }
    
    /**
     * get the mass of the entity
     *
     * @return mass of the entity
     */
    public float getMass() {
        return mass;
    }
    
    /**
     * set the mass of the entity
     *
     * @param mass new mass for entity
     */
    public Physics setMass(float mass) {
        this.mass = mass;
        return this;
    }
    
    /**
     * set the entities the object is in a scene with, only callable by parent class
     *
     * @param entities list of physics objects
     */
    @SuppressWarnings("unchecked")
    protected void setEntities(LinkedList<? extends Physics> entities) {
        this.entities = (LinkedList<Physics>) entities;
    }
    
    /**
     * set if the entity can update, called by parent class for special function
     *
     * @param updatable true to allow updating
     */
    protected void setUpdatable(boolean updatable) {
        this.updatable = updatable;
    }
    
    /**
     * check if the entity can update, called by parent class for special function
     *
     * @return true if entity can update
     */
    protected boolean isUpdatable() {
        return updatable;
    }
    
    /**
     * check if another set of physics data is equal to this one
     *
     * @param o object to check for equality
     * @return true if physics data is equivalent to this
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Physics physics = (Physics) o;
        return solid == physics.solid &&
                colliding == physics.colliding &&
                overlapping == physics.overlapping &&
                kinematic == physics.kinematic &&
                pushable == physics.pushable &&
                Float.compare(physics.mass, mass) == 0 &&
                updatable == physics.updatable &&
                Objects.equals(gravity, physics.gravity) &&
                Objects.equals(position, physics.position) &&
                Objects.equals(velocity, physics.velocity) &&
                Objects.equals(terminalVelocity, physics.terminalVelocity) &&
                Objects.equals(acceleration, physics.acceleration) &&
                Objects.equals(drag, physics.drag) &&
                Objects.equals(roughness, physics.roughness) &&
                Objects.equals(collidingEntities, physics.collidingEntities) &&
                Objects.equals(overlappingEntities, physics.overlappingEntities);
    }
}
