package jndev.pseudo3d.listener;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import jndev.pseudo3d.application.Pseudo3D;
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
     * @param pane main pane for the program
     */
    public static void initialize(Pane pane) {
        pane.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            pressed.add(e.getButton());
        });
        pane.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
            pressed.remove(e.getButton());
        });
        pane.addEventFilter(ScrollEvent.SCROLL, e -> {
            wheelRotation = e.getDeltaY();
        });
        pane.addEventFilter(MouseEvent.MOUSE_MOVED, e -> {
            position = new Vector((float) e.getSceneX(), (float) e.getSceneY());
        });
    }
}
