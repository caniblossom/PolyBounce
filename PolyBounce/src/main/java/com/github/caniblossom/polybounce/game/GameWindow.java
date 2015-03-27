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
package com.github.caniblossom.polybounce.game;

import com.github.caniblossom.polybounce.renderer.RenderCanvas;
import javax.swing.JFrame;
import org.lwjgl.LWJGLException;

/**
 * Encapsulates the user interface for the game.
 * @author Jani Salo
 */
public class UserInterface {
    private final int width;
    private final int height;
    
    private JFrame frame;
    private RenderCanvas canvas;
    
    /**
     * Constructs a new game user interface.
     * @param width width of the game canvas in pixels
     * @param height height of the game canvas in pixels
     * @throws org.lwjgl.LWJGLException
     */
    public UserInterface(final int width, final int height) throws LWJGLException {
        this.width = width;
        this.height = height;
        canvas = new RenderCanvas();
    }
    
    /**
     * Constructs a new game user interface with preset dimensions in pixels.
     * @throws org.lwjgl.LWJGLException
     */
    public UserInterface() throws LWJGLException {
        this(800, 600);
    }

    /**
     * Creates and shows the frame for the user interface.
     * Only call from the EDT.
     */
    public void createAndShowFrame() {
        frame = new JFrame("Poly Bounce");
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height);

        frame.add(canvas);
        frame.getContentPane().validate();
        frame.getContentPane().repaint();

        frame.setVisible(true);        
    }
}
