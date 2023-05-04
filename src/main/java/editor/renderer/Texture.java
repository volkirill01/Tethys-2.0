package editor.renderer;

import editor.parsers.TextureParser;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;

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

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);

        this.textureID = TextureParser.loadFromFile(filepath, width, height);
        this.width = width.get(0);
        this.height = height.get(0);
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

    public void unbind() { glBindTexture(GL_TEXTURE_2D, 0); } // Bind nothing

    public int getTextureID() { return this.textureID; }

    public String getFilepath() { return this.filepath; }

    public int getWidth() { return this.width; }

    public int getHeight() { return this.height; }
}
