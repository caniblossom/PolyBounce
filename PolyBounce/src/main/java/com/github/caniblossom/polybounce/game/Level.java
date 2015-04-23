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
package com.github.caniblossom.polybounce.game;

import com.github.caniblossom.polybounce.math.BoundingBox;
import com.github.caniblossom.polybounce.math.ConvexPolygon;
import com.github.caniblossom.polybounce.math.PolygonBuilder;
import com.github.caniblossom.polybounce.math.Vector2;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A class for representing a game level.
 * @author Jani Salo
 */
public class Level {
    private final ArrayList<Structure> structureList;

    private Vector2 playerSpawnPosition;
    private ConvexPolygon goalPolygon;

    private BoundingBox boundingBox;
    
    /**
     * Constructs a new level.
     */
    public Level() {
        this.structureList = new ArrayList();
        this.playerSpawnPosition = new Vector2(0.0f, 0.0f);
        this.goalPolygon = new PolygonBuilder().createBox(new Vector2(-1.0f, -1.0f), new Vector2(1.0f, 1.0f));
        this.boundingBox = new BoundingBox(new Vector2(0.0f, 0.0f), 0.0f, 0.0f);
    }
    
    /**
     * Adds a new structure to the level.
     * @param structure structure to add
     */
    public void addStructure(final Structure structure) {
        structureList.add(structure);
       
        boundingBox = structureList.get(0).getBoundingBox();
        for (Structure s : structureList) {
            boundingBox = s.getBoundingBox().combine(boundingBox);
        }
    }
    
    /**
     * @return unmodifiable list containing level structures
     */
    public List<Structure> getUnmodifiableViewToStructures() {
        return Collections.unmodifiableList(structureList);
    }
    
    /**
     * @return player spawning position
     */
    public Vector2 getPlayerSpawnPosition() {
        return playerSpawnPosition;
    }
    
    /**
     * @return goal of the level
     */
    public ConvexPolygon getGoal() {
        return goalPolygon;
    }
    
    /**
     * @return bounding box containing the whole level.
     */
    public BoundingBox getLevelBounds() {
        return boundingBox;
    }

    /**
     * @param position new spawning position for player
     */
    public void setPlayerSpawnPosition(final Vector2 position) {
        playerSpawnPosition = position;
    }
    
    /**
     * @param poly polygon to set as the level goal
     */
    public void setGoal(final ConvexPolygon poly) {
        goalPolygon = poly;
    }
}
