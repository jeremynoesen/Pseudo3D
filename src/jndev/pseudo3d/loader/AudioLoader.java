package jndev.pseudo3d.loader;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * used to load audio files and save them to speed up loading times
 *
 * @author JNDev (Jeremaster101)
 */
public class AudioLoader {
    
    /**
     * hashmap of all loaded audio files. files are saved to their path to make retrieving them easier
     */
    private static final HashMap<String, Image> audios = new HashMap<>();
    
    /**
     * this will load all audio files into a hash map with the file's path as the key
     *
     * @param dir directory where audio files are stored
     */
    public static void load(File dir) {
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isFile()) {
                    try {
                        audios.put(file.getPath(), ImageIO.read(file));
                    } catch (IOException e) {
                        System.out.println("Failed to load audio file " + file.getPath());
                    }
                } else {
                    if (file.isDirectory())
                        load(file);
                }
            }
        } else {
            try {
                audios.put(dir.getPath(), ImageIO.read(dir));
            } catch (IOException e) {
                System.out.println("Failed to load audio file " + dir.getPath());
            }
        }
    }
    
    /**
     * get a n audio file from the list of loaded audio files
     *
     * @param name name of file to retrieve
     * @return image object
     */
    public static Image get(String name) {
        return audios.get(name);
    }

}
