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

import com.github.caniblossom.polybounce.math.Vector2;
import java.util.List;

// TODO Implement tests.
// TODO Handle restitution and friction.

/**
 * An utility class for handling collisions between rigid bodies.
 * See: http://en.wikipedia.org/wiki/Collision_response
 * @author Jani Salo
 */
public class PhysicsCollider {
    // Hide constructor to denote static class
    private PhysicsCollider() {}

    // Returns the impulse as experienced by the passive body. 
    private static Vector2 computeImpulse(final PhysicsBody active, final PhysicsBody passive, final BodyIntersection intersection) {
        final Vector2 v = passive.getVelocityAtPosition(intersection.getPassivePosition()).difference(active.getVelocityAtPosition(intersection.getActivePosition()));
        final Vector2 n = intersection.getNormal();
        
        final Vector2 r1 = intersection.getActivePosition().difference(active.getCurrentCenterOfMass());
        final Vector2 r2 = intersection.getPassivePosition().difference(passive.getCurrentCenterOfMass());
        
        // TODO Check whether any of this makes any sense at all.
        final float a = 1.0f / active.getMass() + (float) Math.abs(r1.cross(n)) / active.getMomentOfInertiaAroundCenterOfMass();
        final float b = 1.0f / passive.getMass() + (float) Math.abs(r2.cross(n)) / passive.getMomentOfInertiaAroundCenterOfMass();
        
        return intersection.getNormal().scale(v.dot(n) / (a + b));
    }
    
    /**
     * Steps the active body with given change in time and checks if it intersects
     * any of the passive bodies. If an intersection does occur, the collision
     * response is calculated and impulses are applied to both bodies. 
     * Note that all sorts of insane things can and will happen if the time step 
     * is too large relative to the motion or rotation of the active body.
     * @param active body to be stepped
     * @param passiveList list of passive bodies to test against
     * @param dt change in time
     * @return true if and only if a collision occurred
     */
    public static boolean stepAndCollide(final PhysicsBody active, final List<PhysicsBody> passiveList, final float dt) {
        assert dt != 0.0f;
        
        BodyIntersection intersection = new BodyIntersection();
        PhysicsBody passive = null;
        
        for (PhysicsBody body : passiveList) {
            BodyIntersection newIntersection = new BodyIntersection(active, body, dt);
            if (newIntersection.getDistance() < intersection.getDistance()) {
                intersection = newIntersection;
                passive = body;
            }
        }
        
        if (intersection.didIntersect() && passive != null) {
            final Vector2 impulse = computeImpulse(active, passive, intersection);
            
            active.applyImpulse(intersection.getActivePosition(), impulse.scale(-1.0f));
            passive.applyImpulse(intersection.getActivePosition(), impulse);        
        }
        
        return intersection.didIntersect();
    }
}
