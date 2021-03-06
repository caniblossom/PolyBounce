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

import com.github.caniblossom.polybounce.renderer.misc.Color;
import com.github.caniblossom.polybounce.math.ConvexPolygon;
import com.github.caniblossom.polybounce.math.Segment2;
import com.github.caniblossom.polybounce.math.Vector2;
import com.github.caniblossom.polybounce.renderer.shader.SimpleShaderVertex;
import java.util.List;

/**
 * An utility class for tessellating convex polygons into format used by the simple shader.
 * @author Jani Salo
 */
public class Tessellator {
    private List<Float> output = null;

    // Adds front triangle to the output list.
    private void outputFrontTriangle(final Vector2 a, final Vector2 b, final Vector2 c, final Color color, final float depth) {
        // There is currently no method for adding back triangles, as the camera will 
        // never look at the scene from such an angle that they would actually be visible.
        new SimpleShaderVertex(a.getX(), a.getY(), depth, color.getRed(), color.getGreen(), color.getBlue(), 0.0f, 0.0f, 1.0f).addToList(output);
        new SimpleShaderVertex(b.getX(), b.getY(), depth, color.getRed(), color.getGreen(), color.getBlue(), 0.0f, 0.0f, 1.0f).addToList(output);
        new SimpleShaderVertex(c.getX(), c.getY(), depth, color.getRed(), color.getGreen(), color.getBlue(), 0.0f, 0.0f, 1.0f).addToList(output);
    }
    
    // Adds two triangles making up a side quad to the output list.
    private void outputSideQuad(final Segment2 s, final Color color, final float frontDepth, final float backDepth) {
        new SimpleShaderVertex(s.getA().getX(), s.getA().getY(), frontDepth, color.getRed(), color.getGreen(), color.getBlue(), s.getRightNormal().getX(), s.getRightNormal().getY(), 0.0f).addToList(output);
        new SimpleShaderVertex(s.getA().getX(), s.getA().getY(), backDepth, color.getRed(), color.getGreen(), color.getBlue(), s.getRightNormal().getX(), s.getRightNormal().getY(), 0.0f).addToList(output);
        new SimpleShaderVertex(s.getB().getX(), s.getB().getY(), backDepth, color.getRed(), color.getGreen(), color.getBlue(), s.getRightNormal().getX(), s.getRightNormal().getY(), 0.0f).addToList(output);

        new SimpleShaderVertex(s.getB().getX(), s.getB().getY(), backDepth, color.getRed(), color.getGreen(), color.getBlue(), s.getRightNormal().getX(), s.getRightNormal().getY(), 0.0f).addToList(output);
        new SimpleShaderVertex(s.getB().getX(), s.getB().getY(), frontDepth, color.getRed(), color.getGreen(), color.getBlue(), s.getRightNormal().getX(), s.getRightNormal().getY(), 0.0f).addToList(output);
        new SimpleShaderVertex(s.getA().getX(), s.getA().getY(), frontDepth, color.getRed(), color.getGreen(), color.getBlue(), s.getRightNormal().getX(), s.getRightNormal().getY(), 0.0f).addToList(output);
    }
    
    /**
     * Constructs a new tessellator with output as target
     * @param output output list or null
     */
    public Tessellator(List output) {
        this.output = output;
    }
    
    /**
     * @param polygon convex polygon to be tessellated
     * @param color color for the polygon
     * @param frontDepth z component of the front face
     * @param backDepth z component of the back face
     */
    public void generateTriangles(final ConvexPolygon polygon, final Color color, final float frontDepth, final float backDepth) {
        if (output == null) {
            return;
        }

        for (Segment2 s : polygon.getUnmodifiableViewToSegmentList()) {
            outputFrontTriangle(s.getA(), s.getB(), polygon.getVertexAverage(), color, frontDepth);
            outputSideQuad(s, color, frontDepth, backDepth);
        }
    }
}
