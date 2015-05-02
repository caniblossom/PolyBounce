# PolyBounce

### Requirements
Support for OpenGL 3.0 and a relatively new version of Java.

### How to run
Extract the zip, enter the folder and type: "java -jar polybounce.jar" on the command line. The zip includes all platform specific binaries for LWJGL, and I'm hoping for Java to find them from the program root. These binaries are also included in the jar file, but I didn't have enough time to figure out how to unpack them at the runtime, so I just lazily stuffed them along.

### How to play
You are a pentagon and your goal is to reach the right edge of the level, hitting a red star shaped thing there. Whenever you reach the star, you get a congratulatory screen, and get to the next, longer and possibly more difficult level. The levels are randomly generated and there is no checks on their playability, although most of them should be passable with some persistence.

Use arrows for controls: left- and right arrow are for acceleration, while pressing up or down will give a very slight thrust. The latter is mostly useful for gliding or dropping fast. You can use (N) to start a new game or (R) to restart a level. You can restart the level as many times as you like, but starting a new game will always reset the level length back to beginner difficulty. You can also quit by simply pressing (Q).

### Known bugs
- Some collisions to the goal might not register as a win
- Objects might very rarely get stuck to each other
- Physics are way too bouncy
 