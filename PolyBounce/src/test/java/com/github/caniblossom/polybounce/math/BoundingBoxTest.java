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
public class BoundingBoxTest {
    @Test
    public void testBasicGetters() {
        final BoundingBox box = new BoundingBox(new Vector2(-14.0f, 538.0f), 53.0f, 111.0f);
        
        assertEquals(box.getPosition().getX(), -14.0f, 0.0f);
        assertEquals(box.getPosition().getY(), 538.0f, 0.0f);        
        assertEquals(box.getWidth(), 53.0f, 0.0f);
        assertEquals(box.getHeight(), 111.0f, 0.0f);        
    }
    
    @Test
    public void testGetMaximum() {
        final BoundingBox box = new BoundingBox(new Vector2(-64.0f, 58.0f), 12.0f, 84.0f);
        
        assertEquals(box.getMaximum().getX(), -52.0f, 0.0f);
        assertEquals(box.getMaximum().getY(), 142.0f, 0.0f);
    }
    
    @Test
    public void testDoesIntersect() {
        final BoundingBox boxX = new BoundingBox(new Vector2(-5.00f, -3.00f), 1.0f, 1.0f);
        final BoundingBox boxA = new BoundingBox(new Vector2(-5.75f, -3.75f), 1.0f, 1.0f);
        final BoundingBox boxB = new BoundingBox(new Vector2(-4.25f, -3.75f), 1.0f, 1.0f);
        final BoundingBox boxC = new BoundingBox(new Vector2(-4.25f, -2.25f), 1.0f, 1.0f);
        final BoundingBox boxD = new BoundingBox(new Vector2(-5.75f, -2.25f), 1.0f, 1.0f);
    
        final BoundingBox boxY = new BoundingBox(new Vector2(2.0f, 2.0f), 1.0f, 1.0f);
        final BoundingBox boxE = new BoundingBox(new Vector2(1.0f, 2.0f), 1.0f, 1.0f);
        final BoundingBox boxF = new BoundingBox(new Vector2(2.0f, 1.0f), 1.0f, 1.0f);
        final BoundingBox boxG = new BoundingBox(new Vector2(3.0f, 2.0f), 1.0f, 1.0f);
        final BoundingBox boxH = new BoundingBox(new Vector2(2.0f, 3.0f), 1.0f, 1.0f);
        
        assertTrue(boxX.doesIntersect(boxX));
        assertTrue(boxX.doesIntersect(boxA));
        assertTrue(boxX.doesIntersect(boxB));
        assertTrue(boxX.doesIntersect(boxC));
        assertTrue(boxX.doesIntersect(boxD));

        assertFalse(boxA.doesIntersect(boxB));
        assertFalse(boxA.doesIntersect(boxC));
        assertFalse(boxA.doesIntersect(boxD));
        
        assertFalse(boxB.doesIntersect(boxA));
        assertFalse(boxB.doesIntersect(boxC));
        assertFalse(boxB.doesIntersect(boxD));

        assertFalse(boxC.doesIntersect(boxA));
        assertFalse(boxC.doesIntersect(boxB));
        assertFalse(boxC.doesIntersect(boxD));

        assertFalse(boxD.doesIntersect(boxA));
        assertFalse(boxD.doesIntersect(boxB));
        assertFalse(boxD.doesIntersect(boxC));
        
        assertTrue(boxY.doesIntersect(boxE));
        assertTrue(boxY.doesIntersect(boxF));
        assertTrue(boxY.doesIntersect(boxG));
        assertTrue(boxY.doesIntersect(boxH));

        assertTrue(boxE.doesIntersect(boxF));
        assertFalse(boxE.doesIntersect(boxG));
        assertTrue(boxE.doesIntersect(boxH));

        assertTrue(boxF.doesIntersect(boxE));
        assertTrue(boxF.doesIntersect(boxG));
        assertFalse(boxF.doesIntersect(boxH));

        assertFalse(boxG.doesIntersect(boxE));
        assertTrue(boxG.doesIntersect(boxF));
        assertTrue(boxG.doesIntersect(boxH));

        assertTrue(boxH.doesIntersect(boxE));
        assertFalse(boxH.doesIntersect(boxF));
        assertTrue(boxH.doesIntersect(boxG));
    }

    @Test
    public void testCombine() {
        final BoundingBox boxA = new BoundingBox(new Vector2(-5.0f, -3.0f), 2.0f, 2.0f);
        final BoundingBox boxB = new BoundingBox(new Vector2(-4.5f, -2.5f), 1.0f, 1.0f);
        final BoundingBox boxC = new BoundingBox(new Vector2( 0.0f,  0.0f), 1.0f, 1.0f);
        
        final BoundingBox boxAB = boxA.combine(boxB);
        final BoundingBox boxCA = boxC.combine(boxA);
        
        assertEquals(boxAB.getPosition().getX(), boxA.getPosition().getX(), 0.0f);
        assertEquals(boxAB.getPosition().getY(), boxA.getPosition().getY(), 0.0f);
        assertEquals(boxAB.getWidth(), boxA.getWidth(), 0.0f);
        assertEquals(boxAB.getHeight(), boxA.getHeight(), 0.0f);
        
        assertEquals(boxCA.getPosition().getX(), boxA.getPosition().getX(), 0.0f);
        assertEquals(boxCA.getPosition().getY(), boxA.getPosition().getY(), 0.0f);
        assertEquals(boxCA.getWidth(), 6.0f, 0.0f);
        assertEquals(boxCA.getHeight(), 4.0f, 0.0f);
    }
}
