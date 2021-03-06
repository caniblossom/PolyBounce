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
package com.github.caniblossom.polybounce.renderer.shader;

import com.github.caniblossom.polybounce.renderer.opengl.ShaderProgram;
import java.nio.FloatBuffer;
import org.lwjgl.opengl.GL20;

/**
 * The basic shader used in the game.
 * @author Jani Salo
 */
public class SimpleShaderProgram extends ShaderProgram {
    private final int uProjectionName;
    private final int uViewName;

    private final int uLightPositionName;
    private final int uLightColorName;
     
    // Vertex shader inputs.
    private static String[] getInputNameList() {
        return new String[]{"positionIn", "colorIn", "normalIn"};
    }

    // Fragment shader outputs.
    private static String[] getOutputNameList() {
        return new String[]{"colorOut"};
    }

    /**
     * Constructs a new simple shader program.
     * @throws RuntimeException 
     */
    public SimpleShaderProgram() throws RuntimeException {
        super(getShaderSourceFromResource("simpleshader.vs"), getShaderSourceFromResource("simpleshader.ps"), getInputNameList(), getOutputNameList());

        uProjectionName    = GL20.glGetUniformLocation(getProgramName(), "projection");
        uViewName          = GL20.glGetUniformLocation(getProgramName(), "view");
        uLightPositionName = GL20.glGetUniformLocation(getProgramName(), "lightPosition");
        uLightColorName    = GL20.glGetUniformLocation(getProgramName(), "lightColor");
                
        if (uProjectionName == -1 || uViewName == -1 || uLightPositionName == -1 || uLightColorName == -1) {
            // Destroy the shader program object that might be lingering around.
            deleteProgram();

            throw new RuntimeException("Error getting uniform names.");
        }
    }
    
    /**
     * Sets the projection matrix used in the shader.
     * @param projection a buffer containing at least 16 floats representing a 4x4 projection matrix
     */
    public void setProjection(final FloatBuffer projection) {        
        assert isGood();        

        projection.rewind();
        if (projection.remaining() < 16) {
            return;
        }

        GL20.glUniformMatrix4(uProjectionName, false, projection);
    }

    /**
     * Sets the view transformation used in the shader.
     * @param view a buffer containing at least 16 floats representing a 4x4 view matrix
     */
    public void setView(final FloatBuffer view) {        
        assert isGood();        
        
        view.rewind();
        if (view.remaining() < 16) {
            return;
        }
        
        GL20.glUniformMatrix4(uViewName, false, view);
    }
 
    /**
     * Sets the position of the light used in the shader.
     * @param x x component of the position
     * @param y y component of the position
     * @param z z component of the position
     */
    public void setLightPosition(final float x, final float y, final float z) {        
        assert isGood();        
        GL20.glUniform3f(uLightPositionName, x, y, z);
    }

    /**
     * Sets the color of the light used in the shader.
     * @param r red component of the light
     * @param g green component of the light
     * @param b blue component of the light
     */
    public void setLightColor(final float r, final float g, final float b) {        
        assert isGood();
        GL20.glUniform3f(uLightColorName, r, g, b);
    }
}
