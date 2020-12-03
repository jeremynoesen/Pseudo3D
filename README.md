# Pseudo3D

## About
Pseudo3D is a 2.5D game engine written with JavaFX. This engine uses sprite scaling and order of drawing to achieve a fake 3D rendering effect for a 3D physics world. Physics in this engine are AABB (Axis Aligned Bounding Box) basic kinematics.

## Purpose
Pseudo3D was originally just a 2D game I was working on. Later, that transformed into a 2D game engine. It was the middle of the first quarantine for COVID-19, and I decided to add a 3rd axis, as 2D was not interesting enough. Now this is a project that keeps me going when I have nothing to do, and I plan to use it in the future to make some 2.5D style games.

## Usage
***Wiki coming soon!***<br>
For now, view the Sandbox class in `src/test/java` for a basic idea of how to create a simple scene. More details will be given when the wiki is created.

## Screenshots
The following are screenshots made with the use of the Pseudo3D renderer.

### Orthographic
This is what an orthographic, or zero-degree field-of-view, render looks like. This is the style used for 2D sprite-based games. There is no depth whatsoever, making it difficult to decipher the exact positioning of the character with respect to the background or ground.

![Orthograpgic rendering](https://i.imgur.com/eyxUMyG.png)

### Perspective
This is what the Pseudo3D rendering looks like. Sprites scale based on distance from the camera plane, field of view, etc. You can now see that the background here is not actually far away. You're also able to see that the character is standing in the middle of the platform on the horizontal axes.

![Pseudo3D rendering](https://i.imgur.com/7UZJGv9.png)

## Building
This project is not complete yet, but if you wish to build it to use it, you can clone or download this repo. In the folder of the project, you can open terminal and run `gradle build`. This should make a jar out of Pseudo3D. With this, you can add the jar as a dependency in your IDE of choice.

## Contributing
I will only permit contributions directly to this repo from those who I have directly asked for help from. Any other contribution attempt will be rejected. You are free to fork this repo and do whatever you please, as I cannot stop you from doing that.

## Disclaimer
This project is far from complete. You can view the associated GitHub project to see what is planned for this. A wiki will be made eventually explaining how to use this, but until then, there are no explanations available. Not everything is implemented yet, so use at your own risk.