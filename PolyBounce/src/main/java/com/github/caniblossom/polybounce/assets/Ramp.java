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
import com.github.caniblossom.polybounce.physics.StaticBody;
import java.util.ArrayList;

/**
 * A simple ramp.
 * @author Jani Salo
 */
public class Ramp extends Structure {
    private static final float MASS             = 1000.0f;
    private static final float BOUNCINESS       =    0.8f;
    private static final float STATIC_FRICTION  =    0.4f;
    private static final float DYNAMIC_FRICTION =    0.5f;
    
    private final Vector2 topSpawnPosition;
    private final BoundingBox boundingBox;

    /**
     * Constructs a ramp arc at given position.
     * @param position lower left corner of the ramp
     * @param delta top right corner of the ramp
     */
    public Ramp(final Vector2 position, final Vector2 delta) {
        super();

        assert delta.getX() > 0.0f && delta.getY() > 0.0f;
        
        final ArrayList<Vector2> vertexList = new ArrayList();
        final Vector2 origo = new Vector2(0.0f, 0.0f);

        vertexList.add(origo);
        vertexList.add(origo.sum(new Vector2(delta.getX(), 0.0f)));
        vertexList.add(origo.sum(delta));

        final ConvexPolygon ramp = ConvexPolygon.constructNew(vertexList);
        staticBodyList.add(new StaticBody(ramp, MASS, BOUNCINESS, STATIC_FRICTION, DYNAMIC_FRICTION, position, 0.0f));

        topSpawnPosition = position.sum(delta.scale(0.5f)).sum(new Vector2(0.0f, 2.0f));
        boundingBox = ramp.getBoundingBox();
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
