package xyz.jeremynoesen.pseudo3d.scene.render;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Affine;
import xyz.jeremynoesen.pseudo3d.scene.Scene;
import xyz.jeremynoesen.pseudo3d.scene.entity.Entity;
import xyz.jeremynoesen.pseudo3d.scene.entity.Sprite;
import xyz.jeremynoesen.pseudo3d.scene.util.Box;
import xyz.jeremynoesen.pseudo3d.scene.util.Vector;

import java.util.Comparator;
import java.util.Objects;

/**
 * scene renderer, will turn a scene into a render on a javafx canvas
 *
 * @author Jeremy Noesen
 */
public class Renderer {

    /**
     * comparator used to sort scene entities from lowest to highest z position for draw order
     */
    private static final Comparator<Entity> zComparator = (o1, o2) -> {
        float diff = o1.getPosition().getZ() - o2.getPosition().getZ();
        return Math.round(diff / (diff == 0 ? 1 : Math.abs(diff)));
    };

    /**
     * scene being rendered by this renderer
     */
    private final Scene scene;

    /**
     * reference to scene's camera
     */
    private Camera camera;

    /**
     * position on the canvas to render from
     */
    private Vector renderPos;

    /**
     * javafx canvas graphics context to render to
     */
    private GraphicsContext graphicsContext;

    /**
     * time elapsed in the last render frame
     */
    private float deltaTime;

    /**
     * create a new renderer for the specified scene
     *
     * @param scene scene to render
     */
    public Renderer(Scene scene) {
        this.scene = scene;
    }

    /**
     * render the next full frame
     *
     * @param graphicsContext graphics context to draw to
     * @param deltaTime       time elapsed in last frame, used for sprite updating
     */
    public void render(GraphicsContext graphicsContext, float deltaTime) {
        this.graphicsContext = graphicsContext;
        this.deltaTime = deltaTime;
        init();
        drawBackground();
        for (Entity entity : scene.getEntities()) drawEntity(entity);
    }

    /**
     * initialize a few variables and sort the scene entities before starting with the render
     */
    private void init() {
        scene.getEntities().sort(zComparator);
        graphicsContext.setImageSmoothing(false);
        camera = scene.getCamera();
        renderPos = new Vector((float) graphicsContext.getCanvas().getWidth() / 2.0f + camera.getOffset().getX(),
                (float) graphicsContext.getCanvas().getHeight() / 2.0f + camera.getOffset().getY());
    }

    /**
     * draw the background image
     */
    private void drawBackground() {
        if (scene.getBackground() != null) {
            Sprite background = scene.getBackground();
            Affine original = graphicsContext.getTransform();

            if (camera.getRotation() != 0 || background.getRotation() != 0) {
                Affine transform = new Affine();
                transform.appendRotation(-camera.getRotation() - background.getRotation(),
                        renderPos.getX(), renderPos.getY());
                graphicsContext.setTransform(transform);
            }

            float zoom = camera.getZoom();
            graphicsContext.drawImage(scene.getBackground().getImage(),
                    (renderPos.getX() - (background.getWidth() * scene.getGridScale().getX() * zoom) / 2),
                    (renderPos.getY() - (background.getHeight() * scene.getGridScale().getY() * zoom) / 2),
                    background.getWidth() * zoom * scene.getGridScale().getX(),
                    background.getHeight() * zoom * scene.getGridScale().getY());
            graphicsContext.setTransform(original);
            scene.getBackground().update(deltaTime);
        }
    }

