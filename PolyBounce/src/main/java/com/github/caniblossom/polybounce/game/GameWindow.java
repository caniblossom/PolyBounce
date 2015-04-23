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

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

/**
 * A class representing the game window (and the game itself).
 * @author Jani Salo
 */
public class GameWindow {
    private static final int DEFAULT_WINDOW_WIDTH = 1280;
    private static final int DEFAULT_WINDOW_HEIGHT = 720;
    
    private GameEngine gameEngine;
           
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
     * Constructs a new game window.
     * @param width width of the game canvas in pixels
     * @param height height of the game canvas in pixels
     * @throws RuntimeException 
     */
    public GameWindow(final int width, final int height) throws RuntimeException {
        try {
            Display.setTitle("Poly Bounce");
            Display.setDisplayMode(new DisplayMode(width, height));
            Display.create(getDefaultPixelFormat(), getDefaultContextAttributes());
        } catch (LWJGLException e) {
            throw new RuntimeException("Unable to create OpenGL context: " + e.getMessage());
        }
        
        // It's important to create the engine only after the OpenGL context has been created.
        gameEngine = new GameEngine(width, height);
    }
    
    /**
     * Constructs a new game window with preset dimensions in pixels.
     */
    public GameWindow() throws RuntimeException {
        this(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
    }

    /**
     * Executes the loop running the game window (and the game itself).
     */
    public void run() {
        while (!Display.isCloseRequested()) {
            gameEngine.update(1.0f / 60.0f);
            
            Display.update();
            Display.sync(60);
        }
        
        gameEngine.deleteGLResources();
        Display.destroy();
    }
}
