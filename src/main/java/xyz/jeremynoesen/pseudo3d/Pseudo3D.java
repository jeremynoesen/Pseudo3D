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
    private static xyz.jeremynoesen.pseudo3d.scene.Scene activeScene;

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
     * Launch the instance of the Application
     *
     * @param width     Width of window
     * @param height    Height of window
     * @param framerate Framerate for rendering in frames per second
     * @param tickSpeed Tick speed for physics in hertz
     * @param resizable Resizable status
     * @param title     Window title
     */
    public static void launch(int width, int height, int framerate, int tickSpeed, boolean resizable, String title) {
        Pseudo3D.resizable = resizable;
        Pseudo3D.title = title;

        tickLoop.getKeyFrames().add(new KeyFrame(Duration.millis(1000f / tickSpeed), ae -> activeScene.tick()));
        renderLoop.getKeyFrames().add(new KeyFrame(Duration.millis(1000f / framerate), ae -> {
            canvas.setWidth(scene.getWidth());
            canvas.setHeight(scene.getHeight());
            activeScene.render(canvas.getGraphicsContext2D());
        }));

        canvas = new Canvas(width, height);

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
        Mouse.initialize(canvas);
        Keyboard.initialize(canvas);
        root.getChildren().add(canvas);
        scene = new Scene(root);
        primaryStage.setTitle(title);
        primaryStage.setResizable(resizable);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> System.exit(0));
        canvas.requestFocus();
        tickLoop.setCycleCount(Animation.INDEFINITE);
        renderLoop.setCycleCount(Animation.INDEFINITE);
        setPaused(false);
    }

    /**
     * Stop the Application
     *
     * @throws Exception If stopping fails
     */
    @Override
    public void stop() throws Exception {
        renderLoop.stop();
        tickLoop.stop();
        activeScene.clearDeltaTime();
        super.stop();
    }

    /**
     * Pause or unpause the game loops
     *
     * @param paused True to pause
     */
    public static void setPaused(boolean paused) {
        if (paused) {
            renderLoop.pause();
            tickLoop.pause();
            activeScene.clearDeltaTime();
        } else {
            renderLoop.play();
            tickLoop.play();
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
    public static xyz.jeremynoesen.pseudo3d.scene.Scene getActiveScene() {
        return activeScene;
    }

    /**
     * Set a new Pseudo3D Scene to be active
     *
     * @param activeScene Pseudo3D Scene
     */
    public static void setActiveScene(xyz.jeremynoesen.pseudo3d.scene.Scene activeScene) {
        Pseudo3D.activeScene = activeScene;
    }
}
