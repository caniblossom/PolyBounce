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
package com.github.caniblossom.polybounce.renderer.opengl;

import java.nio.FloatBuffer;
import org.lwjgl.opengl.GL15;

/**
 * A (relatively useless) wrapper for an OpenGL array buffer.
 * Will probably explode if constructed without proper OpenGL context active.
 * @author Jani Salo
 */
public class VertexBuffer {
    private int bufferName = 0;
    
    /**
     * Constructs a new vertex buffer.
     * @throws RuntimeException 
     */
    public VertexBuffer() throws RuntimeException {
        bufferName = GL15.glGenBuffers();

        if (bufferName == 0) {
            throw new RuntimeException("Error creating vertex buffer.");
        }
    }
    
    /**
     * Binds the related buffer as current GL_ARRAY_BUFFER.
     */
    public void bind() {
        assert isGood();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferName);
    }
    
    /**
     * Binds null object as current GL_ARRAY_BUFFER.
     */
    public void unbind() {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    /**
     * Writes contents of an list to the buffer. Any old data is lost.
     * @param buffer float buffer containing the vertex data
     */
    public void write(final FloatBuffer buffer) {
        assert isGood();
        
        bind();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_DYNAMIC_DRAW);
        unbind();
    }
    
    /**
     * @return true if and only if the OpenGL buffer object is good to use.
     */
    public boolean isGood() {
        return bufferName != 0;
    }

    /**
     * Deletes the OpenGL buffer object.
     */
    public void deleteBuffer() {
        GL15.glDeleteBuffers(bufferName);
        bufferName = 0;
    }
}
