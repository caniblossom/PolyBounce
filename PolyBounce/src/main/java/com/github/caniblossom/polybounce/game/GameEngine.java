/*
 * Copyright (c) 2015, Jani Salo
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.github.caniblossom.polybounce.game;

import com.github.caniblossom.polybounce.game.objects.Structure;
import com.github.caniblossom.polybounce.game.objects.Player;
import com.github.caniblossom.polybounce.game.objects.Level;
import com.github.caniblossom.polybounce.math.BoundingBox;
import com.github.caniblossom.polybounce.renderer.RenderingEngine;
import com.github.caniblossom.polybounce.math.Vector2;
import com.github.caniblossom.polybounce.physics.PhysicsEngine;
import com.github.caniblossom.polybounce.renderer.misc.Color;
import org.lwjgl.input.Keyboard;

// TODO The intersection test for winning uses different stepping logic from the actual physics, causing some minute touches to the goal to be missed at times.

/**
 * A class representing the game engine.
 * @author Jani Salo
 */
public class GameEngine {
    private static final float TIME_STEP = 1.0f / 60.0f;
    private static final float INERTIA = 0.995f;
    
    private static final Vector2 GRAVITY = new Vector2(0.0f, -0.5f);

    private static final float PLAYER_ACCELERATION = 4.0f;    
    private static final float PLAYER_X_THRUST = 0.1f;    
    private static final float PLAYER_Y_THRUST = 0.2f;    
    
    private static final float LEVEL_PADDING = 8.0f;
    
    private final PhysicsEngine physicsEngine;
    private final RenderingEngine renderingEngine;
    
    private Player player;
    private boolean playerHasWon = false;            

    private Level currentLevel;
    private Level activeLevel;
    private int levelLength = 3;
    
    private float cameraDistance = 0.0f;    
    private float timeScale = 0.0f;

    private boolean quitRequested = false;
    
    // Computed the world box from level box.
    private BoundingBox computeWorldBox(final Level level) {
        BoundingBox box = level.getLevelInitialBounds();
        return new BoundingBox(box.getPosition().difference(new Vector2(LEVEL_PADDING, LEVEL_PADDING)), box.getWidth() + 2.0f * LEVEL_PADDING, box.getHeight() + 2.0f * LEVEL_PADDING);
    }

    // Generates a new level.
    private void createNewLevel() {
        LevelGenerator generator = new LevelGenerator();
        currentLevel = generator.generate(levelLength);
    }
    
    // Restarts the level.
    private void restart() {
        player = new Player(currentLevel.getPlayerSpawnPosition());
        playerHasWon = false;

        activeLevel = new Level(currentLevel);

        cameraDistance = 64.0f;
        timeScale = 5.0f;
        
        physicsEngine.reset(computeWorldBox(activeLevel));

        for (Structure structure : activeLevel.getUnmodifiableViewToStructures()) {
            physicsEngine.addRigidBodies(structure.getUnmodifiableViewToRigidBodyList());
            physicsEngine.addStaticBodies(structure.getUnmodifiableViewToStaticBodyList());
        }

        physicsEngine.add(player.getBody());
        physicsEngine.addRigidBodies(activeLevel.getGoal().getUnmodifiableViewToRigidBodyList());
        physicsEngine.addStaticBodies(activeLevel.getGoal().getUnmodifiableViewToStaticBodyList());

        renderingEngine.signalRestartLevel();
    }
    
    // Simply checks if the player has fallen too far away.
    private boolean playerDroppedOut() {
        return player.getBody().getCenterOfMass().getY() < activeLevel.getLevelInitialBounds().getPosition().getY() - LEVEL_PADDING;
    }
    
    // Checks whether the player will reach the goal next frame.
    private boolean playerWillWin(final float dt) {
        if (physicsEngine.willCollide(player.getBody(), activeLevel.getGoal().getUnmodifiableViewToRigidBodyList(), dt) || physicsEngine.willCollide(player.getBody(), activeLevel.getGoal().getUnmodifiableViewToStaticBodyList(), dt)) {
            return true;
        }
        
        return false;
    }
    
    // Increases level length and activates slow-down.
    private void handlePlayerWin() {
        playerHasWon = true;
        
        levelLength += 2;
        timeScale = 1.0f;
        
        renderingEngine.signalWinLevel();        
    }
    
