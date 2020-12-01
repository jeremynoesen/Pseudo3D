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
     * main instance of this class
     */
    public static Pseudo3D instance;
    
    /**
     * main game loop
     */
    private GameLoop gameLoop;
    
    /**
     * root pane of app
     */
    private Pane root;
    
    /**
     * canvas for root pane
     */
    private Canvas canvas;
    
    /**
     * primary stage in application
     */
    private Stage primaryStage;
    
    /**
     * width of main window
     */
    private double width;
    
    /**
     * height of main window
     */
    private double height;
    
    /**
     * whether the window can be resized
     */
    private boolean resizable;
    
    /**
     * title of window
     */
    private String title;
    
    /**
     * get the main instance of Pseudo3D as instantiated by launching the JavaFX application
     *
     * @return main instance
     */
    public static Pseudo3D getInstance() {
        return instance;
    }
    
    /**
     * start the application
     *
     * @param primaryStage primary stage of application
     */
    @Override
    public void start(Stage primaryStage) {
        instance = this;
        this.primaryStage = primaryStage;
        gameLoop = new GameLoop(this);
        root = new Pane();
        canvas = new Canvas(1000, 1000);
        root.getChildren().add(canvas);
        primaryStage.setTitle(title);
        primaryStage.setResizable(resizable);
        primaryStage.setScene(new Scene(root, 1000, 1000));
        primaryStage.show();
        Mouse.initialize(root);
        Keyboard.initialize(root);
    }
    
    /**
     * get the game loop
     *
     * @return game loop
     */
    public GameLoop getGameLoop() {
        return gameLoop;
    }
    
    /**
     * change the game loop used for the engine
     *
     * @param gameLoop gameloop to set as game loop
     */
    public void setGameLoop(GameLoop gameLoop) {
        this.gameLoop = gameLoop;
    }
    
    /**
     * get the main canvas
     *
     * @return main canvas
     */
    public Canvas getCanvas() {
        return canvas;
    }
    
    /**
     * get root pane
     *
     * @return root pane
     */
    public Pane getRoot() {
        return root;
    }
    
    /**
     * set the width of the main window
     *
     * @param width width of main window
     */
    public void setWidth(double width) {
        this.width = width;
        primaryStage.setWidth(width);
    }
    
    /**
     * get the width of the main window
     *
     * @return width of main window
     */
    public double getWidth() {
        return width;
    }
    
    /**
     * set the height of the main window
     *
     * @param height height of main window
     */
    public void setHeight(double height) {
        this.height = height;
        primaryStage.setHeight(height);
    }
    
    /**
     * get the height of the main window
     *
     * @return height of main window
     */
    public double getHeight() {
        return height;
    }
    
    /**
     * allow or disallow resizing the window
     *
     * @param resizable true to allow resizing
     */
    public void setResizable(boolean resizable) {
        this.resizable = resizable;
        primaryStage.setResizable(resizable);
    }
    
    /**
     * check if the window is resizable
     *
     * @return true if resizable
     */
    public boolean isResizable() {
        return resizable;
    }
    
    /**
     * set the title of the window
     *
     * @param title window title
     */
    public void setTitle(String title) {
        this.title = title;
        primaryStage.setTitle(title);
    }
    
    /**
     * get the title of the window
     *
     * @return window title
     */
    public String getTitle() {
        return title;
    }
}
