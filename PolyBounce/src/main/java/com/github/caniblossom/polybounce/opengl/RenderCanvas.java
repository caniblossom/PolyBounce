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
package com.github.caniblossom.polybounce.opengl;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.AWTGLCanvas;
import org.lwjgl.opengl.ContextAttribs;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glViewport;
import org.lwjgl.opengl.PixelFormat;

// TODO Fix the resize induced flicker, if possible. The problem is caused by
//      the content pane momentarily showing when the canvas is resized.

/**
 * An OpenGL canvas for rendering visuals.
 * @author Jani Salo
 */
public class RenderCanvas extends AWTGLCanvas {
    /**
     * @return default graphics device for the host system
     */
    private static GraphicsDevice getDefaultGraphicsDevice() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    }
    
    /**
     * @return Pixel format used by the game
     */
    private static PixelFormat getDefaultPixelFormat() {
        return new PixelFormat(8, 16, 0);
    }

    /**
     * @return OpenGL context version used by the game
     */
    private static ContextAttribs getDefaultContextAttributes() {
        return new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true);
    }
    
    /**
     * Indirectly called by the EDT to repaint the canvas.
     */
    @Override
    protected void paintGL() {
        try {
            // Sets the OpenGL context owned by this canvas current.
            // It's probably not necessary to call this, but it won't hurt.
            makeCurrent();
           
            glViewport(0, 0, getWidth(), getHeight());
            glClearColor(0.0f, 1.0f, 0.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            swapBuffers();
        } catch (LWJGLException e) {
            // TODO Handle this. Should probably be a fatal error.
            Logger.getLogger(RenderCanvas.class.getName()).log(Level.SEVERE, null, e);
        }
        
        repaint();
    } 

    /**
     * Constructs a new game canvas with specified dimensions.
     * @param width canvas width in pixels
     * @param height canvas height in pixels
     * @throws LWJGLException 
     */
    public RenderCanvas(final int width, final int height) throws LWJGLException {
        // LWJGL documentation isn't very good - so far it seems that this is
        // the only way to set the OpenGL context version, which is necessary
        // to us, as using any OpenGL 3.0+ API requires for it to be specified. 
        // I have no idea what to with the drawable parameter, but I suspect it's
        // used for some sort of context sharing. The default constructor for
        // AWTGLCanvas sets it null too, so I'm assuming it's safe to do so here.
        super(getDefaultGraphicsDevice(), getDefaultPixelFormat(), null, getDefaultContextAttributes());

        setSize(width, height);
    }
    
    /**
     * Constructs a new game canvas with minimal size.
     * @throws LWJGLException 
     */
    public RenderCanvas() throws LWJGLException {
        this(1, 1);
    }
}
