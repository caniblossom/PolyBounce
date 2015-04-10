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
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Jani Salo
 */
public class ConvexPolygonTest {
    private ArrayList<Vector2> getSquareVertexList(final float scale, final Vector2 displacement) {
        final ArrayList<Vector2> list = new ArrayList();
        
        list.add(displacement.sum(new Vector2( 0.0f,  0.0f)));
        list.add(displacement.sum(new Vector2(scale,  0.0f)));
        list.add(displacement.sum(new Vector2(scale, scale)));
        list.add(displacement.sum(new Vector2( 0.0f, scale)));

        return list;
    }

    private ConvexPolygon getSquarePolygon(final float scale, final Vector2 displacement) {
        ConvexPolygon poly = null;
        
        try {
            poly = ConvexPolygon.constructNew(getSquareVertexList(scale, displacement)); 
        } catch (Exception e) {
            fail();
        }

        return poly;
    }
    
    @Test
    public void testConvexPolygon() {
        final ArrayList<Vector2> listA = new ArrayList();
        final ArrayList<Vector2> listB = new ArrayList();
        final ArrayList<Vector2> listC = new ArrayList();
        final ArrayList<Vector2> listD = new ArrayList();
        final ArrayList<Vector2> listE = new ArrayList();
        final ArrayList<Vector2> listF = new ArrayList();
        
        listA.add(new Vector2(0.0f,  0.0f));
        listA.add(new Vector2(1.0f,  0.0f));

        listB.add(new Vector2(0.0f,  0.0f));
        listB.add(new Vector2(1.0f,  0.0f));
        listB.add(new Vector2(0.0f, -1.0f));

        listC.add(new Vector2(0.0f,  0.0f));
        listC.add(new Vector2(1.0f,  0.0f));
        listC.add(new Vector2(0.0f,  1.0f));
        listC.add(new Vector2(1.0f, -1.0f));
        listC.add(new Vector2(1.0f,  1.0f));

        listD.add(new Vector2(0.0f,  0.0f));
        listD.add(new Vector2(1.0f,  0.0f));
        listD.add(new Vector2(2.0f,  0.0f));
        listD.add(new Vector2(0.0f,  1.0f));

        listE.add(new Vector2(0.0f,  0.0f));
        listE.add(new Vector2(0.0f,  0.0f));
        listE.add(new Vector2(0.0f,  0.0f));

        listF.add(new Vector2(0.0f,  0.0f));
        listF.add(new Vector2(1.0f,  0.0f));
        listF.add(new Vector2(0.0f,  1.0f));

        try {
            final ConvexPolygon poly = ConvexPolygon.constructNew(listA);
            fail();
        } catch (IllegalArgumentException e) {
        } catch (Exception e) {
            fail();
        } 
        
        try {
            final ConvexPolygon poly = ConvexPolygon.constructNew(listB);
            fail();
        } catch (IllegalArgumentException e) {
        } catch (Exception e) {
            fail();
        } 

        try {
            final ConvexPolygon poly = ConvexPolygon.constructNew(listC);
            fail();
        } catch (IllegalArgumentException e) {
        } catch (Exception e) {
            fail();
        } 

        try {
            final ConvexPolygon poly = ConvexPolygon.constructNew(listD);
            fail();
        } catch (IllegalArgumentException e) {
        } catch (Exception e) {
            fail();
        } 

        try {
            final ConvexPolygon poly = ConvexPolygon.constructNew(listE);
            fail();
        } catch (IllegalArgumentException e) {
        } catch (Exception e) {
            fail();
        } 
        
        try {
            final ConvexPolygon poly = ConvexPolygon.constructNew(listF); 
        } catch (Exception e) {
            fail();
        }
     }
    
    @Test
    public void testGetUnmodifiableViewToVertexList() {
        final List<Vector2> list = getSquareVertexList(1.0f, new Vector2(0.0f, 0.0f));
        final ConvexPolygon poly = getSquarePolygon(1.0f, new Vector2(0.0f, 0.0f));
        
        List<Vector2> unmodifiable = poly.getUnmodifiableViewToVertexList();
        assertTrue(unmodifiable.equals(list));

        try {
            unmodifiable.add(null);
            fail();
        } catch (UnsupportedOperationException e) {}
    }

