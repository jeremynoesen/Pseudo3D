package jndev.pseudo3d.application;

import jndev.pseudo3d.listener.Keyboard;
import jndev.pseudo3d.listener.Mouse;

import javax.swing.*;
import java.awt.*;

/**
 * main application for any project using Pseudo3D
 *
 * @author JNDev (Jeremaster101)
 */
public class Pseudo3D extends JFrame {
    
    /**
     * instance of application
     */
    private static final Pseudo3D pseudo3d = new Pseudo3D();
    
    /**
     * main game loop
     */
    private static final GameLoop gameLoop = new GameLoop();
    
    /**
     * can't create a new instance outside Application class
     */
    private Pseudo3D() {
    }
    
    /**
     * start the application if it has not been started yet
     *
     * @param width     window width
     * @param height    window height
     * @param resizable whether the window can be resized
     * @param title     title of window
     */
    public static void launch(int width, int height, boolean resizable, String title) {
        EventQueue.invokeLater(() -> {
            if (!pseudo3d.isVisible()) {
                pseudo3d.addKeyListener(new Keyboard());
                pseudo3d.addMouseListener(new Mouse());
                pseudo3d.addMouseMotionListener(new Mouse());
                pseudo3d.addMouseWheelListener(new Mouse());
                pseudo3d.add(gameLoop);
                pseudo3d.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                pseudo3d.setSize(new Dimension(width, height));
                pseudo3d.setLocationRelativeTo(null);
                pseudo3d.setVisible(true);
                pseudo3d.setResizable(resizable);
                pseudo3d.setTitle(title);
            }
        });
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
     * get the running instance of Pseudo3D
     *
     * @return instance of Pseudo3D
     */
    public static Pseudo3D getInstance() {
        return pseudo3d;
    }
}
