package editor.renderer;

import static org.lwjgl.opengl.GL30.*;

public class Framebuffer {

    private int fboID;
    private Texture texture;

    public Framebuffer(int width, int height) {
        // Generate framebuffer
        this.fboID = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, this.fboID);

        // Create the texture to render the data to, and attach it to framebuffer
        this.texture = new Texture(width, height);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.texture.getTextureID(), 0);

        // Create renderbuffer store the depth info
        int rboID = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rboID);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT32, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rboID);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            throw new RuntimeException("Framebuffer creation fails.");

        glBindFramebuffer(GL_FRAMEBUFFER, 0); // Bind nothing
    }

    public void bind() { glBindFramebuffer(GL_FRAMEBUFFER, this.fboID); }

    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0); // Bind nothing
    }

    public int getFboID() { return this.fboID; }

    public int getTextureID() { return this.texture.getTextureID(); }
}
