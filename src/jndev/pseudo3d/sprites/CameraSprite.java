package jndev.pseudo3d.sprites;

import jndev.pseudo3d.objects.Camera;
import jndev.pseudo3d.scene.Renderer;
import jndev.pseudo3d.scene.Scene;

import java.awt.*;
import java.awt.image.BufferedImage;
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
        setImage(new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB));
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
        scene = cameraSprite.scene;
        camera = cameraSprite.camera;
        width = cameraSprite.width;
        height = cameraSprite.height;
        setImage(new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB));
        update();
    }
    
    /**
     * render the next frame of the sprite
     */
    public void update() {
        BufferedImage updated = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics graphics = updated.createGraphics();
        graphics.setClip(0, 0, image.getWidth(), image.getHeight());
        Renderer.render(scene, camera, graphics);
        Toolkit.getDefaultToolkit().sync();
        image = updated;
        graphics.dispose();
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
