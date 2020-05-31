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
     * this will load all sprite images of .png format into an array list with a name that relates to their relative
     * path. a sprite stored in resources/sprites/player/front.png will be saved as PLAYER_FRONT
     *
     * @param dir directory where sprites are stored
     */
    public static void load(File dir) {
        for (File file : dir.listFiles()) {
            if (!file.isDirectory() && file.getName().toLowerCase().endsWith(".png")) {
                String[] pathFiles = dir.getPath().split("/");
                String fileName = "";
                boolean start = false;
                for (String pathFile : pathFiles) {
                    if (dir.getPath().replace("/", "").trim().endsWith(pathFile)) {
                        start = true;
                        continue;
                    }
                    if (start) fileName += pathFile + "_";
                }
                fileName += file.getName().substring(0, file.getName().indexOf("."));
                fileName = fileName.toUpperCase();
                try {
                    sprites.put(fileName, ImageIO.read(file));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
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
