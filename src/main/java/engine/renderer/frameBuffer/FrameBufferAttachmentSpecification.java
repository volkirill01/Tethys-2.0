package engine.renderer.frameBuffer;

import java.util.List;

public class FrameBufferAttachmentSpecification {

    private final List<FrameBufferTextureSpecification> attachments;

    public FrameBufferAttachmentSpecification(List<FrameBufferTextureSpecification> attachments) { this.attachments = attachments; }

    public List<FrameBufferTextureSpecification> getAttachments() { return this.attachments; }
}
