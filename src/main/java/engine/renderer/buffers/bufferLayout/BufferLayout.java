package engine.renderer.buffers.bufferLayout;

import java.util.List;

public class BufferLayout {

    private final List<VertexBufferElement> elements;
    private int stride = 0;

    public BufferLayout(List<VertexBufferElement> elements) {
        this.elements = elements;
        calculateOffsetsAndStride();
    }

    private void calculateOffsetsAndStride() {
        int offset = 0;
        this.stride = 0;
        for (VertexBufferElement element : this.elements) {
            element.setOffset(offset);
            offset += element.getSize();
            this.stride += element.getSize();
        }
    }

    public List<VertexBufferElement> getElements() { return this.elements; }

    public int getStride() { return this.stride; }
}
