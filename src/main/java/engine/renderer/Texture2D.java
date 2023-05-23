package engine.renderer;

import engine.parsers.TextureParser;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL45.*;

public class Texture2D extends Texture {

    public Texture2D(int width, int height, int textureID) {
        super("_GENERATED_", width, height);
        this.textureID = textureID;
    }

    public Texture2D(int width, int height, int[] data, boolean useAlpha) {
        super("_GENERATED_", width, height);
//        // Generate texture on GPU
//        this.textureID = glCreateTextures(GL_TEXTURE_2D);
//        glBindTexture(GL_TEXTURE_2D, this.textureID);
//        glTextureStorage2D(this.textureID, 0, GL_RGBA, width, height);
//
//        // Set texture parameters
//        // When stretching the image, pixelate it and stretch it
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
//        // When shrinking an image, pixelate it and stretch it
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
//
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
//
//        glTexSubImage2D(this.textureID, 0, 0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, data);

        this.textureID = glCreateTextures(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, this.textureID);

        // Set texture parameters
        // When shrinking an image, pixelate it and stretch it
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        // When stretching the image, pixelate it and stretch it
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        glTexImage2D(GL_TEXTURE_2D, 0, useAlpha ? GL_RGBA : GL_RGB, width, height, 0, useAlpha ? GL_RGBA : GL_RGB, GL_UNSIGNED_BYTE, data);
    }

    public Texture2D(String filepath) {
        super(filepath);

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);

        this.textureID = TextureParser.loadFromFile(filepath, width, height);
        this.width = width.get(0);
        this.height = height.get(0);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) return false;
        if (!(object instanceof Texture2D t)) return false;

        if (object == this) return true;

        return t.getWidth() == this.getWidth() && t.getHeight() == this.getHeight() && t.getTextureID() == this.getTextureID() && t.getFilepath().equals(this.getFilepath());
    }

    @Override
    public void bind(int textureSlot) {
        glActiveTexture(GL_TEXTURE0 + textureSlot);
        glBindTexture(GL_TEXTURE_2D, this.textureID);
    }

    @Override
    public void unbind() { glBindTexture(GL_TEXTURE_2D, 0); } // Bind nothing

    @Override
    public void freeMemory() { glDeleteTextures(this.textureID); }
}
