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
import java.util.Collections;
import java.util.List;

/**
 * A class for representing an immutable convex polygon. The winding rule is 
 * counter-clockwise and no consecutive segments can lie on the same line.
 * @author Jani Salo
 */
public class ConvexPolygon {
    private final ArrayList<Vector2> vertexList;
    private final ArrayList<Segment2> segmentList;
    
    private final Vector2 vertexAverage;   
    private final BoundingBox boundingBox;
    
    // Simply sums the vertices together and returns average.
    private static Vector2 computeVertexAverage(final List<Vector2> vertexList) {
        float x = 0.0f;
        float y = 0.0f;
        
        for (Vector2 v : vertexList) {
            x += v.getX();
            y += v.getY();
        }
        
        return new Vector2(x / (float) vertexList.size(), y / (float) vertexList.size());
    }
    
    // Finds the bounding box for the polygon.
    private static BoundingBox computeBoundingBox(final List<Vector2> vertexList) {
        float xMin =  Float.MAX_VALUE;
        float xMax = -Float.MAX_VALUE;
        float yMin =  Float.MAX_VALUE;
        float yMax = -Float.MAX_VALUE;
    
        for (Vector2 v : vertexList) {
            xMin = Math.min(xMin, v.getX());
            xMax = Math.max(xMax, v.getX());
            yMin = Math.min(yMin, v.getY());
            yMax = Math.max(yMax, v.getY());
        }
        
        return new BoundingBox(new Vector2(xMin, yMin), xMax - xMin, yMax - yMin);
    }
    
    // Constructs a new convex polygon without any checks due to speed reasons.
    private ConvexPolygon(final List<Vector2> vertexList, final Vector2 vertexAverage, final BoundingBox boundingBox) {
        this.vertexList = new ArrayList();
        this.vertexList.addAll(vertexList);
        this.segmentList = new ArrayList();

        for (int i = 0; i < vertexList.size(); i++) {
            Vector2 a = vertexList.get(i);
            Vector2 b = vertexList.get((i + 1) % vertexList.size());
            
            segmentList.add(new Segment2(a, b));
        }
        
        this.vertexAverage = vertexAverage;
        this.boundingBox = boundingBox;
    }    
    
    // Checks that the polygon is wound counter-clockwise. 
    private boolean isWoundCounterClockwise() {
        for (int i = 0; i < segmentList.size(); i++) {
            Segment2 a = segmentList.get(i);
            Segment2 b = segmentList.get((i + 1) % segmentList.size());
            
            final float sinAngle = a.getRightNormal().dot(b.getNormal());
            
            if (sinAngle >= 0.0f) {
                return false;
            }
        }
        
        return true;
    }

    // Checks that the polygon doesn't self intersect in a bad manner.
    private boolean doesNotSelfIntersect() {
        for (int a = 1; a < segmentList.size(); a++) {
            for (int b = 0; b < a; b++) {
                if (a - b == 1 || b - a + segmentList.size() == 1) {
                    continue;
                }
                
                Segment2 segA = segmentList.get(a);
                Segment2 segB = segmentList.get(b);
                
                final Segment2Intersection i = segA.intersect(segB);
                if (i.didIntersect() || segA.sharesVertexWith(segB)) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    // Projects the polygon to an axis defined by a normal.
    private Vector2 projectOnNormal(final Vector2 n) {
        float min = Float.MAX_VALUE;
        float max = -Float.MAX_VALUE;

        for (Vector2 v : vertexList) {
            final float p = n.dot(v);
            min = Math.min(min, p);
            max = Math.max(max, p);
        }            
        
        return new Vector2(min, max);
    }
    // Intersects this against another polygon on a list axes defined by right segment normals. 
    private boolean doesIntersectOnAllAxes(final ConvexPolygon polygon, final ArrayList<Segment2> list) {
        for (Segment2 s : list) {
            final Vector2 a = projectOnNormal(s.getRightNormal());
            final Vector2 b = polygon.projectOnNormal(s.getRightNormal());
            
            if (b.getX() > a.getY() || b.getY() < a.getX()) {
                return false;
            }           
        }
        
        return true;
    }
    
    /**
     * Constructs a new convex polygon from a list of vertices. 
     * @param vertexList a list of vectors defining the vertices of a counter-clockwise wound convex polygon
     * @return newly constructed convex polygon
     * @throws IllegalArgumentException
     */
    public static ConvexPolygon constructNew(List<Vector2> vertexList) throws IllegalArgumentException {
        final Vector2 average = computeVertexAverage(vertexList);
        final BoundingBox box = computeBoundingBox(vertexList);
        
        final ConvexPolygon poly = new ConvexPolygon(vertexList, average, box);
            
        if (!poly.isWoundCounterClockwise()) {
            throw new IllegalArgumentException("The polygon isn't wound counter-clockwise.");
        } else if (!poly.doesNotSelfIntersect()) {
            throw new IllegalArgumentException("The polygon self intersects.");
        }

        return poly;
    }
    
    /**
     * Copy constructor.
     * @param poly convex polygon to copy.
     */
    public ConvexPolygon(final ConvexPolygon poly) {
        this.vertexList = new ArrayList();
        this.segmentList = new ArrayList();

        this.vertexList.addAll(poly.vertexList);
        this.segmentList.addAll(poly.segmentList);
        
        this.vertexAverage = new Vector2(poly.vertexAverage);
        this.boundingBox = new BoundingBox(poly.boundingBox);
    }   
    
    /**
     * @return an unmodifiable view to the vertex list
     */
    public List<Vector2> getUnmodifiableViewToVertexList() {
        return Collections.unmodifiableList(vertexList);
    }

    /**
     * @return an unmodifiable view to the segment list
     */
    public List<Segment2> getUnmodifiableViewToSegmentList() {
        return Collections.unmodifiableList(segmentList);
    }
   
    /**
     * @return vertex average
     */
    public Vector2 getVertexAverage() {
        return vertexAverage;
    }
        
    /**
     * @return bounding box for the object
     */
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }
    
    /**
     * Checks whether this polygon intersects another
     * @param polygon polygon to be tested
     * @return true if and only if the polygons intersect each other.
     */
    public boolean doesIntersect(final ConvexPolygon polygon) { 
        if (!doesIntersectOnAllAxes(polygon, segmentList)) {
            return false;
        } else if (!doesIntersectOnAllAxes(polygon, polygon.segmentList)) {
            return false; // I'm so sorry PIT but I'm way too lazy to conjure up a proper testcase right now.
        }

        return true;
    }
    
    /**
     * Returns a copy of this polygon first rotated and then translated. 
     * The relative order of vertices and segments is guaranteed to not change.
     * @param origo point to rotate around
     * @param angle angle of rotation in radians
     * @param translation translation (ie. displacement) after rotation
     * @return new convex polygon
     */
    public ConvexPolygon rotateAndTranslate(final Vector2 origo, final float angle, final Vector2 translation) {
        final ArrayList<Vector2> newVertexList = new ArrayList();

        for (Vector2 v : vertexList) {
            newVertexList.add(v.rotation(origo, angle).sum(translation));
        }

        final Vector2 average = vertexAverage.rotation(origo, angle).sum(translation);
        final BoundingBox box = computeBoundingBox(newVertexList);

        return new ConvexPolygon(newVertexList, average, box);
    }
}
