
package engine.renderer.stuff;

import engine.stuff.Window;
import org.joml.Vector2i;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import static org.lwjgl.opengl.GL30.*;

public class Fbo {

	public static final int NONE = 0;
	public static final int DEPTH_TEXTURE = 1;
	public static final int DEPTH_RENDER_BUFFER = 2;

	private final int width;
	private final int height;

	private int frameBuffer;

	private boolean multiSample = false;
	private final int samplesCount = 5;

	private boolean multiTarget = false;

	private int depthTexture;

	private int depthBuffer;
	private int colorBuffer;
	private int colorBuffer2;

	/**
	 * Creates an FBO of a specified width and height, with the desired type of
	 * depth buffer attachment.
	 * 
	 * @param width
	 *            - the width of the FBO.
	 * @param height
	 *            - the height of the FBO.
	 * @param depthBufferType
	 *            - an int indicating the type of depth buffer attachment that
	 *            this FBO should use.
	 */
	public Fbo(int width, int height, int depthBufferType) {
		this.width = width;
		this.height = height;
		initialiseFrameBuffer(depthBufferType);
	}

	public Fbo(int width, int height, int depthBufferType, boolean multiSample) {
		this.width = width;
		this.height = height;
		this.multiSample = multiSample;
		initialiseFrameBuffer(depthBufferType);
	}

	public Fbo(int width, int height, int depthBufferType, boolean multiSample, boolean multiTarget) {
		this.width = width;
		this.height = height;
		this.multiSample = multiSample;
		this.multiTarget = multiTarget;
		initialiseFrameBuffer(depthBufferType);
	}

	/**
	 * Deletes the frame buffer and its attachments when the game closes.
	 */
	public void cleanUp() {
		glDeleteFramebuffers(this.frameBuffer);
		glDeleteTextures(this.depthTexture);
		glDeleteRenderbuffers(this.depthBuffer);
		glDeleteRenderbuffers(this.colorBuffer);
		glDeleteRenderbuffers(this.colorBuffer2);
	}

