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
import jndev.pseudo3d.sceneobject.Renderable;
import jndev.pseudo3d.sprite.AnimatedSprite;
import jndev.pseudo3d.sprite.CameraSprite;
import jndev.pseudo3d.sprite.Sprite;

/**
 * main application for any project using Pseudo3D
 *
 * @author JNDev (Jeremaster101)
 */
public class Pseudo3D extends Application {
    
    /**
     * canvas for root pane
     */
    private static Canvas canvas = new Canvas(1000, 1000);
    
    /**
     * active scene to render and tick
     */
    private static jndev.pseudo3d.scene.Scene activeScene;
    
    private static Timeline tickLoop = new Timeline(new KeyFrame(Duration.millis(8.3333333),
            ae -> activeScene.tick()));
    
    private static Timeline renderLoop = new Timeline(new KeyFrame(Duration.millis(16.666667),
            ae -> {
                for (Renderable object : activeScene.getObjects()) {
                    if (object.getSprite() != null) {
                        Sprite sprite = object.getSprite();
                        
                        if (sprite instanceof AnimatedSprite) {
                            ((AnimatedSprite) sprite).update();
                            //update animated sprites
                            
                        } else if (sprite instanceof CameraSprite) {
                            ((CameraSprite) sprite).update();
                            //update camera sprites
                        }
                    }
                }
                SceneRenderer.render(activeScene, activeScene.getCamera(),
                        Pseudo3D.getCanvas().getGraphicsContext2D());
            }));
    
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
        primaryStage.setTitle("Untitled");
        primaryStage.setResizable(true);
        primaryStage.setScene(new Scene(root, 1000, 1000));
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> System.exit(0));
        canvas.requestFocus();
        tickLoop.setCycleCount(Animation.INDEFINITE);
        renderLoop.setCycleCount(Animation.INDEFINITE);
        unpause();
    }
    
    /**
     * launch the application
     */
    public static void launch() {
        new Thread(Application::launch).start();
    }
    
    public static void pause() {
        renderLoop.pause();
        tickLoop.pause();
    }
    
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
    
    public static jndev.pseudo3d.scene.Scene getActiveScene() {
        return activeScene;
    }
    
    public static void setActiveScene(jndev.pseudo3d.scene.Scene activeScene) {
        Pseudo3D.activeScene = activeScene;
    }
}
