package jndev.pseudo3d.application;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import jndev.pseudo3d.listener.Keyboard;
import jndev.pseudo3d.listener.Mouse;
import jndev.pseudo3d.scene.SceneRenderer;

/**
 * main application for any project using Pseudo3D
 *
 * @author JNDev (Jeremaster101)
 */
public class Pseudo3D extends Application {
    
    /**
     * canvas for root pane
     */
    private static Canvas canvas;
    
    /**
     * javafx scene for the stage
     */
    private static Scene scene;
    
    /**
     * active scene to render and tick
     */
    private static jndev.pseudo3d.scene.Scene activeScene;
    
    /**
     * width of the window
     */
    private static int width = 842;
    
    /**
     * height of the window
     */
    private static int height = 480;
    
    /**
     * whether the window can be resized
     */
    private static boolean resizable = true;
    
    /**
     * title of window
     */
    private static String title = "Pseudo3D";
    
    /**
     * timeline loop for ticking operations
     */
    private static final Timeline tickLoop = new Timeline(new KeyFrame(Duration.millis(1),
            ae -> activeScene.tick()));
    
    /**
     * timeline loop for rendering
     */
    private static final Timeline renderLoop = new Timeline(new KeyFrame(Duration.millis(1),
            ae -> {
                canvas.setWidth(scene.getWidth());
                canvas.setHeight(scene.getHeight());
                SceneRenderer.render(activeScene, Pseudo3D.getCanvas().getGraphicsContext2D());
            }));
    
    /**
     * start the application
     *
     * @param primaryStage primary stage of application
     */
    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        canvas = new Canvas(width, height);
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
        unpause();
        setRenderFrequency(60);
        setTickFrequency(120);
    }
    
    /**
     * stop the application
     *
     * @throws Exception
     */
    @Override
    public void stop() throws Exception {
        renderLoop.stop();
        tickLoop.stop();
        super.stop();
    }
    
    /**
     * launch the application
     */
    public static void launch() {
        new Thread(Application::launch).start();
    }
    
    /**
     * pause ticking and rendering
     */
    public static void pause() {
        renderLoop.pause();
        tickLoop.pause();
    }
    
    /**
     * unpause ticking and rendering
     */
    public static void unpause() {
        renderLoop.play();
        tickLoop.play();
    }
    
    /**
     * get the main canvas
     *
     * @return main canvas
     */
    public static Canvas getCanvas() {
        return canvas;
    }
    
    /**
     * get the scene on the stage
     *
     * @return scene on stage
     */
    public static Scene getScene() {
        return scene;
    }
    
    /**
     * get the active scene
     *
     * @return active scene
     */
    public static jndev.pseudo3d.scene.Scene getActiveScene() {
        return activeScene;
    }
    
    /**
     * set a new scene to be active
     *
     * @param activeScene pseudo3d scene
     */
    public static void setActiveScene(jndev.pseudo3d.scene.Scene activeScene) {
        Pseudo3D.activeScene = activeScene;
    }
    
    /**
     * set the tick frequency for the tick loop
     *
     * @param hertz ticks per second
     */
    public static void setTickFrequency(double hertz) {
        tickLoop.setRate(1 / (1000 / hertz));
    }
    
    /**
     * get the tick frequency
     *
     * @return tick frequency
     */
    public static double getTickFrequency() {
        return 1000 / (1 / tickLoop.getRate());
    }
    
    /**
     * set the render frequency for the render loop
     *
     * @param hertz frames per second
     */
    public static void setRenderFrequency(double hertz) {
        renderLoop.setRate(1 / (1000 / hertz));
    }
    
    /**
     * get the render frequency
     *
     * @return render frequency
     */
    public static double getRenderFrequency() {
        return 1000 / (1 / renderLoop.getRate());
    }
    
    /**
     * initialize parameters of the main window. this must be run before launch(), otherwise it will have no effect
     *
     * @param width     width of window
     * @param height    height of window
     * @param resizable resizable status
     * @param title     window title
     */
    public static void init(int width, int height, boolean resizable, String title) {
        Pseudo3D.width = width;
        Pseudo3D.height = height;
        Pseudo3D.resizable = resizable;
        Pseudo3D.title = title;
    }
}
