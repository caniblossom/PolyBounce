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

/**
 * Utilities for compiling and linking OpenGL shaders.
 * @author Jani Salo
 */
public class ShaderUtil {
    // Make the constructor private to signify a static class.
    private ShaderUtil() {}

    // A helper function for binding input and output names for the shader program.
    private static void bindProgramLocations(final int programName, final String[] inputNameList, final String[] outputNameList) {
        for (int i = 0; i < inputNameList.length; i++) {
            GL20.glBindAttribLocation(programName, i, inputNameList[i]);
        }
            
        for (int i = 0; i < outputNameList.length; i++) {
            GL30.glBindFragDataLocation(programName, i, outputNameList[i]);
        }
    }

    // Throws an exception on a faulty shader.
    private static void checkShader(final int shaderName) throws RuntimeException {        
        if (GL20.glGetShaderi(shaderName, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            int infoLogLength = GL20.glGetShaderi(shaderName, GL20.GL_INFO_LOG_LENGTH);
            String infoLog = infoLogLength > 0 ? GL20.glGetShaderInfoLog(shaderName, infoLogLength) : "";

            if (infoLog == null) {
                infoLog = "No info log.";
            }

            throw new RuntimeException("Error while compiling shader: " + infoLog);
        }        
    }
        
    // Throws an exception on a faulty shader program.
    private static void checkProgram(final int programName) throws RuntimeException {
        if (GL20.glGetProgrami(programName, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            int infoLogLength = GL20.glGetProgrami(programName, GL20.GL_INFO_LOG_LENGTH);
            String infoLog = infoLogLength > 0 ? GL20.glGetProgramInfoLog(programName, infoLogLength) : "";

            if (infoLog == null) {
                infoLog = "No info log.";
            }
        
            throw new RuntimeException("Error while compiling shader program: " + infoLog);
        }        
    }
    
    /**
     * Creates a new shader directly from source.
     * @param source source for the shader
     * @param type GL_GEOMETRY_SHADER, GL_VERTEX_SHADER or GL_FRAGMENT_SHADER
     * @return name of the shader object
     * @throws RuntimeException
     */
    public static int createShader(final String source, final int type) throws RuntimeException {
        int shaderName = GL20.glCreateShader(type);

        if (shaderName == 0) {
            throw new RuntimeException("Error creating shader.");
        }
        
        GL20.glShaderSource(shaderName, source);
        GL20.glCompileShader(shaderName);

        if (GL11.glGetError() != GL11.GL_NO_ERROR) {
            GL20.glDeleteProgram(shaderName);
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
     * Creates a new shader program from vertex and fragment shaders.
     * @param vertexShaderName name of the vertex shader object
     * @param fragmentShaderName name of the fragment shader object
     * @param inputNameList list of N shader input names in the order they are to be bound from 0 to N - 1
     * @param outputNameList list of N shader output names in the order they are to be bound from 0 to N - 1
     * @return name of the program object
     */
    public static int createProgram(final int vertexShaderName, final int fragmentShaderName, final String[] inputNameList, final String[] outputNameList) {
        int programName = GL20.glCreateProgram();

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
}
