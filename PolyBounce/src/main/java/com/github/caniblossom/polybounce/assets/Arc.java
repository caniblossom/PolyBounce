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
package com.github.caniblossom.polybounce.assets;

import com.github.caniblossom.polybounce.game.Structure;
import com.github.caniblossom.polybounce.math.BoundingBox;
import com.github.caniblossom.polybounce.math.ConvexPolygon;
import com.github.caniblossom.polybounce.math.Vector2;
import com.github.caniblossom.polybounce.physics.RigidBody;
import com.github.caniblossom.polybounce.physics.StaticBody;

/**
 * A simple arc for the game.
 * @author Jani Salo
 */
public class Arc extends Structure {
    private static final float MASS_A             = 1.0f;
    private static final float BOUNCINESS_A       = 0.2f;
    private static final float STATIC_FRICTION_A  = 0.4f;
    private static final float DYNAMIC_FRICTION_A = 0.5f;
    
    private static final float MASS_B             = 1000.0f;
    private static final float BOUNCINESS_B       =    0.8f;
    private static final float STATIC_FRICTION_B  =    0.1f;
    private static final float DYNAMIC_FRICTION_B =    0.0f;
    
    private static final ConvexPolygon HBAR = getBuilder().createBox(new Vector2(0.001f, 0.001f), new Vector2(5.999f, 0.499f));
    private static final ConvexPolygon VBAR = getBuilder().createBox(new Vector2(0.001f, 0.001f), new Vector2(0.499f, 3.999f));
    private static final ConvexPolygon BASE = getBuilder().createBox(new Vector2(0.001f, 0.001f), new Vector2(0.999f, 0.999f));
    
    private final Vector2 topSpawnPosition;
    private final BoundingBox boundingBox;
            
    private void addLayer(final Vector2 offset) {
        rigidBodyList.add(new RigidBody(VBAR, MASS_A, BOUNCINESS_A, STATIC_FRICTION_A, DYNAMIC_FRICTION_A, offset.sum(new Vector2(0.25f, 1.0f)), 0.0f, new Vector2(0.0f, 0.0f), 0.0f));
        rigidBodyList.add(new RigidBody(VBAR, MASS_A, BOUNCINESS_A, STATIC_FRICTION_A, DYNAMIC_FRICTION_A, offset.sum(new Vector2(5.25f, 1.0f)), 0.0f, new Vector2(0.0f, 0.0f), 0.0f));
        rigidBodyList.add(new RigidBody(HBAR, MASS_A, BOUNCINESS_A, STATIC_FRICTION_A, DYNAMIC_FRICTION_A, offset.sum(new Vector2(0.00f, 5.0f)), 0.0f, new Vector2(0.0f, 0.0f), 0.0f));        
    }
    
    /**
     * Constructs a new arc at given position.
     * @param position lower left corner of the arc
     * @param layers number of layers on the arc
     */
    public Arc(final Vector2 position, final int layers) {
        super();
                
        staticBodyList.add(new StaticBody(BASE, MASS_B, BOUNCINESS_B, STATIC_FRICTION_B, DYNAMIC_FRICTION_B, position.sum(new Vector2(0.0f, 0.0f)), 0.0f));
        staticBodyList.add(new StaticBody(BASE, MASS_B, BOUNCINESS_B, STATIC_FRICTION_B, DYNAMIC_FRICTION_B, position.sum(new Vector2(5.0f, 0.0f)), 0.0f));

        for (int i = 0; i < layers; i++) {
            addLayer(position.sum(new Vector2(0.0f, 4.5f).scale((float) i)));
        }

        BoundingBox box = new BoundingBox(position, 0.0f, 0.0f);
        box = combineBoundingBoxes(staticBodyList, box);
        box = combineBoundingBoxes(rigidBodyList, box);

        topSpawnPosition = new Vector2(3.0f, 3.0f + 4.5f * (float) layers).sum(position);
        boundingBox = box;
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
}
