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
import com.github.caniblossom.polybounce.math.Vector2;
import java.util.ArrayList;
import java.util.List;

// TODO Implement tests.

/**
 * An utility class for handling collisions between rigid bodies.
 * See: http://en.wikipedia.org/wiki/Collision_response
 * @author Jani Salo
 */
public class CollisionHandler {
    private final ArrayList<Vector2> currentCollisionRayList;
    private final ArrayList<Vector2> currentInverseCollisionRayList;

    // Computes relative motions of vertices for a convex polygon from samples at
    // different points in time, ie. the collision rays for the hull of an active body.
    private void computeCurrentCollisionRays(final ConvexPolygon t0, final ConvexPolygon t1) {
        final List<Vector2> listT0 = t0.getUnmodifiableViewToVertexList();
        final List<Vector2> listT1 = t1.getUnmodifiableViewToVertexList();
        assert listT0.size() == listT1.size();

        currentCollisionRayList.clear();
       
        int vertex = 0;
        for (Vector2 v : listT1) {
            currentCollisionRayList.add(v.difference(listT0.get(vertex++)));
        }
    }
    
    // Computes relative motions of vertices for a polygon relative to the motion of 
    // some other body, ie. inverse collision rays for the hull of a passive body.
    private void computeCurrentInverseCollisionRays(final ConvexPolygon polyT0, final Vector2 spaceOrigo, final float spaceRotation, final Vector2 spaceTranslation, final float dt) {
        final ConvexPolygon translated = polyT0.rotateAndTranslate(spaceOrigo, 0.0f, spaceTranslation.scale(-1.0f));
        final ConvexPolygon polyT1 = translated.rotateAndTranslate(spaceOrigo, -spaceRotation, new Vector2(0.0f, 0.0f));

        final List<Vector2> listT0 = polyT0.getUnmodifiableViewToVertexList();        
        final List<Vector2> listT1 = polyT1.getUnmodifiableViewToVertexList();

        currentInverseCollisionRayList.clear();
        
        int vertex = 0;
        for (Vector2 v : listT1) {
            currentInverseCollisionRayList.add(v.difference(listT0.get(vertex++)));
        }
    }
    
    // If no collision can be found from the internal collision ray lists, then one is made up.
    private void computeCollisionFromCurrentData(RigidBody active, RigidBody passive) {
        // TODO Implement.
    }    
    
    /**
     * Constructs a new collision handler.
     */
    public CollisionHandler() {
        currentCollisionRayList = new ArrayList();
        currentInverseCollisionRayList = new ArrayList();
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
        
        if (!activeT1.doesIntersect(passiveT0)) {
            active.update(dt);
            return false;
        }
        
        computeCurrentCollisionRays(activeT0, activeT1);
        computeCurrentInverseCollisionRays(passiveT0, active.getPosition(), active.getRotation(), active.getVelocity(), dt);
        computeCollisionFromCurrentData(active, passive);

        return true;
    }
}
