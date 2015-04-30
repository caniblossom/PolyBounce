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

import com.github.caniblossom.polybounce.renderer.shader.SimpleShaderProgram;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import org.lwjgl.opengl.GL20;

/**
 * A wrapper for OpenGL Shader Program.
 * @author Jani Salo
 */
public class ShaderProgram {
    private int programName = 0;
    
    // Loads a shader source from a resource.
    protected static String getShaderSourceFromResource(final String name) {
        final InputStream is = SimpleShaderProgram.class.getClassLoader().getResourceAsStream(name);
        final Scanner scanner = new Scanner(is).useDelimiter("\\A");
        final String source = scanner.hasNext() ? scanner.next() : "";
        
        try {
            is.close();
        } catch (IOException e) {
            // TODO Handle.
        }

        return source;        
    }
    
    /**
     * @return name of the program object
     */
    protected int getProgramName() {
        return programName;
    }

    /**
     * Constructs a new shader program from source.
     * @param vertexShaderSource source for the vertex shader
     * @param fragmentShaderSource source for the fragment shader
     * @param inputNameList list of N shader input names in the order they are to be bound from 0 to N - 1
     * @param outputNameList list of N shader output names in the order they are to be bound from 0 to N - 1
     * @throws RuntimeException 
     */
    public ShaderProgram(final String vertexShaderSource, final String fragmentShaderSource, final String[] inputNameList, final String[] outputNameList) throws RuntimeException {
        int vertexShaderName = 0;
        int fragmentShaderName = 0;

        try {
            vertexShaderName = ShaderUtil.createShader(vertexShaderSource, GL20.GL_VERTEX_SHADER);
            fragmentShaderName = ShaderUtil.createShader(fragmentShaderSource, GL20.GL_FRAGMENT_SHADER);
            
            programName = ShaderUtil.createProgram(vertexShaderName, fragmentShaderName, inputNameList, outputNameList);
        } catch (Exception e) {
            GL20.glDeleteProgram(programName);
            programName = 0;
            
            GL20.glDeleteShader(fragmentShaderName);
            GL20.glDeleteShader(vertexShaderName);
        
            throw e;
        }  

        GL20.glDeleteShader(fragmentShaderName);
        GL20.glDeleteShader(vertexShaderName);
    }
        
    /**
     * Sets this as current shader program.
     */
    public void use() {
        assert isGood();
        GL20.glUseProgram(programName);
    }
    
    /**
     * @return true if and only if the OpenGL shader program object is good to use.
     */
    public boolean isGood() {
        return programName != 0;
    }

    /**
     * Deletes the OpenGL shader program object.
     */
    public void deleteProgram() {
        GL20.glDeleteProgram(programName);
        programName = 0;
    }
}
