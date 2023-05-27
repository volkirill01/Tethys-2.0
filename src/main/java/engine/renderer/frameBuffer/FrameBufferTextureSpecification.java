package engine.renderer.frameBuffer;

public class FrameBufferTextureSpecification {

    private final FrameBufferTextureFormat format;

    public FrameBufferTextureSpecification(FrameBufferTextureFormat format) { this.format = format; } // TODO FILTERING/WRAP_MODE

    public FrameBufferTextureFormat getFormat() { return this.format; }
}