    // Reads controls input and acts on it.
    private void handleControlInput(final float dt) {
        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
            player.accelerate(PLAYER_ACCELERATION, dt);
            player.thrust(new Vector2(-PLAYER_X_THRUST, 0.0f), dt);
        } else if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
            player.accelerate(-PLAYER_ACCELERATION, dt);
            player.thrust(new Vector2(PLAYER_X_THRUST, 0.0f), dt);
        }
        
        if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
            player.thrust(new Vector2(0.0f, PLAYER_Y_THRUST), dt);
        } else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
            player.thrust(new Vector2(0.0f, -PLAYER_Y_THRUST), dt);
        }
    }

    // Reads menu input and acts on it.
    private void handleMenuInput() {
        boolean startNewGame = false;
        boolean createNewLevel = false;
        boolean restartLevel = false;
        
        // We need to loop until the buffer is empty.
        while (Keyboard.next()) {            
            if (Keyboard.getEventKeyState() == false) {                
                if (playerHasWon) {
                    if (Keyboard.getEventKey() == Keyboard.KEY_SPACE) {
                        createNewLevel = true;
                    }

                    break;
                }
                
                switch (Keyboard.getEventKey()) {
                    case Keyboard.KEY_Q:
                        quitRequested = true;
                        break;
                    case Keyboard.KEY_N:
                        startNewGame = true;
                        createNewLevel = true;
                        restartLevel = true;
                        break;
                    case Keyboard.KEY_R:
                        restartLevel = true;
                        break;
                }
            }            
        }
        
        if (startNewGame) {
            levelLength = 3;
        }
        
        if (createNewLevel) {
            createNewLevel();
            restart();
        } 
        
        if (restartLevel) {
            restart();
        }
    }
        
    // Handles physics and camera.
    private void handlePhysics(final float dt) {
        final Vector2 old = player.getBody().getPosition();
        physicsEngine.update(dt);

        final float delta = player.getBody().getPosition().difference(old).length();
        cameraDistance = 0.95f * cameraDistance + 0.05f * (4.0f + 4.0f * Math.min(1.0f, 5.0f * delta));
    }
    
    // Handles rendering.
    private void handleRendering() {
        renderingEngine.resetRenderingData();

        for (Structure structure : activeLevel.getUnmodifiableViewToStructures()) {
            renderingEngine.addBodiesToDraw(structure.getUnmodifiableViewToRigidBodyList(), new Color(1.0f, 1.0f, 1.0f));        
            renderingEngine.addBodiesToDraw(structure.getUnmodifiableViewToStaticBodyList(), new Color(0.8f, 0.8f, 1.2f));        
        }

        renderingEngine.addBodyToDraw(player.getBody(), new Color(0.8f, 1.2f, 1.2f));
        renderingEngine.addBodiesToDraw(activeLevel.getGoal().getUnmodifiableViewToRigidBodyList(), new Color(1.2f, 0.8f, 0.8f));
        renderingEngine.addBodiesToDraw(activeLevel.getGoal().getUnmodifiableViewToStaticBodyList(), new Color(1.2f, 0.8f, 0.8f));
        
        renderingEngine.setCamera(player.getBody().getPosition(), cameraDistance);        
        renderingEngine.drawCurrentFrame();        
    }

    /**
     * Constructs a new game engine.
     * @param viewWidth viewport width in pixels
     * @param viewHeight viewport height in pixels
     */
    public GameEngine(final int viewWidth, final int viewHeight) {
        physicsEngine = new PhysicsEngine(TIME_STEP, INERTIA, GRAVITY, new BoundingBox(new Vector2(0.0f, 0.0f), 1.0f, 1.0f));
        renderingEngine = new RenderingEngine(viewWidth, viewHeight);
        
        createNewLevel();    
        restart();
    } 
    
    
    /**
     * Updates the internal state of the game engine.
     * @param dt change in time
     */   
    public void update(final float dt) {
        if (playerDroppedOut()) {
            if (playerHasWon) {
                createNewLevel();
            }

            restart();
        }
        
        if (!playerHasWon && playerWillWin(timeScale * dt)) {
            handlePlayerWin();
        }
        
        handleMenuInput();
        handleControlInput(timeScale * dt);
        
        handlePhysics(timeScale * dt);
        handleRendering();                
    }
    
    /**
     * @return true if and only if quit was requested by something.
     */
    public boolean isQuitRequested() {
        return quitRequested;
    }
           
    /**
     * @return true if and only if all OpenGL resources are good to use.
     */
    public boolean isGood() {
        return renderingEngine.isGood();
    }

    /**
     * Deletes all OpenGL resources related to this object.
     */
    public void deleteGLResources() {
        renderingEngine.deleteGLResources();
    }
}
