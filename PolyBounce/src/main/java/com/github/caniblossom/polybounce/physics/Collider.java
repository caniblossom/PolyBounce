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
package com.github.caniblossom.polybounce.physics;

import com.github.caniblossom.polybounce.math.Vector2;
import java.util.ArrayList;
import java.util.List;

/**
 * A class for handling collisions between rigid bodies.
 * See: http://en.wikipedia.org/wiki/Collision_response
 * @author Jani Salo
 */
public class Collider {        
    private final static float MIN_IMPULSE = 0.0f;

    private final Solver solver;
    private final ArrayList<Collision> collisionList;
    
    // Returns the collision impulse magnitude as experienced by the active body. 
    private float computeImpulseMagnitude(final Body active, final Body passive, final Collision collision, final float restitution) {
        final Vector2 r1 = collision.getActivePosition().difference(active.getCenterOfMass());
        final Vector2 r2 = collision.getPassivePosition().difference(passive.getCenterOfMass());
        
        final float a = 1.0f / active.getMass() + (float) Math.abs(r1.cross(collision.getNormal())) / active.getMomentOfInertiaAroundCenterOfMass();
        final float b = 1.0f / passive.getMass() + (float) Math.abs(r2.cross(collision.getNormal())) / passive.getMomentOfInertiaAroundCenterOfMass();

        final Vector2 delta = passive.getVelocityAtPosition(collision.getPassivePosition()).difference(active.getVelocityAtPosition(collision.getActivePosition()));
        return (1.0f + restitution) * delta.dot(collision.getNormal()) / (a + b);
    }

    // Handles the normal part of the collision.
    private void handleCollision(final Body active, final Body passive, final Collision collision) {
        final float restitution = 0.5f * (active.getBounciness() + passive.getBounciness());
        final float magnitude = computeImpulseMagnitude(active, passive, collision, restitution);
        
        // Due to the simplicity of the collision algorithm, a minimum bump needs 
        // to be added to each collision to avoid objects getting stuck to each other.
        final float activeMagnitude = magnitude > active.getMass() * MIN_IMPULSE ? magnitude : active.getMass() * MIN_IMPULSE;
        final float passiveMagnitude = magnitude > passive.getMass() * MIN_IMPULSE ? -magnitude : passive.getMass() * -MIN_IMPULSE;
        
        active.applyImpulse(collision.getActivePosition(), collision.getNormal().scale(activeMagnitude));
        passive.applyImpulse(collision.getPassivePosition(), collision.getNormal().scale(passiveMagnitude));  
    } 

    // Handles friction for a collision.
    private void handleFriction(final Body active, final Body passive, final Collision collision) {
        final Vector2 tangent = new Vector2(-collision.getNormal().getY(), collision.getNormal().getX());
        Collision frictionCollision = new Collision(collision.getDistance(), collision.getActivePosition(), collision.getPassivePosition(), tangent);

        float magnitude = computeImpulseMagnitude(active, passive, frictionCollision, 1.0f);

        // This is very unlikely to be the "correct" way to compute this stuff.
        final float staticFriction = 0.5f * (active.getStaticFriction() + passive.getStaticFriction());
        final float dynamicFriction = 0.5f * (active.getDynamicFriction() + passive.getDynamicFriction());
        
        final Vector2 delta = passive.getVelocityAtPosition(collision.getPassivePosition()).difference(active.getVelocityAtPosition(collision.getActivePosition()));
        if (Math.abs(collision.getNormal().dot(delta)) < (1.0f - staticFriction)) {
            magnitude *= dynamicFriction;
        }
        
        active.applyImpulse(collision.getActivePosition(), tangent.scale(magnitude));
        passive.applyImpulse(collision.getPassivePosition(), tangent.scale(-magnitude));  
    } 

    // Prevents the objects from getting stuck to each other.
    private void fixBrokenPhysics(final Body active, final Body passive, final Collision collision, final float amountToFix) {
        final float activeAgainstNormal = Math.max(0.0f, -active.getVelocityAtPosition(collision.getActivePosition()).dot(collision.getNormal()));
        active.applyImpulse(collision.getActivePosition(), collision.getNormal().scale(amountToFix * active.getMass() * activeAgainstNormal));

        final float passiveAgainstNormal = Math.max(0.0f, passive.getVelocityAtPosition(collision.getPassivePosition()).dot(collision.getNormal()));
        passive.applyImpulse(collision.getPassivePosition(), collision.getNormal().scale(amountToFix * passive.getMass() * passiveAgainstNormal));
    }
    
    /**
     * Constructs a new collider.
     */
    public Collider() {
        solver = new Solver();
        collisionList = new ArrayList();
    }

    /**
     * @param active body to be updated
     * @param passiveList list of passive bodies to test intersection against
     * @param dt change in time
     * @return true if and only if updating the active body doesn't result in an intersection
     */
    public boolean isSafeToUpdate(final Body active, final List<Body> passiveList, final float dt) {
        assert dt != 0.0f;

        for (Body passive : passiveList) {
            if (active == passive) {
                continue;
            }
            
            if (solver.wouldIntersect(active, passive, dt)) {
                return false;
            }
        }

        return true;
    }

    /** 
     * Tests the motion of the active body against passive bodies and handles any collisions that might occur.
     * @param active body to be collided
     * @param passiveList list of passive bodies to collide against
     * @param dt change in time
     */
    public void collide(final Body active, final List<Body> passiveList, final float dt) {
        assert dt != 0.0f;

        for (Body passive : passiveList) {
            if (active == passive) {
                continue;
            }
            
            collisionList.clear();
            solver.findCollisions(collisionList, active, passive, dt);
 
            // This seems to make no difference.
            // Collections.sort(collisionList);
            
            for (Collision collision : collisionList) {
                handleCollision(active, passive, collision);
                handleFriction(active, passive, collision);
                
                fixBrokenPhysics(active, passive, collision, 0.999f);
            }
        }
    }
}
