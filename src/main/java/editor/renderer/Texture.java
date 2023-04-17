package editor.renderer;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture {

    private final transient int textureID;
    private final String filepath;
    private int width, height;

    public Texture(int width, int height) {
        this.filepath = "Generated Texture";

        // Generate texture on GPU
        this.textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, this.textureID);

        // Set texture parameters
        // When stretching the image, blurs it and stretch it
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        // When shrinking an image, blurs it and stretch it
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
    }

    public Texture(String filepath) {
        this.filepath = filepath;

        // Generate texture on GPU
        this.textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, this.textureID);

        // Set texture parameters
        // Repeat image in both directions
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        // When stretching the image, pixelete it
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        // When shrinking an image, pixelete it
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        stbi_set_flip_vertically_on_load(true);
        ByteBuffer image = stbi_load(this.filepath, width, height, channels, 0);

        if (image == null)
            throw new NullPointerException("Could not load image - '" + this.filepath + "'");

        this.width = width.get(0);
        this.height = height.get(0);

        switch (channels.get(0)) {
            case 3 -> glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0), 0, GL_RGB, GL_UNSIGNED_BYTE, image);
            case 4 -> glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            default -> throw new IllegalStateException("Unknown number of channels: (" + channels.get(0) + ") - '" + this.filepath + "'");
        }

        stbi_image_free(image);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) return false;
        if (!(object instanceof Texture)) return false;

        if (object == this) return true;

        Texture t = (Texture) object;
        return t.getWidth() == this.getWidth() && t.getHeight() == this.getHeight() && t.getTextureID() == this.getTextureID() && t.getFilepath().equals(this.getFilepath());
    }

    public void bind() { glBindTexture(GL_TEXTURE_2D, this.textureID); }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0); // Bind nothing
    }

    public int getTextureID() { return this.textureID; }

    public String getFilepath() { return this.filepath; }

    public int getWidth() { return this.width; }

    public int getHeight() { return this.height; }
}
