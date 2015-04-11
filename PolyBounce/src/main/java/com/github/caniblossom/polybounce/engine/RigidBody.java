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
import com.github.caniblossom.polybounce.math.Vector2;

// TODO Implement tests.

/**
 * A very simple representation of a rigid body.
 * See: http://en.wikipedia.org/wiki/Collision_response
 * @author Jani Salo
 */
public class RigidBody {
    private final ConvexPolygon hull;
    
    private final float mass;
    private final float massPerVertex;
    private final float momentOfInertiaAroundCenterOfMass;
    
    private Vector2 position;
    private float rotation;
    private Vector2 velocity;
    private float angularVelocity;
    
    /**
     * Construct a new rigid body.
     * @param hull a convex polygon representing the shape of the body
     * @param mass total mass of the body
     * @param position position of the body
     * @param rotation rotation of the body
     * @param velocity velocity of the body
     * @param angularVelocity angular velocity of the body around center of mass
     */
    public RigidBody(final ConvexPolygon hull, final float mass, final Vector2 position, final float rotation, final Vector2 velocity, final float angularVelocity) {
        this.hull = hull;
        this.mass = mass;
        this.massPerVertex = mass / (float) hull.getUnmodifiableViewToVertexList().size();
        
        float sum = 0.0f;
        
        for (Vector2 p : hull.getUnmodifiableViewToVertexList()) {
            final float r = p.difference(hull.getVertexAverage()).length();
            sum += massPerVertex * r * r;
        }
        
        this.momentOfInertiaAroundCenterOfMass = sum;

        this.position = position;
        this.rotation = rotation;
        this.velocity = velocity;
        this.angularVelocity = angularVelocity;
    }

    /**
     * Constructs a new rigid body with rest of the parameters set to zero
     * @param hull a convex polygon representing the shape for the body
     * @param mass total mass of the body
     */
    public RigidBody(final ConvexPolygon hull, final float mass) {
        this(hull, mass, new Vector2(0.0f, 0.0f), 0.0f, new Vector2(0.0f, 0.0f), 0.0f);
    }
    
    /**
     * @return total mass of the body
     */
    public float getMass() {
        return mass;
    }

    /**
     * @return moment of inertia around the center of the mass
     */
    public float getMomentOfInertiaAroundCenterOfMass() {
        return momentOfInertiaAroundCenterOfMass;
    }
    
    /**
     * @return center of mass for the body in world space
     */
    public Vector2 getCurrentCenterOfMass() {
        return position.sum(hull.getVertexAverage());
    }
     
    /**
     * @return current position in world space
     */
    public Vector2 getPosition() {
        return position;
    }
    
    /**
     * @return current rotation relative to center of mass
     */
    public float getRotation() {
        return rotation;
    }
    
    /**
     * @return current velocity
     */
    public Vector2 getVelocity() {
        return velocity;
    }
    
    /**
     * @return current angular velocity
     */
    public float getAngularVelocity() {
        return angularVelocity;
    }

    /**
     * Returns the instantaneous velocity of the body at given position relative to world space.
     * Note that no checks are made whether the position actually lies inside the body.
     * @param position point in world space
     * @return instantaneous velocity of the body at that point
     */
    public Vector2 getVelocityAtPosition(final Vector2 position) {
         final Vector2 r = position.difference(getCurrentCenterOfMass());
         final Vector2 tangent = new Vector2(-r.getY(), r.getX());
         return tangent.scale(angularVelocity).sum(velocity);
    }

    /**
     * Returns a convex polygon representing the hull in world space relative to time
     * @param dt change in time
     * @return new convex polygon representing the hull of the body in world space at current time + dt 
     */
    public ConvexPolygon getHullRelativeToTime(final float dt) {
        return hull.rotateAndTranslate(hull.getVertexAverage(), rotation + dt * angularVelocity, position.sum(velocity.scale(dt)));
    }

    /**
     * @param position new position
     */
    public void setPosition(final Vector2 position) {
        this.position = position;
    }

    /**
     * @param rotation new rotation
     */
    public void setRotation(final float rotation) {
        this.rotation = rotation;
    }    

    /**
     * @param velocity new velocity 
     */
    public void setVelocity(final Vector2 velocity) {
        this.velocity = velocity;
    }

    /**
     * @param velocity new angular velocity
     */ 
    public void setAngularVelocity(final float velocity) {
        angularVelocity = velocity;
    }    
        
    /**
     * Updates the position and rotation of the body
     * @param dt change in time
     */
    public void update(final float dt) {
        position = position.sum(velocity.scale(dt));
        rotation += dt * angularVelocity;
    } 

    /**
     * Applies an impulse to the body at given position in world space.
     * Note that no checks are made that the position actually lies inside the body.
     * @param position position to apply the impulse at in world space
     * @param impulse vector representing the magnitude and direction of the impulse
     */
    public void applyImpulse(final Vector2 position, final Vector2 impulse) {
        velocity = velocity.sum(impulse.scale(1.0f / mass));

        // TODO Verify that this is correct.
        final Vector2 r = position.difference(getCurrentCenterOfMass());
        angularVelocity += r.cross(impulse.normal()) * impulse.length() / momentOfInertiaAroundCenterOfMass;
    }
}
