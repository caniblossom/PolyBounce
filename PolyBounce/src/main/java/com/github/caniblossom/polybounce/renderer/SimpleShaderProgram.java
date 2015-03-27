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

import com.github.caniblossom.polybounce.opengl.ShaderProgram;
import java.nio.FloatBuffer;
import org.lwjgl.opengl.GL20;

// TODO See if it's possible to test this.

/**
 * A very simple shader.
 * @author Jani Salo
 */
public class SimpleShaderProgram extends ShaderProgram {
    private final int uProjectionName;
    private final int uViewName;
    
    /**
     * @return source for the vertex shader
     */
    private static String getVertexShaderSource() {
        return 
            "#version 150                                                            \n" + 
            "                                                                        \n" +
            "uniform mat4 projection;                                                \n" +
            "uniform mat4 view;                                                      \n" +
            "                                                                        \n" +
            "in vec3 positionIn;                                                     \n" +
            "                                                                        \n" +
            "void main() {                                                           \n" +                                                         
            "    gl_Position = projection * view * vec4(positionIn, 1.0);            \n" +
            "}                                                                       \n";
    }

    /**
     * @return source for the fragment shader
     */
    private static String getFragmentShaderSource() {
        return 
            "#version 150                                                            \n" + 
            "                                                                        \n" +
            "out vec4 colorOut;                                                      \n" + 
            "                                                                        \n" +
            "void main() {                                                           \n" +
            "    colorOut = vec4(1.0, 1.0, 1.0, 1.0);                                \n" +
            "}                                                                       \n";
    }
    
    /**
     * @return input name list.
     */
    private static String[] getInputNameList() {
        return new String[]{"positionIn"};
    }

    /**
     * Constructs a new simple shader program.
     * @throws RuntimeException 
     */
    public SimpleShaderProgram() throws RuntimeException {
        super(getVertexShaderSource(), getFragmentShaderSource(), getInputNameList());
        
        uProjectionName  = GL20.glGetUniformLocation(getProgramName(), "projection");
        uViewName        = GL20.glGetUniformLocation(getProgramName(), "view");
        
        if (uProjectionName == -1 || uViewName == -1) {
            // TODO Delete the shader program.
            throw new RuntimeException("Error getting uniform names.");
        }
    }
    
    /**
     * @param projection a buffer containing at least 16 floats representing a 4x4 projection matrix
     */
    public void setProjection(final FloatBuffer projection) {        
        projection.reset();
        
        if (projection.remaining() < 16) {
            // Fail silently on invalid buffer.
            return;
        }

        GL20.glUniformMatrix4(uProjectionName, false, projection);
    }

    /**
     * @param view a buffer containing at least 16 floats representing a 4x4 view matrix
     */
    public void setView(final FloatBuffer view) {        
        view.reset();

        if (view.remaining() < 16) {
            // Fail silently on invalid buffer.
            return;
        }
        
        GL20.glUniformMatrix4(uProjectionName, false, view);
    }
}
