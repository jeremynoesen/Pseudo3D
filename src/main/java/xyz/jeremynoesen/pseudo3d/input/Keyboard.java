package xyz.jeremynoesen.pseudo3d.input;

import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Class to listen for key strokes, allowing multiple key presses
 *
 * @author Jeremy Noesen
 */
public class Keyboard {

    /**
     * Set of all keys currently pressed
     */
    private static final Set<KeyCode> pressed = new HashSet<>();

    /**
     * Check if keys are pressed
     *
     * @param key KeyCodes to check if pressed
     * @return True if the keys are pressed
     */
    public static boolean isPressed(KeyCode... key) {
        return pressed.containsAll(Arrays.asList(key));
    }

    /**
     * Add the event listeners to the main Canvas of the program
     *
     * @param canvas Main Canvas of the program
     */
    public static void init(Canvas canvas) {
        canvas.setOnKeyPressed(e -> pressed.add(e.getCode()));
        canvas.setOnKeyReleased(e -> pressed.remove(e.getCode()));
    }
}
