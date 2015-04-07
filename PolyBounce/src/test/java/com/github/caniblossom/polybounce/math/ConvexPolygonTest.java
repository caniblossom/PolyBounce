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
    private ArrayList<Vector2> getUnitSquareVertexList() {
        final ArrayList<Vector2> list = new ArrayList();
        
        list.add(new Vector2(0.0f, 0.0f));
        list.add(new Vector2(1.0f, 0.0f));
        list.add(new Vector2(1.0f, 1.0f));
        list.add(new Vector2(0.0f, 1.0f));

        return list;
    }

    private ConvexPolygon getUnitSquarePolygon() {
        ConvexPolygon poly = null;
        
        try {
            poly = new ConvexPolygon(getUnitSquareVertexList()); 
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
            final ConvexPolygon poly = new ConvexPolygon(listA);
            fail();
        } catch (IllegalArgumentException e) {
        } catch (Exception e) {
            fail();
        } 
        
        try {
            final ConvexPolygon poly = new ConvexPolygon(listB);
            fail();
        } catch (IllegalArgumentException e) {
        } catch (Exception e) {
            fail();
        } 

        try {
            final ConvexPolygon poly = new ConvexPolygon(listC);
            fail();
        } catch (IllegalArgumentException e) {
        } catch (Exception e) {
            fail();
        } 

        try {
            final ConvexPolygon poly = new ConvexPolygon(listD);
            fail();
        } catch (IllegalArgumentException e) {
        } catch (Exception e) {
            fail();
        } 

        try {
            final ConvexPolygon poly = new ConvexPolygon(listE);
            fail();
        } catch (IllegalArgumentException e) {
        } catch (Exception e) {
            fail();
        } 
        
        try {
            final ConvexPolygon poly = new ConvexPolygon(listF); 
        } catch (Exception e) {
            fail();
        }
     }
    
    @Test
    public void testGetUnmodifiableViewToVertexList() {
        final List<Vector2> list = getUnitSquareVertexList();
        final ConvexPolygon poly = getUnitSquarePolygon();
        
        List<Vector2> unmodifiable = poly.getUnmodifiableViewToVertexList();
        assertTrue(unmodifiable.equals(list));

        try {
            unmodifiable.add(null);
            fail();
        } catch (UnsupportedOperationException e) {}
    }

    @Test
    public void testGetUnmodifiableViewToSegmentList() {
        final List<Vector2> list = getUnitSquareVertexList();
        final List<Segment2> segmentList = new ArrayList();
        
        for (int i = 0; i < list.size(); i++) {
            Vector2 a = list.get(i);
            Vector2 b = list.get((i + 1) % list.size());
        
            segmentList.add(new Segment2(a, b));
        }

        final ConvexPolygon poly = getUnitSquarePolygon();
        
        List<Segment2> unmodifiable = poly.getUnmodifiableViewToSegmentList();
        assertTrue(unmodifiable.equals(segmentList));

        try {
            unmodifiable.add(null);
            fail();
        } catch (UnsupportedOperationException e) {}
    }

    @Test
    public void testGetVertexAverage() {
        assertTrue(getUnitSquarePolygon().getVertexAverage().equals(new Vector2(0.5f, 0.5f)));
    }
}
