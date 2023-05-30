
package engine.renderer.frameBuffer;

import engine.logging.DebugLog;
import engine.profiling.Profiler;
import engine.stuff.Window;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_2D_MULTISAMPLE;
import static org.lwjgl.opengl.GL32.glTexImage2DMultisample;
import static org.lwjgl.opengl.GL42.glTexStorage2D;
import static org.lwjgl.opengl.GL44.glClearTexImage;
import static org.lwjgl.opengl.GL45.glCreateTextures;

public class Framebuffer {

	private int fboID;
	private final int width, height;
	private final FrameBufferAttachmentSpecification attachments;
	private final int samples;

	private final List<FrameBufferTextureSpecification> colorAttachmentSpecifications = new ArrayList<>();
	private final List<Integer> colorAttachments = new ArrayList<>();
	private FrameBufferTextureSpecification depthAttachmentSpecification = new FrameBufferTextureSpecification(FrameBufferTextureFormat.None);
	private int depthAttachment;

	public Framebuffer(int width, int height, FrameBufferAttachmentSpecification attachments) {
		this.width = width;
		this.height = height;
		this.attachments = attachments;
		this.samples = 1;
		init();
	}

	public Framebuffer(int width, int height, FrameBufferAttachmentSpecification attachments, int samples) {
		this.width = width;
		this.height = height;
		this.attachments = attachments;
		this.samples = samples;
		init();
	}

//	public void resolveToFbo(int readBuffer, FrameBufferSpecification outputFbo) {
//		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, outputFbo.frameBuffer);
//		glBindFramebuffer(GL_READ_FRAMEBUFFER, this.frameBuffer);
//		glReadBuffer(readBuffer);
//		glBlitFramebuffer(0, 0, this.width, this.height, 0, 0, outputFbo.width, outputFbo.height, GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT, GL_NEAREST);
//		this.unbind();
//	}
//
//	public void resolveToScreen() {
//		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
//		glBindFramebuffer(GL_READ_FRAMEBUFFER, this.frameBuffer);
//		glDrawBuffer(GL_BACK);
//		glBlitFramebuffer(0, 0, this.width, this.height, 0, 0, Window.getWidth(), Window.getHeight(), GL_COLOR_BUFFER_BIT, GL_NEAREST);
//		this.unbind();
//	}

	private void init() {
		DebugLog.log("FrameBuffer:Init");

		Profiler.startTimer("Framebuffer Initialization");
		this.fboID = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, this.fboID);

		boolean multiSample = samples > 1;

		// Attachments
		sortAttachments();

		if (this.colorAttachmentSpecifications.size() > 0) {
			for (int i = 0; i < this.colorAttachmentSpecifications.size(); i++) {
				FrameBufferTextureSpecification specification = this.colorAttachmentSpecifications.get(i);
				int attachment = createTexture(multiSample);
				this.colorAttachments.add(attachment);

				bindTexture(multiSample, attachment);
				switch (specification.getFormat()) {
					case RGBA8 -> attachColorTexture(attachment, this.samples, GL_RGBA8, GL_RGBA, this.width, this.height, i);
					case RED_INTEGER -> attachColorTexture(attachment, this.samples, GL_R32I, GL_RED_INTEGER, this.width, this.height, i);

					default -> throw new IllegalStateException(String.format("Unknown FrameBufferTextureFormat - '%s'", specification.getFormat().name()));
				}
			}
		}

		if (this.depthAttachmentSpecification.getFormat() != FrameBufferTextureFormat.None) {
			this.depthAttachment = createTexture(multiSample);
			bindTexture(multiSample, this.depthAttachment);
			if (this.depthAttachmentSpecification.getFormat() == FrameBufferTextureFormat.DEPTH24STENCIL8)
				attachDepthTexture(this.depthAttachment, this.samples, GL_DEPTH24_STENCIL8, GL_DEPTH_STENCIL_ATTACHMENT, this.width, this.height);
			else
				throw new IllegalStateException(String.format("Unknown FrameBufferTextureFormat - '%s'", this.depthAttachmentSpecification.getFormat().name()));
		}

