package jndev.pseudo3d.input;

import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;

import java.util.HashSet;
import java.util.Set;

/**
 * class to keep track of key strokes allowing multiple key presses
 *
 * @author Jeremy Noesen
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
     * @param key key to check if pressed
     * @return true if the key is pressed
     */
    public static boolean isPressed(KeyCode key) {
        return pressed.contains(key);
    }
    
    /**
     * add the event listeners to the main pane of the program to allow this class to work
     *
     * @param canvas main pane for the program
     */
    public static void initialize(Canvas canvas) {
        canvas.setOnKeyPressed(e -> pressed.add(e.getCode()));
        canvas.setOnKeyReleased(e -> pressed.remove(e.getCode()));
    }
}
