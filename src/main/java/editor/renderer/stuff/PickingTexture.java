package editor.renderer.stuff;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;

public class PickingTexture {

    private int pickingTextureID;
    private int fboID;
    private int depthTextureID;

    public PickingTexture(int width, int height) {
        if (!init(width, height))
            throw new RuntimeException("Fails to initialize picking texture.");
    }

    private boolean init(int width, int height) {
        // Generate framebuffer
        this.fboID = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, this.fboID);

        // Create the texture to render the data to, and attach it to framebuffer
        this.pickingTextureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, this.pickingTextureID);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB32F, width, height, 0, GL_RGB, GL_FLOAT, 0);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.pickingTextureID, 0);

        // Create the texture object for the depth buffer
        glEnable(GL_TEXTURE_2D);
        this.depthTextureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, this.depthTextureID);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, 0);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, this.depthTextureID, 0);

        // Disable the reading
        glReadBuffer(GL_NONE);
        glDrawBuffer(GL_COLOR_ATTACHMENT0);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            throw new RuntimeException("Framebuffer creation fails.");

        // Unbind the texture and framebuffer
        glBindTexture(GL_TEXTURE_2D, 0); // Bind nothing
        glBindFramebuffer(GL_FRAMEBUFFER, 0); // Bind nothing
        return true;
    }

    public void bind() { glBindFramebuffer(GL_DRAW_FRAMEBUFFER, this.fboID); }

    public void unbind() {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0); // Bind nothing
    }

    public int readPixel(int x, int y) {
        glBindFramebuffer(GL_READ_FRAMEBUFFER, this.fboID);
        glReadBuffer(GL_COLOR_ATTACHMENT0);

        float[] pixels = new float[3]; // (r, g, b)
        glReadPixels(x, y, 1, 1, GL_RGB, GL_FLOAT, pixels);

        return (int) (pixels[0]) - 1;
    }
}