		if (this.colorAttachments.size() > 1) {
			if (this.colorAttachments.size() > 4)
				throw new IllegalStateException(String.format("Color attachments count: %d. Engine supports maximum 4 color attachments.", this.colorAttachments.size()));
			int[] buffers = new int[]{ GL_COLOR_ATTACHMENT0 };
			switch (this.colorAttachments.size()) {
				case 2 -> buffers = new int[]{ GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1 };
				case 3 -> buffers = new int[]{ GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1, GL_COLOR_ATTACHMENT2 };
				case 4 -> buffers = new int[]{ GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1, GL_COLOR_ATTACHMENT2, GL_COLOR_ATTACHMENT3 };
			}
			glDrawBuffers(buffers);
		} else if (this.colorAttachments.size() == 0) {
			// One use case only for depth pass
			glDrawBuffers(GL_NONE);
		}

		if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
			throw new RuntimeException("Framebuffer creation failed.");

		unbind();
		Profiler.stopTimer("Framebuffer Initialization");
	}

	private void sortAttachments() {
		for (FrameBufferTextureSpecification specification : this.attachments.getAttachments()) {
			if (!isDepthFormat(specification.getFormat()))
				this.colorAttachmentSpecifications.add(specification);
			else
				this.depthAttachmentSpecification = specification;
		}
	}

	private int createTexture(boolean multiSample) { return glCreateTextures(textureTarget(multiSample)); }

	private boolean isDepthFormat(FrameBufferTextureFormat format) {
		return switch (format) {
			case RGBA8, RED_INTEGER -> false;
			case DEPTH24STENCIL8 -> true;
			default -> throw new IllegalStateException(String.format("Unknown FrameBufferTextureFormat - '%s'", format.name()));
		};
	}

	private int framebufferTextureFormatToOpenGLBaseType(FrameBufferTextureFormat format) {
		return switch (format) {
			case RGBA8 				-> GL_RGBA8;
			case RED_INTEGER 		-> GL_RED_INTEGER;
			case DEPTH24STENCIL8 	-> GL_DEPTH24_STENCIL8;

			default -> throw new IllegalStateException(String.format("Unknown FrameBufferTextureFormat - '%s'", format.name()));
		};
	}

	private int textureTarget(boolean multiSample) { return multiSample ? GL_TEXTURE_2D_MULTISAMPLE : GL_TEXTURE_2D; }

	private void bindTexture(boolean multiSample, int id) { glBindTexture(textureTarget(multiSample), id); }

	private void attachColorTexture(int id, int samples, int internalFormat, int format, int width, int height, int index) {
		Profiler.startTimer("Framebuffer Attach Color texture");
		boolean multiSample = samples > 1;
		if (multiSample)
			glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, samples, internalFormat, width, height, false);
		else {
			glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, GL_UNSIGNED_BYTE, 0);

			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		}

		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + index, textureTarget(multiSample), id, 0);
		Profiler.stopTimer("Framebuffer Attach Color texture");
	}

	private void attachDepthTexture(int id, int samples, int format, int attachmentType, int width, int height) {
		Profiler.startTimer("Framebuffer Attach Depth texture");
		boolean multiSample = samples > 1;
		if (multiSample)
			glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, samples, format, width, height, false);
		else {
			glTexStorage2D(GL_TEXTURE_2D, 1, format, width, height);

			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		}

		glFramebufferTexture2D(GL_FRAMEBUFFER, attachmentType, textureTarget(multiSample), id, 0);
		Profiler.stopTimer("Framebuffer Attach Depth texture");
	}

	public void bindToWrite() {
		Profiler.startTimer("Framebuffer Bind to write");
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, this.fboID);
		glViewport(0, 0, this.width, this.height);
		Profiler.stopTimer("Framebuffer Bind to write");
	}

	public void unbind() {
		Profiler.startTimer("Framebuffer Unbind");
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glViewport(0, 0, Window.getScreenWidth(), Window.getScreenHeight());
		Profiler.stopTimer("Framebuffer Unbind");
	}

	public void clearColorAttachment(int attachmentIndex, int value) {
		Profiler.startTimer(String.format("Framebuffer Clear color Attachment - '%d'", attachmentIndex));
		if (attachmentIndex > 4)
			throw new IllegalStateException(String.format("Get color attachment: %d. Engine supports maximum of 4 color attachments.", attachmentIndex));
		if (attachmentIndex > this.colorAttachments.size())
			throw new IllegalStateException(String.format("Get color attachment: %d. This FOB only contains %d color attachments.", attachmentIndex, this.colorAttachments.size()));

		FrameBufferTextureSpecification specification = colorAttachmentSpecifications.get(attachmentIndex);

		glClearTexImage(colorAttachments.get(attachmentIndex), 0, framebufferTextureFormatToOpenGLBaseType(specification.getFormat()), GL_INT, new int[]{ value });
		Profiler.stopTimer(String.format("Framebuffer Clear color Attachment - '%d'", attachmentIndex));
	}

	public int getColorAttachmentID() { return this.colorAttachments.get(0); }

	public int getColorAttachmentID(int attachmentIndex) {
		if (attachmentIndex > 4)
			throw new IllegalStateException(String.format("Get color attachment: %d. Engine supports maximum of 4 color attachments.", attachmentIndex));
		if (attachmentIndex > this.colorAttachments.size())
			throw new IllegalStateException(String.format("Get color attachment: %d. This FOB only contains %d color attachments.", attachmentIndex, this.colorAttachments.size()));
		return this.colorAttachments.get(attachmentIndex);
	}

	public int getDepthTexture() { return this.depthAttachment; }

	private void bindColorAttachmentToRead(int attachment) { glReadBuffer(GL_COLOR_ATTACHMENT0 + attachment); }

	public float readPixel(int attachmentIndex, int x, int y) {
		Profiler.startTimer(String.format("Framebuffer Read pixel from color Attachment - '%d'", attachmentIndex));
		glBindFramebuffer(GL_FRAMEBUFFER, this.fboID);
		bindColorAttachmentToRead(attachmentIndex);

		int[] pixels = new int[1];
		glReadPixels(x, y, 1, 1, GL_RED_INTEGER, GL_INT, pixels);

		Profiler.stopTimer(String.format("Framebuffer Read pixel from color Attachment - '%d'", attachmentIndex));
		return pixels[0];
	}

	public int[] readPixels(int attachmentIndex, Vector2i start, Vector2i end) {
		Profiler.startTimer(String.format("Framebuffer Read pixels from color Attachment - '%d'", attachmentIndex));
		glBindFramebuffer(GL_FRAMEBUFFER, this.fboID);
		bindColorAttachmentToRead(attachmentIndex);

		Vector2i size = new Vector2i(end).sub(start).absolute();
		int numberOfPixels = size.x * size.y;
		int[] pixels = new int[numberOfPixels];
		glReadPixels(start.x, start.y, size.x, size.y, GL_RED_INTEGER, GL_INT, pixels);

		Profiler.stopTimer(String.format("Framebuffer Read pixels from color Attachment - '%d'", attachmentIndex));
		return pixels;
	}

	public void freeMemory() {
		Profiler.startTimer("Framebuffer Free memory");
		glDeleteFramebuffers(this.fboID);
		for (int attachment : this.colorAttachments)
			glDeleteTextures(attachment);
		if (this.depthAttachment != 0)
			glDeleteTextures(this.depthAttachment);
		Profiler.stopTimer("Framebuffer Free memory");
	}
}
