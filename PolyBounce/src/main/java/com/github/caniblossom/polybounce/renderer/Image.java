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
package com.github.caniblossom.polybounce.renderer;

import com.github.caniblossom.polybounce.math.BoundingBox;
import com.github.caniblossom.polybounce.renderer.opengl.Texture2D;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

/**
 * A class for representing an image to be displayed.
 * @author Jani Salo
 */
public class Image {
    private final float alphaT0;
    private final float alphaT1;

    private final long timeBegin;
    private final long duration;
    private final boolean hasDuration;

    private final Texture2D texture;
    private final FloatBuffer view;

    private boolean hasFinished = false;
    
    // Constructs a matrix for mapping an unit square to clipping space.
    private FloatBuffer createViewMatrix(final BoundingBox box) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);

        final float[] matrix = new float[]{
            box.getWidth(),           0.0f,                     0.0f, 0.0f, 
            0.0f,                     box.getHeight(),          0.0f, 0.0f, 
            0.0f,                     0.0f,                     1.0f, 0.0f, 
            box.getPosition().getX(), box.getPosition().getY(), 0.0f, 1.0f
        };

        buffer.put(matrix);
        return buffer;
    }
    
    /**
     * Constructs a new image task.
     * @param box bounding box of the image in clipping space
     * @param alphaT0 alpha multiplier at the beginning
     * @param alphaT1 alpha multiplier at the end
     * @param duration duration of the image in milliseconds
     * @param texture texture containing the image
     */
    public Image(final BoundingBox box, final float alphaT0, final float alphaT1, final long duration, final Texture2D texture) {
        this.alphaT0 = alphaT0;
        this.alphaT1 = alphaT1;
        
        this.timeBegin = System.currentTimeMillis();
        this.duration = duration;
        this.hasDuration = true;
        
        this.texture = texture;
        this.view = createViewMatrix(box);
    }

    /**
     * Constructs a new image task.
     * @param box bounding box of the image in clipping space
     * @param alpha alpha multiplier
     * @param texture texture containing the image
     */
    public Image(final BoundingBox box, final float alpha, final Texture2D texture) {
        this.alphaT0 = alpha;
        this.alphaT1 = alpha;
        
        this.timeBegin = Long.MAX_VALUE;
        this.duration = Long.MAX_VALUE;
        this.hasDuration = false;
        
        this.texture = texture;
        this.view = createViewMatrix(box);
    }

    /**
     * @return true if and only if the task has finished.
     */
    public boolean hasFinished() {
        if (hasDuration && System.currentTimeMillis() > timeBegin + duration) {
            setFinished();
        }
        
        return hasFinished;
    }
    
    /**
     * Sets the task finished.
     */
    public void setFinished() {
        hasFinished = true;
    }
    
    /**
     * @return current alpha for the image to be displayed.
     */
    public float getAlpha() {
        if (hasDuration) {
            final float w = Math.max(0.0f, Math.min(1.0f, (float) (System.currentTimeMillis() - timeBegin) / (float) duration));
            return (1.0f - w) * alphaT0 + w * alphaT1;
        }

        return alphaT0;
    }
    
    /**
     * @return view matrix for this image.
     */
    public FloatBuffer getView() {
        return view;
    }
    
    /**
     * @return texture for this image.
     */
    public Texture2D getTexture() {
        return texture;
    }
}
