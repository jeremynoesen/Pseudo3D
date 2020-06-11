package test;

import jndev.pseudo3d.listener.Keyboard;
import jndev.pseudo3d.loader.Sprites;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class TestMain extends JFrame {
    
    public TestMain() {
        initializeUI();
    }
    
    public static void main(String[] args) {
        
        EventQueue.invokeLater(() -> {
            TestMain window = new TestMain();
            window.setVisible(true);
            
        });
    }
    
    private void initializeUI() {
        Sprites.load(new File("res/sprites/"));
        addKeyListener(new Keyboard());
        //add(new RenderTest());
        add(new CollisionTest());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(1200, 1200));
        setLocationRelativeTo(null);
    }
}