    /**
     * draw an entity to the canvas
     *
     * @param entity entity to draw to the canvas
     */
    private void drawEntity(Entity entity) {
        Vector objPos = entity.getPosition().multiply(scene.getGridScale());
        Vector camPos = camera.getPosition().multiply(scene.getGridScale());
        float camDist = camPos.getZ() - objPos.getZ();

        if (!entity.isEnabled() || !entity.isVisible() || entity.getSprite() == null ||
                camDist >= camera.getViewDistance() * scene.getGridScale().getZ()) {
            entity.setOnScreen(false);
            return;
        }

        float scale = (float) (camera.getZoom() * (camera.getSensorSize() / (camera.getSensorSize() + (2.0 *
                camDist * (Math.sin(Math.toRadians(camera.getFieldOfView()) / 2.0f) /
                Math.sin((Math.PI / 2.0) - Math.toRadians(camera.getFieldOfView()) / 2.0f))))));

        if (scale <= 0) {
            entity.setOnScreen(false);
            return;
        }

        Sprite sprite = entity.getSprite();
        short gWidth = (short) graphicsContext.getCanvas().getWidth();
        short gHeight = (short) graphicsContext.getCanvas().getHeight();

        int widthScaled = (int) Math.ceil(sprite.getWidth() * scene.getGridScale().getX() * scale);
        int heightScaled = (int) Math.ceil(sprite.getHeight() * scene.getGridScale().getY() * scale);
        float x = ((objPos.getX() - camPos.getX()) * scale) + renderPos.getX();
        float y = gHeight - (((objPos.getY() - camPos.getY()) * scale) + (gHeight - renderPos.getY()));

        Box screenBox = new Box(gWidth, gHeight, new Vector(gWidth / 2.0f, gHeight / 2.0f));
        Box spriteBox;
        Affine original = graphicsContext.getTransform();
        Affine transform = new Affine();

        if (camera.getRotation() != 0 || sprite.getRotation() != 0) {
            float spriteRotation = -sprite.getRotation();
            float cameraRotation = -camera.getRotation();
            transform.appendRotation(cameraRotation, renderPos.getX(), renderPos.getY());
            transform.appendRotation(spriteRotation, x, y);

            spriteRotation = (float) Math.toRadians(spriteRotation);
            cameraRotation = (float) Math.toRadians(cameraRotation);
            float sprRotSin = (float) Math.sin(spriteRotation + cameraRotation);
            float sprRotCos = (float) Math.cos(spriteRotation + cameraRotation);
            float camRotSin = (float) Math.sin(cameraRotation);
            float camRotCos = (float) Math.cos(cameraRotation);
            float relX = x - renderPos.getX();
            float relY = y - renderPos.getY();

            float heightRotated = Math.abs(widthScaled * sprRotSin) + Math.abs(heightScaled * sprRotCos);
            float widthRotated = Math.abs(widthScaled * sprRotCos) + Math.abs(heightScaled * sprRotSin);
            float yRotated = (relX * camRotSin) + (relY * camRotCos) + renderPos.getY();
            float xRotated = (relX * camRotCos) - (relY * camRotSin) + renderPos.getX();

            spriteBox = new Box(widthRotated, heightRotated, new Vector(xRotated, yRotated));
        } else {
            spriteBox = new Box(widthScaled, heightScaled, new Vector(x, y));
        }

        if (spriteBox.overlaps(screenBox)) {
            graphicsContext.setTransform(transform);
            graphicsContext.drawImage(sprite.getImage(), x - (widthScaled / 2.0),
                    y - (heightScaled / 2.0), widthScaled, heightScaled);
            graphicsContext.setTransform(original);
            sprite.update(deltaTime * entity.getSpeed());
            entity.setOnScreen(true);
        } else {
            entity.setOnScreen(false);
            if (entity.canUpdateOffScreen()) sprite.update(deltaTime * entity.getSpeed());
        }
    }

    /**
     * check if two renderer objects are equal
     *
     * @param o object to check
     * @return true if equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Renderer renderer = (Renderer) o;
        return Objects.equals(scene, renderer.scene) &&
                Objects.equals(camera, renderer.camera) &&
                Objects.equals(renderPos, renderer.renderPos) &&
                Objects.equals(graphicsContext, renderer.graphicsContext);
    }
}
