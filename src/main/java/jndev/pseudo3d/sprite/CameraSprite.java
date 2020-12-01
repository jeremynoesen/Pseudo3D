package jndev.pseudo3d.sprite;

import javafx.scene.image.WritableImage;
import jndev.pseudo3d.sceneobject.Camera;
import jndev.pseudo3d.scene.Scene;

import java.util.Objects;

/**
 * sprite whose image is created from a render of a scene
 *
 * @author JNDev (Jeremaster101)
 */
public class CameraSprite extends Sprite {
    
    /**
     * scene to render from
     */
    private final Scene scene;
    
    /**
     * camera to render with
     */
    private final Camera camera;
    
    /**
     * create a new camera sprite for a specific scene with set dimensions
     *
     * @param scene  scene to render from
     * @param camera camera to render with
     * @param width  width of image
     * @param height height of image
     */
    public CameraSprite(Scene scene, Camera camera, int width, int height) {
        super(new WritableImage(width, height));
        this.scene = scene;
        this.camera = camera;
        update();
    }
    
    /**
     * copy constructor for camera sprites
     *
     * @param cameraSprite camera sprite to copy
     */
    public CameraSprite(CameraSprite cameraSprite) {
        super(cameraSprite);
        scene = cameraSprite.scene;
        camera = cameraSprite.camera;
        update();
    }
    
    /**
     * render the next frame of the sprite
     */
    public void update() {
        WritableImage updated = new WritableImage((int) image.getWidth(), (int) image.getHeight());
//        SceneRenderer.render(scene, camera, updated.getGraphics());
        //todo fix once rendere is updated
        image = updated;
    }
    
    /**
     * check that this camera sprite is similar to another
     *
     * @param o object to check
     * @return true if sprites are similar
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CameraSprite that = (CameraSprite) o;
        return Objects.equals(scene, that.scene) &&
                Objects.equals(camera, that.camera) &&
                super.equals(that);
    }
}
