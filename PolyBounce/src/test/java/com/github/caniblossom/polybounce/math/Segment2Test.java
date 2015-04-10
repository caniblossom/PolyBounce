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
    public void testGetNormal() {
        final Vector2 a = new Vector2(  0.0f,   0.0f);
        final Vector2 b = new Vector2(123.0f,   0.0f);
        final Vector2 c = new Vector2(  0.0f, 456.0f);

        final Segment2 u = new Segment2(a, b); 
        final Segment2 v = new Segment2(a, c); 
        final Segment2 w = new Segment2(a, a); 
        
        final Vector2 uNormal = new Vector2(1.0f, 0.0f); 
        final Vector2 vNormal = new Vector2(0.0f, 1.0f); 
        final Vector2 wNormal = new Vector2(); 

        assertTrue(u.getNormal().equals(uNormal));
        assertTrue(v.getNormal().equals(vNormal));
        assertTrue(w.getNormal().equals(wNormal));
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
    public void testProjectPointOntNormal() {
        final Vector2 a = new Vector2(124.0f, 456.0f);
        final Vector2 b = new Vector2(248.0f, 912.0f);
        
        final Vector2 u = new Vector2(-124.0f, -456.0f);
        final Vector2 v = new Vector2( 248.0f,  912.0f);
        final Vector2 w = new Vector2( 352.0f,  394.0f);        
        
        final Segment2 s = new Segment2(a, b);
        
        assertTrue(s.projectPointOnNormal(u) < 0.0f);
        assertTrue(s.projectPointOnNormal(v) > 0.0f);

        final float epsilon = Math.abs(s.projectPointOnNormal(w));
        
        assertTrue(epsilon < 0.000001f);
    }  

    @Test
    public void testProjectPointOnRightNormal() {
        final Vector2 a = new Vector2(124.0f, 456.0f);
        final Vector2 b = new Vector2(248.0f, 912.0f);

        final Vector2 u = new Vector2(-124.0f,  456.0f);
        final Vector2 v = new Vector2( 124.0f, -456.0f);
        final Vector2 w = new Vector2( 186.0f,  684.0f);        
        
        final Segment2 s = new Segment2(a, b);
        
        assertTrue(s.projectPointOnRightNormal(u) < 0.0f);
        assertTrue(s.projectPointOnRightNormal(v) > 0.0f);

        final float epsilon = Math.abs(s.projectPointOnRightNormal(w));
        
        assertTrue(epsilon < 0.000001f);
    }  

    @Test
    public void testSharesVertexWith() {
        final Vector2 a = new Vector2(5.0f, 5.0f);
        final Vector2 b = new Vector2(6.0f, 5.0f);
        final Vector2 c = new Vector2(5.5f, 5.5f);
        final Vector2 d = new Vector2(5.0f, 4.0f);
        final Vector2 e = new Vector2(6.0f, 4.0f);
        
        final Segment2 ab = new Segment2(a, b);
        final Segment2 ac = new Segment2(a, c);
        final Segment2 cb = new Segment2(c, b);
        final Segment2 de = new Segment2(d, e);
        
        assertTrue(ab.sharesVertexWith(ab));
        assertTrue(ab.sharesVertexWith(ac));
        assertTrue(ab.sharesVertexWith(cb));
        assertFalse(ab.sharesVertexWith(de));      
    }
    
    @Test
    public void testIntersect() {
        final Vector2 a = new Vector2(2.00f, 2.00f);
        final Vector2 b = new Vector2(4.00f, 2.00f);
        final Vector2 c = new Vector2(4.00f, 4.00f);
        final Vector2 d = new Vector2(2.00f, 4.00f);
        final Vector2 e = new Vector2(3.00f, 4.00f);
        final Vector2 f = new Vector2(3.00f, 6.00f);
        final Vector2 g = new Vector2(0.00f, 3.00f);
        final Vector2 h = new Vector2(3.00f, 3.00f);
        final Vector2 i = new Vector2(3.50f, 3.00f);
        final Vector2 j = new Vector2(3.50f, 3.01f);

        final Segment2 ac = new Segment2(a, c);
        final Segment2 ad = new Segment2(a, d);
        final Segment2 af = new Segment2(a, f);
        final Segment2 bc = new Segment2(b, c);
        final Segment2 bd = new Segment2(b, d);
        final Segment2 be = new Segment2(b, e);
        final Segment2 gb = new Segment2(g, b);
        final Segment2 hh = new Segment2(h, h); 
        final Segment2 hi = new Segment2(h, i); 
        final Segment2 jh = new Segment2(j, h); 
        
        final Segment2Intersection achh = ac.intersect(hh);
        final Segment2Intersection adac = ad.intersect(ac);
        final Segment2Intersection adbc = ad.intersect(bc);
        final Segment2Intersection adbd = ad.intersect(bd);
        final Segment2Intersection afbe = af.intersect(be);
        final Segment2Intersection afgb = af.intersect(gb);
        final Segment2Intersection beaf = be.intersect(af);
        final Segment2Intersection hhac = hh.intersect(ac);
        final Segment2Intersection hijh = hi.intersect(jh);
        
        assertFalse(achh.didIntersect());
        assertTrue(adac.didIntersect());
        assertFalse(adbc.didIntersect());
        assertTrue(adbd.didIntersect());       
        assertFalse(afbe.didIntersect());
        assertTrue(afgb.didIntersect());
        assertFalse(beaf.didIntersect());
        assertFalse(hhac.didIntersect());

        assertEquals(adac.getDistance(), 0.0f, 0.0f);
        assertEquals(adbd.getDistance(), 2.828427f, 0.000001f);
        assertNotEquals(hijh.getDistance(), 0.0f, 0.0f);

        assertTrue(adac.getPosition().equals(a));        
    }  
    
    @Test
    public void testEquals() {
        final Vector2 a = new Vector2(1.0f, 2.0f);
        final Vector2 b = new Vector2(3.0f, 4.0f);
        final Vector2 c = new Vector2(3.0f, 4.0f);
        final Vector2 d = new Vector2(5.0f, 6.0f);
        final Vector2 n = null;
        
        final Segment2 ab = new Segment2(a, b);
        final Segment2 ac = new Segment2(a, c);
        final Segment2 ad = new Segment2(a, d);
        final Segment2 ba = new Segment2(b, a);
        
        assertTrue(ab.equals(ab));
        assertTrue(ab.equals(ac));
        assertFalse(ab.equals(ba));
        assertFalse(ab.equals(ad));
        assertFalse(ab.equals(n));
    }

    @Test
    public void testHash() {
        final Vector2 a = new Vector2(456.0f, 789.0f);
        final Vector2 b = new Vector2(456.0f, 789.0f);

        assertEquals(a.hashCode(), a.hashCode());
        assertEquals(a.hashCode(), b.hashCode());
    }
    
    @Test
    public void testToString() {
        final Vector2 a = new Vector2(123.0f, 456.0f);
        final Vector2 b = new Vector2(321.0f, 654.0f);
        final Segment2 ab = new Segment2(a, b);

        assertEquals(ab.toString(), "(123.0, 456.0) -> (321.0, 654.0)");
    }
}
