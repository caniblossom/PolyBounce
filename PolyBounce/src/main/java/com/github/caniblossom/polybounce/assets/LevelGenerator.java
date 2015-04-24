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

import com.github.caniblossom.polybounce.game.Level;
import com.github.caniblossom.polybounce.game.Structure;
import com.github.caniblossom.polybounce.math.PolygonBuilder;
import com.github.caniblossom.polybounce.math.Vector2;

/**
 * A little class for generating levels.
 * @author Jani Salo
 */
public class LevelGenerator {
    /**
     * Generates a new level
     * @param length length of the level in structures
     * @return new level
     */
    public Level generate(int length) {
        final Level level = new Level();

        Structure last = new Arc(new Vector2(0.0f, 0.0f), 2);

        level.addStructure(last);
        level.setPlayerSpawnPosition(last.getTopSpawnPosition());

        for (int i = 0; i < length; i++) {
            final Vector2 lastMin = last.getBoundingBox().getPosition();
            final Vector2 lastMax = last.getBoundingBox().getMaximum();
            
            last = new Arc(new Vector2(lastMax.getX() + 1.0f, lastMin.getY() + (float) Math.random() * 0.5f), 1);
            level.addStructure(last);
        }
        
        final Vector2 offset = last.getTopSpawnPosition();
        level.setGoal(new PolygonBuilder().createBox(offset.sum(new Vector2(-0.5f, -0.5f)), new Vector2(0.5f, 0.5f)));

        return level;
    }
}
