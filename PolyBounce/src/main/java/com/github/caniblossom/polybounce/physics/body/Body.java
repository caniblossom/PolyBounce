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
package com.github.caniblossom.polybounce.physics.body;

import com.github.caniblossom.polybounce.math.ConvexPolygon;
import com.github.caniblossom.polybounce.math.Vector2;

/**
 * Abstract base class for physics bodies.
 * @author Jani Salo
 */
public abstract class Body {
    private final float mass;
    private final float bounciness;
    private final float staticFriction;
    private final float dynamicFriction;
    
    private Vector2 position;
    private float rotation;
    private Vector2 velocity;
    private float angularVelocity;
    
    /**
     * Construct a new rigid body.
     * @param mass total mass of the body
     * @param bounciness bounciness of the body, value range [0, 1]
     * @param staticFriction static friction as a cosine of an angle, value range [0, 1]
     * @param dynamicFriction friction after overcoming static friction as relative resistance, value range [0, 1]
     * @param position position of the body
     * @param rotation rotation of the body
     * @param velocity velocity of the body
     * @param angularVelocity angular velocity of the body around center of mass
     */
    public Body(final float mass, final float bounciness, final float staticFriction, final float dynamicFriction, final Vector2 position, final float rotation, final Vector2 velocity, final float angularVelocity) {
        this.mass = mass;
        this.bounciness = bounciness;
        this.staticFriction = staticFriction;
        this.dynamicFriction = dynamicFriction;
        
        this.position = position;
        this.rotation = rotation;
        this.velocity = velocity;
        this.angularVelocity = angularVelocity;
    }

    /**
     * @return total mass of the body
     */
    public float getMass() {
        return mass;
    }
     
    /**
     * @return bounciness of the body
     */
    public float getBounciness() {
        return bounciness;
    }
    
    /**
     * @return static friction of the body
     */
    public float getStaticFriction() {
        return staticFriction;
    }
    
    /**
     * @return dynamic friction of the body
     */
    public float getDynamicFriction() {
        return dynamicFriction;
    }
    
    /**
     * Returns the position of the body, which is arbitrary and only guaranteed to
     * be at fixed distance from the center of the mass for this type of body.
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
     * @return moment of inertia around the center of the mass
     */
    public abstract float getMomentOfInertiaAroundCenterOfMass();
    
    /**
     * @return center of mass for the body in world space
     */
    public abstract Vector2 getCenterOfMass();

    /**
     * @param position point in world space
     * @return instantaneous velocity of the body at that point
     */
    public abstract Vector2 getVelocityAtPosition(final Vector2 position);

    /**
     * @return new convex polygon representing the hull of the body in world space currently. 
     */
    public abstract ConvexPolygon getHull();
    
    /**
     * Updates the position and rotation of the body
     * @param dt change in time
     */
    public abstract void update(final float dt); 

    /**
     * Applies an impulse to the body at given position in world space.
     * @param position position to apply the impulse at in world space
     * @param impulse vector representing the magnitude and direction of the impulse
     */
    public abstract void applyImpulse(final Vector2 position, final Vector2 impulse);
}
