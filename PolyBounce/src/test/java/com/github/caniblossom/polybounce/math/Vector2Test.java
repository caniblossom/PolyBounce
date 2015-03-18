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
public class Vector2Test {
    public Vector2Test() {}

    @Test
    public void testConstructors() {
        final Vector2 a = new Vector2(123.0f, 456.0f);
        final Vector2 b = new Vector2(123.0f);
        final Vector2 c = new Vector2();
        
        assertEquals(a.getX(), 123.0f, 0.0f);
        assertEquals(a.getY(), 456.0f, 0.0f);
        assertEquals(b.getX(), 123.0f, 0.0f);
        assertEquals(b.getY(), 123.0f, 0.0f);
        assertEquals(c.getX(),   0.0f, 0.0f);
        assertEquals(c.getY(),   0.0f, 0.0f);
    }
    
    @Test
    public void testGetX() {
        final Vector2 a = new Vector2(-9.0f, 1.0f);
        final Vector2 b = new Vector2( 0.0f, 1.0f);
        final Vector2 c = new Vector2( 9.0f, 1.0f);

        assertEquals(-9.0f, a.getX(), 0.0f);
        assertEquals(0.0f, b.getX(), 0.0f);
        assertEquals(9.0f, c.getX(), 0.0f);
    }

    @Test
    public void testGetY() {
        final Vector2 a = new Vector2(1.0f, -9.0f);
        final Vector2 b = new Vector2(1.0f,  0.0f);
        final Vector2 c = new Vector2(1.0f,  9.0f);

        assertEquals(-9.0f, a.getY(), 0.0f);
        assertEquals(0.0f, b.getY(), 0.0f);
        assertEquals(9.0f, c.getY(), 0.0f);
    }

    @Test
    public void testLength() {
        final Vector2 a = new Vector2(13.0f, 84.0f);
        final Vector2 b = new Vector2(77.0f, 36.0f);

        assertEquals(85.0f, a.length(), 0.0f);
        assertEquals(85.0f, b.length(), 0.0f);
    }

    @Test
    public void testSum() {
        final Vector2 a = new Vector2(1.0f, 2.0f);
        final Vector2 b = new Vector2(9.0f, 3.0f);
        
        final Vector2 sumAB = new Vector2(10.0f, 5.0f);

        assertTrue(a.sum(b).equals(sumAB));
        assertTrue(b.sum(a).equals(sumAB));
    }

    @Test
    public void testDifference() {
        final Vector2 a = new Vector2(1.0f, 2.0f);
        final Vector2 b = new Vector2(9.0f, 3.0f);
        
        final Vector2 differenceAB = new Vector2(-8.0f, -1.0f);
        final Vector2 differenceBA = new Vector2( 8.0f,  1.0f);

        assertTrue(a.difference(b).equals(differenceAB));
        assertTrue(b.difference(a).equals(differenceBA));
    }

    @Test
    public void testScale() {
        final Vector2 v = new Vector2(3.0f, 2.0f);
        final Vector2 a = v.scale(-3.0f);
        final Vector2 b = v.scale( 0.0f);
        final Vector2 c = v.scale( 3.0f);
   
        assertEquals(a.getX(), -9.0f, 0.0f);
        assertEquals(a.getY(), -6.0f, 0.0f);
        assertEquals(b.getX(),  0.0f, 0.0f);
        assertEquals(b.getY(),  0.0f, 0.0f);
        assertEquals(c.getX(),  9.0f, 0.0f);
        assertEquals(c.getY(),  6.0f, 0.0f);
    }
    
    @Test
    public void testDot() {
        final Vector2 a = new Vector2( 2.0f,  1.0f);
        final Vector2 b = new Vector2( 0.0f,  3.0f);
        final Vector2 c = new Vector2(-1.0f, -1.0f);

        assertEquals(a.dot(a),  5.0f, 0.0f);
        assertEquals(a.dot(b),  3.0f, 0.0f);
        assertEquals(a.dot(c), -3.0f, 0.0f);
    }

    @Test
    public void testNormal() {
        final Vector2 a = new Vector2(-7.0f, 9.0f);
        final Vector2 b = new Vector2( 0.0f, 0.0f);

        final float aLength = a.length();
        
        assertEquals(a.normal().getX(), a.getX() / a.length(), 0.0f);
        assertEquals(a.normal().getY(), a.getY() / a.length(), 0.0f);
        assertEquals(b.normal().getX(), 0.0f, 0.0f);
        assertEquals(b.normal().getY(), 0.0f, 0.0f);
    }
    
    @Test
    public void testEquals() {
        final Vector2 a = new Vector2(1.0f, 2.0f);
        final Vector2 b = new Vector2(0.0f, 2.0f);
        final Vector2 c = new Vector2(1.0f, 0.0f);
        final Vector2 v = new Vector2(1.0f, 2.0f);
        final Vector2 n = null;
        
        assertTrue(v.equals(v));
        assertTrue(v.equals(a));
        assertFalse(v.equals(b));
        assertFalse(v.equals(c));
        assertFalse(v.equals(n));
    }

    @Test
    public void testHash() {
        final Vector2 a = new Vector2(123.0f, 345.0f);
        final Vector2 b = new Vector2(456.0f, 567.0f);
        final Vector2 c = new Vector2(123.0f, 345.0f);
        final Vector2 d = new Vector2(456.0f, 567.0f);

        final Segment2 ab = new Segment2(a, b);
        final Segment2 cd = new Segment2(c, d);
        
        assertEquals(ab.hashCode(), ab.hashCode());
        assertEquals(ab.hashCode(), cd.hashCode());
    }
    
    // TODO Add test for toString.
}
