# A rough description of the program structure
The program is divided into four major parts: mathematics, physics, graphics and the game itself.

### Mathematics
The mathematics subdivision contains all generic mathematical types, such as vectors and segments, that have no game specific purpose. It's not very large, but is used widely in the rest of the code. This part has no external dependencies and is represented by yellow in the diagram.

### Physics
The physics are roughly divided into classes for physical bodies, and into algorithms acting on the bodies. The physics engine stores the physical bodies for the game world, and uses a physics collider to collide groups of bodies together. The physics collider in turn uses physics solver (the naming scheme is somewhat messed up) to solve individual body to body collisions, up to one collision per vertex per body, and computes any forces acting on them as a result. The physics in general are based on stepping, I.e. each object makes a small step at a time and the engine checks whether any intersections occur. Physics are dependent on the mathematics, and are represented by pink in the diagram.

### Renderer
Rendering is (unfortunately) a bit all over the place. There are simple wrappers for some OpenGL objects, a rendering manager running rendering tasks, classes representing shader programs, and the rendering engine itself. The rendering engine takes mostly care of game specific stuff (making it somewhat badly designed), configuring the rendering tasks as required by the current game state. Any actual OpenGL calls happen inside the rendering tasks themselves, which in turn are called by the rendering manager owned by the rendering engine. There is also tessellator that converts convex polygons into raw triangle data used the shaders. Rendering is dependent on physics and mathematics, and is represented by green in the diagram.

### Game
The game engine has an instance of both the physics and a the graphics engine, and is therefore dependent on both. There isn't much game logic beyond physics, so the rest of the game classes are classes representing objects or the level itself. This part of the program is represented by blue in the diagram.