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
     * constructs a new default entity
     */
    public Entity() {
        super();
        scene = null;
        sprite = null;
        onScreen = false;
        updateOffScreen = false;
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
        setUpdatable(onScreen || updateOffScreen);
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
                Objects.equals(scene, entity.scene) &&
                Objects.equals(sprite, entity.sprite);
    }
}
