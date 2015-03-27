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

// TODO Add tests.

import org.lwjgl.opengl.GL11;



/**
 * A very simple render task for clearing the canvas.
 * @author Jani Salo
 */
public class ClearRenderTask implements RenderTask {
    final float r, g, b, a;
    final float z;
    
    /**
     * Constructs a new task with given clear values.
     * @param r red component of the clear color
     * @param g green component of the clear color
     * @param b blue component of the clear color
     * @param a alpha component of the clear color
     * @param z value to clear the depth buffer to
     */
    public ClearRenderTask(final float r, final float g, final float b, final float a, final float z) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        this.z = z;
    }

    /**
     * Constructs a new task with black color, full alpha and z-buffer to farthest maximum.
     */
    public ClearRenderTask() {
        this(0.0f, 0.0f, 0.0f, 1.0f, 1.0f);
    }

    @Override
    public void run() {
        GL11.glClearColor(r, g, b, a);
        GL11.glClearDepth(z);

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }
}
