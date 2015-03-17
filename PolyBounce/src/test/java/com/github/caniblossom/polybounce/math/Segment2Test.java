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
 *
 * @author Jani Salo
 */
public class Segment2Test {
    @Test
    public void testConstructors() {
        final Vector2 a = new Vector2( 1.0f, 1.0f);
        final Vector2 b = new Vector2(-3.0f, 3.0f);
        final Vector2 o = new Vector2();
        
        final Segment2 u = new Segment2(a, b); 
        final Segment2 v = new Segment2(); 

        assertTrue(u.getA().equals(a));
        assertTrue(u.getB().equals(b));
        assertTrue(v.getA().equals(o));
        assertTrue(v.getB().equals(o));
    }

    @Test
    public void testGetA() {
        final Vector2 a = new Vector2( 1.0f,  1.0f);
        final Vector2 b = new Vector2(-1.0f, -1.0f);

        final Segment2 u = new Segment2(a, b); 
        
        assertTrue(u.getA().equals(a));
    }

    @Test
    public void testGetB() {
        final Vector2 a = new Vector2( 1.0f,  1.0f);
        final Vector2 b = new Vector2(-1.0f, -1.0f);

        final Segment2 u = new Segment2(a, b); 
        
        assertTrue(u.getB().equals(b));
    }

    @Test
    public void testGetAB() {
        final Vector2 a = new Vector2( 1.0f,  1.0f);
        final Vector2 b = new Vector2(-1.0f, -1.0f);
        final Vector2 ab = b.difference(a);
        
        final Segment2 u = new Segment2(a, b); 
        
        assertTrue(u.getAB().equals(ab));
    }

    @Test
    public void testGetRightNormal() {
        final Vector2 a = new Vector2(  0.0f,   0.0f);
        final Vector2 b = new Vector2(123.0f,   0.0f);
        final Vector2 c = new Vector2(  0.0f, 456.0f);

        final Segment2 u = new Segment2(a, b); 
        final Segment2 v = new Segment2(a, c); 
        final Segment2 w = new Segment2(a, a); 
        
        final Vector2 uRightNormal = new Vector2(0.0f, -1.0f); 
        final Vector2 vRightNormal = new Vector2(1.0f,  0.0f); 
        final Vector2 wRightNormal = new Vector2(); 

        assertTrue(u.getRightNormal().equals(uRightNormal));
        assertTrue(v.getRightNormal().equals(vRightNormal));
        assertTrue(w.getRightNormal().equals(wRightNormal));
    }

    @Test
    public void testTestPoint() {
        final Vector2 a = new Vector2(124.0f, 456.0f);
        final Vector2 b = new Vector2(248.0f, 912.0f);

        final Vector2 u = new Vector2(-124.0f,  456.0f);
        final Vector2 v = new Vector2( 124.0f, -456.0f);
        final Vector2 w = new Vector2( 186.0f,  684.0f);        
        
        final Segment2 s = new Segment2(a, b);
        
        assertTrue(s.testPoint(u) < 0.0f);
        assertTrue(s.testPoint(v) > 0.0f);

        final float epsilon = Math.abs(s.testPoint(w));
        
        assertTrue(epsilon < 0.000001f);
    }  
}
