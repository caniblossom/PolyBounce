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

// TODO Implement tests if possible.

/**
 * A wrapper for OpenGL Shader Program.
 * @author Jani Salo
 */
public class ShaderProgram implements GLDependent {
    private static final int INFO_LOG_DEFAULT_LENGTH = 1024;

    private int programName = 0;

    /**
     * Throws an exception on a faulty shader.
     * @param shaderName name of the shader program object
     * @throws RuntimeException 
     */
    private void checkShader(final int shaderName) throws RuntimeException {
        int infoLogLength = GL20.glGetShaderi(shaderName, GL20.GL_INFO_LOG_LENGTH);
        infoLogLength = infoLogLength > 0 ? infoLogLength : INFO_LOG_DEFAULT_LENGTH;

        String infoLog = GL20.glGetShaderInfoLog(shaderName, infoLogLength);
        
        if (infoLog == null) {
            infoLog = "No info log.";
        }
        
        if (GL20.glGetShaderi(shaderName, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            throw new RuntimeException("Error while compiling shader: " + infoLog);
        }        
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
        
        GL20.glShaderSource(shaderName, source);
        GL20.glCompileShader(shaderName);

        if (GL11.glGetError() != GL11.GL_NO_ERROR) {
            GL20.glDeleteProgram(programName);
            throw new RuntimeException("OpenGL error while creating shader.");
        } 
        
        try {
            checkShader(shaderName);
        } catch (RuntimeException e) {
            GL20.glDeleteShader(shaderName);            
            throw e;
        }
        
        return shaderName;
    }
    
    /**
     * A helper function for binding input and output names for the shader program
     * @param programName name of the shader program object
     * @param inputNameList list of input names in the order they are to be bound.
     * @param outputNameList list of output names in the order they are to be bound.
     */
    private void bindProgramLocations(final int programName, final String[] inputNameList, final String[] outputNameList) {
        for (int i = 0; i < inputNameList.length; i++) {
            GL20.glBindAttribLocation(programName, i, inputNameList[i]);
        }
            
        for (int i = 0; i < outputNameList.length; i++) {
            GL30.glBindFragDataLocation(programName, i, outputNameList[i]);
        }
    }

    /**
     * Throws an exception on a faulty shader program.
     * @param programName name of the shader program object
     * @throws RuntimeException 
     */
    private void checkProgram(final int programName) throws RuntimeException {
        int infoLogLength = GL20.glGetProgrami(programName, GL20.GL_INFO_LOG_LENGTH);
        infoLogLength = infoLogLength > 0 ? infoLogLength : INFO_LOG_DEFAULT_LENGTH;
        
        String infoLog = GL20.glGetProgramInfoLog(programName, infoLogLength);
        
        if (infoLog == null) {
            infoLog = "No info log.";
        }
        
        if (GL20.glGetProgrami(programName, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            throw new RuntimeException("Error while compiling shader program: " + infoLog);
        }        
    }
    
    /**
     * Creates a new shader program from vertex and fragment shaders.
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
        
        bindProgramLocations(programName, inputNameList, outputNameList);

        GL20.glLinkProgram(programName);
    
        if (GL11.glGetError() != GL11.GL_NO_ERROR) {
            GL20.glDeleteShader(programName);
            throw new RuntimeException("OpenGL error while creating shader program.");
        } 
        
        try {
            checkProgram(programName);
        } catch (RuntimeException e) {
            GL20.glDeleteShader(programName);
            throw e;
        }
        
        return programName;
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
            vertexShaderName = createShader(vertexShaderSource, GL20.GL_VERTEX_SHADER);
            fragmentShaderName = createShader(fragmentShaderSource, GL20.GL_FRAGMENT_SHADER);
            
            programName = createProgram(vertexShaderName, fragmentShaderName, inputNameList, outputNameList);
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
     * @return true if and only if the object represents an actual shader program object
     */
    @Override
    public boolean isGood() {
        return programName != 0;
    }

    /**
     * Forces the destruction of the shader program object.
     */
    @Override
    public void release() {
        GL20.glDeleteProgram(programName);
        programName = 0;
    }
    
    /**
     * Sets this as current shader program.
     */
    public void use() {
        GL20.glUseProgram(programName);
    }
}
