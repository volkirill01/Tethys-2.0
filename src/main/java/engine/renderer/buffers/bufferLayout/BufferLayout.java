package engine.renderer.buffers.bufferLayout;

import java.util.List;

public class BufferLayout {

    private final List<BufferElement> elements;
    private int stride = 0;

    public BufferLayout(List<BufferElement> elements) {
        this.elements = elements;
        calculateOffsetsAndStride();
    }

    private void calculateOffsetsAndStride() {
        int offset = 0;
        this.stride = 0;
        for (BufferElement element : this.elements) {
            element.setOffset(offset);
            offset += element.getSize();
            this.stride += element.getSize();
        }
    }

    public List<BufferElement> getElements() { return this.elements; }

    public int getStride() { return this.stride; }
}
