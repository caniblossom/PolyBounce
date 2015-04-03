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

import com.github.caniblossom.polybounce.math.ConvexPolygon;
import com.github.caniblossom.polybounce.opengl.VertexBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

// TODO Implement tests if possible.

/**
 * A task for rendering convex polygons with the simple shader.
 * @author Jani Salo
 */
public class PolygonRenderingTask extends SimpleShaderRenderingTask {
    private static final int VERTEX_SIZE_IN_FLOATS = 3 * 3;

    private static final float FRONT_DEPTH = -1.0f;
    private static final float BACK_DEPTH = -1.25f;
    
    private int vertexArrayName = 0;
    private VertexBuffer vertexBuffer = null;
 
    private ArrayList<Float> triangleList = null;
    private FloatBuffer triangleBuffer = null;
    
    private Tessellator tessellator = null;
    
    /**
     * Sets up the vertex array for rendering.
     */
    private void setupVertexArray() {
        GL30.glBindVertexArray(vertexArrayName);
        vertexBuffer.bind();
        
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 36, 0 * Float.BYTES);
        GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 36, 3 * Float.BYTES);
        GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, 36, 6 * Float.BYTES);

        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        // The order is "wrong" on purpose.
        GL30.glBindVertexArray(0);
        vertexBuffer.unbind();
    }

    /**
     * Uploads the data from current triangle buffer to the vertex buffer, if available.
     */
    private void uploadTriangleData() {
        // While reuploading the triangle data every time we render is relatively 
        // inefficient, it's still way fast enough and makes everything a lot simpler
        if (triangleBuffer != null) {
            triangleBuffer.rewind();
            vertexBuffer.write(triangleBuffer, VERTEX_SIZE_IN_FLOATS);
        }        
    }
    
    /**
     * Constructs a new task.
     */
    public PolygonRenderingTask() {
        super();
                
        try {
            vertexArrayName = GL30.glGenVertexArrays();
            
            if (vertexArrayName == 0) {
                throw new RuntimeException("Error creating vertex array");
            }

            vertexBuffer = new VertexBuffer();
            setupVertexArray();
        } catch (Exception e) {
            GL30.glDeleteVertexArrays(vertexArrayName);
            release();

            throw e;
        }
        
        triangleList = new ArrayList();
        tessellator = new Tessellator(triangleList);
    }
 
    @Override
    public void run() {
        uploadTriangleData();
        
        // Short explanation: To render contents of an Array Buffer Object we
        // need to specify how the vertices are laid out in the buffer, this,
        // like everything in OpenGL, is done by setting internal states.
        // The purpose of the Vertex Array Object (VAO) is to store the internal
        // state, so that we can simply bind the related VAO when we need to do
        // some rendering. Not only that, but I'm quite sure that OpenGL 3.0
        // requires you to have a VAO around to do any drawing at all.

        GL11.glEnable(GL11.GL_DEPTH_TEST);

        GL30.glBindVertexArray(vertexArrayName);
        vertexBuffer.bind();

        useAndSetupShaderProgram();
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertexBuffer.getElementCount());
        
        vertexBuffer.unbind();
        GL30.glBindVertexArray(0);
    }
    
    /**
     * Sets the polygon data to be rendered.
     * @param list a list of convex polygons
     */
    public void setPolygonData(final ArrayList<ConvexPolygon> list) {
        triangleList.clear();

        for (ConvexPolygon polygon : list) {
            tessellator.generateTriangles(polygon, FRONT_DEPTH, BACK_DEPTH);
        }
        
        if (triangleBuffer == null || triangleBuffer.capacity() < triangleList.size()) {
            triangleBuffer = BufferUtils.createFloatBuffer(triangleList.size());
        }

        for (Float f : triangleList) {
            triangleBuffer.put(f);
        }
    }
    
    /**
     * @return true if and only if all the OpenGL resources are good.
     */
    @Override
    public boolean isGood() {
        return super.isGood() && vertexArrayName != 0;
    }
    
    /**
     * Releases all OpenGL resources related to this object.
     */
    @Override
    public void release() {
        GL30.glDeleteVertexArrays(vertexArrayName);
        vertexArrayName = 0;
    }
}
