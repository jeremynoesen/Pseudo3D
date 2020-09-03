package jndev.pseudo3d.sprites;

import jndev.pseudo3d.objects.Camera;
import jndev.pseudo3d.scene.Renderer;
import jndev.pseudo3d.scene.Scene;

import java.awt.*;
import java.awt.image.BufferedImage;

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
}
