
package editor.renderer.stuff;

import editor.stuff.Window;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

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

	private int colorTexture;
	private int depthTexture;

	private int depthBuffer;
	private int colourBuffer;
	private int colourBuffer2;

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

	public Fbo(int width, int height, boolean multiSample) {
		this.width = width;
		this.height = height;
		this.multiSample = multiSample;
		initialiseFrameBuffer(DEPTH_RENDER_BUFFER);
	}

	public Fbo(int width, int height, boolean multiSample, boolean multiTarget) {
		this.width = width;
		this.height = height;
		this.multiSample = multiSample;
		this.multiTarget = multiTarget;
		initialiseFrameBuffer(DEPTH_RENDER_BUFFER);
	}

	/**
	 * Deletes the frame buffer and its attachments when the game closes.
	 */
	public void cleanUp() {
		glDeleteFramebuffers(frameBuffer);
		glDeleteTextures(colorTexture);
		glDeleteTextures(depthTexture);
		glDeleteRenderbuffers(depthBuffer);
		glDeleteRenderbuffers(colourBuffer);
		glDeleteRenderbuffers(colourBuffer2);
	}

	/**
	 * Binds the frame buffer, setting it as the current render target. Anything
	 * rendered after this will be rendered to this FBO, and not to the screen.
	 */
	public void bind() {
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, frameBuffer);
		glViewport(0, 0, width, height);
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
	public void bindToRead() {
		glBindTexture(GL_TEXTURE_2D, 0);
		glBindFramebuffer(GL_READ_FRAMEBUFFER, frameBuffer);
		glReadBuffer(GL_COLOR_ATTACHMENT0);
	}

	/**
	 * @return The ID of the texture containing the colour buffer of the FBO.
	 */
	public int getColorTexture() { return colorTexture; }

	/**
	 * @return The texture containing the FBOs depth buffer.
	 */
	public int getDepthTexture() { return depthTexture; }

	public void resolveToFbo(int readBuffer, Fbo outputFbo) {
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, outputFbo.frameBuffer);
		glBindFramebuffer(GL_READ_FRAMEBUFFER, this.frameBuffer);
		glReadBuffer(readBuffer);
		glBlitFramebuffer(0, 0, width, height, 0, 0, outputFbo.width, outputFbo.height, GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT, GL_NEAREST);
		this.unbind();
	}

	public void resolveToScreen() {
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
		glBindFramebuffer(GL_READ_FRAMEBUFFER, this.frameBuffer);
		glDrawBuffer(GL_BACK);
		glBlitFramebuffer(0, 0, width, height, 0, 0, Window.getWidth(), Window.getHeight(), GL_COLOR_BUFFER_BIT, GL_NEAREST);
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
		if (!multiSample)
			createTextureAttachment();
		else {
			colourBuffer = createMultisampleColorAttachment(GL_COLOR_ATTACHMENT0);
			if (multiTarget)
				colourBuffer2 = createMultisampleColorAttachment(GL_COLOR_ATTACHMENT1);
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
		frameBuffer = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
		determineDrawBuffers();
	}

	private void determineDrawBuffers() {
		IntBuffer drawBuffers = BufferUtils.createIntBuffer(2);
		drawBuffers.put(GL_COLOR_ATTACHMENT0);
		if (multiTarget)
			drawBuffers.put(GL_COLOR_ATTACHMENT1);
		drawBuffers.flip();

		glDrawBuffers(drawBuffers);
	}

	/**
	 * Creates a texture and sets it as the colour buffer attachment for this
	 * FBO.
	 */
	private void createTextureAttachment() {
		colorTexture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, colorTexture);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colorTexture, 0);
	}

	/**
	 * Adds a depth buffer to the FBO in the form of a texture, which can later
	 * be sampled.
	 */
	private void createDepthTextureAttachment() {
		depthTexture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, depthTexture);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT24, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer) null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexture, 0);
	}

	private int createMultisampleColorAttachment(int attachment) {
		int colourBuffer = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, colourBuffer);
		glRenderbufferStorageMultisample(GL_RENDERBUFFER, samplesCount, GL_RGBA16F, width, height);
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, attachment, GL_RENDERBUFFER, colourBuffer);
		return colourBuffer;
	}

	/**
	 * Adds a depth buffer to the FBO in the form of a render buffer. This can't
	 * be used for sampling in the shaders.
	 */
	private void createDepthBufferAttachment() {
		depthBuffer = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, depthBuffer);
		if (!multiSample)
			glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, width, height);
		else
			glRenderbufferStorageMultisample(GL_RENDERBUFFER, samplesCount, GL_DEPTH_COMPONENT24, width, height);

		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthBuffer);
	}
}
