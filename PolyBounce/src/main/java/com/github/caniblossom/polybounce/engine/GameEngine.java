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
package com.github.caniblossom.polybounce.engine;

import com.github.caniblossom.polybounce.math.ConvexPolygon;
import com.github.caniblossom.polybounce.math.PolygonBuilder;
import com.github.caniblossom.polybounce.math.Vector2;
import java.util.ArrayList;

// TODO Implement tests if possible.

/**
 * A class representing the game engine.
 * @author Jani Salo
 */
public class GameEngine {
    private static final float TIME_STEP = 0.01f;
    private static final Vector2 GRAVITY = new Vector2(0.0f, -0.0025f);
    
    private final PhysicsEngine physicsEngine;
    private final RenderingEngine renderingEngine;

    private final ArrayList<ConvexPolygon> polygonList;
    
    // Initializes the engine, mostly a test at this point.
    private void initialize() {
        final PolygonBuilder builder = new PolygonBuilder();

        final float h0 = 4.0f;
        final float h1 = 5.0f;
        final float v0 = 2.0f;
        final float v1 = 3.0f;
        
        for (float u = 0.0f; u < 5.0f; u += 1.0f) {
            for (float v = 0.0f; v < 3.0f; v += 1.0f) {
                final float radius = 0.5f;
                
                final float x = u - 2.0f;
                final float y = v - 1.0f;
                final float a = (float) Math.random();
                
                final RigidBody shape = new RigidBody(builder.createRegularPolygon(new Vector2(0.0f, 0.0f), radius, 4), 4.0f, new Vector2(x, y), 0.0f, new Vector2(0.0f, 0.0f), a);
                physicsEngine.add(shape);
            }
        }
        
        final StaticBody wall0 = new StaticBody(builder.createBox(new Vector2(-h1, -v1), new Vector2( h1, -v0)), 10000.0f, new Vector2(0.0f, 0.0f), 0.0f);
        final StaticBody wall1 = new StaticBody(builder.createBox(new Vector2(-h1, -v1), new Vector2(-h0,  v1)), 10000.0f, new Vector2(0.0f, 0.0f), 0.0f);
        final StaticBody wall2 = new StaticBody(builder.createBox(new Vector2( h0, -v1), new Vector2( h1,  v1)), 10000.0f, new Vector2(0.0f, 0.0f), 0.0f);
        final StaticBody wall3 = new StaticBody(builder.createBox(new Vector2(-h1,  v0), new Vector2( h1,  v1)), 10000.0f, new Vector2(0.0f, 0.0f), 0.0f);

        physicsEngine.add(wall0);
        physicsEngine.add(wall1);
        physicsEngine.add(wall2);
        physicsEngine.add(wall3);
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
        initialize();
    } 
    
    /**
     * Updates the internal state of the game engine.
     * @param dt change in time
     */   
    public void update(final float dt) {
        physicsEngine.update(10.0f * dt);
        
        polygonList.clear();
        for (PhysicsBody body : physicsEngine.getUnmodifiableViewToBodyList()) {
             polygonList.add(body.getHullRelativeToTime(0.0f));
        }
        
        renderingEngine.setRenderingData(polygonList);
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
