package jndev.pseudo3d.sprite;

import java.awt.*;
import java.util.ArrayList;

public class AnimatedSprite implements Sprite {
    
    private Image currentImage;
    
    public AnimatedSprite(ArrayList<Image> images) {
        currentImage = images.get(0);
    }
    
    @Override
    public Image getImage() {
        return null;
    }
}
