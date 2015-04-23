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
import java.util.Collections;
import java.util.List;

// TODO Sort objects spatially before intersection tests.
// TODO Implement better stepping logic.
// TODO Clean up code.

/**
 * A physics engine.
 * @author Jani Salo
 */
public class PhysicsEngine {    
    private final static int ITERATION_COUNT = 1; 

    private final float timeStep;
    private final float inertia;
    private final Vector2 gravity;

    private final ArrayList<Body> bodyList;
    private final ArrayList<RigidBody> rigidBodyList;
    private final ArrayList<StaticBody> staticBodyList;        

    private final Collider collider;
    
    /**
     * Constructs a new physics engine.
     * @param timeStep stepping constant used for physics
     * @param inertia multiplier applied to all velocities per update
     * @param gravity vector representing external forces
     */
    public PhysicsEngine(final float timeStep, final float inertia, Vector2 gravity) {
        assert timeStep > 0.0f;

        this.timeStep = timeStep;
        this.inertia = inertia;
        this.gravity = gravity;
        
        this.bodyList = new ArrayList();
        this.rigidBodyList = new ArrayList();
        this.staticBodyList = new ArrayList();
        
        this.collider = new Collider();        
    }
    
    // Applies inertial multiplier to the bodies.
    private void applyExternalForces(final float dt) {
        // I'm too lazy to integrate.
        for (RigidBody body : rigidBodyList) {
            body.applyImpulse(body.getCenterOfMass(), gravity.scale(dt * body.getMass()));

            body.setVelocity(body.getVelocity().scale(inertia));
            body.setAngularVelocity(inertia * body.getAngularVelocity());
        }
    }
    
    // Steps and collides the bodies.
    private void stepAndCollide(final float dt) {
        for (Body body : rigidBodyList) {
            collider.collide(body, bodyList, dt);
                
            if (collider.isSafeToUpdate(body, bodyList, dt)) {
                body.update(dt);
            }
        }
    }

    /**
     * @param body rigid body to add
     */
    public void add(final RigidBody body) {
        bodyList.add(body);
        rigidBodyList.add(body);
    }

    /**
     * @param body static body to add
     */
    public void add(final StaticBody body) {
        bodyList.add(body);
        staticBodyList.add(body);
    }

    /**
     * @param bodyList list of rigid bodies to add to the engine
     */
    public void addRigidBodies(final List<RigidBody> bodyList) {
        for (RigidBody body : bodyList) {
            add(body);
        }
    }
    
    /**
     * @param bodyList list of static bodies to add to the engine
     */
    public void addStaticBodies(final List<StaticBody> bodyList) {
        for (StaticBody body : bodyList) {
            add(body);
        }
    }

    /**
     * Simply removes all objects from the engine.
     */
    public void reset() {
        bodyList.clear();
        rigidBodyList.clear();
        staticBodyList.clear();       
    }
    
    /**
     * @return unmodifiable view to a list of all bodies.
     */
    public List<Body> getUnmodifiableViewToBodyList() {
        return Collections.unmodifiableList(bodyList);
    } 
    
    /**
     * Updates the world.
     * @param dt change in time
     */
    public void update(final float dt) {
        final int stepCount = dt < timeStep ? 1 : (int) Math.ceil(dt / timeStep);
        final float stepLength = dt / (float) stepCount;
        
        // Doing this just once per update seems to make things less glitchy.
        applyExternalForces(dt);            
        
        for (int step = 0; step < stepCount; step++) {
            stepAndCollide(stepLength);
        }
    }
}
