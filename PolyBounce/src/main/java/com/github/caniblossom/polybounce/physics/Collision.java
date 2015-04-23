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

import com.github.caniblossom.polybounce.math.Vector2;

/**
 * A class for representing an collision between two physics bodies.
 * @author Jani Salo
 */
public class Collision implements Comparable<Collision> {
    private final float distance;
    private final Vector2 activePosition;
    private final Vector2 passivePosition;
    private final Vector2 normal;

    /**
     * Constructs a new collision.
     * @param distance collision distance
     * @param activePosition collision position for active body
     * @param passivePosition collision position for passive body
     * @param normal collision normal
     */
    public Collision(final float distance, final Vector2 activePosition, final Vector2 passivePosition, final Vector2 normal) {
        this.distance        = distance;
        this.activePosition  = activePosition;
        this.passivePosition = passivePosition;
        this.normal          = normal;
    }
    
    /**
     * @return distance to intersection
     */
    public float getDistance() {
        return distance;
    }
    
    /**
     * @return intersection position for the active body
     */
    public Vector2 getActivePosition() {
        return activePosition;
    }

    /**
     * @return intersection position for the passive body
     */
    public Vector2 getPassivePosition() {
        return passivePosition;
    }

    /**
     * @return intersection normal
     */
    public Vector2 getNormal() {
        return normal;
    }
    
    /**
     * @param collision another collision to compare to
     * @return result based on the collision distance
     */
    @Override
    public int compareTo(Collision collision) {
        if (getDistance() < collision.getDistance()) {
            return -1;
        } else if (getDistance() > collision.getDistance()) {
            return 1;
        }

        return 0;
    }
}
