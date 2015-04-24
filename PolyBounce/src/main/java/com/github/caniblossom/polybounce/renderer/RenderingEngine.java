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
import com.github.caniblossom.polybounce.math.Vector2;
import com.github.caniblossom.polybounce.physics.Body;
import java.util.ArrayList;
import java.util.List;

/**
 * A class representing the rendering engine of the game.
 * @author Jani Salo
 */
public class RenderingEngine {
    private final float aspectRatio;
    private final RenderingManager manager;
    
    private final ClearRenderingTask clearTask;
    private final PolygonRenderingTask polygonTask;
    
    private final ArrayList<ConvexPolygon> polygonList;
    private final ArrayList<Color> colorList;
    
    private boolean renderingDataChanged;

    
    // Updates the internal state in preparation for drawing the next frame.
    private void update() {        
        polygonTask.setProjectionTransformation(120.0f, aspectRatio, 0.0625f, 16.0f);        
        polygonTask.setLightColor(1.1f, 1.0f, 0.9f);
    }
    
    /**
     * Constructs a new rendering engine.
     * @param width width of the display in pixels
     * @param height height of the display in pixels
     */
    public RenderingEngine(final int width, final int height) {
        aspectRatio = (float) width / (float) height;

        clearTask = new ClearRenderingTask(0.0f, 0.0f, 0.0f, 1.0f, 1.0f);
        polygonTask = new PolygonRenderingTask();

        polygonList = new ArrayList();
        colorList = new ArrayList();

        renderingDataChanged = false;
        
        manager = new RenderingManager();
        manager.addTask(clearTask);
        manager.addTask(polygonTask);        
    }

    /**
     * Draws current frame.
     */
    public void drawCurrentFrame() {
        assert isGood();        

        if (renderingDataChanged) {
            polygonTask.setPolygonData(polygonList, colorList);
            renderingDataChanged = false;
        }
        
        update();
        manager.runTasks();        
    }

    /**
     * Sets camera.
     * @param focus point to focus on
     * @param distance distance from that point
     */
    public void setCamera(final Vector2 focus, final float distance) {
        polygonTask.setViewTransformation(focus.getX(), focus.getY(), distance);        
        polygonTask.setLightPosition(focus.getX() - 1.0f, focus.getY() + 1.0f, 1.0f);
    }
    
    /**
     * Resets current rendering data.
     */
    public void resetRenderingData() {
        renderingDataChanged = true;
        polygonList.clear();
        colorList.clear();
    }
    
    /**
     * Adds a list of bodies to be drawn
     * @param bodyList list of physics bodies to draw
     */
    public void addBodiesToDraw(final List<Body> bodyList) {
        renderingDataChanged = true;
        
        for (Body body : bodyList) {
            polygonList.add(body.getHull());
            colorList.add(
                new Color(
                    1.0f - body.getBounciness(),
                    1.0f,
                    1.0f + body.getBounciness()
                )
            );
        }        
    }
            
    /**
     * @return true if and only if all OpenGL resources are good to use.
     */
    public boolean isGood() {
        return polygonTask.isGood();
    }

    /**
     * Deletes all OpenGL resources related to this object.
     */
    public void deleteGLResources() {
        polygonTask.deleteGLResources();
    }
}
