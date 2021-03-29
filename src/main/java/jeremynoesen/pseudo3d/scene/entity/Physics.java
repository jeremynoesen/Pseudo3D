package jeremynoesen.pseudo3d.scene.entity;

import jeremynoesen.pseudo3d.Pseudo3D;
import jeremynoesen.pseudo3d.scene.Scene;
import jeremynoesen.pseudo3d.scene.util.Box;
import jeremynoesen.pseudo3d.scene.util.Vector;

import java.util.*;

/**
 * axis-aligned bounding box entity physics
 *
 * @author Jeremy Noesen
 */
public abstract class Physics extends Box {
    
    /**
     * gravity applied to the entity (meters / second ^ 2)
     */
    private Vector gravity;
    
    /**
     * applied force on entity (newtons)
     */
    private Vector appliedForce;
    
    /**
     * net force on entity (newtons)
     */
    private Vector netForce;
    
    /**
     * position of entity (meters)
     */
    private Vector position;
    
    /**
     * velocity of entity, or rate of change of position (meters / second)
     */
    private Vector velocity;
    
    /**
     * acceleration of entity, or rate of change of velocity (meters / second ^ 2)
     */
    private Vector acceleration;
    
    /**
     * coefficient of drag per axis
     */
    private Vector drag;
    
    /**
     * roughness of object per axis, used for friction
     */
    private Vector roughness;
    
    /**
     * scene the entity is in
     */
    private Scene scene;
    
    /**
     * if an entity is solid, allowing collision
     */
    private boolean solid;
    
    /**
     * list of entities colliding with per side
     */
    private final HashMap<Box.Side, HashSet<Physics>> collidingObjects;
    
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
    private boolean pushable;
    
    /**
     * mass of an entity used for calculations, such as momentum conservation
     */
    private float mass;
    
    /**
     * create a new aabb entity with default values
     */
    public Physics() {
        super();
        gravity = new Vector(0, -9.81f, 0);
        appliedForce = new Vector();
        netForce = new Vector();
        position = new Vector();
        velocity = new Vector();
        acceleration = new Vector();
        drag = new Vector(0.01f, 0.01f, 0.01f);
        roughness = new Vector(0.1f, 0.1f, 0.1f);
        scene = null;
        solid = true;
        colliding = false;
        overlapping = false;
        kinematic = true;
        pushable = true;
        mass = 1.0f;
        collidingObjects = new HashMap<>();
        for (Box.Side s : Box.Side.values()) collidingObjects.put(s, new HashSet<>());
    }
    
    /**
     * copy constructor for aabb entities
     *
     * @param physics aabb entity to copy
     */
    public Physics(Physics physics) {
        super(physics);
        gravity = physics.gravity;
        appliedForce = physics.appliedForce;
        netForce = physics.netForce;
        position = physics.position;
        velocity = physics.velocity;
        acceleration = physics.acceleration;
        drag = physics.drag;
        roughness = physics.roughness;
        scene = physics.scene;
        solid = physics.solid;
        colliding = physics.colliding;
        overlapping = physics.overlapping;
        mass = physics.mass;
        kinematic = physics.kinematic;
        pushable = physics.pushable;
        collidingObjects = new HashMap<>();
        for (Box.Side s : Box.Side.values()) collidingObjects.put(s, new HashSet<>());
    }
    
    /**
     * update the motion of the entity
     */
    public void tickMotion() {
        if (!kinematic) return;
        updateNetForce();
        updateKinematics();
    }
    
    /**
     * get the net force on the entity based on external forces
     */
    private void updateNetForce() {
        netForce = new Vector();
        //reset net force to 0 on all axes
    
        netForce = netForce.add(appliedForce);
        //add applied force
    
        netForce = netForce.add(gravity.multiply(mass));
        //add weight force
    
        if (colliding) {
            //todo friction
        }
    
        //todo drag
    }
    
    /**
     * update the motion vectors of the entity based on forces acting on it
     */
    private void updateKinematics() {
        acceleration = netForce.divide(mass);
        //get acceleration from net force
    
        velocity = velocity.add(acceleration.multiply(Pseudo3D.getDeltaTime()));
        //add acceleration to velocity
    
        if (colliding) {
            //todo momentum
        }
    
        setPosition(position.add(velocity.multiply(Pseudo3D.getDeltaTime())));
        //add velocity to position
    }
    
    /**
     * check if a entity has collided with this entity
     */
    public void tickCollisions() {
        if (!kinematic) return;
        
        colliding = false;
        overlapping = false;
        collidingObjects.values().forEach(HashSet::clear);
        //reset all collision data
        
        for (Entity entity : scene.getEntities()) {
            //loop through all entities in scene
            if (entity != this && (entity.isOnScreen() || entity.canUpdateOffScreen())) {
                //check that this is not itself, or can't be checked at the moment
                if (overlaps(entity)) {
                    //check for an overlap
                    if (entity.isSolid() && solid) {
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
            collidingObjects.get(dir == -1 ? Box.Side.LEFT : Box.Side.RIGHT).add(physics);
            //add to colliding entities for the colliding side
        } else if (axis == 2) {
            if (velocity.getY() * dir > 0) {
                if (Math.signum(velocity.getY()) == -Math.signum(physics.velocity.getY()))
                    distance *= velocity.getY() / (velocity.getY() - physics.velocity.getY());
                setPosition(position.setY(position.getY() - (distance * dir)));
            }
            collidingObjects.get(dir == -1 ? Box.Side.BOTTOM : Box.Side.TOP).add(physics);
        } else {
            if (velocity.getZ() * dir > 0) {
                if (Math.signum(velocity.getZ()) == -Math.signum(physics.velocity.getZ()))
                    distance *= velocity.getZ() / (velocity.getZ() - physics.velocity.getZ());
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
     * set the scene this entity is in
     *
     * @param scene scene for collisions
     */
    public Physics setScene(Scene scene) {
        this.scene = scene;
        return this;
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
        
        for (HashSet<Physics> list : collidingObjects.values()) {
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
     * @return true if the object is colliding with the other object on the specified side
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
    public Physics setKinematic(boolean kinematic) {
        this.kinematic = kinematic;
        return this;
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
    public Physics setPushable(boolean pushable) {
        this.pushable = pushable;
        return this;
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
    public Physics setMass(float mass) {
        this.mass = mass;
        return this;
    }
    
    /**
     * get the applied force vector for this entity
     *
     * @return applied force vector
     */
    public Vector getAppliedForce() {
        return appliedForce;
    }
    
    /**
     * apply a force on this entity
     *
     * @param appliedForce force vector to apply
     */
    public Physics setAppliedForce(Vector appliedForce) {
        this.appliedForce = appliedForce;
        return this;
    }
    
    /**
     * get the net force vector for this entity
     *
     * @return net force vector
     */
    public Vector getNetForce() {
        return appliedForce;
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
        Physics that = (Physics) o;
        return solid == that.solid &&
                colliding == that.colliding &&
                overlapping == that.overlapping &&
                kinematic == that.kinematic &&
                Objects.equals(gravity, that.gravity) &&
                Objects.equals(position, that.position) &&
                Objects.equals(velocity, that.velocity) &&
                Objects.equals(acceleration, that.acceleration) &&
                Objects.equals(drag, that.drag) &&
                Objects.equals(roughness, that.roughness) &&
                Objects.equals(scene, that.scene) &&
                Objects.equals(collidingObjects, that.collidingObjects) &&
                super.equals(that);
    }
}
