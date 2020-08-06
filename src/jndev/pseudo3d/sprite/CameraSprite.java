package jndev.pseudo3d.sprite;

import jndev.pseudo3d.scene.Camera;
import jndev.pseudo3d.scene.Renderer;
import jndev.pseudo3d.scene.Scene;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * sprite whose image is created from a render of a scene
 *
 * @author JNDev (Jeremaster101)
 */
public class CameraSprite implements Sprite {
    
    /**
     * image of sprite
     */
    private BufferedImage image;
    
    /**
     * scene to render from
     */
    private final Scene scene;
    
    /**
     * camera to render with
     */
    private final Camera camera;
    
    /**
     * sprite width
     */
    private final int width;
    
    /**
     * sprite height
     */
    private final int height;
    
    /**
     * create a new camera sprite for a specific scene with set dimensions
     *
     * @param scene  scene to render from
     * @param camera camera to render with
     * @param width  width of image
     * @param height height of image
     */
    public CameraSprite(Scene scene, Camera camera, int width, int height) {
        this.scene = scene;
        this.camera = camera;
        this.width = width;
        this.height = height;
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
        update();
    }
    
    /**
     * render the next frame of the sprite
     */
    public void update() {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.createGraphics();
        graphics.setClip(0, 0, width, height);
        Renderer.render(scene, camera, graphics);
        Toolkit.getDefaultToolkit().sync();
        this.image = image;
    }
    
    /**
     * get the image used for the sprite
     *
     * @return image of sprite
     */
    @Override
    public Image getImage() {
        return image;
    }
}
