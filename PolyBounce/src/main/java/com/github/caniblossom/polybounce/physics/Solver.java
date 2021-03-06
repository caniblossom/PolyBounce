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
import com.github.caniblossom.polybounce.math.ConvexPolygon;
import com.github.caniblossom.polybounce.math.Segment2;
import com.github.caniblossom.polybounce.math.Segment2Intersection;
import com.github.caniblossom.polybounce.math.Vector2;
import java.util.ArrayList;
import java.util.List;

/**
 * A class used for solving collisions between bodies.
 * @author Jani Salo
 */
public class Solver {
       // Helper class.
    private static class IntersectionResult {
        public final Segment2 ray;
        public final Segment2 wall;
        
        public final Segment2Intersection intersection; 

        public IntersectionResult(final Segment2 ray, final Segment2 wall, final Segment2Intersection intersection) {
            this.ray = ray; 
            this.wall = wall;
            this.intersection = intersection;
        }
    }
        
    private final ArrayList<Segment2> collisionRayList;    
    private final ArrayList<IntersectionResult> intersectionResultList;
    
    // Generates collision rays from samples of the polygon at different points in time.
    private  void computeCollisionRays(final ArrayList<Segment2> list, final ConvexPolygon polyT0, final ConvexPolygon polyT1) {
        final List<Vector2> listT0 = polyT0.getUnmodifiableViewToVertexList();
        final List<Vector2> listT1 = polyT1.getUnmodifiableViewToVertexList();
        
        int vertex = 0;
        for (Vector2 v : listT0) {
            list.add(new Segment2(v, listT1.get(vertex++)));
        }
    }

    // Finds and lists all intersections found through ray casting.
    private void listIntersections(ArrayList<IntersectionResult> resultList, List<Segment2> rayList, List<Segment2> wallList) {        
        for (Segment2 currentRay : rayList) {
            IntersectionResult candidate = null;

            for (Segment2 currentWall : wallList) {
                final Segment2Intersection intersection = currentWall.intersect(currentRay);
                
                if (intersection.didIntersect()) {
                    if (candidate == null || intersection.getDistance() < candidate.intersection.getDistance()) {
                        candidate = new IntersectionResult(currentRay, currentWall, intersection);
                    }
                }
            }

            if (candidate != null) {
                resultList.add(candidate);
            }
        }
    }

    // Lists collisions generated by shooting rays from active body to passive body.
    private void listActiveToPassiveCollisions(final List<Collision> resultList, final ConvexPolygon activeT0, final ConvexPolygon activeT1, final ConvexPolygon passiveT0) {
        collisionRayList.clear();
        computeCollisionRays(collisionRayList, activeT0, activeT1);

        intersectionResultList.clear();
        listIntersections(intersectionResultList, collisionRayList, passiveT0.getUnmodifiableViewToSegmentList());

        for (IntersectionResult result : intersectionResultList) {
            resultList.add(new Collision(result.intersection.getDistance(), result.ray.getA(), result.intersection.getPosition(), result.wall.getRightNormal()));
        }        
    }

    // Lists collisions generated by shooting rays from passive body to active body.
    private void listPassiveToActiveCollisions(final List<Collision> resultList, final ConvexPolygon passiveT0, final ConvexPolygon passiveT1, final ConvexPolygon activeT0) {
        collisionRayList.clear();
        computeCollisionRays(collisionRayList, passiveT0, passiveT1);

        intersectionResultList.clear();
        listIntersections(intersectionResultList, collisionRayList, activeT0.getUnmodifiableViewToSegmentList());

        for (IntersectionResult result : intersectionResultList) {
            resultList.add(new Collision(result.intersection.getDistance(), result.intersection.getPosition(), result.ray.getA(), result.wall.getRightNormal().scale(-1.0f)));
        }
    }
    
    // Finds and lists collisions from supplied parameters
    private void listCollisions(final List<Collision> resultList, final Body active, final Body passive, final ConvexPolygon activeT0, final ConvexPolygon activeT1, final ConvexPolygon passiveT0, final ConvexPolygon passiveT1) {
        if (activeT1.doesIntersect(passiveT0)) {
            listActiveToPassiveCollisions(resultList, activeT0, activeT1, passiveT0);
            listPassiveToActiveCollisions(resultList, passiveT0, passiveT1, activeT0); 

            // Invent a collision if for some reason we couldn't find one even though the polygons intersect.
            if (resultList.isEmpty()) {
                final Vector2 average = active.getPosition().sum(passive.getPosition()).scale(0.5f);
                resultList.add(new Collision(0.0f, average, average, active.getPosition().difference(passive.getPosition()).normal()));                
            }
        }
    }
    
    /**
     * Constructs a new solver.
     */
    public Solver() {
        this.collisionRayList = new ArrayList();    
        this.intersectionResultList = new ArrayList();
    }
    
    /**
     * @param active active or primary body
     * @param passive passive or secondary body
     * @param dt change in time
     * @return true if an only if the bodies would intersect after accounting for the motion of the active body
     */
    public boolean wouldIntersect(final Body active, final Body passive, final float dt) {
        final ConvexPolygon activeT0 = active.getHull();
        final ConvexPolygon activeT1 = activeT0.rotateAndTranslate(active.getCenterOfMass(), active.getAngularVelocity() * dt, active.getVelocity().scale(dt));
        final ConvexPolygon passiveT0 = passive.getHull();

        return activeT1.doesIntersect(passiveT0);
    }

    /**
     * Lists collisions caused by the motion of an active body.
     * @param resultList list to append the collisions to
     * @param active active or primary body
     * @param passive passive or secondary body
     * @param dt change in time
     */    
    public void findCollisions(final List<Collision> resultList, final Body active, final Body passive, final float dt) {
        final ConvexPolygon activeT0 = active.getHull();
        final ConvexPolygon activeT1 = activeT0.rotateAndTranslate(active.getCenterOfMass(), active.getAngularVelocity() * dt, active.getVelocity().scale(dt));

        final ConvexPolygon passiveT0 = passive.getHull();
        final ConvexPolygon passiveT1 = passiveT0.rotateAndTranslate(passive.getCenterOfMass(), 0.0f, active.getVelocity().scale(-dt)).rotateAndTranslate(active.getCenterOfMass(), -active.getAngularVelocity() * dt, new Vector2(0.0f, 0.0f));
        
        listCollisions(resultList, active, passive, activeT0, activeT1, passiveT0, passiveT1);
    }
}
