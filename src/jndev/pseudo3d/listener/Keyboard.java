package jndev.pseudo3d.listener;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

/**
 * class to keep track of key strokes allowing multiple key presses
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
     * check if a key or keys are pressed
     *
     * @param key key(s) to check if pressed (KeyEvent.VK_KEYNAME)
     * @return true if all keys are pressed
     */
    public static boolean isPressed(int... key) {
        for(int k : key) {
            if(!pressed.contains(k)) return false;
        }
        return true;
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
