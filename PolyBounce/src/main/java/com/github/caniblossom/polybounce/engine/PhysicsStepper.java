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
package com.github.caniblossom.polybounce.engine;

import com.github.caniblossom.polybounce.math.ConvexPolygon;
import com.github.caniblossom.polybounce.math.Segment2;
import com.github.caniblossom.polybounce.math.Segment2Intersection;
import com.github.caniblossom.polybounce.math.Vector2;
import java.util.ArrayList;
import java.util.List;

// TODO Implement tests.

/**
 * An utility class for handling collisions between rigid bodies.
 * See: http://en.wikipedia.org/wiki/Collision_response
 * @author Jani Salo
 */
public class PhysicsStepper {
    // Helper class because it turned out to be the cleanest solution.
    private static class IntersectionResult {
        public final int rayIndex;
        public final int wallIndex;

        public final Segment2Intersection intersection;
        
        public IntersectionResult(final int rayIndex, final int wallIndex, final Segment2Intersection intersection) {
            this.rayIndex = rayIndex;
            this.wallIndex = wallIndex;
            this.intersection = intersection;
        }
    }
    
    private final ArrayList<Segment2> activeCollisionRayList;
    private final ArrayList<Segment2> passiveCollisionRayList;
    
    // Generates collision rays from samples of the polygon at different points in time.
    private static void computeCollisionRays(final ArrayList<Segment2> list, final ConvexPolygon polyT0, final ConvexPolygon polyT1) {
        final List<Vector2> listT0 = polyT0.getUnmodifiableViewToVertexList();
        final List<Vector2> listT1 = polyT1.getUnmodifiableViewToVertexList();
        
        int vertex = 0;
        for (Vector2 v : listT0) {
            list.add(new Segment2(v, listT1.get(vertex++)));
        }
    }
    
    // Generates collision rays from polygon and its translation + rotation (in that order).
    private static void computeCollisionRays(final ArrayList<Segment2> list, final ConvexPolygon polyT0, final Vector2 origo, final Vector2 translation, final float rotation) {
        final ConvexPolygon translated = polyT0.rotateAndTranslate(origo, 0.0f, translation);
        final ConvexPolygon polyT1 = translated.rotateAndTranslate(origo, rotation, new Vector2(0.0f, 0.0f));

        final List<Vector2> listT0 = polyT0.getUnmodifiableViewToVertexList();        
        final List<Vector2> listT1 = polyT1.getUnmodifiableViewToVertexList();

        int vertex = 0;
        for (Vector2 v : listT0) {
            list.add(new Segment2(v, listT1.get(vertex++)));
        }
    }

    // Considers only rays colliding from the right side of the wall.
    private static IntersectionResult findShortestIntersection(List<Segment2> rayList, List<Segment2> wallList) {        
        int rayCandidate = -1;
        int wallCandidate = -1;
    
        Segment2Intersection intersectionCandidate = new Segment2Intersection();

        int wallIndex = 0;        
        for (Segment2 wall : wallList) {

            int rayIndex = 0;
            for (Segment2 ray : rayList) {
                // See that the ray comes from the business-side of the polygon.
                if (wall.projectPointOnRightNormal(ray.getA()) < 0.0f) {
                    continue;
                }
                
                Segment2Intersection intersection = wall.intersect(ray);
                
                // Apparently the order of operations is guaranteed to be from left to right.
                if (!intersectionCandidate.didIntersect() || intersection.getDistance() < intersectionCandidate.getDistance()) {
                    rayCandidate = rayIndex;
                    wallCandidate = wallIndex;
                    intersectionCandidate = intersection;
                }
                
                rayIndex++;
            }

            rayIndex = 0;
            wallIndex++;
        }
      
        return new IntersectionResult(rayCandidate, wallCandidate, intersectionCandidate);
    }
    
    /**
     * Constructs a new physics stepper.
     */
    public PhysicsStepper() {
        activeCollisionRayList = new ArrayList();
        passiveCollisionRayList = new ArrayList();
    }
    
    /**
     * Steps the active body with given change in time and checks if it intersects
     * with the passive body as a result. If intersection does occur, the collision
     * response is calculated and impulses are applied to both bodies. 
     * Note that all sorts of insane things can and will happen if the time step 
     * is too large relative to the motion or rotation of the active body.
     * @param active body to be stepped
     * @param passive body to be tested against
     * @param dt change in time
     * @return true if and only if a collision occurred
     */
    public boolean stepAndCollide(final RigidBody active, final RigidBody passive, final float dt) {
        assert dt != 0.0f;
        
        final ConvexPolygon activeT0 = active.getHullRelativeToTime(0.0f);
        final ConvexPolygon activeT1 = active.getHullRelativeToTime(dt);
        final ConvexPolygon passiveT0 = passive.getHullRelativeToTime(0.0f);
        
        // Normally step the body if no intersection occurs.
        if (!activeT1.doesIntersect(passiveT0)) {
            active.update(dt);
            return false;
        }
        
        activeCollisionRayList.clear();
        passiveCollisionRayList.clear();
        
        computeCollisionRays(activeCollisionRayList, activeT0, activeT1);
        computeCollisionRays(passiveCollisionRayList, passiveT0, active.getPosition(), active.getVelocity().scale(-1.0f), -active.getRotation());
        
        final IntersectionResult activeToPassive = findShortestIntersection(activeCollisionRayList, passiveT0.getUnmodifiableViewToSegmentList());
        final IntersectionResult passiveToActive = findShortestIntersection(passiveCollisionRayList, activeT0.getUnmodifiableViewToSegmentList());
        
        // TODO Use best intersection to compute impuse.
        // TODO Apply impulses to both bodies.

        return true;
    }
}
