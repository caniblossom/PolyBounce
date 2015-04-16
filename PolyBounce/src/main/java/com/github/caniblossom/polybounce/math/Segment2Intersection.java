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
 * A class for representing an intersection between two 2-segments.
 * @author Jani Salo
 */
public class Segment2Intersection {
    private final boolean status;

    private final float distance;
    private final Vector2 position;

    /**
     * Constructs a new intersection with distance and position
     * @param distance distance to the intersection
     * @param position position of the intersection
     */
    public Segment2Intersection(final float distance, final Vector2 position) {
        this.status = true;
        this.distance = distance;
        this.position = position;
    }
    
    /**
     * Constructs a new object representing no intersection
     */
    public Segment2Intersection() {
        this.status = false;
        this.distance = Float.MAX_VALUE;
        this.position = new Vector2();
    }
    
    /**
     * @return true if an intersection did take place
     */
    public boolean didIntersect() {
        return status;
    }
    
    /**
     * @return distance to the intersection, if an intersection did take place, otherwise zero
     */
    public float getDistance() {
        return distance;
    }
    
    /**
     * @return position to the intersection, if an intersection took place, otherwise a default constructed vector
     */
    public Vector2 getPosition() {
        return position;
    }
}
