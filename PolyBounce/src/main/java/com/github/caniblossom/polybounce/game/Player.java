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

import com.github.caniblossom.polybounce.math.PolygonBuilder;
import com.github.caniblossom.polybounce.math.Vector2;
import com.github.caniblossom.polybounce.physics.RigidBody;

// TODO Implement tests if possible.

/**
 * Class for player.
 * @author Jani Salo
 */
public class Player {
    private final static float PLAYER_RADIUS           =  0.5f;
    private final static float PLAYER_MASS             = 10.0f;    
    private final static float PLAYER_MAX_ACCELERATION =  2.0f; 
    private final static float PLAYER_MAX_THRUST       =  5.0f;
    
    private final static int PLAYER_VERTEX_COUNT = 5;
    
    private final RigidBody body;
    
    /**
     * Creates a new player
     * @param position player position
     */
    public Player(final Vector2 position) {
        final PolygonBuilder builder = new PolygonBuilder();
        body = new RigidBody(builder.createRegularPolygon(new Vector2(0.0f, 0.0f), PLAYER_RADIUS, PLAYER_VERTEX_COUNT), PLAYER_MASS, position, 0.0f, new Vector2(0.0f, 0.0f), 0.0f);
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
        // TODO Handle this properly.
        if (Math.abs(body.getAngularVelocity()) > PLAYER_MAX_ACCELERATION) {
            return;
        }
        
        body.setAngularVelocity(body.getAngularVelocity() + delta * dt);
    }

    /**
     * Thrusts the player to a direction.
     * @param delta change in velocity
     */
    public void thrust(final Vector2 delta, final float dt) {
        // TODO Handle this properly.
        if (body.getVelocity().length() > PLAYER_MAX_THRUST) {
            return;
        }
        
        body.setVelocity(body.getVelocity().sum(delta.scale(dt)));
    }
}