    @Test
    public void testGetUnmodifiableViewToSegmentList() {
        final List<Vector2> list = getSquareVertexList(1.0f, new Vector2(0.0f, 0.0f));
        final List<Segment2> segmentList = new ArrayList();
        
        for (int i = 0; i < list.size(); i++) {
            Vector2 a = list.get(i);
            Vector2 b = list.get((i + 1) % list.size());
        
            segmentList.add(new Segment2(a, b));
        }

        final ConvexPolygon poly = getSquarePolygon(1.0f, new Vector2(0.0f, 0.0f));
        
        List<Segment2> unmodifiable = poly.getUnmodifiableViewToSegmentList();
        assertTrue(unmodifiable.equals(segmentList));

        try {
            unmodifiable.add(null);
            fail();
        } catch (UnsupportedOperationException e) {}
    }

    @Test
    public void testGetVertexAverage() {
        final ConvexPolygon poly = getSquarePolygon(1.0f, new Vector2(0.0f, 0.0f));
        assertTrue(poly.getVertexAverage().equals(new Vector2(0.5f, 0.5f)));
    }
    
    @Test
    public void testDoesIntersectA() {
        final ConvexPolygon poly = getSquarePolygon(1.0f, new Vector2(0.0f, 0.0f));

        final Vector2 a = new Vector2(-0.001f,  0.500f);
        final Vector2 b = new Vector2( 0.500f, -0.001f);
        final Vector2 c = new Vector2( 1.000f,  0.500f);
        final Vector2 d = new Vector2( 0.500f,  1.000f);
        
        assertFalse(poly.doesIntersect(a));
        assertFalse(poly.doesIntersect(b));
        assertTrue(poly.doesIntersect(c));
        assertTrue(poly.doesIntersect(d));
    }

    @Test
    public void testDoesIntersectB() {
        final ConvexPolygon poly = getSquarePolygon(1.0f, new Vector2(0.0f, 0.0f));

        final ConvexPolygon polyA0 = getSquarePolygon(0.5f, new Vector2(-0.50f,  0.25f));
        final ConvexPolygon polyB0 = getSquarePolygon(0.5f, new Vector2( 0.25f, -0.50f));
        final ConvexPolygon polyC0 = getSquarePolygon(0.5f, new Vector2( 1.00f,  0.25f));
        final ConvexPolygon polyD0 = getSquarePolygon(0.5f, new Vector2( 0.25f,  1.00f));
        
        final ConvexPolygon polyA1 = getSquarePolygon(0.5f, new Vector2(-0.51f,  0.25f));
        final ConvexPolygon polyB1 = getSquarePolygon(0.5f, new Vector2( 0.25f, -0.51f));
        final ConvexPolygon polyC1 = getSquarePolygon(0.5f, new Vector2( 1.01f,  0.25f));
        final ConvexPolygon polyD1 = getSquarePolygon(0.5f, new Vector2( 0.25f,  1.01f));

        assertTrue(poly.doesIntersect(polyA0));
        assertTrue(poly.doesIntersect(polyB0));
        assertTrue(poly.doesIntersect(polyC0));
        assertTrue(poly.doesIntersect(polyD0));

        assertTrue(polyA0.doesIntersect(poly));
        assertTrue(polyB0.doesIntersect(poly));
        assertTrue(polyC0.doesIntersect(poly));
        assertTrue(polyD0.doesIntersect(poly));

        assertFalse(poly.doesIntersect(polyA1));
        assertFalse(poly.doesIntersect(polyB1));
        assertFalse(poly.doesIntersect(polyC1));
        assertFalse(poly.doesIntersect(polyD1));

        assertFalse(polyA1.doesIntersect(poly));
        assertFalse(polyB1.doesIntersect(poly));
        assertFalse(polyC1.doesIntersect(poly));
        assertFalse(polyD1.doesIntersect(poly));
    }
    
    @Test
    public void testRotateAndTranslate() {
        final ConvexPolygon poly = getSquarePolygon(1.0f, new Vector2(0.0f, 0.0f));
        
        final ConvexPolygon rotated = poly.rotateAndTranslate(new Vector2(0.5f, 0.5f), (float) Math.PI, new Vector2(1.0f, 1.0f));
        final List<Vector2> listRotated = rotated.getUnmodifiableViewToVertexList();
        
        assertEquals(listRotated.get(0).getX(), 2.0f, 0.001f);
        assertEquals(listRotated.get(0).getY(), 2.0f, 0.001f);
        assertEquals(listRotated.get(1).getX(), 1.0f, 0.001f);
        assertEquals(listRotated.get(1).getY(), 2.0f, 0.001f);
        assertEquals(listRotated.get(2).getX(), 1.0f, 0.001f);
        assertEquals(listRotated.get(2).getY(), 1.0f, 0.001f);
        assertEquals(listRotated.get(3).getX(), 2.0f, 0.001f);
        assertEquals(listRotated.get(3).getY(), 1.0f, 0.001f);
    }
}
