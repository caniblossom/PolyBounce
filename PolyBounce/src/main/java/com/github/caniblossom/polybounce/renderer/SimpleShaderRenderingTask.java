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

import com.github.caniblossom.polybounce.renderer.shader.SimpleShaderProgram;
import java.nio.FloatBuffer;

/**
 * An abstract base class for rendering tasks using the simple shader.
 * @author Jani Salo
 */
public abstract class SimpleShaderRenderingTask implements RenderingTask {
    private SimpleShaderProgram shaderProgram = null;

    private FloatBuffer projectionMatrix;
    private FloatBuffer viewMatrix;
    
    private float lightPositionX = 0.0f;
    private float lightPositionY = 0.0f;
    private float lightPositionZ = 0.0f;
    
    private float lightColorR = 0.0f;
    private float lightColorG = 0.0f;
    private float lightColorB = 0.0f;

    /**
     * Enables and sets up the shader program.
     */
    protected void useAndSetupShaderProgram() {
        assert isGood();

        shaderProgram.use();
               
        shaderProgram.setProjection(projectionMatrix);
        shaderProgram.setView(viewMatrix);
        
        shaderProgram.setLightPosition(lightPositionX, lightPositionY, lightPositionZ);
        shaderProgram.setLightColor(lightColorR, lightColorG, lightColorB);
    }
    
    /**
     * @return true if and only if the necessary OpenGL resources are good to use.
     */
    protected boolean isGood() {
        if (shaderProgram == null) {
            return false;
        } else {
            return shaderProgram.isGood();
        }
    }
    
    /**
     * Deletes any OpenGL resources allocated by this object.
     */
    protected void deleteGLResources() {
        if (shaderProgram != null) {
            shaderProgram.deleteProgram();
        }
    }

    /**
     * Constructs a new rendering task.
     */
    public SimpleShaderRenderingTask() {
        try {
            shaderProgram = new SimpleShaderProgram();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }

        projectionMatrix = MatrixUtil.createProjection(90.0f, 1.0f, 0.01f, 100.0f);
        viewMatrix = MatrixUtil.createViewPlaneXY(0.0f, 0.0f, 0.0f);
    }
    
    /**
     * Sets the projection to be used.
     * @param fov vertical field of view
     * @param aspect aspect ratio of the view
     * @param near near clipping plane
     * @param far far clipping plane
     */
    public void setProjectionTransformation(final float fov, final float aspect, final float near, final float far) {
        assert isGood();
        projectionMatrix = MatrixUtil.createProjection(fov, aspect, near, far);
    }

    /**
     * Sets the view transformation to be used.
     * @param x x position of the view
     * @param y y position of the view
     * @param distance relative distance in z-direction
     */
    public void setViewTransformation(final float x, final float y, final float distance) {
        assert isGood();
        viewMatrix = MatrixUtil.createViewPlaneXY(x, y, distance);
    }
    
    /**
     * Sets the light position
     * @param x x component of the position
     * @param y y component of the position
     * @param z z component of the position
     */
    public void setLightPosition(final float x, final float y, final float z) {
        assert isGood();

        lightPositionX = x;
        lightPositionY = y;
        lightPositionZ = z;
    }

    /**
     * Sets the light color
     * @param r red component of the color
     * @param g green component of the color
     * @param b blue component of the color
     */
    public void setLightColor(final float r, final float g, final float b) {
        assert isGood();

        lightColorR = r;
        lightColorG = g;
        lightColorB = b;        
    }
}
