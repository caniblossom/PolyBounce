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
import com.github.caniblossom.polybounce.math.PolygonBuilder;
import com.github.caniblossom.polybounce.math.Vector2;
import com.github.caniblossom.polybounce.physics.body.Body;
import com.github.caniblossom.polybounce.physics.body.RigidBody;
import com.github.caniblossom.polybounce.physics.body.StaticBody;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An abstract class for level structures.
 * @author Jani Salo
 */
public abstract class Structure {
    private static final PolygonBuilder BUILDER = new PolygonBuilder();

    protected final ArrayList<RigidBody> rigidBodyList;
    protected final ArrayList<StaticBody> staticBodyList;
    
    /**
     * @return reference to a static builder instance for building polygons
     */
    protected static PolygonBuilder getBuilder() {
        return BUILDER;
    }
    
    /**
     * Computes a bounding box from a list of boxes.
     * @param <T> type extending Body
     * @param list list of Bodies whose bounding boxes to combine
     * @param boundingBox initial bounding box
     * @return combined bounding box
     */
    protected <T extends Body> BoundingBox combineBoundingBoxes(List<T> list, BoundingBox boundingBox) {
        for (T body : list) {
            boundingBox = body.getHull().getBoundingBox().combine(boundingBox);
        }
        
        return boundingBox;
    }
    
    /**
     * Constructs a new structure.
     */
    public Structure() {
        this.rigidBodyList = new ArrayList();
        this.staticBodyList = new ArrayList();
    }
    
    /**
     * @return an unmodifiable view to rigid body list.
     */
    public List<RigidBody> getUnmodifiableViewToRigidBodyList() {
        return Collections.unmodifiableList(rigidBodyList);
    }

    /**
     * @return an unmodifiable view to static body list.
     */
    public List<StaticBody> getUnmodifiableViewToStaticBodyList() {
        return Collections.unmodifiableList(staticBodyList);
    }

    /**
     * @return bounding box of the structure
     */
    public abstract BoundingBox getBoundingBox();    

    /**
     * @return the position for objects to be put on top of the box in world space
     */
    public abstract Vector2 getTopSpawnPosition();
    
    /**
     * Should call the copy constructor.
     * @return copy of the structure
     */
    public abstract Structure getCopy(); 
}
