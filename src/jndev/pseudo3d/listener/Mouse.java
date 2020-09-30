package jndev.pseudo3d.listener;

import jndev.pseudo3d.application.Pseudo3D;
import jndev.pseudo3d.util.Vector;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.HashSet;
import java.util.Set;

/**
 * class to keep track of mouse buttons and motions
 *
 * @author JNDev (Jeremaster101)
 */
public class Mouse extends MouseAdapter {
    
    /**
     * set of all buttons pressed
     */
    private static final Set<Integer> pressed = new HashSet<>();
    
    /**
     * position of pointer on screen
     */
    private static Vector position = new Vector();
    
    /**
     * get the motion of the scroll wheel. positive values indicate scrolling towards user, while negative values
     * indicate scrolling away
     */
    private static float wheelRotation = 0;
    
    /**
     * get a list of all buttons pressed with their button codes
     *
     * @return set of buttons pressed
     */
    public static Set<Integer> getPressed() {
        return pressed;
    }
    
    /**
     * check if a button is pressed
     *
     * @param button button to check if pressed (MouseEvent.BUTTON)
     * @return true if the button is pressed
     */
    public static boolean isPressed(int button) {
        return pressed.contains(button);
    }
    
    /**
     * get the motion of the scroll wheel with speed and direction
     *
     * @return motion of the scroll wheel
     */
    public static float getWheelRotation() {
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
     * mouse event method for when a button is pressed
     *
     * @param e mouse event
     */
    @Override
    public void mousePressed(MouseEvent e) {
        pressed.add(e.getButton());
    }
    
    /**
     * mouse event method for when a button is released
     *
     * @param e mouse event
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        pressed.remove(e.getButton());
    }
    
    /**
     * mouse event method for when the scroll wheel moves
     *
     * @param e mouse event
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        wheelRotation = (float) e.getPreciseWheelRotation();
    }
    
    /**
     * mouse event method for when the mouse moves
     *
     * @param e mouse event
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        position = new Vector((float) (e.getXOnScreen() - Pseudo3D.getInstance().getLocationOnScreen().getX()),
                (float) (e.getYOnScreen() - Pseudo3D.getInstance().getLocationOnScreen().getY()));
    }
}
