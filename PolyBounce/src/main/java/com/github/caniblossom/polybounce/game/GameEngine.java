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

import com.github.caniblossom.polybounce.assets.LevelGenerator;
import com.github.caniblossom.polybounce.renderer.RenderingEngine;
import com.github.caniblossom.polybounce.math.Vector2;
import com.github.caniblossom.polybounce.physics.PhysicsEngine;
import org.lwjgl.input.Keyboard;

/**
 * A class representing the game engine.
 * @author Jani Salo
 */
public class GameEngine {
    private static final float TIME_STEP  = 1.0f / 60.0f;
    private static final float INERTIA    = 0.995f;
    private static final float TIME_SCALE = 5.0f;
    
    private static final Vector2 GRAVITY = new Vector2(0.0f, -0.5f);

    private static final float PLAYER_ACCELERATION = 4.0f;    
    private static final float PLAYER_THRUST = 0.0f;    
    
    private static final float LEVEL_DROP_OUT_PADDING = 16.0f;
    
    private final PhysicsEngine physicsEngine;
    private final RenderingEngine renderingEngine;
    
    private Player player;
    private Level level;
    
    // Resets the game.
    private void reset() {
        LevelGenerator generator = new LevelGenerator();

        level = generator.generate(6);
        player = new Player(level.getPlayerSpawnPosition());

        physicsEngine.reset();
        physicsEngine.add(player.getBody());
        
        for (Structure structure : level.getUnmodifiableViewToStructures()) {
            physicsEngine.addRigidBodies(structure.getUnmodifiableViewToRigidBodyList());
            physicsEngine.addStaticBodies(structure.getUnmodifiableViewToStaticBodyList());
        }
    }

    // Simply checks if the player has fallen too far away.
    private boolean playerHasDroppedOut() {
        return player.getBody().getCenterOfMass().getY() < level.getLevelBounds().getPosition().getY() - LEVEL_DROP_OUT_PADDING;
    }
    
    // Reads input and acts on it.
    private void handleInput(final float dt) {
        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
            player.accelerate(PLAYER_ACCELERATION, dt);
            player.thrust(new Vector2(-PLAYER_THRUST, 0.0f), dt);
        } else if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
            player.accelerate(-PLAYER_ACCELERATION, dt);
            player.thrust(new Vector2(PLAYER_THRUST, 0.0f), dt);
        } 
    }
        
    /**
     * Constructs a new game engine.
     * @param viewWidth viewport width in pixels
     * @param viewHeight viewport height in pixels
     */
    public GameEngine(final int viewWidth, final int viewHeight) {
        physicsEngine = new PhysicsEngine(TIME_STEP, INERTIA, GRAVITY);
        renderingEngine = new RenderingEngine(viewWidth, viewHeight);
        
        reset();    
    } 
    
    
    /**
     * Updates the internal state of the game engine.
     * @param dt change in time
     */   
    public void update(final float dt) {
        if (playerHasDroppedOut()) {
            reset();
        }
        
        handleInput(TIME_SCALE * dt);
        physicsEngine.update(TIME_SCALE * dt);

        renderingEngine.resetRenderingData();
        renderingEngine.addBodiesToDraw(physicsEngine.getUnmodifiableViewToBodyList());        
        
        renderingEngine.setCamera(player.getBody().getPosition(), 4.0f);        
        renderingEngine.drawCurrentFrame();
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
