package jndev.pseudo3d.application;

import jndev.pseudo3d.listener.Keyboard;
import jndev.pseudo3d.loader.Sprites;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * main application for any project using Pseudo3D
 */
public class Application extends JFrame {
    
    /**
     * create a new application and initialize
     */
    private Application() {
        initialize();
    }
    
    /**
     * main method, will show the window and start the application
     *
     * @param args
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Application application = new Application();
            application.setVisible(true);
        });
    }
    
    /**
     * initialize the listeners and load all necessary files
     */
    private void initialize() {
        Sprites.load(new File("res/sprites/"));
        addKeyListener(new Keyboard());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}
