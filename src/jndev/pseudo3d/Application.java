package jndev.pseudo3d;

import jndev.pseudo3d.listener.KeyboardListener;
import jndev.pseudo3d.loader.SpriteLoader;

import javax.swing.*;
import java.awt.*;

public class Application extends JFrame {
    
    public Application() {
        initializeUI();
    }
    
    public static void main(String[] args) {
        
        EventQueue.invokeLater(() -> {
            Application application = new Application();
            application.setVisible(true);
        });
    }
    
    private void initializeUI() {
        new SpriteLoader();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        addKeyListener(new KeyboardListener());
    }
}
