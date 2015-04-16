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

// TODO Implement tests if possible.

/**
 * A class for representing an intersection between two physics bodies.
 * @author Jani Salo
 */
public class BodyIntersection {
    // Helper class for making the constructor more readable.
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
    
    private final static ArrayList<Segment2> ACTIVE_RAY_LIST = new ArrayList();
    private final static ArrayList<Segment2> PASSIVE_RAY_LIST = new ArrayList();

    private final boolean didIntersect; 

    private final float distance;
    private final Vector2 activePosition;
    private final Vector2 passivePosition;
    private final Vector2 normal;
    
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
        final ConvexPolygon polyT1 = polyT0.rotateAndTranslate(origo.difference(translation), rotation, translation);

        final List<Vector2> listT0 = polyT0.getUnmodifiableViewToVertexList();        
        final List<Vector2> listT1 = polyT1.getUnmodifiableViewToVertexList();

        int vertex = 0;
        for (Vector2 v : listT0) {
            list.add(new Segment2(v, listT1.get(vertex++)));
        }
    }

    // Considers only rays colliding from the right side of the wall.
    private static IntersectionResult findShortestIntersection(List<Segment2> rayList, List<Segment2> wallList) {        
        Segment2 rayCandidate = new Segment2();
        Segment2 wallCandidate = new Segment2();

        Segment2Intersection intersectionCandidate = new Segment2Intersection();

        for (Segment2 currentWall : wallList) {
            for (Segment2 currentRay : rayList) {
                Segment2Intersection intersection = currentWall.intersect(currentRay);
                
                if (intersection.getDistance() < intersectionCandidate.getDistance()) {
                    rayCandidate = currentRay;
                    wallCandidate = currentWall;
                    intersectionCandidate = intersection;
                }
            }
        }
              
        return new IntersectionResult(rayCandidate, wallCandidate, intersectionCandidate);
    }

    /**
     * Constructs a default body intersection with intersection set to false.
     */
    public BodyIntersection() {
        didIntersect    = false;
        distance        = Float.MAX_VALUE;
        activePosition  = new Vector2();
        passivePosition = new Vector2();
        normal          = new Vector2();
    }

    /**
     * Constructs a new body intersection from two bodies and change in time.
     * @param active active or primary body
     * @param passive passive or secondary body
     * @param dt change in time
     */
    public BodyIntersection(final PhysicsBody active, final PhysicsBody passive, final float dt) {
        final ConvexPolygon activeT0 = active.getHullRelativeToTime(0.0f);
        final ConvexPolygon activeT1 = active.getHullRelativeToTime(dt);
        final ConvexPolygon passiveT0 = passive.getHullRelativeToTime(0.0f);

        if (!activeT1.doesIntersect(passiveT0)) {
            didIntersect    = false;
            distance        = Float.MAX_VALUE;
            activePosition  = new Vector2();
            passivePosition = new Vector2();
            normal          = new Vector2();
        } else {
            didIntersect = true;
            
            ACTIVE_RAY_LIST.clear();
            PASSIVE_RAY_LIST.clear();

            computeCollisionRays(ACTIVE_RAY_LIST, activeT0, activeT1);
            computeCollisionRays(PASSIVE_RAY_LIST, passiveT0, active.getPosition(), active.getVelocity().scale(-1.0f), -active.getRotation());

            final IntersectionResult activeToPassive = findShortestIntersection(ACTIVE_RAY_LIST, passiveT0.getUnmodifiableViewToSegmentList());
            final IntersectionResult passiveToActive = findShortestIntersection(PASSIVE_RAY_LIST, activeT0.getUnmodifiableViewToSegmentList());

            // Resolve the intersection parameters, making sure the normal is towards the active body.
            if (!activeToPassive.intersection.didIntersect() && !passiveToActive.intersection.didIntersect()) {
                // Make up an intersection if none could be found through ray casts.
                distance        = active.getPosition().difference(passive.getPosition()).length();
                activePosition  = active.getPosition();
                passivePosition = passive.getPosition();
                normal          = active.getPosition().difference(passive.getPosition()).normal();
            } else if (activeToPassive.intersection.getDistance() <= passiveToActive.intersection.getDistance()) {
                distance        = activeToPassive.intersection.getDistance();
                activePosition  = activeToPassive.ray.getA();
                passivePosition = activeToPassive.intersection.getPosition();
                normal          = activeToPassive.wall.getRightNormal();
            } else {
                distance        = passiveToActive.intersection.getDistance();
                activePosition  = passiveToActive.intersection.getPosition();
                passivePosition = passiveToActive.ray.getA();
                normal          = passiveToActive.wall.getRightNormal().scale(-1.0f);
            }
        }
    } 
   
    /**
     * @return true if an only if there was an intersection.
     */
    public boolean didIntersect() {
        return didIntersect;
    }
   
    /**
     * @return distance to intersection
     */
    public float getDistance() {
        return distance;
    }
    
    /**
     * @return intersection position for the active body
     */
    public Vector2 getActivePosition() {
        return activePosition;
    }

    /**
     * @return intersection position for the passive body
     */
    public Vector2 getPassivePosition() {
        return passivePosition;
    }

    /**
     * @return intersection normal
     */
    public Vector2 getNormal() {
        return normal;
    }
}
