package jndev.pseudo3d.loader;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * this class is used to load and retrieve all sprites for a game
 */
public class Sprites {
    private static final HashMap<String, Image> sprites = new HashMap<>();
    
    /**
     * this will load all sprite images of .png format into an array list with the file's name without an extension
     *
     * @param dir directory where sprites are stored
     */
    public static void load(File dir) {
        for (File file : dir.listFiles()) {
            if (file.isFile() && file.getName().toLowerCase().endsWith(".png")) {
                try {
                    sprites.put(file.getName().replace(".png", ""), ImageIO.read(file));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                if (file.isDirectory())
                    load(file);
            }
        }
    }
    
    /**
     * get a sprite from the list of loaded sprites
     *
     * @param name name of sprite to retrieve
     * @return Image object of sprite
     */
    public static Image get(String name) {
        return sprites.get(name);
    }
}
