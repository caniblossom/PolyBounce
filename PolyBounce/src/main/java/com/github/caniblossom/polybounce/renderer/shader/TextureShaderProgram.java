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
 * A simple shader for rendering textured triangles.
 * Partial copy-paste, but it's hard not to copy-paste some of this stuff.
 * @author Jani Salo
 */
public class TextureShaderProgram extends ShaderProgram {
    private final int uViewName;
    private final int uAlphaName;
    private final int uSamplerName;
        
    // Vertex shader inputs.
    private static String[] getInputNameList() {
        return new String[]{"positionIn", "texCoordIn"};
    }

    // Fragment shader outputs.
    private static String[] getOutputNameList() {
        return new String[]{"colorOut"};
    }

    /**
     * Constructs a new simple shader program.
     * @throws RuntimeException 
     */
    public TextureShaderProgram() throws RuntimeException {
        super(getShaderSourceFromResource("textureshader.vs"), getShaderSourceFromResource("textureshader.ps"), getInputNameList(), getOutputNameList());

        uViewName = GL20.glGetUniformLocation(getProgramName(), "view");
        uAlphaName = GL20.glGetUniformLocation(getProgramName(), "alpha");
        uSamplerName = GL20.glGetUniformLocation(getProgramName(), "sampler");
        
        if (uViewName == -1 || uAlphaName == -1 || uSamplerName == -1) {
            deleteProgram();
            throw new RuntimeException("Error getting uniform names.");
        }
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
     * Sets the alpha multiplier used for transparency.
     * @param alpha multiplier value in the range [0, 1]
     */
    public void setAlpha(final float alpha) {
        assert isGood();
        GL20.glUniform1f(uAlphaName, alpha);
    }

    /**
     * Sets the texture sampler index.
     * @param index index of texture to sample.
     */
    public void setSampler(final int index) {
        assert isGood();
        GL20.glUniform1i(uSamplerName, index);
    }
}
