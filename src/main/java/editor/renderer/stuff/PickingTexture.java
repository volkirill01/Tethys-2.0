package editor.renderer.stuff;

import org.joml.Vector2i;

import static org.lwjgl.opengl.GL30.*;

public class PickingTexture {

    private int fboID;

    public PickingTexture(int width, int height) { init(width, height); }

    private void init(int width, int height) {
        // Generate framebuffer
        this.fboID = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, this.fboID);

        // Create the texture to render the data to, and attach it to framebuffer
        int pickingTextureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, pickingTextureID);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB32F, width, height, 0, GL_RGB, GL_FLOAT, 0);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, pickingTextureID, 0);

        // Create the texture object for the depth buffer
        glEnable(GL_TEXTURE_2D);
        int depthTextureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, depthTextureID);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, 0);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTextureID, 0);

        // Disable the reading
        glReadBuffer(GL_NONE);
        glDrawBuffer(GL_COLOR_ATTACHMENT0);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            throw new RuntimeException("Framebuffer creation fails.");

        // Unbind the texture and framebuffer
        glBindTexture(GL_TEXTURE_2D, 0); // Bind nothing
        glBindFramebuffer(GL_FRAMEBUFFER, 0); // Bind nothing
    }

    public void bind() { glBindFramebuffer(GL_DRAW_FRAMEBUFFER, this.fboID); }

    public void unbind() { glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0); } // Bind nothing

    public int readPixel(int x, int y) {
        glBindFramebuffer(GL_READ_FRAMEBUFFER, this.fboID);
        glReadBuffer(GL_COLOR_ATTACHMENT0);

        float[] pixels = new float[3]; // (r, g, b)
        glReadPixels(x, y, 1, 1, GL_RGB, GL_FLOAT, pixels);

        return (int) (pixels[0]) - 1;
    }

    public float[] readPixels(Vector2i start, Vector2i end) {
        glBindFramebuffer(GL_READ_FRAMEBUFFER, this.fboID);
        glReadBuffer(GL_COLOR_ATTACHMENT0);

        Vector2i size = new Vector2i(end).sub(start).absolute();
        int numberOfPixels = size.x * size.y;
        float[] pixels = new float[3 * numberOfPixels]; // (r, g, b) * number of Pixels
        glReadPixels(start.x, start.y, size.x, size.y, GL_RGB, GL_FLOAT, pixels);
        for (int i = 0; i < pixels.length; i++)
            pixels[i] -= 1;

        return pixels;
    }
}
