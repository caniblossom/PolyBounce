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

import java.util.ArrayList;

/**
 * An utility class for building convex polygons.
 * @author Jani Salo
 */
public class PolygonBuilder {
    private final ArrayList<Vector2> vertexList; 
    
    /**
     * Constructs a new polygon builder.
     */
    public PolygonBuilder() {
        vertexList = new ArrayList();
    }
    
    /**
     * Constructs a box.
     * @param lowerLeft lower left corner of the box
     * @param upperRight upper right corner of the box
     * @return a new convex polygon shaped like a box
     * @throws IllegalArgumentException
     */
    public ConvexPolygon createBox(final Vector2 lowerLeft, final Vector2 upperRight) throws IllegalArgumentException {
        vertexList.clear();
        
        vertexList.add(lowerLeft);
        vertexList.add(new Vector2(upperRight.getX(), lowerLeft.getY()));
        vertexList.add(upperRight);
        vertexList.add(new Vector2(lowerLeft.getX(), upperRight.getY()));
        
        return ConvexPolygon.constructNew(vertexList);
    }
    
    /**
     * Constructs a regular polygon
     * @param position center of the polygon
     * @param radius radius 
     * @param vertexCount number of vertices
     * @return a new convex polygon shaped like a regular polygon
     * @throws IllegalArgumentException
     */
    public ConvexPolygon createRegularPolygon(final Vector2 position, final float radius, final int vertexCount) throws IllegalArgumentException {
        vertexList.clear();
         
        for (int i = 0; i < vertexCount; i++) {
            final float r = (float) i / (float) vertexCount * 2.0f * (float) Math.PI;
            vertexList.add(new Vector2(radius * (float) Math.cos(r) + position.getX(), radius * (float) Math.sin(r) + position.getY()));
        }
    
        return ConvexPolygon.constructNew(vertexList);
    }
}
