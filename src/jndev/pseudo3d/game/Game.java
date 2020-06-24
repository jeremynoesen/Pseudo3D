package jndev.pseudo3d.game;

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
    private static Game game;
    
    /**
     * the main game loop
     */
    private static Loop loop;
    
    /**
     * can't create a new instance outside Application class
     */
    private Game() {}
    
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
        game = new Game();
        loop = new Loop();
        game.addKeyListener(new Keyboard());
        game.add(loop);
        game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        game.setSize(new Dimension(width, height));
        game.setLocationRelativeTo(null);
        game.setVisible(true);
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
