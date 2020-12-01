package jndev.pseudo3d.listener;

import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;

import java.util.HashSet;
import java.util.Set;

/**
 * class to keep track of key strokes allowing multiple key presses
 *
 * @author JNDev (Jeremaster101)
 */
public class Keyboard {
    
    /**
     * set of all keys pressed
     */
    private static final Set<KeyCode> pressed = new HashSet<>();
    
    /**
     * get a list of all keys pressed with their key codes
     *
     * @return set of keys pressed
     */
    public static Set<KeyCode> getPressed() {
        return pressed;
    }
    
    /**
     * check if a key is pressed
     *
     * @param key key to check if pressed (KeyEvent.VK_KEYNAME)
     * @return true if the key is pressed
     */
    public static boolean isPressed(KeyCode key) {
        return pressed.contains(key);
    }
    
    /**
     * add the event listeners to the main pane of the program to allow this class to work
     *
     * @param pane main pane for the program
     */
    public static void initialize(Pane pane) {
        pane.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, e -> {
            pressed.add(e.getCode());
        });
        pane.addEventFilter(javafx.scene.input.KeyEvent.KEY_RELEASED, e -> {
            pressed.remove(e.getCode());
        });
    }
}
