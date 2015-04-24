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

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Jani Salo
 */
public class PolygonBuilderTest {
    @Test
    public void testCreateBox() {
        final PolygonBuilder builder = new PolygonBuilder();
        
        try {
            // PIT go home you are drunk.
            for (int i = 0; i < 7; i++) {
                final ConvexPolygon box = builder.createBox(new Vector2(-3.0f, -5.0f), new Vector2(9.0f, 4.0f)); 

                assertTrue(box.getVertexAverage().equals(new Vector2(3.0f, -0.5f)));
                assertTrue(box.getUnmodifiableViewToVertexList().size() == 4);
                assertTrue(box.getUnmodifiableViewToSegmentList().size() == 4);

                final BoundingBox bounds = box.getBoundingBox();

                assertEquals(bounds.getPosition().getX(), -3.0f, 0.0f);
                assertEquals(bounds.getPosition().getY(), -5.0f, 0.0f);
                assertEquals(bounds.getWidth(), 12.0f, 0.0f);
                assertEquals(bounds.getHeight(), 9.0f, 0.0f);
            }
        } catch (Exception e) {
            fail();
        }
    }
    
    @Test
    public void testCreateRegularPolygon() {
        final PolygonBuilder builder = new PolygonBuilder();
        
        try {
            // Number of loops chosen by a fair roll on D6.
            for (int i = 0; i < 7; i++) {
                final ConvexPolygon poly = builder.createRegularPolygon(new Vector2(3.0f, 7.0f), 3.0f, 123);

                assertEquals(poly.getVertexAverage().getX(), 3.0f, 0.001f);
                assertEquals(poly.getVertexAverage().getY(), 7.0f, 0.001f);

                assertTrue(poly.getUnmodifiableViewToVertexList().size() == 123);
                assertTrue(poly.getUnmodifiableViewToSegmentList().size() == 123);

                final BoundingBox bounds = poly.getBoundingBox();

                assertEquals(bounds.getPosition().getX(), 0.0f, 0.01f);
                assertEquals(bounds.getPosition().getY(), 4.0f, 0.01f);
                assertEquals(bounds.getWidth(), 6.0f, 0.01f);
                assertEquals(bounds.getHeight(), 6.0f, 0.01);
            }
        } catch (Exception e) {
            fail();
        }
    }
}
