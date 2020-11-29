package jndev.pseudo3d.listener;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

/**
 * class to keep track of key strokes allowing multiple key presses
 *
 * @author JNDev (Jeremaster101)
 */
public class Keyboard extends KeyAdapter {
    
    /**
     * set of all keys pressed
     */
    private static final Set<Integer> pressed = new HashSet<>();
    
    /**
     * get a list of all keys pressed with their key codes
     *
     * @return set of keys pressed
     */
    public static Set<Integer> getPressed() {
        return pressed;
    }
    
    /**
     * check if a key is pressed
     *
     * @param key key to check if pressed (KeyEvent.VK_KEYNAME)
     * @return true if the key is pressed
     */
    public static boolean isPressed(int key) {
        return pressed.contains(key);
    }
    
    /**
     * key event method for when a key is released
     *
     * @param e key event
     */
    @Override
    public void keyReleased(KeyEvent e) {
        pressed.remove(e.getKeyCode());
    }
    
    /**
     * key event method for when a key is pressed
     *
     * @param e key event
     */
    @Override
    public void keyPressed(KeyEvent e) {
        pressed.add(e.getKeyCode());
    }
}
