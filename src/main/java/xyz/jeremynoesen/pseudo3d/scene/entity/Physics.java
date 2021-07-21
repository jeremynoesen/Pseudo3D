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
    private final boolean[] pushable;

    /**
     * mass of an entity used for calculations, such as momentum conservation
     */
    private float mass;

    /**
     * whether the entity can update or not
     */
    private boolean updatable;

    /**
     * time elapsed in the previous tick
     */
    private float deltaTime;

    /**
     * temp set used in special cases of momentum
     */
    private final Set<Vector.Axis> skipMomentum;

    /**
     * temp set used in special cases of collisions
     */
    private final Set<Physics> specialCollisions;

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
        pushable = new boolean[]{true, true, true};
        skipMomentum = new HashSet<>();
        specialCollisions = new HashSet<>();
        updatable = true;
        deltaTime = 0;
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
        deltaTime = physics.deltaTime;
        skipMomentum = new HashSet<>();
        specialCollisions = new HashSet<>();
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
        if (!updatable || !kinematic) return;

        this.deltaTime = deltaTime;

        applyAcceleration();
        applyFriction();
        applyDrag();
        applyMomentum();

        setPosition(position.add(velocity.multiply(deltaTime)));
    }

    /**
     * apply acceleration and gravity to the velocity
     */
    private void applyAcceleration() {
        for (Vector.Axis axis : Vector.Axis.values()) {
            float v = velocity.get(axis);
            float a = acceleration.get(axis) + gravity.get(axis);
            float vt = terminalVelocity.get(axis);

            if (v > -vt && a < 0)
                velocity = velocity.set(axis, Math.max(v + (a * deltaTime), -vt));
            else if (v < vt && a > 0)
                velocity = velocity.set(axis, Math.min(v + (a * deltaTime), vt));
        }
    }

    /**
     * apply the effect of friction to the velocity
     */
    private void applyFriction() {
        Vector friction = calculateFriction();

        float vx = velocity.getX(), vy = velocity.getY(), vz = velocity.getZ();
        float fx = friction.getX(), fy = friction.getY(), fz = friction.getZ();

        if (vx < 0) vx = Math.min(vx + fy + fz, 0);
        else if (vx > 0) vx = Math.max(vx - fy - fz, 0);
        if (vy < 0) vy = Math.min(vy + fx + fz, 0);
        else if (vy > 0) vy = Math.max(vy - fx - fz, 0);
        if (vz < 0) vz = Math.min(vz + fx + fy, 0);
        else if (vz > 0) vz = Math.max(vz - fx - fy, 0);

        velocity = new Vector(vx, vy, vz);
    }

    /**
     * get the friction vector for the entity
     *
     * @return friction vector
     */
    private Vector calculateFriction() {
        Vector output = new Vector();
        for (Vector.Axis axis : Vector.Axis.values()) {
            float v = velocity.get(axis);

            if (colliding) {
                float f = 0;
                int count = 0;
                Side side = getSide(axis, v);
                float stackedMass = calculateStackedMass(v, axis);

                if (side != null && collidesOn(side)) {
                    for (Physics physics : collidingEntities.get(side)) {
                        f += physics.roughness.get(axis) * Math.abs(v - physics.getVelocity().get(axis));
                        count++;
                    }
                }

                if (count > 0) f = ((f + roughness.get(axis)) / (count + 1)) * stackedMass * deltaTime;
                output = output.set(axis, f);

                f = 0;
                count = 0;

                Side opposite = getSide(axis, -v);
                if (opposite != null && collidesOn(opposite)) {
                    for (Physics physics : collidingEntities.get(opposite)) {
                        if (Math.signum(physics.getVelocity().get(axis)) == Math.signum(v)) {
                            f += physics.roughness.get(axis) * Math.abs(physics.getVelocity().get(axis) - v);
                            count++;
                        }
                    }
                }

                if (count > 0) f = ((f + roughness.get(axis)) / (count + 1)) * (stackedMass - mass) * deltaTime;
                output = output.set(axis, output.get(axis) + f);
            }
        }
        return output;
    }

    /**
     * sum masses of stacked entities
     *
     * @param velocity input velocity
     * @param axis     axis of motion
     * @return sum of masses
     */
    private float calculateStackedMass(float velocity, Vector.Axis axis) {
        float totalMass = 0;
        Queue<Physics> current = new ArrayDeque<>();
        Set<Physics> visited = new HashSet<>();
        current.add(this);

        while (!current.isEmpty()) {
            Physics physics = current.poll();

            if (!visited.contains(physics)) {
                totalMass += physics.mass;
                Side side = getSide(axis, -velocity);
                if (side != null) current.addAll(physics.getCollidingEntities(side));
                visited.add(physics);
            }
        }
        return totalMass;
    }

    /**
     * apply the effect of drag to the velocity
     */
    private void applyDrag() {
        Vector drag = calculateDrag();

        float vx = velocity.getX(), vy = velocity.getY(), vz = velocity.getZ();
        float dx = drag.getX(), dy = drag.getY(), dz = drag.getZ();

        if (vx < 0) vx = Math.min(vx + dx, 0);
        else if (vx > 0) vx = Math.max(vx - dx, 0);
        if (vy < 0) vy = Math.min(vy + dy, 0);
        else if (vy > 0) vy = Math.max(vy - dy, 0);
        if (vz < 0) vz = Math.min(vz + dz, 0);
        else if (vz > 0) vz = Math.max(vz - dz, 0);

        velocity = new Vector(vx, vy, vz);
    }

    /**
     * get the drag vector based on velocity and dimensions
     *
     * @return drag vector
     */
    private Vector calculateDrag() {
        float dx = drag.getX() * getHeight() * getDepth() * deltaTime * Math.abs(velocity.getX());
        float dy = drag.getY() * getWidth() * getDepth() * deltaTime * Math.abs(velocity.getY());
        float dz = drag.getZ() * getHeight() * getWidth() * deltaTime * Math.abs(velocity.getZ());
        return new Vector(dx, dy, dz);
    }

    /**
     * apply the effects of momentum to the velocity
     */
    private void applyMomentum() {
        skipMomentum.clear();
        for (Vector.Axis axis : Vector.Axis.values()) {
            float v = velocity.get(axis);

            if (colliding) {
                Side side = getSide(axis, v);
                if (side != null && collidesOn(side)) {
                    for (Physics physics : collidingEntities.get(side)) {

                        if (physics.updatable) {
                            if (physics.kinematic && ((axis == Vector.Axis.X && physics.pushable[0]) ||
                                    (axis == Vector.Axis.Y && physics.pushable[1]) ||
                                    (axis == Vector.Axis.Z && physics.pushable[2]))) {

                                float sum = mass + physics.mass;
                                float diff = mass - physics.mass;
                                float v1 = v;
                                float v2 = physics.velocity.get(axis);
                                v = ((diff / sum) * v1) + ((2 * physics.mass / sum) * v2);
                                if (!physics.skipMomentum.contains(axis))
                                    physics.velocity =
                                            physics.velocity.set(axis, ((-diff / sum) * v2) + ((2 * mass / sum) * v1));

                            } else {
                                v = 0;
                                skipMomentum.add(axis);
                            }
                        }
                    }
                }
            }
            velocity = velocity.set(axis, v);
        }
    }

    /**
     * check if a entity has collided with this entity
     */
    public void tickCollisions() {
        if (!updatable || entities == null) return;

        resetCollisions();

        for (Physics entity : entities) {
            if (entity != this && entity.updatable && super.overlaps(entity)) {
                if (solid && entity.isSolid()) {
                    collideWith(entity);
                } else {
                    overlapWith(entity);
                }
            }
        }
    }

    /**
     * resets all collision data
     */
    private void resetCollisions() {
        colliding = false;
        overlapping = false;
        collidingEntities.values().forEach(HashSet::clear);
        overlappingEntities.clear();
        specialCollisions.clear();
    }

    /**
     * fix the position of this entity to make a collision occur
     *
     * @param physics entity colliding with this entity
     */
    private void collideWith(Physics physics) {
        float[] overlaps = new float[6];
        overlaps[0] = Math.abs(getMinimum().getX() - physics.getMaximum().getX()); //left overlap
        overlaps[1] = Math.abs(getMaximum().getX() - physics.getMinimum().getX()); //right overlap
        overlaps[2] = Math.abs(getMinimum().getY() - physics.getMaximum().getY()); //bottom overlap
        overlaps[3] = Math.abs(getMaximum().getY() - physics.getMinimum().getY()); //top overlap
        overlaps[4] = Math.abs(getMinimum().getZ() - physics.getMaximum().getZ()); //back overlap
        overlaps[5] = Math.abs(getMaximum().getZ() - physics.getMinimum().getZ()); //front overlap

        Vector.Axis axis = Vector.Axis.X;
        byte dir = -1;
        byte zeros = 0;
        float distance = overlaps[0];

        for (int i = 0; i < 6; i++) {
            if (overlaps[i] < distance) {
                distance = overlaps[i];
                dir = (byte) -Math.pow(-1, i);
                switch (i) {
                    case 0, 1 -> axis = Vector.Axis.X;
                    case 2, 3 -> axis = Vector.Axis.Y;
                    case 4, 5 -> axis = Vector.Axis.Z;
                }
            }
            if (overlaps[i] == 0) zeros++;
        }

        if (zeros > 1) return;

        if (kinematic && velocity.get(axis) * dir > 0) {
            if (Math.signum(velocity.get(axis)) == -Math.signum(physics.velocity.get(axis))
                    && !physics.specialCollisions.contains(this)) {
                distance *= velocity.get(axis) / (velocity.get(axis) - physics.velocity.get(axis));
                specialCollisions.add(physics);
            }
            setPosition(position.set(axis, position.get(axis) - (distance * dir)));
        }

        colliding = true;
        collidingEntities.get(getSide(axis, dir)).add(physics);
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
     * get the drag coefficient vector of the entity
     *
     * @return drag coefficient vector of an entity
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
     * set the roughness of the entity
     *
     * @param roughness roughness vector
     */
    public Physics setRoughness(Vector roughness) {
        this.roughness = roughness;
        return this;
    }

    /**
     * check if an entity can be collided with
     *
     * @return true if able to be collided with
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
     * check if this entity is colliding on the specified sides perpendicular to the axis
     *
     * @param axis axis of collision
     * @return true if the entity is colliding on the axis
     */
    public boolean collidesOn(Vector.Axis axis) {
        return switch (axis) {
            case X -> collidesOn(Side.LEFT) || collidesOn(Side.RIGHT);
            case Y -> collidesOn(Side.BOTTOM) || collidesOn(Side.TOP);
            case Z -> collidesOn(Side.BACK) || collidesOn(Side.FRONT);
        };
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
     * get all the sides the entity is colliding on
     *
     * @return set of all colliding sides
     */
    public HashSet<Side> getCollidingSides() {
        HashSet<Side> sides = new HashSet<>();
        for (Side side : Side.values()) if (collidesOn(side)) sides.add(side);
        return sides;
    }

    /**
     * get all the axes the entity is colliding on
     *
     * @return set of all colliding axes
     */
    public HashSet<Vector.Axis> getCollidingAxes() {
        HashSet<Vector.Axis> axes = new HashSet<>();
        for (Vector.Axis axis : Vector.Axis.values()) if (collidesOn(axis)) axes.add(axis);
        return axes;
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
     * set whether the entity can be pushed or not per axis
     *
     * @param x true to allow pushing on the x axis
     * @param y true to allow pushing on the y axis
     */
    public Physics setPushable(boolean x, boolean y) {
        pushable[0] = x;
        pushable[1] = y;
        pushable[2] = false;
        return this;
    }

    /**
     * set whether the entity can be pushed or not for all axes
     *
     * @param pushable true to allow pushing on all axes
     */
    public Physics setPushable(boolean pushable) {
        this.pushable[0] = pushable;
        this.pushable[1] = pushable;
        this.pushable[2] = pushable;
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
