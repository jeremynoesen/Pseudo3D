package jndev.pseudo3d.application;

import jndev.pseudo3d.listener.Keyboard;

import javax.swing.*;
import java.awt.*;

/**
 * main application for any project using Pseudo3D
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
     * start the application
     *
     * @param width window width
     * @param height window height
     * @param resizable whether the window can be resized
     */
    public static void launch(int width, int height, boolean resizable) {
        EventQueue.invokeLater(() -> initialize(width, height, resizable));
    }
    
    /**
     * initialize the listeners and load all necessary files
     *
     * @param width window width
     * @param height window height
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
     * get the application's game loop
     *
     * @return game loop
     */
    public static Loop getLoop() {
        return loop;
    }
}
