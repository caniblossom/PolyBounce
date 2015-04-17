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
 * Represents a physics body that can't move.
 * @author Jani Salo
 */
public class StaticBody extends PhysicsBody {
    private final ConvexPolygon hull;
        
    /**
     * Constructs a new static body.
     * @param hull a convex polygon representing the shape of the body
     * @param mass value returned for any mass related methods
     * @param position position of the body
     * @param rotation rotation of the body
     */
    public StaticBody(final ConvexPolygon hull, final float mass, final Vector2 position, final float rotation) {
        super(mass, position, rotation, new Vector2(0.0f, 0.0f), 0.0f);
        this.hull = hull.rotateAndTranslate(getPosition().sum(hull.getVertexAverage()), getRotation(), getPosition());
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
    public Vector2 getCurrentCenterOfMass() {
        return getPosition().sum(hull.getVertexAverage());
    }

    /**
     * @param position position in world space (ignored)
     * @return always zero vector
     */
    @Override
    public Vector2 getVelocityAtPosition(Vector2 position) {
        return new Vector2(0.0f, 0.0f);
    }

    /**
     * @param dt time delta (ignored)
     * @return the hull in world space
     */
    @Override
    public ConvexPolygon getHullRelativeToTime(float dt) {
        return hull;
    }

    /**
     * Does nothing as the body is static.
     * @param position position in world space (ignored)
     * @param impulse impulse vector(ignored)
     */
    @Override
    public void applyImpulse(Vector2 position, Vector2 impulse) {}
}
