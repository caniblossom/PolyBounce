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

import com.github.caniblossom.polybounce.math.ConvexPolygon;
import com.github.caniblossom.polybounce.math.Vector2;

// TODO Implement tests if possible.

/**
 * A very simple representation of a rigid body.
 * See: http://en.wikipedia.org/wiki/Collision_response
 * @author Jani Salo
 */
public class RigidBody extends PhysicsBody {
    private final ConvexPolygon hull;
    
    private final float massPerVertex;
    private final float momentOfInertiaAroundCenterOfMass;
        
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
        super(mass, position, rotation, velocity, angularVelocity);
        
        this.hull = hull;
        this.massPerVertex = getMass() / (float) hull.getUnmodifiableViewToVertexList().size();
        
        float sum = 0.0f;
        for (Vector2 p : hull.getUnmodifiableViewToVertexList()) {
            final float r = 1.0f * p.difference(hull.getVertexAverage()).length();
            sum += massPerVertex * r * r;
        }
        
        // TODO See if this needs a control parameter, like scale.
        this.momentOfInertiaAroundCenterOfMass = sum;
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
     * @return mass per vertex.
     */
    public float getMassPerVertex() {
        return massPerVertex;
    }
    
    /**
     * @return moment of inertia around the center of the mass
     */
    @Override
    public float getMomentOfInertiaAroundCenterOfMass() {
        return momentOfInertiaAroundCenterOfMass;
    }
    
    /**
     * @return center of mass for the body in world space
     */
    @Override
    public Vector2 getCurrentCenterOfMass() {
        return getHullRelativeToTime(0.0f).getVertexAverage();
    }
    
    /**
     * Returns the instantaneous velocity of the body at given position relative to world space.
     * Note that no checks are made whether the position actually lies inside the body.
     * @param position point in world space
     * @return instantaneous velocity of the body at that point
     */
    @Override
    public Vector2 getVelocityAtPosition(final Vector2 position) {
        final Vector2 r = position.difference(getCurrentCenterOfMass());
        final Vector2 tangent = new Vector2(-r.getY(), r.getX());
        return tangent.scale(getAngularVelocity()).sum(getVelocity());
    }

    /**
     * Returns a convex polygon representing the hull in world space relative to time.
     * @param dt change in time
     * @return new convex polygon representing the hull of the body in world space at current time + dt 
     */
    @Override
    public ConvexPolygon getHullRelativeToTime(final float dt) {
        return hull.rotateAndTranslate(hull.getVertexAverage(), getRotation() + dt * getAngularVelocity(), getPosition().sum(getVelocity().scale(dt)));
    }

    /**
     * Applies an impulse to the body at given position in world space.
     * Note that no checks are made that the position actually lies inside the body.
     * @param position position to apply the impulse at in world space
     * @param impulse vector representing the magnitude and direction of the impulse
     */
    @Override
    public void applyImpulse(final Vector2 position, final Vector2 impulse) {        
        setVelocity(getVelocity().sum(impulse.scale(1.0f / getMass())));
        
        final Vector2 r = position.difference(getCurrentCenterOfMass());
        setAngularVelocity(getAngularVelocity() + r.cross(impulse.normal()) * impulse.length() / momentOfInertiaAroundCenterOfMass);
    }
}
