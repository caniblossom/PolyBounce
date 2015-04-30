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

/**
 * Represents a physics body that can't move.
 * @author Jani Salo
 */
public class StaticBody extends Body {
    private final ConvexPolygon hull;
        
    /**
     * Constructs a new static body.
     * @param hull a convex polygon representing the shape of the body
     * @param mass value returned for any mass related methods
     * @param bounciness bounciness of the body, value range [0, 1]
     * @param staticFriction static friction as a cosine of an angle, value range [0, 1]
     * @param dynamicFriction friction after overcoming static friction as relative resistance, value range [0, 1]
     * @param position position of the body
     * @param rotation rotation of the body
     */
    public StaticBody(final ConvexPolygon hull, final float mass, final float bounciness, final float staticFriction, final float dynamicFriction, final Vector2 position, final float rotation) {
        super(mass, bounciness, staticFriction, dynamicFriction, position, rotation, new Vector2(0.0f, 0.0f), 0.0f);
        this.hull = hull.rotateAndTranslate(getPosition().sum(hull.getVertexAverage()), getRotation(), getPosition());
    }

    /**
     * Copy constructor.
     * @param body body to copy
     */
    public StaticBody(final StaticBody body) {
        super(body.getMass(), body.getBounciness(), body.getStaticFriction(), body.getDynamicFriction(), new Vector2(body.getPosition()), body.getRotation(), new Vector2(body.getVelocity()), body.getAngularVelocity());        
        this.hull = new ConvexPolygon(body.hull);
    }
    /**
     * @return parameter given for mass
     */
    @Override
    public float getMomentOfInertiaAroundCenterOfMass() {
        return getMass();
    }

    /**
     * @return hull center in world space
     */
    @Override
    public Vector2 getCenterOfMass() {
        return getPosition().sum(hull.getVertexAverage());
    }

    /**
     * @param position ignored
     * @return always zero vector
     */
    @Override
    public Vector2 getVelocityAtPosition(Vector2 position) {
        return new Vector2(0.0f, 0.0f);
    }
    
    /**
     * @return the hull in world space
     */
    @Override
    public ConvexPolygon getHull() {
        return hull;
    }

    /**
     * Does nothing as the body is static.
     * @param dt ignored
     */
    @Override
    public void update(float dt) {}
    
    /**
     * Does nothing as the body is static.
     * @param position ignored
     * @param impulse ignored
     */
    @Override
    public void applyImpulse(Vector2 position, Vector2 impulse) {}
}
