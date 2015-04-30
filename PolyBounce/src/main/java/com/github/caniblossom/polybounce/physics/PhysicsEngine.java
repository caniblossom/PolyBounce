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

import com.github.caniblossom.polybounce.math.BoundingBox;
import com.github.caniblossom.polybounce.math.Vector2;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A physics engine.
 * @author Jani Salo
 */
public class PhysicsEngine {    
    // Values of 1.0f or 2.0f seem to give best measured speed.
    private final static float SPATIAL_BUCKET_WIDTH = 2.0f; 
    private final static float SPATIAL_BUCKET_HEIGHT = 2.0f; 
    
    private final float timeStep;
    private final float inertia;
    private final Vector2 gravity;

    private final ArrayList<Body> bodyList;
    private final ArrayList<RigidBody> rigidBodyList;
    private final ArrayList<StaticBody> staticBodyList;        

    private final Collider collider;
    private final ArrayList<Body> collisionList;

    private SpatialTable spatialTable = null;
    
    // Computes correct spatial table size.
    private SpatialTable createSpatialTable(final BoundingBox box) {
        final int hBuckets = (int) Math.ceil(box.getWidth() / SPATIAL_BUCKET_WIDTH);
        final int vBuckets = (int) Math.ceil(box.getHeight() / SPATIAL_BUCKET_HEIGHT);
        return new SpatialTable(box, hBuckets, vBuckets);
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
    
    // Collides the bodies.
    private void collide(final float dt) {
        spatialTable.clear();
        spatialTable.addRigidBodyList(rigidBodyList);
        spatialTable.addStaticBodyList(staticBodyList);
        
        for (Body body : rigidBodyList) {
            collisionList.clear();
            spatialTable.findPossibleIntersections(collisionList, body, dt);
            
            collider.collide(body, collisionList, dt);
        }
    }

    // Steps the bodies.
    private void step(final float dt) {
        spatialTable.clear();
        spatialTable.addRigidBodyList(rigidBodyList);
        spatialTable.addStaticBodyList(staticBodyList);
 
        for (Body body : rigidBodyList) {
            collisionList.clear();
            spatialTable.findPossibleIntersections(collisionList, body, dt);

            if (collider.isSafeToUpdate(body, collisionList, dt)) {
                body.update(dt);
            }
        }
    }

    /**
     * Constructs a new physics engine.
     * @param timeStep stepping constant used for physics
     * @param inertia multiplier applied to all velocities per update
     * @param gravity vector representing external forces
     * @param worldBox a bounding box encompassing the game world
     */
    public PhysicsEngine(final float timeStep, final float inertia, Vector2 gravity, final BoundingBox worldBox) {
        assert timeStep > 0.0f;

        this.timeStep = timeStep;
        this.inertia = inertia;
        this.gravity = gravity;
        
        this.bodyList = new ArrayList();
        this.rigidBodyList = new ArrayList();
        this.staticBodyList = new ArrayList();
        
        this.collider = new Collider(); 
        this.collisionList = new ArrayList();
        
        this.spatialTable = createSpatialTable(worldBox);
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
     * Removes all objects from the engine and resets the world box
     * @param worldBox a bounding box encompassing the game world
     */
    public void reset(final BoundingBox worldBox) {
        bodyList.clear();
        rigidBodyList.clear();
        staticBodyList.clear();       

        this.spatialTable = createSpatialTable(worldBox);
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
            collide(stepLength);
            step(stepLength);
        }
    }
}
