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
     * start the application
     */
    public static void launch() {
        EventQueue.invokeLater(() -> {
            Application application = new Application();
            application.setVisible(true);
        });
    }
    
    /**
     * initialize the listeners and load all necessary files
     */
    private void initialize() {
        addKeyListener(new Keyboard());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(new Dimension(1000, 1000));
        add(new GameLoop());
    }
}
