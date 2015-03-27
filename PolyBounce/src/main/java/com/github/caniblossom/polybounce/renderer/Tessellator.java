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
package com.github.caniblossom.polybounce.renderer;

import com.github.caniblossom.polybounce.math.ConvexPolygon;
import com.github.caniblossom.polybounce.math.Segment2;
import com.github.caniblossom.polybounce.math.Vector2;
import java.nio.FloatBuffer;
import java.util.List;

// TODO Add tests for this.
// TODO Mock NetBeans for not recognizing the word tessellate.

/**
 * An utility class for tessellating convex polygons into float data (triangles) for rendering.
 * @author Jani Salo
 */
public class ConvexPolygonTessellator {
    private FloatBuffer output = null;
     

    /**
     * Adds a position to the output list.
     * @param pos position
     * @param depth z component
     */
    private void outputPosition(final Vector2 pos, final float depth) {
        output.put(pos.getX());
        output.put(pos.getY());
        output.put(depth);
    }
    /**
     * Adds a triangle to the output list
     * @param a first vertex
     * @param b second vertex
     * @param c third vertex
     * @param depth z component
     */
    private void outputTriangle(final Vector2 a, final Vector2 b, final Vector2 c, final float depth) {
        outputPosition(a, depth);
        outputPosition(b, depth);
        outputPosition(c, depth);
    }
    
    /**
     * Constructs a new tessellator with output as target
     * @param output output list or null
     */
    public ConvexPolygonTessellator(FloatBuffer output) {
        this.output = output;
    }
    
    /**
     * @param polygon convex polygon to be tessellated
     * @param frontDepth z component of the front face
     * @param backDepth z component of the back face
     */
    public void generateTriangles(final ConvexPolygon polygon, final float frontDepth, final float backDepth) {
        if (output == null) {
            return;
        }

        for (Segment2 s : polygon.getUnmodifiableViewToSegmentList()) {
            outputTriangle(s.getA(), s.getB(), polygon.getVertexAverage(), frontDepth);
            outputTriangle(s.getB(), s.getA(), polygon.getVertexAverage(), backDepth);
        }
        
        // TODO Generate triangles for the sides as well.
    }
}
