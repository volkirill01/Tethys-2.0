package engine.renderer.buffers.bufferLayout;

import engine.stuff.openGL.BufferElement;
import engine.stuff.openGL.ShaderDataType;

public class VertexBufferElement extends BufferElement {

    private final boolean isNormalized;

    public VertexBufferElement(ShaderDataType type, String name) {
        super(type, name);
        this.isNormalized = false;
    }

    public VertexBufferElement(ShaderDataType type, String name, boolean isNormalized) {
        super(type, name);
        this.isNormalized = isNormalized;
    }

    public boolean isNormalized() { return this.isNormalized; }
}
