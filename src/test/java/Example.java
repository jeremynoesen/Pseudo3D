import javafx.scene.input.KeyCode;
import xyz.jeremynoesen.pseudo3d.Pseudo3D;
import xyz.jeremynoesen.pseudo3d.input.Keyboard;
import xyz.jeremynoesen.pseudo3d.core.Scene;
import xyz.jeremynoesen.pseudo3d.core.object.Entity;
import xyz.jeremynoesen.pseudo3d.core.graphics.Sprite;
import xyz.jeremynoesen.pseudo3d.core.graphics.Camera;
import xyz.jeremynoesen.pseudo3d.core.util.Axis;
import xyz.jeremynoesen.pseudo3d.core.util.Side;
import xyz.jeremynoesen.pseudo3d.core.util.Vector;

import java.io.FileNotFoundException;

/**
 * Example usage of the Pseudo3D API
 *
 * @author Jeremy Noesen
 */
public class Example {

    /**
     * Run the example Scene
     *
     * @param args Program arguments
     */
    public static void main(String[] args) throws FileNotFoundException {
        Sprite playerFront = new Sprite(0.85f, 2, "src/test/resources/images/player/front.png");
        Sprite playerBack = new Sprite(0.85f, 2, "src/test/resources/images/player/back.png");
        Sprite playerLeft = new Sprite(0.6f, 2, "src/test/resources/images/player/left.png");
        Sprite playerRight = new Sprite(0.6f, 2, "src/test/resources/images/player/right.png");
        Sprite floor = new Sprite(1, 1, "src/test/resources/images/floor.png");
        Sprite background = new Sprite(16, 16, "src/test/resources/images/background.png");
        //Load all Sprites

        Entity player = (Entity) new Entity()
                .setUpdateOffScreen(true)
                .setSprite(playerFront)
                .setDimensions(new Vector(0.8f, 2, 0.8f));
        //Create player Entity

        Entity dummy = new Entity(player).setUpdateOffScreen(false);
        //Create dummy Entity

        Camera camera = new Camera().setFieldOfView(49);
        //Create Camera

        Scene scene = new Scene()
                .setGridScale(new Vector(48, 48, 48))
                .setBackground(background)
                .addEntity(player)
                .addEntity(dummy)
                .setCamera(camera);
        //Create and initialize Scene

        for (int j = -8; j <= 8; j++) {
            for (int i = -3; i <= 0; i++) {
                Entity block = (Entity) new Entity()
                        .setSprite(floor)
                        .setKinematic()
                        .setPosition(new Vector(j, -4.75f, i))
                        .setDimensions(new Vector(1, 1, 1));
                scene.addEntity(block);
            }
        }
        //Generate floor

        Pseudo3D.launch(500, 500, 60, 120, false, true, "Sandbox");
        //Launch program

        Pseudo3D.setActiveScene(scene);
        //Set active Scene

        //Scene Keyboard controls
        scene.addTickRunnable(() -> {
            Vector accel = new Vector();

            if (Keyboard.isPressed(KeyCode.W)) {
                if (Float.compare(player.getVelocity().getZ(), -2) > 0)
                    accel = accel.subtract(new Vector(0, 0, 20));
                player.setSprite(playerBack);
            }
            //Accelerate forward

            if (Keyboard.isPressed(KeyCode.S)) {
                if (Float.compare(player.getVelocity().getZ(), 2) < 0)
                    accel = accel.add(new Vector(0, 0, 20));
                player.setSprite(playerFront);
            }
            //Accelerate backward

            if (Keyboard.isPressed(KeyCode.A)) {
                if (Float.compare(player.getVelocity().getX(), -2) > 0)
                    accel = accel.subtract(new Vector(20, 0, 0));
                player.setSprite(playerLeft);
            }
            //Accelerate left

            if (Keyboard.isPressed(KeyCode.D)) {
                if (Float.compare(player.getVelocity().getX(), 2) < 0)
                    accel = accel.add(new Vector(20, 0, 0));
                player.setSprite(playerRight);
            }
            //Accelerate right

            player.setAcceleration(accel);
            //Apply acceleration

            if (Float.compare(camera.getFieldOfView(), 0) > 0) {
                player.setKinematic(Axis.values());
            } else {
                player.setKinematic(Axis.X, Axis.Y);
            }
            //Prevent moving on Z axis when FOV is 0

            if (Keyboard.isPressed(KeyCode.SPACE) && player.isColliding(Side.BOTTOM)) {
                player.setVelocity(player.getVelocity().setY(7.5f));
            }
            //Jump

            if (Keyboard.isPressed(KeyCode.UP)) {
                camera.setFieldOfView(camera.getFieldOfView() + 0.5f);
            }
            //Increase Camera FOV

            if (Keyboard.isPressed(KeyCode.DOWN)) {
                camera.setFieldOfView(Math.max(camera.getFieldOfView() - 0.5f, 0));
            }
            //Decrease Camera FOV

            if (Keyboard.isPressed(KeyCode.LEFT)) {
                camera.setRotation(camera.getRotation() + 0.5f);
            }
            //Rotate Camera counter-clock-wise

            if (Keyboard.isPressed(KeyCode.RIGHT)) {
                camera.setRotation(camera.getRotation() - 0.5f);
            }
            //Rotate Camera clock-wise

            if (Keyboard.isPressed(KeyCode.R)) {
                camera.setOffset(new Vector());
                camera.setRotation(0);
                player.setPosition(new Vector());
                player.setVelocity(new Vector());
                player.setAcceleration(new Vector());
                dummy.setPosition(new Vector());
                dummy.setVelocity(new Vector());
                dummy.setAcceleration(new Vector());
                camera.setFieldOfView(49);
            }
            //Reset Scene
        });
    }
}
