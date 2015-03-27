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

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

// TODO Do some testing on these, although it's somewhat difficult to get all the cases.

/**
 * A static utility class for generating the matrices used in the renderer.
 * @author Jani Salo
 */
public class MatrixUtil {
    private static FloatBuffer createBufferFromArray(float[] array) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(array.length);

        buffer.put(array);
        buffer.rewind();
    
        return buffer;
    }
    
    /**
     * Private constructor because there doesn't seem to be a better way of doing this in Java.
     */
    private MatrixUtil() {     
    }

    /**
     * Creates the kind of Projection matrix that plays along with the shader pipeline.
     * See https://www.opengl.org/sdk/docs/man2/xhtml/gluPerspective.xml for details.
     * @param fov field of view in vertical direction in degrees
     * @param aspect aspect ratio of the output
     * @param near near clip plane
     * @param far far clip plane
     * @return FloatBuffer containing the matrix
     */
    public static FloatBuffer createProjection(final float fov, float aspect, float near, final float far) {
        if (aspect == 0.0f) {
            aspect = Float.MIN_VALUE;
        }
        
        final float f = (float) (1.0f / Math.tan(fov / 360.0f * 2.0f * (float) Math.PI / 2.0f));
 
        // I know it's ugly.
        final float[] matrix = new float[]{f / aspect, 0.0f, 0.0f, 0.0f, 0.0f, f, 0.0f, 0.0f, 0.0f, 0.0f, (far + near) / (near - far), -1.0f, 0.0f, 0.0f, 2.0f * far * near / (near - far), 0.0f };
        
        return createBufferFromArray(matrix);
    }

    /**
     * Creates a vies matrix for a camera directly facing the XY plane.
     * @param x x position of the camera
     * @param y y position of the camera
     * @param distance relative distance
     * @return FloatBuffer containing the matrix
     */
    public static FloatBuffer createViewPlaneXY(final float x, final float y, float distance) {
        if (distance == 0.0f) {
            distance = Float.MIN_VALUE;
        }
        
        final float[]matrix = new float[]{1.0f / distance, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f / distance, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 00f, -x / distance, -y / distance, 0.0f, 1.0f};

        return createBufferFromArray(matrix);
    }
}
