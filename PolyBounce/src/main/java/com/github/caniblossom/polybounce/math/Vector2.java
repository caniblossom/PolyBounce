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
 * A class representing an immutable floating point 2-vector.
 * @author Jani Salo
 */
public class Vector2 {
    private final float x, y;
    
    /**
     * Constructs a new 2-vector.
     * @param x x component
     * @param y y component
     */
    public Vector2(final float x, final float y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Constructs a new 2-vector
     * @param f value to set all components to
     */
    public Vector2(final float f) {
        this(f, f);
    }

    /**
     * Constructs a new 2-vector with all components initialized to zero
     */
    public Vector2() {
        this(0.0f, 0.0f);
    }
    
    /**
     * Copy constructor.
     * @param v vector to copy
     */
    public Vector2(final Vector2 v) {
        this(v.x, v.y);
    }
    
    /**
     * @return x component
     */
    public final float getX() {
        return x;
    } 
    
    /**
     * @return y component
     */
    public final float getY() {
        return y;
    } 
    
    /**
     * @return length of the vector
     */
    public float length() {
        return (float) Math.sqrt((double) dot(this));
    }

    /**
     * @param rhs right hand side of the addition
     * @return new vector representing the sum of this and rhs
     */
    public Vector2 sum(final Vector2 rhs) {
        return new Vector2(x + rhs.x, y + rhs.y);
    }

    /**
     * @param rhs right hand side of the subtraction
     * @return new vector representing the difference of this and rhs
     */
    public Vector2 difference(final Vector2 rhs) {
        return new Vector2(x - rhs.x, y - rhs.y);
    }

    /**
     * @param lhs left hand side of the scalar multiplication
     * @return new vector representing this multiplied by lhs
     */
    public Vector2 scale(final float lhs) {
        return new Vector2(lhs * x, lhs * y);
    }
    
    /**
     * @param rhs right hand side of the dot product
     * @return dot product of this and rhs
     */
    public float dot(final Vector2 rhs) {
        return x * rhs.x + y * rhs.y;
    }
    
    /**
     * @param rhs right hand side of the cross product
     * @return equivalent of right handed cross product in two dimensions
     */
    public float cross(final Vector2 rhs) {
        return x * rhs.y - y * rhs.x;
    }
    
    /**
     * @return a normal for this vector
     */
    public Vector2 normal() {
        final float length = length();
        
        if (length == 0.0f) {
            return new Vector2();
        } else {
            return new Vector2(x / length, y / length);        
        }
    }
    
    /**
     * Returns a copy of this vector rotated around arbitary point.
     * @param origo point to rotate around
     * @param angle angle in radians (counter-clockwise)
     * @return new rotated vector
     */
    public Vector2 rotation(final Vector2 origo, final float angle) {
        final float u = x - origo.x;
        final float v = y - origo.y;

        final float c = (float) Math.cos(angle);
        final float s = (float) Math.sin(angle);
        
        // It rotates, but will it blend?
        return new Vector2(origo.x + c * u - s * v, origo.y + s * u + c * v);
    }
    
    /**
     * @param o vector to compare to
     * @return true if and only if the components are equal
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Vector2) {
            final Vector2 v = (Vector2) o;
            return x == v.x && y == v.y;
        }
        
        return false;
    }

    /**
     * @return hash
     */
    @Override
    public int hashCode() {
        int hash = 13;
        hash = 13 * hash + Float.floatToIntBits(this.x);
        hash = 13 * hash + Float.floatToIntBits(this.y);
        return hash;
    }
    
    /**
     * @return a string representing the vector
     */
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
