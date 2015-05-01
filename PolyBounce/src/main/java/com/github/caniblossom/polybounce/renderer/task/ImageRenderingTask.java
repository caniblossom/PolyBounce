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
package com.github.caniblossom.polybounce.renderer.task;

import com.github.caniblossom.polybounce.renderer.shader.TextureShaderProgram;
import com.github.caniblossom.polybounce.renderer.opengl.VertexArray;
import com.github.caniblossom.polybounce.renderer.opengl.VertexBuffer;
import com.github.caniblossom.polybounce.renderer.shader.TextureShaderVertex;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.function.Predicate;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**
 * A rendering task for rendering a set images.
 * @author Jani Salo
 */
public class ImageRenderingTask implements RenderingTask {
    private final static int FLOATS_PER_VERTEX = 2 + 2;
    
    private VertexArray vertexArray = null;
    private VertexBuffer vertexBuffer = null;    
    private TextureShaderProgram shaderProgram = null;
    
    private final ArrayList<Image> imageList;
    
    // Writes a quad corresponding to the bounding box to the vertex buffer.
    private void setupVertexBuffer() {
        final FloatBuffer triangleData = BufferUtils.createFloatBuffer(6 * FLOATS_PER_VERTEX);
                
        triangleData.put(new TextureShaderVertex(0.0f, 0.0f, 0.0f, 0.0f).getAsAnArray());
        triangleData.put(new TextureShaderVertex(1.0f, 0.0f, 1.0f, 0.0f).getAsAnArray());
        triangleData.put(new TextureShaderVertex(1.0f, 1.0f, 1.0f, 1.0f).getAsAnArray());
        
        triangleData.put(new TextureShaderVertex(1.0f, 1.0f, 1.0f, 1.0f).getAsAnArray());
        triangleData.put(new TextureShaderVertex(0.0f, 1.0f, 0.0f, 1.0f).getAsAnArray());
        triangleData.put(new TextureShaderVertex(0.0f, 0.0f, 0.0f, 0.0f).getAsAnArray());

        triangleData.rewind();
        vertexBuffer.write(triangleData);        
    }
    
    // Sets up the vertex array for the format used by the texture shader program.
    private void setupVertexArray() {
        vertexArray.bind();
        vertexBuffer.bind();
        
        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 16, 0 * Float.BYTES);
        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 16, 2 * Float.BYTES);

        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);

        vertexArray.bind();
        vertexBuffer.unbind();
    }
        
    // Clears up any tasks that have finished
    private void removeFinishedTasks() {
        imageList.removeIf(new Predicate<Image>() {
            @Override
            public boolean test(Image t) {
                return t.hasFinished();
            }
        });
    }
    
    /**
     * Constructs a new image rendering task.
     */
    public ImageRenderingTask() {
        try {
            this.vertexArray = new VertexArray();
            this.shaderProgram = new TextureShaderProgram();
            this.vertexBuffer = new VertexBuffer();
        } catch (Exception e) {
            deleteGLResources();
            throw e;
        }

        setupVertexArray();
        setupVertexBuffer();
        
        imageList = new ArrayList();
    }
    
    /**
     * @return true if and only if the necessary OpenGL resources are good to use.
     */
    public boolean isGood() {
        if (vertexArray == null || shaderProgram == null || vertexBuffer == null) {
            return false;
        } else {
            return vertexArray.isGood() && shaderProgram.isGood() && vertexBuffer.isGood();
        }
    }
    
    /**
     * Deletes any OpenGL resources allocated by this object.
     */
    public void deleteGLResources() {        
        if (shaderProgram != null) {
            shaderProgram.deleteProgram();
        }

        if (vertexBuffer != null) {
            vertexBuffer.deleteBuffer();
        }
        
        if (vertexArray != null) {
            vertexArray.deleteArray();
        }
    }

    /**
     * @param image image task to add
     */
    public void addImage(final Image image) {
        imageList.add(image);
    }
    
    /**
     * Removes all image tasks from this image rendering task.
     */
    public void removeAllImages() {
        imageList.clear();
    }
    
    /**
     * Executes all the image tasks.
     */
    @Override
    public void run() {
        assert isGood();

        removeFinishedTasks();
        
        vertexArray.bind();
        vertexBuffer.bind();

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        shaderProgram.use();
        shaderProgram.setSampler(0);
        
        for (Image image : imageList) {
            image.getTexture().bind();

            shaderProgram.setAlpha(image.getAlpha());
            shaderProgram.setView(image.getView());
            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 2 * 3);

            image.getTexture().unbind();
        }
        
        GL11.glDisable(GL11.GL_BLEND);

        vertexArray.unbind();
        vertexBuffer.unbind();
    }
}
