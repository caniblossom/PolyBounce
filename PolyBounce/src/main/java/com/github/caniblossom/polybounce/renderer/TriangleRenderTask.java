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

import com.github.caniblossom.polybounce.opengl.VertexBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

// TODO Add tests if somehow possible.
// TODO Add a method for releasing the vertex array object / invalidating the class instance.

/**
 * A "task" for rendering plain triangles.
 * @author Jani Salo
 */
public class TriangleRenderTask implements RenderTask {
    private boolean isInitialized = false;

    private int vertexArrayName = 0;
    private VertexBuffer vertexBuffer = null;

    private SimpleShaderProgram shaderProgram = null;

    /**
     * Sets up the vertex array for rendering.
     */
    private void setupVertexArray() {
        GL30.glBindVertexArray(vertexArrayName);
        vertexBuffer.bind();
        
        // Specify consecutive attributes consisting of two float values.
        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);
        GL20.glEnableVertexAttribArray(0);

        // TODO Enable after verifying the code works.
        // vertexBuffer.unbind();
        GL30.glBindVertexArray(0);
    }
    
    /**
     * Initializes the object.
     * @throws RuntimeException
     */
    private void initialize() throws RuntimeException {
        try {
            vertexArrayName = GL30.glGenVertexArrays();

            if (vertexArrayName == 0) {
                throw new RuntimeException("Error creating vertex array");
            }

            vertexBuffer = new VertexBuffer();
            setupVertexArray();

            shaderProgram = new SimpleShaderProgram();
        } catch (Exception e) {
            GL30.glDeleteVertexArrays(vertexArrayName);
        }
        
        isInitialized = true;
    }

    /**
     * Empty constructor.
     */
    public TriangleRenderTask() {}
 
    @Override
    public void run() {
        if (!isInitialized) {
            try {
                // It's safest to construct at the last possible moment to make sure
                // that an OpenGL context is available and set as current context.
                initialize();
            } catch (Exception e) {
                System.out.println("FUCK!");
                // TODO Handle this.
            }
        }

        // Short explanation: To render contents of an Array Buffer Object we
        // need to specify how the vertices are laid out in the buffer, this,
        // like everything in OpenGL, is done by setting internal states.
        // The purpose of the Vertex Array Object (VAO) is to store the internal
        // state, so that we can simply bind the related VAO when we need to do
        // some rendering. Not only that, but I'm quite sure that OpenGL 3.0
        // requires you to have a VAO around to do any drawing at all.
        
        // TODO Set up the shader program.
        shaderProgram.use();
        
        GL30.glBindVertexArray(vertexArrayName);
        vertexBuffer.bind();
        
        // Draw the triangles.
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertexBuffer.getElementCount() / 3);

        vertexBuffer.unbind();
        GL30.glBindVertexArray(0);
    }
}