	/**
	 * Binds the frame buffer, setting it as the current render target. Anything
	 * rendered after this will be rendered to this FBO, and not to the screen.
	 */
	public void bind() {
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, this.frameBuffer);
		glViewport(0, 0, this.width, this.height);
	}

	/**
	 * Unbinds the frame buffer, setting the default frame buffer as the current
	 * render target. Anything rendered after this will be rendered to the
	 * screen, and not this FBO.
	 */
	public void unbind() {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glViewport(0, 0, Window.getScreenWidth(), Window.getScreenHeight());
	}

	/**
	 * Binds the current FBO to be read from (not used in tutorial 43).
	 */
	public void bindToRead(int attachment) {
		glBindTexture(GL_TEXTURE_2D, 0);
		glBindFramebuffer(GL_READ_FRAMEBUFFER, this.frameBuffer);
		glReadBuffer(attachment);
	}

	public int getColorTexture() { return this.colorBuffer; }

	public int getColorTexture2() { return this.colorBuffer2; }

	/**
	 * @return The texture containing the FBOs depth buffer.
	 */
	public int getDepthTexture() { return this.depthTexture; }

	public void resolveToFbo(int readBuffer, Fbo outputFbo) {
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, outputFbo.frameBuffer);
		glBindFramebuffer(GL_READ_FRAMEBUFFER, this.frameBuffer);
		glReadBuffer(readBuffer);
		glBlitFramebuffer(0, 0, this.width, this.height, 0, 0, outputFbo.width, outputFbo.height, GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT, GL_NEAREST);
		this.unbind();
	}

	public void resolveToScreen() {
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
		glBindFramebuffer(GL_READ_FRAMEBUFFER, this.frameBuffer);
		glDrawBuffer(GL_BACK);
		glBlitFramebuffer(0, 0, this.width, this.height, 0, 0, Window.getWidth(), Window.getHeight(), GL_COLOR_BUFFER_BIT, GL_NEAREST);
		this.unbind();
	}

	/**
	 * Creates the FBO along with a colour buffer texture attachment, and
	 * possibly a depth buffer.
	 * 
	 * @param type
	 *            - the type of depth buffer attachment to be attached to the
	 *            FBO.
	 */
	private void initialiseFrameBuffer(int type) {
		createFrameBuffer();
		if (!this.multiSample) {
			this.colorBuffer = createTextureAttachment(GL_COLOR_ATTACHMENT0);
			if (this.multiTarget)
				this.colorBuffer2 = createTextureAttachment(GL_COLOR_ATTACHMENT1);
		} else {
			this.colorBuffer = createMultisampleColorAttachment(GL_COLOR_ATTACHMENT0);
			if (this.multiTarget)
				this.colorBuffer2 = createMultisampleColorAttachment(GL_COLOR_ATTACHMENT1);
		}

		if (type == DEPTH_RENDER_BUFFER)
			createDepthBufferAttachment();
		else if (type == DEPTH_TEXTURE)
			createDepthTextureAttachment();

		unbind();
	}

	/**
	 * Creates a new frame buffer object and sets the buffer to which drawing
	 * will occur - colour attachment 0. This is the attachment where the colour
	 * buffer texture is.
	 * 
	 */
	private void createFrameBuffer() {
		this.frameBuffer = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, this.frameBuffer);
		determineDrawBuffers();
	}

	private void determineDrawBuffers() {
		IntBuffer drawBuffers = BufferUtils.createIntBuffer(2);
		drawBuffers.put(GL_COLOR_ATTACHMENT0);
		if (this.multiTarget)
			drawBuffers.put(GL_COLOR_ATTACHMENT1);
		drawBuffers.flip();

		glDrawBuffers(drawBuffers);
	}

	/**
	 * Creates a texture and sets it as the colour buffer attachment for this
	 * FBO.
	 */
	private int createTextureAttachment(int attachment) {
		int colorBuffer = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, colorBuffer);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, this.width, this.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glFramebufferTexture2D(GL_FRAMEBUFFER, attachment, GL_TEXTURE_2D, colorBuffer, 0);
		return colorBuffer;
	}

	/**
	 * Adds a depth buffer to the FBO in the form of a texture, which can later
	 * be sampled.
	 */
	private void createDepthTextureAttachment() {
		this.depthTexture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, this.depthTexture);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT24, this.width, this.height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer) null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, this.depthTexture, 0);
	}

	private int createMultisampleColorAttachment(int attachment) {
		int colorBuffer = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, colorBuffer);
		glRenderbufferStorageMultisample(GL_RENDERBUFFER, this.samplesCount, GL_RGBA16F, this.width, this.height);
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, attachment, GL_RENDERBUFFER, colorBuffer);
		return colorBuffer;
	}

	/**
	 * Adds a depth buffer to the FBO in the form of a render buffer. This can't
	 * be used for sampling in the shaders.
	 */
	private void createDepthBufferAttachment() {
		this.depthBuffer = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, this.depthBuffer);
		if (!this.multiSample)
			glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, this.width, this.height);
		else
			glRenderbufferStorageMultisample(GL_RENDERBUFFER, this.samplesCount, GL_DEPTH_COMPONENT24, this.width, this.height);

		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, this.depthBuffer);
	}

	public float readPixel(int x, int y, int attachment) {
		bindToRead(attachment);

		float[] pixels = new float[4]; // (r, g, b, a)
		glReadPixels(x, y, 1, 1, GL_RGBA, GL_FLOAT, pixels);

		return pixels[0];
	}

	public float[] readPixels(Vector2i start, Vector2i end, int attachment) { // TODO FIX DRAG SELECT OBJECTS
		bindToRead(attachment);

		Vector2i size = new Vector2i(end).sub(start).absolute();
		int numberOfPixels = size.x * size.y;
		float[] pixels = new float[3 * numberOfPixels]; // (r, g, b) * number of Pixels
		glReadPixels(start.x, start.y, size.x, size.y, GL_RGB, GL_FLOAT, pixels);

		return pixels;
	}
}
