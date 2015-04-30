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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;

/**
 * Simple wrapper for OpenGL 2D texture.
 * @author Jani Salo
 */
public class Texture2D {
    private int textureName = 0;

    // Converts buffered image into a raw byte buffer.
    private static ByteBuffer createByteBufferFromImage(final BufferedImage image) {
        ByteBuffer buffer = BufferUtils.createByteBuffer(4 * image.getWidth() * image.getHeight());
        
        // Flip the image while reading pixels.
        for (int y = image.getHeight() - 1; y > 0; y--) {
            for (int x = 0; x < image.getWidth(); x++) {
                final int rgba = image.getRGB(x, y);

                buffer.put((byte) ((rgba >> 16) & 0xFF));
                buffer.put((byte) ((rgba >>  8) & 0xFF));
                buffer.put((byte) ((rgba >>  0) & 0xFF));
                buffer.put((byte) ((rgba >> 24) & 0xFF));
            }
        }
        
        buffer.rewind();
        return buffer;
    }
    
    /**
     * Constructs a new texture from a PNG image.
     * @param name name of the resource
     * @return new texture with the image as data
     * @throws RuntimeException
     */
    public static Texture2D createTextureFromResourcePNG(final String name) throws RuntimeException {
        final InputStream is = Texture2D.class.getClassLoader().getResourceAsStream(name);
        BufferedImage image = null;

        try {
            image = ImageIO.read(is);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load resource: " + name + ", reason: " + e.getMessage());
        }

        Texture2D texture = new Texture2D();
        texture.write(createByteBufferFromImage(image), image.getWidth(), image.getHeight());

        try {
            is.close();
        } catch (IOException e) {
            // TODO Handle.
        }

        return texture;
    }
    
    /**
     * Constructs a new texture object.
     * @throws RuntimeException 
     */
    public Texture2D() throws RuntimeException {
        textureName = GL11.glGenTextures();

        if (textureName == 0) {
            throw new RuntimeException("Error creating texture.");
        }

        bind();
        
        // Just use the same settings for all textures.
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        unbind();
    }
    
    /**
     * Binds the related texture as current GL_TEXTURE_2D.
     */
    public void bind() {
        assert isGood();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureName);
    }
    
    /**
     * Binds null object as current GL_TEXTURE_2D.
     */
    public void unbind() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    /**
     * Sets current texture data and automatically generates mipmaps. Any old data is lost.
     * @param buffer a buffer of bytes containing RGBA data for the texture.
     * @param width width of the image data
     * @param height height of the image data
     * 
     */
    public void write(final ByteBuffer buffer, final int width, final int height) {
        assert buffer.remaining() == 4 * width * height;
        assert isGood();
        
        bind();

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        
        unbind();
    }
    
    /**
     * @return true if and only if the texture is good to use.
     */
    public boolean isGood() {
        return textureName != 0;
    }

    /**
     * Deletes the texture.
     */
    public void deleteTexture() {
        GL11.glDeleteTextures(textureName);
        textureName = 0;
    }
}
