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

import com.github.caniblossom.polybounce.math.ConvexPolygon;
import com.github.caniblossom.polybounce.math.PolygonBuilder;
import com.github.caniblossom.polybounce.math.Vector2;
import com.github.caniblossom.polybounce.physics.RigidBody;

/**
 * Class for player.
 * @author Jani Salo
 */
public class Player {
    private final static float RADIUS           = 1.0f;
    private final static float MASS             = 2.0f;    
    private final static float BOUNCINESS       = 0.9f;    
    private final static float STATIC_FRICTION  = 0.2f;    
    private final static float DYNAMIC_FRICTION = 0.5f;    
    private final static float MAX_ACCELERATION = 2.0f; 
    private final static float MAX_THRUST       = 5.0f;
    
    private final static int HULL_VERTEX_COUNT = 6;
    
    private final RigidBody body;
    
    /**
     * Creates a new player
     * @param position player position
     */
    public Player(final Vector2 position) {
        final PolygonBuilder builder = new PolygonBuilder();
        final ConvexPolygon hull = builder.createRegularPolygon(new Vector2(0.0f, 0.0f), RADIUS, HULL_VERTEX_COUNT);
        body = new RigidBody(hull, MASS, BOUNCINESS, STATIC_FRICTION, DYNAMIC_FRICTION, position, 0.5f * (float) Math.PI, new Vector2(0.0f, 0.0f), 0.0f);
    }
    
    /**
     * @return player physics body
     */
    public RigidBody getBody() {
        return body;
    }

    /**
     * Accelerates player.
     * @param delta change in angular velocity
     * @param dt change in time
     */
    public void accelerate(final float delta, final float dt) {
        if (Math.abs(body.getAngularVelocity()) > MAX_ACCELERATION) {
            return;
        }

        // TODO Clamp delta if necessary.         
        body.setAngularVelocity(body.getAngularVelocity() + delta * dt);
    }

    /**
     * Thrusts the player to a direction.
     * @param delta change in velocity
     * @param dt change in time
     */
    public void thrust(final Vector2 delta, final float dt) {
        if (body.getVelocity().length() > MAX_THRUST) {
            return;
        }

        // TODO Clamp delta if necessary.         
        body.setVelocity(body.getVelocity().sum(delta.scale(dt)));
    }
}
