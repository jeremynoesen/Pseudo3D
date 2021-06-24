package xyz.jeremynoesen.pseudo3d.scene.entity;

import xyz.jeremynoesen.pseudo3d.scene.Scene;

import java.util.Objects;

/**
 * entity to be placed in a scene, includes physics and a sprite
 *
 * @author Jeremy Noesen
 */
public class Entity extends Physics {
    
    /**
     * scene the entity is in
     */
    private Scene scene;
    
    /**
     * image that represents the entity when rendered
     */
    private Sprite sprite;
    
    /**
     * whether the entity is on screen or not
     */
    private boolean onScreen;
    
    /**
     * whether to allow updating the entity when it is not on-screen
     */
    private boolean updateOffScreen;
    
    /**
     * speed modifier for physics and rendering
     */
    private float speed;
    
    /**
     * whether the entity is visible and updatable in the scene
     */
    boolean enabled;
    
    /**
     * whether the entity is visible in the scene or not
     */
    boolean visible;
    
    /**
     * constructs a new default entity
     */
    public Entity() {
        super();
        scene = null;
        sprite = null;
        onScreen = false;
        updateOffScreen = false;
        speed = 1;
        enabled = true;
        visible = true;
    }
    
    /**
     * copy constructor for entities
     *
     * @param entity entity to copy
     */
    public Entity(Entity entity) {
        super(entity);
        if (entity.sprite != null) sprite = new Sprite(entity.sprite);
        this.scene = entity.scene;
        this.onScreen = entity.onScreen;
        this.updateOffScreen = entity.updateOffScreen;
        this.speed = entity.speed;
        this.enabled = entity.enabled;
        this.visible = entity.visible;
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
    public Entity setSprite(Sprite sprite) {
        this.sprite = sprite;
        return this;
    }
    
    /**
     * check if the entity is shown on screen. if the entity has no sprite, this will always be false
     *
     * @return true if shown on screen
     */
    public boolean isOnScreen() {
        return onScreen;
    }
    
    /**
     * set if the entity is on screen. manually setting this will do nothing, as this value is automatically updated by
     * the renderer
     *
     * @param onScreen true when visible on screen
     */
    public void setOnScreen(boolean onScreen) {
        this.onScreen = onScreen;
        setUpdatable((onScreen || updateOffScreen || !visible) && enabled);
    }
    
    /**
     * get whether the entity can update when not visible on screen
     *
     * @return true if can update off screen
     */
    public boolean canUpdateOffScreen() {
        return updateOffScreen;
    }
    
    /**
     * get whether the entity can update when not visible on screen
     *
     * @param updateOffScreen true to allow updating off screen
     */
    public Entity setUpdateOffScreen(boolean updateOffScreen) {
        this.updateOffScreen = updateOffScreen;
        return this;
    }
    
    /**
     * set the scene the entity is in
     *
     * @param scene scene to place entity in
     */
    public Entity setScene(Scene scene) {
        if (scene != null) {
            super.setEntities(scene.getEntities());
        } else {
            super.setEntities(null);
        }
        this.scene = scene;
        return this;
    }
    
    /**
     * get the scene the entity is in
     *
     * @return scene entity is in
     */
    public Scene getScene() {
        return scene;
    }
    
    /**
     * get the speed modifier of the entity
     *
     * @return speed modifier value
     */
    public float getSpeed() {
        return speed;
    }
    
    /**
     * set the speed modifier for the entity
     *
     * @param speed speed modifier value
     */
    public Entity setSpeed(float speed) {
        this.speed = speed;
        return this;
    }
    
    /**
     * update the motion of the entity
     *
     * @param deltaTime time elapsed to use in calculation
     */
    @Override
    public void tickMotion(float deltaTime) {
        super.tickMotion(deltaTime * speed);
    }
    
    /**
     * check if the entity is enabled in the scene
     *
     * @return true if enabled
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * set the entity to be visible in the scene and updatable in physics
     *
     * @param enabled true to enable entity in scene
     */
    public Entity setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) setUpdatable(false);
        return this;
    }
    
    /**
     * check if the entity is visible in rendering
     *
     * @return true if visible
     */
    public boolean isVisible() {
        return visible;
    }
    
    /**
     * set the entity to be visible in rendering
     */
    public Entity setVisible(boolean visible) {
        this.visible = visible;
        return this;
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
        Entity entity = (Entity) o;
        return onScreen == entity.onScreen &&
                updateOffScreen == entity.updateOffScreen &&
                Float.compare(entity.speed, speed) == 0 &&
                enabled == entity.enabled &&
                visible == entity.visible &&
                Objects.equals(scene, entity.scene) &&
                Objects.equals(sprite, entity.sprite);
    }
}
