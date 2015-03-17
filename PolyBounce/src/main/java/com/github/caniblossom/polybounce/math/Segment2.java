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
package com.github.caniblossom.polybounce.math;

/**
 * A class representing a directional, immutable segment made of two 2-vectors.
 * @author Jani Salo
 */
public class Segment2 {
    private final Vector2 a, b, ab;
    private final Vector2 rightNormal;
    
    /**
     * Constructs a new segment from a to b.
     * @param a start-point of the segment
     * @param b end-point of the segment
     */
    public Segment2(final Vector2 a, final Vector2 b) {
        this.a = a;
        this.b = b;

        // Precompute a vector from a to b.
        this.ab = b.difference(a);
        
        // Precompute a normal orthogonal to the line defined by the segment.
        this.rightNormal = (new Vector2(ab.getY(), -ab.getX())).normal();
    }
    
    /**
     * Constructs a new segment with two default 2-vectors.
     */
    public Segment2() {
        this(new Vector2(), new Vector2());
    }
    
    /**
     * @return start-point of the segment.
     */
    public Vector2 getA() {
        return a;
    }

    /**
     * @return end-point of the segment.
     */
    public Vector2 getB() {
        return b;
    }

    /**
     * @return vector from a to b.
     */
    public Vector2 getAB() {
        return ab;
    }

    /**
     * @return right normal for the line defined by the segment
     */
    public Vector2 getRightNormal() {
        return rightNormal;
    }
    
    /**
     * Returns a value representing the position of the point to be tested
     * relative to the line defined by this segment. The value is negative for
     * points on left, positive for points on right and zero for points on line.
     * @param v point to be tested
     * @return the dot product between right normal and a vector from a to v
     */
    public float testPoint(final Vector2 v) {
        return rightNormal.dot(v.difference(a));
    }
    
    // TODO Add projection.
    // TODO Add intersection test.
}
