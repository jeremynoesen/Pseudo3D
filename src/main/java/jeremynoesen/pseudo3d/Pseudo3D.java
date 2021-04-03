package jeremynoesen.pseudo3d;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import jeremynoesen.pseudo3d.input.Keyboard;
import jeremynoesen.pseudo3d.input.Mouse;

/**
 * main application for any project using Pseudo3D
 *
 * @author Jeremy Noesen
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
    private static jeremynoesen.pseudo3d.scene.Scene activeScene;
    
    /**
     * whether the window can be resized
     */
    private static boolean resizable;
    
    /**
     * title of window
     */
    private static String title;
    
    /**
     * timeline loop for ticking operations
     */
    private static final Timeline tickLoop = new Timeline();
    
    /**
     * timeline loop for rendering
     */
    private static final Timeline renderLoop = new Timeline();
    
    /**
     * launch the instance of the application
     *
     * @param width     width of window
     * @param height    height of window
     * @param framerate framerate of window (hertz)
     * @param tickspeed tick speed for physics (hertz)
     * @param resizable resizable status
     * @param title     window title
     */
    public static void launch(int width, int height, int framerate, int tickspeed, boolean resizable, String title) {
        Pseudo3D.resizable = resizable;
        Pseudo3D.title = title;
        
        tickLoop.getKeyFrames().add(new KeyFrame(Duration.millis(1000f / tickspeed),
                ae -> {
                    activeScene.tick();
                }));
        renderLoop.getKeyFrames().add(new KeyFrame(Duration.millis(1000f / framerate),
                ae -> {
                    canvas.setWidth(scene.getWidth());
                    canvas.setHeight(scene.getHeight());
                    activeScene.render(canvas.getGraphicsContext2D());
                }));
        
        canvas = new Canvas(width, height);
    
        new Thread(Application::launch).start();
    }
    
    /**
     * start the application
     *
     * @param primaryStage primary stage of application
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
        unpause();
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
        activeScene.clearDeltaTime();
        super.stop();
    }
    
    /**
     * pause ticking and rendering
     */
    public static void pause() {
        renderLoop.pause();
        tickLoop.pause();
        activeScene.clearDeltaTime();
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
     * get the active scene
     *
     * @return active scene
     */
    public static jeremynoesen.pseudo3d.scene.Scene getActiveScene() {
        return activeScene;
    }
    
    /**
     * set a new scene to be active
     *
     * @param activeScene pseudo3d scene
     */
    public static void setActiveScene(jeremynoesen.pseudo3d.scene.Scene activeScene) {
        Pseudo3D.activeScene = activeScene;
    }
}
