package xyz.jeremynoesen.pseudo3d;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import xyz.jeremynoesen.pseudo3d.input.Keyboard;
import xyz.jeremynoesen.pseudo3d.input.Mouse;

/**
 * Starting point of all functions of Pseudo3D
 *
 * @author Jeremy Noesen
 */
public class Pseudo3D extends Application {

    /**
     * Canvas for root Pane
     */
    private static Canvas canvas;

    /**
     * JavaFX Scene for the Stage
     */
    private static Scene scene;

    /**
     * Active Pseudo3D Scene to render and tick
     */
    private static xyz.jeremynoesen.pseudo3d.core.Scene activeScene;

    /**
     * Whether the window can be resized or not
     */
    private static boolean resizable;

    /**
     * Title of window
     */
    private static String title;

    /**
     * Timeline loop for ticking
     */
    private static final Timeline tickLoop = new Timeline();

    /**
     * Timeline loop for rendering
     */
    private static final Timeline renderLoop = new Timeline();

    /**
     * Delta time for the tick loop
     */
    private static float tickDeltaTime = 0;

    /**
     * Delta time for the render loop
     */
    private static float renderDeltaTime = 0;

    /**
     * Previous time a tick finished in nanoseconds
     */
    private static long lastTick = 0;

    /**
     * Previous time a render finished in nanoseconds
     */
    private static long lastRender = 0;

    /**
     * Launch the instance of the Application
     *
     * @param width          Width of window
     * @param height         Height of window
     * @param framerate      Framerate for rendering in frames per second
     * @param tickSpeed      Tick speed for physics in hertz
     * @param fixedDeltaTime Whether the delta time is a fixed value
     * @param resizable      Resizable status
     * @param title          Window title
     */
    public static void launch(int width, int height, float framerate, float tickSpeed,
                              boolean fixedDeltaTime, boolean resizable, String title) {
        if (Float.compare(tickSpeed, 0) == 0 || Float.compare(framerate, 0) == 0)
            throw new IllegalArgumentException("Cannot set tickSpeed or framerate to 0");

        Pseudo3D.resizable = resizable;
        Pseudo3D.title = title;

        tickLoop.setCycleCount(Animation.INDEFINITE);
        tickLoop.getKeyFrames().add(new KeyFrame(Duration.millis(1000f / tickSpeed), ae -> {
            if (!fixedDeltaTime && lastTick > 0) tickDeltaTime = (System.nanoTime() - lastTick) / 1000000000.0f;
            else if (fixedDeltaTime) tickDeltaTime = 1f / tickSpeed;
            else tickDeltaTime = 0;

            activeScene.tick(tickDeltaTime);

            lastTick = System.nanoTime();
        }));

        renderLoop.setCycleCount(Animation.INDEFINITE);
        renderLoop.getKeyFrames().add(new KeyFrame(Duration.millis(1000f / framerate), ae -> {
            if (!fixedDeltaTime && lastRender > 0) renderDeltaTime = (System.nanoTime() - lastRender) / 1000000000.0f;
            else if (fixedDeltaTime) renderDeltaTime = 1f / framerate;
            else renderDeltaTime = 0;

            activeScene.render(canvas.getGraphicsContext2D(), renderDeltaTime);

            lastRender = System.nanoTime();
        }));

        canvas = new Canvas(width, height);

        Mouse.init(canvas);
        Keyboard.init(canvas);

        new Thread(Application::launch).start();
    }

    /**
     * Start the application
     *
     * @param primaryStage Primary Stage of the Application
     */
    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        root.getChildren().add(canvas);
        scene = new Scene(root);
        primaryStage.setTitle(title);
        primaryStage.setResizable(resizable);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> System.exit(0));
        primaryStage.show();
        canvas.widthProperty().bind(root.widthProperty());
        canvas.heightProperty().bind(root.heightProperty());
        canvas.requestFocus();
        setPaused(false);
    }

    /**
     * Stop the Application
     *
     * @throws Exception If stopping fails
     */
    @Override
    public void stop() throws Exception {
        tickLoop.stop();
        renderLoop.stop();
        lastTick = 0;
        lastRender = 0;
        super.stop();
    }

    /**
     * Pause or unpause the game loops
     *
     * @param paused True to pause
     */
    public static void setPaused(boolean paused) {
        if (paused) {
            tickLoop.pause();
            renderLoop.pause();
            lastTick = 0;
            lastRender = 0;
        } else {
            tickLoop.play();
            renderLoop.play();
        }
    }

    /**
     * Get the main Canvas
     *
     * @return Main Canvas
     */
    public static Canvas getCanvas() {
        return canvas;
    }

    /**
     * Get the JavaFX Scene the main Canvas is placed on
     *
     * @return JavaFX Scene the main Canvas is placed on
     */
    public static Scene getScene() {
        return scene;
    }

    /**
     * Check if the main window is resizable
     *
     * @return True if the main window is resizable
     */
    public static boolean isResizable() {
        return resizable;
    }

    /**
     * Get the active Pseudo3D Scene
     *
     * @return Active Pseudo3D Scene
     */
    public static xyz.jeremynoesen.pseudo3d.core.Scene getActiveScene() {
        return activeScene;
    }

    /**
     * Set a new Pseudo3D Scene to be active
     *
     * @param activeScene Pseudo3D Scene
     */
    public static void setActiveScene(xyz.jeremynoesen.pseudo3d.core.Scene activeScene) {
        Pseudo3D.activeScene = activeScene;
    }

    /**
     * Get the delta time for the previous iteration of the tick loop
     *
     * @return Delta time in seconds
     */
    public static float getTickDeltaTime() {
        return tickDeltaTime;
    }

    /**
     * Get the delta time for the previous iteration of the render loop
     *
     * @return Delta time in seconds
     */
    public static float getRenderDeltaTime() {
        return renderDeltaTime;
    }
}
