# Pseudo3D
Pseudo3D is a 2D game engine that can render sprites with scaling to create a depth effect. The game objects also have 3D axis-aligned bounding-box (AABB) collisions, as well as simple motion (position, velocity, acceleration, and jerk).

Each physicsObject is represented by an image, or sprite. The camera physicsObject, which is more of a plane, determines how to scale the sprites in a scene. Sprites that lie on the camera plane are drawn one-to-one scale, objects behind the camera plane will be scaled up, and objects in front of it will be scaled down. This creates a depth effect.

Creating a scene is very simple with this game engine. See the Testing class in test/test/ to see how simple it is to make a controllable physicsObject that renders with the Pseudo3D renderer, as well as have 3D collisions and motion.

*(Note: this project's release branch is not complete. A release does not exist.)*

The following are screenshots of a couple rendered frames using this game engine:

![Orthograpgic style rendering](https://i.imgur.com/eyxUMyG.png)

This is what an orthographic, or zero degrees field-of-view, render looks like. This is more of a basic, 2D game style.

![Pseudo3D style rendering](https://i.imgur.com/7UZJGv9.png)

This is what the Pseudo3D rendering looks like for the Testing class. Sprites scale based on distance from the camera plane.

This is my first non-minecraft project, so this is mainly me experimenting with Java in ways I have not done so before.