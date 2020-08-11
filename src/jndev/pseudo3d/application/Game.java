package jndev.pseudo3d.application;

import jndev.pseudo3d.listener.Keyboard;

import javax.swing.*;
import java.awt.*;

/**
 * main application for any project using Pseudo3D
 *
 * @author JNDev (Jeremaster101)
 */
public class Game extends JFrame {
    
    /**
     * instance of application
     */
    private static Game game = new Game();
    
    /**
     * the main game loop
     */
    private static Loop loop = new Loop();
    
    /**
     * can't create a new instance outside Application class
     */
    private Game() {}
    
    /**
     * start the application if it has not been started yet
     *
     * @param width     window width
     * @param height    window height
     * @param resizable whether the window can be resized
     */
    public static void launch(int width, int height, boolean resizable) {
        EventQueue.invokeLater(() -> {
            if (!game.isVisible())
                initialize(width, height, resizable);
        });
    }
    
    /**
     * initialize the listeners and load all necessary files
     *
     * @param width     window width
     * @param height    window height
     * @param resizable whether the window can be resized
     */
    private static void initialize(int width, int height, boolean resizable) {
        game.addKeyListener(new Keyboard());
        game.add(loop);
        game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        game.setSize(new Dimension(width, height));
        game.setLocationRelativeTo(null);
        game.setVisible(true);
        game.setResizable(resizable);
    }
    
    /**
     * get the game's game loop
     *
     * @return game's game loop
     */
    public Loop getLoop() {
        return loop;
    }
    
    /**
     * get the running instance of the game
     *
     * @return instance of the game
     */
    public static Game getInstance() {
        return game;
    }
}
