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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// TODO Implement tests if possible.
// TODO Fix physics, they are hilarious at the moment.

/**
 * A physics engine.
 * @author Jani Salo
 */
public class PhysicsEngine {    
    private final float timeStep;
    private final Vector2 gravity;

    private final ArrayList<PhysicsBody> bodyList;
    private final ArrayList<RigidBody> rigidBodyList;
    private final ArrayList<StaticBody> staticBodyList;        

    private final PhysicsCollider collider;
    private final ArrayList<PhysicsBody> passiveBodyList;
    
    /**
     * Constructs a new physics engine.
     * @param timeStep stepping constant used for physics
     * @param gravity vector representing external forces
     */
    public PhysicsEngine(final float timeStep, final Vector2 gravity) {
        assert timeStep > 0.0f;

        this.timeStep = timeStep;
        this.gravity = gravity;
        
        this.bodyList = new ArrayList();
        this.rigidBodyList = new ArrayList();
        this.staticBodyList = new ArrayList();
        
        this.collider = new PhysicsCollider();
        this.passiveBodyList = new ArrayList();
    }
    
    // Applies any external forces to the bodies.
    private void applyExternalForces(final float dt) {
        for (PhysicsBody body : rigidBodyList) {
            body.applyImpulse(body.getCurrentCenterOfMass(), gravity.scale(dt));
        }
    }
    
    // Steps and collides the bodies.
    private void stepAndCollide(final float dt) {
        for (int activeIndex = 0; activeIndex < rigidBodyList.size(); activeIndex++) {
            final PhysicsBody activeBody = rigidBodyList.get(activeIndex);

            passiveBodyList.clear();
            passiveBodyList.addAll(staticBodyList);

            for (int passiveIndex = 0; passiveIndex < activeIndex; passiveIndex++) {
                if (activeIndex != passiveIndex) {
                    passiveBodyList.add(rigidBodyList.get(passiveIndex));
                }
            }

            final boolean didIntersect = collider.collide(activeBody, passiveBodyList, dt);
            
            if (!didIntersect) {
                activeBody.update(dt);
            }
        }
    }
    
    /**
     * @param body rigid body to add
     */
    public void add(RigidBody body) {
        bodyList.add(body);
        rigidBodyList.add(body);
    }

    /**
     * @param body static body to add
     */
    public void add(StaticBody body) {
        bodyList.add(body);
        staticBodyList.add(body);
    }

    /**
     * @return unmodifiable view to a list of all bodies.
     */
    public List<PhysicsBody> getUnmodifiableViewToBodyList() {
        return Collections.unmodifiableList(bodyList);
    } 
    
    /**
     * Updates the world.
     * @param dt change in time
     */
    public void update(final float dt) {
        final int stepCount = dt < timeStep ? 1 : (int) Math.ceil(dt / timeStep);
        final float stepLength = dt / (float) stepCount;

        for (int step = 0; step < stepCount; step++) {
            applyExternalForces(stepLength);            
            stepAndCollide(stepLength);
        }
    }
}
