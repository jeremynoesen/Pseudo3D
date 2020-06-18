package jndev.pseudo3d.application;

import jndev.pseudo3d.listener.Keyboard;

import javax.swing.*;
import java.awt.*;

/**
 * main application for any project using Pseudo3D
 */
public class Application extends JFrame {
    
    /**
     * instance of application
     */
    private static final Application application = new Application();
    
    /**
     * the main game loop
     */
    private static final GameLoop gameLoop = new GameLoop();
    
    /**
     * can't create a new instance outside Application class
     */
    private Application() {}
    
    /**
     * start the application
     *
     * @param width window width
     * @param height window height
     */
    public static void launch(int width, int height) {
        EventQueue.invokeLater(() -> initialize(width, height));
    }
    
    /**
     * initialize the listeners and load all necessary files
     *
     * @param width window width
     * @param height window height
     */
    private static void initialize(int width, int height) {
        application.addKeyListener(new Keyboard());
        application.add(gameLoop);
        application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        application.setSize(new Dimension(width, height));
        application.setLocationRelativeTo(null);
        application.setVisible(true);
    }
    
    /**
     * get the application's game loop
     *
     * @return game loop
     */
    public static GameLoop getGameLoop() {
        return gameLoop;
    }
}
