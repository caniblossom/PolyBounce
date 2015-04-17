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

import com.github.caniblossom.polybounce.renderer.RenderingEngine;
import com.github.caniblossom.polybounce.math.ConvexPolygon;
import com.github.caniblossom.polybounce.math.PolygonBuilder;
import com.github.caniblossom.polybounce.math.Vector2;
import com.github.caniblossom.polybounce.physics.PhysicsBody;
import com.github.caniblossom.polybounce.physics.PhysicsEngine;
import com.github.caniblossom.polybounce.physics.RigidBody;
import com.github.caniblossom.polybounce.physics.StaticBody;
import java.util.ArrayList;
import org.lwjgl.input.Keyboard;

// TODO Implement tests if possible.
// TODO Clean up.

/**
 * A class representing the game engine.
 * @author Jani Salo
 */
public class GameEngine {
    private static final float TIME_STEP = 0.01f;
    private static final float TIME_SCALE = 5.0f;
    
    private static final Vector2 GRAVITY = new Vector2(0.0f, -0.5f);

    private static final float PLAYER_ACCELERATION = 4.0f;    
    private static final float PLAYER_THRUST = 1.0f;    
    
    private final PhysicsEngine physicsEngine;
    private final RenderingEngine renderingEngine;
    private final ArrayList<ConvexPolygon> polygonList;

    private final Player player;
    
    // TODO Test code.
    private void initialize() {
        final PolygonBuilder builder = new PolygonBuilder();

        // Level bounds.
        final float h0 = 12.0f;
        final float h1 = 22.0f;
        final float v0 = 12.0f;
        final float v1 = 22.0f;
        
        // Create some semi-random polygons.
        for (float x = -10.0f; x <= 10.0f; x += 2.0f) {
            if (x == 0.0f) {
                continue;
            }

            final float y = -1.0f;

            final float s = 2.5f * ((float) Math.random() - 0.5f);
            final float t = 2.5f * ((float) Math.random() - 0.5f);
            final float a = 0.1f * (float) Math.random();

            final float radius = 1.0f;
            final int n = 2 + (int) Math.ceil(4.0 * Math.random() + 0.001);

            final RigidBody shape = new RigidBody(builder.createRegularPolygon(new Vector2(0.0f, 0.0f), radius, n), 1.0f, new Vector2(x, y), 0.0f, new Vector2(s, t), a);
            physicsEngine.add(shape);
        }
        
        physicsEngine.add(player.getBody());
        
        // I honestly think checkstyle enforces making this harder to read.
        final StaticBody wall0 = new StaticBody(builder.createBox(new Vector2(-h1, -v1), new Vector2(h1, -v0)), 10000.0f, new Vector2(0.0f, 0.0f), 0.0f);
        final StaticBody wall1 = new StaticBody(builder.createBox(new Vector2(-h1, -v1), new Vector2(-h0, v1)), 10000.0f, new Vector2(0.0f, 0.0f), 0.0f);
        final StaticBody wall2 = new StaticBody(builder.createBox(new Vector2(h0, -v1), new Vector2(h1, v1)), 10000.0f, new Vector2(0.0f, 0.0f), 0.0f);
        final StaticBody wall3 = new StaticBody(builder.createBox(new Vector2(-h1,  v0), new Vector2(h1, v1)), 10000.0f, new Vector2(0.0f, 0.0f), 0.0f);

        physicsEngine.add(wall0);
        physicsEngine.add(wall1);
        physicsEngine.add(wall2);
        physicsEngine.add(wall3);
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
        
        // TODO Implement actual jumping.
        if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
            player.thrust(new Vector2(0.0f, PLAYER_THRUST), dt);
        }
    }
    
    // Sends current polygon data to rendering engine.
    private void uploadPolygonDataToRenderingEngine() {
        polygonList.clear();
        for (PhysicsBody body : physicsEngine.getUnmodifiableViewToBodyList()) {
            polygonList.add(body.getHullRelativeToTime(0.0f));
        }
        
        renderingEngine.setRenderingData(polygonList);
    }
    
    /**
     * Constructs a new game engine.
     * @param viewWidth viewport width in pixels
     * @param viewHeight viewport height in pixels
     */
    public GameEngine(final int viewWidth, final int viewHeight) {
        physicsEngine = new PhysicsEngine(TIME_STEP, GRAVITY);
        renderingEngine = new RenderingEngine(viewWidth, viewHeight);
        polygonList = new ArrayList();
        
        player = new Player(new Vector2(0.0f, 0.0f));

        initialize();    
    } 
    
    
    /**
     * Updates the internal state of the game engine.
     * @param dt change in time
     */   
    public void update(final float dt) {
        handleInput(TIME_SCALE * dt);
        
        physicsEngine.update(TIME_SCALE * dt);
        uploadPolygonDataToRenderingEngine();        

        renderingEngine.setCamera(player.getBody().getPosition(), 2.0f);        
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
