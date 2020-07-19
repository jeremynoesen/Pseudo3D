package jndev.pseudo3d.sprite;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * this class is used to load and retrieve all sprites for a game
 *
 * @author JNDev (Jeremaster101)
 */
public class ImageLoader {
    private static final HashMap<String, Image> sprites = new HashMap<>();
    
    /**
     * this will load all sprite images into a hash map with the file's path as the key
     *
     * @param dir directory where sprites are stored
     */
    public static void load(File dir) {
        if(dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isFile()) {
                    try {
                        sprites.put(file.getPath(), ImageIO.read(file));
                    } catch (IOException e) {
                        System.out.println("Failed to load sprite " + file.getPath());
                    }
                } else {
                    if (file.isDirectory())
                        load(file);
                }
            }
        } else {
            try {
                sprites.put(dir.getPath(), ImageIO.read(dir));
            } catch (IOException e) {
                System.out.println("Failed to load sprite " + dir.getPath());
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
