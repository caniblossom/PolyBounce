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
package com.github.caniblossom.polybounce.game.objects;

import com.github.caniblossom.polybounce.math.BoundingBox;
import com.github.caniblossom.polybounce.math.ConvexPolygon;
import com.github.caniblossom.polybounce.math.Vector2;
import com.github.caniblossom.polybounce.physics.body.StaticBody;
import java.util.ArrayList;

/**
 * A goal structure for the game. Should 
 * @author Jani Salo
 */
public class Goal extends Structure {
    private static final float MASS             = 1000.0f;
    private static final float BOUNCINESS       =   -10.0f;
    private static final float STATIC_FRICTION  =    0.0f;
    private static final float DYNAMIC_FRICTION =    0.0f;
    
    private final Vector2 topSpawnPosition;
    private final BoundingBox boundingBox;
    
    // Adds a new tooth to the goal.
    private void addTooth(final float outRadius, final float angle, final float ratio, final Vector2 position) {
        final ArrayList<Vector2> vertexList = new ArrayList();
       
        final float goldenRatio = 1.6180339887498948482045868343656f;
        final float inRadius = outRadius / 2.0f / goldenRatio;
        
        final float a = angle - (float) Math.PI * ratio * 0.999f;
        final float b = angle;
        final float c = angle + (float) Math.PI * ratio * 0.999f;
        
        vertexList.add((new Vector2((float) Math.cos(a), (float) Math.sin(a))).scale(inRadius));
        vertexList.add((new Vector2((float) Math.cos(b), (float) Math.sin(b))).scale(outRadius));
        vertexList.add((new Vector2((float) Math.cos(c), (float) Math.sin(c))).scale(inRadius));
        
        final ConvexPolygon hull = ConvexPolygon.constructNew(vertexList);
        staticBodyList.add(new StaticBody(hull, MASS, BOUNCINESS, STATIC_FRICTION, DYNAMIC_FRICTION, position, 0.0f));        
    }
    
    /**
     * Constructs a new goal at given position.
     * @param outRadius outer radius of the goal
     * @param position center of the goal
     */
    public Goal(final float outRadius, final Vector2 position) {
        super();
                
        for (int i = 0; i < 5; i++) {
            final float r = 2.0f * (float) Math.PI * (float) i / 5.0f;
            addTooth(outRadius, r, 1.0f / 5.0f, position);
        }

        BoundingBox box = new BoundingBox(position, 0.0f, 0.0f);
        box = combineBoundingBoxes(staticBodyList, box);

        topSpawnPosition = new Vector2(0.0f, outRadius + 2.0f).sum(position);
        boundingBox = box;
    }
    
    /**
     * Copy constructor.
     * @param goal goal to copy
     */
    public Goal(final Goal goal) {
        super();
        
        for (StaticBody body : goal.staticBodyList) {
            this.staticBodyList.add(new StaticBody(body));
        }

        this.topSpawnPosition = new Vector2(goal.topSpawnPosition);
        this.boundingBox = new BoundingBox(goal.boundingBox);
    }

    /**
     * @return bounding box of the structure
     */
    @Override
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    /**
     * @return the position for objects to be put on top of the box in world space
     */
    @Override
    public Vector2 getTopSpawnPosition() {
        return topSpawnPosition;
    }   
    
    /**
     * @return copy created by the copy constructor.
     */
    @Override
    public Structure getCopy() {
        return new Goal(this);
    }
}
