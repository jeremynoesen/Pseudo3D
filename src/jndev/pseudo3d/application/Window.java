package jndev.pseudo3d.application;

import jndev.pseudo3d.listener.Keyboard;
import jndev.pseudo3d.loader.Sprites;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Window extends JFrame {
    
    public Window() {
        initializeUI();
    }
    
    public static void main(String[] args) {
        
        EventQueue.invokeLater(() -> {
            Window window = new Window();
            window.setVisible(true);
        });
    }
    
    private void initializeUI() {
        Sprites.load(new File("/resources/sprites"));
        addKeyListener(new Keyboard());
    
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}
