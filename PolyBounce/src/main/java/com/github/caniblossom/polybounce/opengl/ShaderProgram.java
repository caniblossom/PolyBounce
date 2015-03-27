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

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

// TODO Again, do tests if reasonable, seems unlikely here though.
// TODO Add method for deleting the shader / invalidating the class instance.

/**
 * A wrapper for OpenGL Shader Program.
 * @author Jani Salo
 */
public class ShaderProgram {
    private int programName;

    /**
     * @return name of the program object
     */
    protected int getProgramName() {
        return programName;
    }

    /**
     * Creates a new shader directly from source.
     * @param source source for the shader
     * @param type GL_GEOMETRY_SHADER, GL_VERTEX_SHADER or GL_FRAGMENT_SHADER
     * @return name of the shader object
     * @throws RuntimeException
     */
    private int createShader(final String source, final int type) throws RuntimeException {
        int shaderName = GL20.glCreateShader(type);

        if (shaderName == 0) {
            throw new RuntimeException("Error creating shader.");
        }
        
        // I pray to god this works, I've had enough trouble doing properly with C/C++
        GL20.glShaderSource(shaderName, source);
        GL20.glCompileShader(shaderName);

        // Prefetch the log in case we need it - there seems to be some issues fetching it while throwing an exception.
        String infoLog = GL20.glGetShaderInfoLog(shaderName, GL20.glGetShaderi(shaderName, GL20.GL_INFO_LOG_LENGTH));
        
        if (GL20.glGetShaderi(shaderName, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            GL20.glDeleteShader(shaderName);
            throw new RuntimeException("Error compiling shader: " + infoLog);
        }
        
        return shaderName;
    }

    /**
     * Creates a new (shader) program from vertex and fragment shader.
     * @param vertexShaderName name of the vertex shader object
     * @param fragmentShaderName name of the fragment shader object
     * @param inputNameList list of N shader input names in the order they are to be bound from 0 to N - 1
     * @param outputNameList list of N shader output names in the order they are to be bound from 0 to N - 1
     * @return name of the program object
     */
    private int createProgram(final int vertexShaderName, final int fragmentShaderName, final String[] inputNameList, final String[] outputNameList) {
        programName = GL20.glCreateProgram();

        if (programName == 0) {
            throw new RuntimeException("Error creating program.");
        }
        
        GL20.glAttachShader(programName, vertexShaderName);
        GL20.glAttachShader(programName, fragmentShaderName);
    
        // Bind inputs in the order they appear on the list.
        for (int i = 0; i < inputNameList.length; i++) {
            GL20.glBindAttribLocation(programName, i, inputNameList[i]);
        }
            
        // Bind outputin the order they appear on the list.
        for (int i = 0; i < outputNameList.length; i++) {
            GL30.glBindFragDataLocation(programName, i, outputNameList[i]);
        }

        GL20.glLinkProgram(programName);
    
        // See the comments above for shader info log.
        String infoLog = GL20.glGetProgramInfoLog(programName, GL20.glGetProgrami(programName, GL20.GL_INFO_LOG_LENGTH));
        
        if (GL20.glGetProgrami(programName, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            GL20.glDeleteProgram(programName);
            throw new RuntimeException("Error linking program: " + infoLog);
        }
        
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
            vertexShaderName = createShader(vertexShaderSource, GL20.GL_VERTEX_SHADER);
            fragmentShaderName = createShader(fragmentShaderSource, GL20.GL_FRAGMENT_SHADER);
            
            programName = createProgram(vertexShaderName, fragmentShaderName, inputNameList, outputNameList);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            
            GL20.glDeleteShader(fragmentShaderName);
            GL20.glDeleteShader(vertexShaderName);
        
            throw e;
        }  
    }
     
    /**
     * Sets this as current shader program.
     */
    public void use() {
        GL20.glUseProgram(programName);
    }
}
