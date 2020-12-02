package jndev.pseudo3d.application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import jndev.pseudo3d.listener.Keyboard;
import jndev.pseudo3d.listener.Mouse;

/**
 * main application for any project using Pseudo3D
 *
 * @author JNDev (Jeremaster101)
 */
public class Pseudo3D extends Application {
    
    /**
     * main game loop
     */
    private static GameLoop gameLoop = new GameLoop();
    
    /**
     * canvas for root pane
     */
    private static Canvas canvas = new Canvas(1000, 1000);
    
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
        canvas.requestFocus();
    }
    
    /**
     * launch the application
     */
    public static void launch() {
        new Thread(Application::launch).start();
    }
    
    /**
     * get the game loop
     *
     * @return game loop
     */
    public static GameLoop getGameLoop() {
        return gameLoop;
    }
    
    /**
     * get the main canvas
     *
     * @return main canvas
     */
    public static Canvas getCanvas() {
        return canvas;
    }
}
