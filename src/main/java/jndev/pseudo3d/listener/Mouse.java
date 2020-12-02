package jndev.pseudo3d.listener;

import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseButton;
import jndev.pseudo3d.util.Vector;

import java.util.HashSet;
import java.util.Set;

/**
 * class to keep track of mouse buttons and motions
 *
 * @author JNDev (Jeremaster101)
 */
public class Mouse {
    
    /**
     * set of all buttons pressed
     */
    private static final Set<MouseButton> pressed = new HashSet<>();
    
    /**
     * position of pointer on screen
     */
    private static Vector position = new Vector();
    
    /**
     * get the motion of the scroll wheel. positive values indicate scrolling towards user, while negative values
     * indicate scrolling away
     */
    private static double wheelRotation = 0;
    
    /**
     * get a list of all buttons pressed with their button codes
     *
     * @return set of buttons pressed
     */
    public static Set<MouseButton> getPressed() {
        return pressed;
    }
    
    /**
     * check if a button is pressed
     *
     * @param button button to check if pressed (MouseEvent.BUTTON)
     * @return true if the button is pressed
     */
    public static boolean isPressed(MouseButton button) {
        return pressed.contains(button);
    }
    
    /**
     * get the motion of the scroll wheel with speed and direction
     *
     * @return motion of the scroll wheel
     */
    public static double getWheelRotation() {
        return wheelRotation;
    }
    
    /**
     * get the position of the pointer on screen
     *
     * @return position of pointer on screen
     */
    public static Vector getPosition() {
        return position;
    }
    
    /**
     * add the event listeners to the main pane of the program to allow this class to work
     *
     * @param canvas main pane for the program
     */
    public static void initialize(Canvas canvas) {
        canvas.setOnMouseClicked(e -> pressed.add(e.getButton()));
        canvas.setOnMouseReleased(e -> pressed.remove(e.getButton()));
        canvas.setOnScroll(e -> wheelRotation = e.getDeltaY());
        canvas.setOnMouseMoved(e -> position = new Vector((float) e.getSceneX(), (float) e.getSceneY()));
    }
}
