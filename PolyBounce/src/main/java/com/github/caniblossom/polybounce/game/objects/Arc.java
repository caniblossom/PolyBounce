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
import com.github.caniblossom.polybounce.physics.body.RigidBody;
import com.github.caniblossom.polybounce.physics.body.StaticBody;
import java.util.ArrayList;

/**
 * A simple arc for the game.
 * @author Jani Salo
 */
public class Arc extends Structure {
    private static final float MASS_A             = 1.0f;
    private static final float BOUNCINESS_A       = 0.2f;
    private static final float STATIC_FRICTION_A  = 0.2f;
    private static final float DYNAMIC_FRICTION_A = 0.2f;
    
    private static final float MASS_B             = 1000.0f;
    private static final float BOUNCINESS_B       =    1.0f;
    private static final float STATIC_FRICTION_B  =    0.1f;
    private static final float DYNAMIC_FRICTION_B =    0.0f;
        
    private final Vector2 topSpawnPosition;
    private final BoundingBox boundingBox;

    // Adds the base blocks for the arc.
    private void addBase(final float width, final Vector2 position) {
        final ConvexPolygon base = getBuilder().createBox(new Vector2(0.001f, 0.001f), new Vector2(0.999f, 0.999f));

        staticBodyList.add(new StaticBody(base, MASS_B, BOUNCINESS_B, STATIC_FRICTION_B, DYNAMIC_FRICTION_B, position.sum(new Vector2(0.0f, 0.0f)), 0.0f));
        staticBodyList.add(new StaticBody(base, MASS_B, BOUNCINESS_B, STATIC_FRICTION_B, DYNAMIC_FRICTION_B, position.sum(new Vector2(3.0f + width, 0.0f)), 0.0f));        
    }
    
    // Adds a new level to the arc.
    private void addLayer(final float width, final float height, final Vector2 position) {
        final ConvexPolygon hBar = getBuilder().createBox(new Vector2(0.001f, 0.001f), new Vector2(3.999f + width, 0.499f));
        final ConvexPolygon vBar = getBuilder().createBox(new Vector2(0.001f, 0.001f), new Vector2(0.499f, 0.999f + height));
        
        rigidBodyList.add(new RigidBody(vBar, MASS_A, BOUNCINESS_A, STATIC_FRICTION_A, DYNAMIC_FRICTION_A, position.sum(new Vector2(0.25f, 1.0f)), 0.0f, new Vector2(0.0f, 0.0f), 0.0f));
        rigidBodyList.add(new RigidBody(vBar, MASS_A, BOUNCINESS_A, STATIC_FRICTION_A, DYNAMIC_FRICTION_A, position.sum(new Vector2(3.25f + width, 1.0f)), 0.0f, new Vector2(0.0f, 0.0f), 0.0f));
        rigidBodyList.add(new RigidBody(hBar, MASS_A, BOUNCINESS_A, STATIC_FRICTION_A, DYNAMIC_FRICTION_A, position.sum(new Vector2(0.00f, 2.0f + height)), 0.0f, new Vector2(0.0f, 0.0f), 0.0f));        
    }
    
    /**
     * Constructs a new arc at given position.
     * @param width width modifier for the arc
     * @param height height modifier for the arc
     * @param position lower left corner of the arc
     * @param layers number of layers on the arc
     */
    public Arc(final float width, final float height, final Vector2 position, final int layers) {
        super();
                
        addBase(width, position);
        for (int i = 0; i < layers; i++) {
            addLayer(width, height, position.sum(new Vector2(0.0f, (float) i * (1.5f + height))));
        }

        BoundingBox box = new BoundingBox(position, 0.0f, 0.0f);
        box = combineBoundingBoxes(staticBodyList, box);
        box = combineBoundingBoxes(rigidBodyList, box);

        topSpawnPosition = new Vector2(2.0f + 0.5f * width, 3.0f + (height + 1.5f) * (float) layers).sum(position);
        boundingBox = box;
    }
    
    /**
     * Copy constructor.
     * @param arc arc to copy
     */
    public Arc(final Arc arc) {
        super();
        
        for (StaticBody body : arc.staticBodyList) {
            this.staticBodyList.add(new StaticBody(body));
        }

        for (RigidBody body : arc.rigidBodyList) {
            this.rigidBodyList.add(new RigidBody(body));
        }

        this.topSpawnPosition = new Vector2(arc.topSpawnPosition);
        this.boundingBox = new BoundingBox(arc.boundingBox);
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
        return new Arc(this);
    }
}
