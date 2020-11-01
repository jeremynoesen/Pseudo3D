# Overview
Pseudo3D is a 2.5D game engine that can render sprites with scaling to create a depth effect. The game objects also have 3D axis-aligned bounding-box (AABB) collisions, simple motion (position, velocity, acceleration, and jerk), and simple friction and drag.

Each physicsObject is represented by a sprite. The camera determines how to scale the sprites in a scene. Sprites that lie on the camera plane are drawn one-to-one scale, objects behind the camera plane will be scaled up, and objects in front of it will be scaled down. This creates a depth effect.

#Screenshots
The following are screenshots of a couple rendered frames using this game engine:

![Orthograpgic style rendering](https://i.imgur.com/eyxUMyG.png)

This is what an orthographic, or zero degree field-of-view, render looks like. This is more of a basic, 2D game style.

![Pseudo3D style rendering](https://i.imgur.com/7UZJGv9.png)

This is what the Pseudo3D rendering looks like for the Sandbox testing class. Sprites scale based on distance from the camera plane.

#Usage
Creating a scene is very simple with this game engine. See the Sandbox class in test/ to see how simple it is to make a controllable PhysicsObject that renders with the Pseudo3D renderer, as well as have 3D collisions and motion.

Pseudo3D is far from done in its current state, so full documentation and tutorials will not be provided yet. 