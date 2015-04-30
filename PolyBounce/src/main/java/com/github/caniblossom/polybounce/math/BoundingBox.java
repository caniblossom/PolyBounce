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
 * A class for representing an immutable axis aligned bounding box.
 * @author Jani Salo
 */
public class BoundingBox {
    private final Vector2 position;

    private final float width;
    private final float height;

    /**
     * Constructs a new bounding box with parameters
     * @param position lower left corner of the bounding box
     * @param width width of the bounding box
     * @param height height of the bounding box
     */
    public BoundingBox(final Vector2 position, final float width, final float height) {
        assert width >= 0.0f;
        assert height >= 0.0f;
        
        this.position = position;
        this.width = width;
        this.height = height;
    }
    
    /**
     * Copy constructor
     * @param box bounding box to copy.
     */
    public BoundingBox(final BoundingBox box) {
        this.position = new Vector2(box.position);
        this.width = box.width;
        this.height = box.height;
    }
    
    /**
     * @return lower left corner of the box
     */
    public Vector2 getPosition() {
        return position;
    }
    
    /**
     * @return width of the box
     */
    public float getWidth() {
        return width;
    }
    
    /**
     * @return height of the box
     */
    public float getHeight() {
        return height;
    }
    
    /**
     * @return upper right corner of the box.
     */
    public Vector2 getMaximum() {
        return new Vector2(position.getX() + width, position.getY() + height);
    }
    
    /**
     * @param box another bounding box.
     * @return true if an only if the this box shares area with the other.
     */
    public boolean doesIntersect(final BoundingBox box) {
        if (position.getX() > box.getMaximum().getX() || getMaximum().getX() < box.getPosition().getX()) {
            return false;
        }

        if (position.getY() > box.getMaximum().getY() || getMaximum().getY() < box.getPosition().getY()) {
            return false;
        }

        return true;
    }
    
    /**
     * Combines this box with another box to create a new box that contains both.
     * @param box another box to combine with
     * @return a new box representing the smallest possible box that can contain both of the boxes.
     */
    public BoundingBox combine(final BoundingBox box) {
        final Vector2 newPosition = new Vector2(Math.min(position.getX(), box.position.getX()), Math.min(position.getY(), box.position.getY()));
        
        final float newWidth  = Math.max(getMaximum().getX(), box.getMaximum().getX()) - newPosition.getX();
        final float newHeight = Math.max(getMaximum().getY(), box.getMaximum().getY()) - newPosition.getY();
        
        return new BoundingBox(newPosition, newWidth, newHeight);
    }
}
