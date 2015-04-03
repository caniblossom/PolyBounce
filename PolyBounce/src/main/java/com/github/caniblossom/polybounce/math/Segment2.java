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

// TODO Implement full tests.

/**
 * A class representing a directional, immutable segment made of two 2-vectors.
 * @author Jani Salo
 */
public class Segment2 {
    private final Vector2 a, b, ab;
    
    private final Vector2 normal;
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
        
        // Precompute normals.
        this.normal = (new Vector2(ab.getX(), ab.getY())).normal();
        this.rightNormal = new Vector2(normal.getY(), -normal.getX());
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
     * @return normal for the line defined by the segment
     */
    public Vector2 getNormal() {
        return normal;
    }

    /**
     * @return normal orthogonal to the line defined by the segment
     */
    public Vector2 getRightNormal() {
        return rightNormal;
    }
    
     /**
     * Projects a point on the line defined by the segment.
     * @param v the point to be projected
     * @return the dot product between the segment normal and av
     */
    public float projectPointOnNormal(final Vector2 v) {
        return normal.dot(v.difference(a));
    }

    /**
     * Projects a point on a line orthogonal to the line defined by the segment. 
     * @param v the point to be projected
     * @return the dot product between right normal and av
     */
    public float projectPointOnRightNormal(final Vector2 v) {
        return rightNormal.dot(v.difference(a));
    }

    /**
     * Intersects another segment against this segment. Considers cases where 
     * the segments lie on the same line always as non-intersecting.
     * @param s segment to be intersected against this segment
     * @return intersection result
     */
    public Intersection intersect(final Segment2 s) {
        final float pa = projectPointOnRightNormal(s.getA());
        final float pb = projectPointOnRightNormal(s.getB());
        
        // Return if no intersection is possible or if the segments lie on the same line.
        if (pa * pb > 0.0f || pa == 0.0f && pb == 0.0f) {
            return new Intersection();
        } 

        final float shortestDistance = Math.abs(rightNormal.dot(s.getA().difference(getA())));
        final float cosAngle = Math.abs(rightNormal.dot(s.getNormal())); 

        final float distance = (cosAngle > 0.0f) ? (shortestDistance / cosAngle) : 0.0f;
        final Vector2 position = s.getA().sum(s.getNormal().scale(distance));
        
        // At this point we have the intersection on the line defined by this segment. 
        // The last thing to do is to check whether the intersection lies on the segment itself.
        final float projection = projectPointOnNormal(position);
        if (projection < 0.0f || projection > ab.length()) {
            return new Intersection();
        }
        
        return new Intersection(distance, position);
    }
    
    /**
     * Checks if this segment share a vertex with target segment.
     * @param s segment to test against
     * @return true if the segments share a vertex
     */
    public boolean sharesVertexWith(Segment2 s) {
        return a.equals(s.a) || a.equals(s.b) || b.equals(s.a) || b.equals(s.b);
    }
    
    /**
     * @param o segment to compare to
     * @return true if the vectors are equal
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Segment2) {
            final Segment2 s = (Segment2) o;
            return a.equals(s.a) && b.equals(s.b);
        }
        
        return false;
    }

    /**
     * @return hash
     */
    @Override
    public int hashCode() {
        int hash = 71;
        hash = 71 * hash + a.hashCode();
        hash = 71 * hash + b.hashCode();
        return hash;
    }
    
    /**
     * @return a string representing the segment
     */
    @Override
    public String toString() {
        return a.toString() + " -> " + b.toString();
    }
}
