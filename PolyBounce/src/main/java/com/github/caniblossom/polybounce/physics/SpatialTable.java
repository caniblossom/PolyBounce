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
package com.github.caniblossom.polybounce.physics;

import com.github.caniblossom.polybounce.physics.body.Body;
import com.github.caniblossom.polybounce.math.BoundingBox;
import com.github.caniblossom.polybounce.math.Vector2;
import com.github.caniblossom.polybounce.physics.body.Body;
import com.github.caniblossom.polybounce.physics.body.RigidBody;
import com.github.caniblossom.polybounce.physics.body.StaticBody;
import java.util.ArrayList;
import java.util.List;

/**
 * A class for sorting objects spatially.
 * @author Jani Salo
 */
public class SpatialTable {
    // Helper class.
    private static class Intersection {
        public final int x0, y0, x1, y1;
        public final boolean isValid;
        
        public Intersection(final int x0, final int y0, final int x1, final int y1) {
            this.x0 = x0;
            this.y0 = y0;
            this.x1 = x1;
            this.y1 = y1;
            
            this.isValid = true;
        }
        
        public Intersection() {
            this.x0 = 0;
            this.y0 = 0;
            this.x1 = 0;
            this.y1 = 0;
            
            this.isValid = false;
        }
    }
    
    private final ArrayList<ArrayList<Body>> bucketTable;
    private final BoundingBox area;
    
    private final int hBuckets;
    private final int vBuckets;
    private final int buckets;
    
    private final float bucketWidth;
    private final float bucketHeight;
    
    // Intersects the bounding box against the table.
    private Intersection computeTableIntersection(final BoundingBox box) {
        if (!box.doesIntersect(area)) {
            return new Intersection();
        }
       
        // Clamp the input box to area.
        final float xMin = Math.max(area.getPosition().getX(), box.getPosition().getX());
        final float yMin = Math.max(area.getPosition().getY(), box.getPosition().getY());
        final float xMax = Math.min(area.getMaximum().getX(), box.getMaximum().getX());
        final float yMax = Math.min(area.getMaximum().getY(), box.getMaximum().getY());
        
        // Compute coordinates relative to area origo.
        final float xRelMin = xMin - area.getPosition().getX();
        final float yRelMin = yMin - area.getPosition().getY();
        final float xRelMax = xMax - area.getPosition().getX();
        final float yRelMax = yMax - area.getPosition().getY();
        
        // Convert to integer coordinates.
        final int x0 = (int) Math.floor(xRelMin / bucketWidth);
        final int y0 = (int) Math.floor(yRelMin / bucketHeight);
        final int x1 = (int) Math.floor(xRelMax / bucketWidth);
        final int y1 = (int) Math.floor(yRelMax / bucketHeight);

        return new Intersection(
            Math.max(0, Math.min(hBuckets - 1, x0)),
            Math.max(0, Math.min(vBuckets - 1, y0)),
            Math.max(0, Math.min(hBuckets - 1, x1)),
            Math.max(0, Math.min(vBuckets - 1, y1))
        );
    }
    
    // Finds bounds for a body relative to time.
    private BoundingBox computeBounds(final Body body, final float dt) {
        final BoundingBox box = body.getHull().getBoundingBox();
        
        final Vector2 center = box.getPosition().sum(new Vector2(0.5f * box.getWidth(), 0.5f * box.getHeight()));

        // Account for possible rotation.
        final float inverseRoot = 0.70710678118654752440084436210485f;
        final float maxRadius = inverseRoot * Math.max(box.getWidth(), box.getHeight());
        
        final float x0 = center.getX() - maxRadius;
        final float y0 = center.getY() - maxRadius;
        final float x1 = center.getX() + maxRadius;
        final float y1 = center.getY() + maxRadius;

        final float vx = body.getVelocity().getX() * dt;
        final float vy = body.getVelocity().getY() * dt;

        float xMin = Math.min(x0, x0 + vx);
        float yMin = Math.min(y0, y0 + vy);
        float xMax = Math.max(x1, x1 + vx);
        float yMax = Math.max(y1, y1 + vy);
        
        return new BoundingBox(new Vector2(xMin, yMin), Math.max(0.0f, xMax - xMin), Math.max(0.0f, yMax - yMin));
    }
    
    // Non checked version.
    private ArrayList<Body> getBucketAt(int x, int y) {
        return bucketTable.get(x + y * hBuckets);        
    }
    
    /**
     * Constructs a new spatial table for sorting bodies.
     * @param area
     * @param hBuckets
     * @param vBuckets 
     */
    public SpatialTable(final BoundingBox area, final int hBuckets, final int vBuckets) {
        assert hBuckets > 0 && vBuckets > 0;
        
        this.bucketTable = new ArrayList();
        this.area = area;
        
        this.hBuckets = hBuckets;
        this.vBuckets = vBuckets;
        this.buckets = hBuckets * vBuckets;

        for (int i = 0; i < buckets; i++) {
            this.bucketTable.add(new ArrayList());
        }
        
        this.bucketWidth = area.getWidth() / (float) hBuckets;
        this.bucketHeight = area.getHeight() / (float) vBuckets;
    }
    
    /**
     * Removes all bodies from the table.
     */
    public void clear() {
        for (ArrayList<Body> list : bucketTable) {
            list.clear();
        }
    }
    
    /**
     * @param body body to add to the table.
     */
    public void addBody(final Body body) {
        final Intersection i = computeTableIntersection(body.getHull().getBoundingBox());
        
        if (i.isValid) {
            for (int y = i.y0; y <= i.y1; y++) {
                for (int x = i.x0; x <= i.x1; x++) {
                    getBucketAt(x, y).add(body);
                }
            }
        }
    }

    /**
     * @param list list of rigid bodies to add
     */
    public void addRigidBodyList(final List<RigidBody> list) {
        for (RigidBody body : list) {
            addBody(body);
        }
    }
    
    /**
     * @param list list of static bodies to add
     */
    public void addStaticBodyList(final List<StaticBody> list) {
        for (StaticBody body : list) {
            addBody(body);
        }
    }

    /**
     * Finds possible intersections for a body from the table.
     * @param output list to add the candidates to
     * @param body body to test
     * @param dt change in time
     */
    public void findPossibleIntersections(final List<Body> output, final Body body, final float dt) {
        final BoundingBox box = computeBounds(body, dt);
        final Intersection i = computeTableIntersection(box);
        
        if (i.isValid) {
            for (int y = i.y0; y <= i.y1; y++) {
                for (int x = i.x0; x <= i.x1; x++) {
                    final ArrayList<Body> currentBucket = getBucketAt(x, y);           

                    // Add any bodies that are not yet listed - the default implementation of equals 
                    // is just fine here. Obviosly very slow if the list is big (which shouldn't be the case).
                    for (Body currentBody : currentBucket) {
                        if (!output.contains(currentBody)) {
                            output.add(currentBody);
                        }
                    }
                }
            }
        }        
    }
}
