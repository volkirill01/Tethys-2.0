package engine.renderer.buffers;

import engine.renderer.buffers.bufferLayout.VertexBufferElement;
import engine.stuff.openGL.OpenGLConversions;

import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class VertexArray {

    private final int ID;
    private VertexBuffer vertexBuffer = null;
    private IndexBuffer indexBuffer = null;

    public VertexArray() { this.ID = glGenVertexArrays(); }

    public void bind() { glBindVertexArray(this.ID); }

    public void unbind() { glBindVertexArray(0); } // Bind nothing

    public void freeMemory() { glDeleteVertexArrays(this.ID); }

    public int getID() { return this.ID; }

    public void setVertexBuffer(VertexBuffer vertexBuffer) {
        if (vertexBuffer.getLayout() == null || vertexBuffer.getLayout().getElements() == null || vertexBuffer.getLayout().getElements().size() == 0)
            throw new NullPointerException("Vertex Buffer has no layout.");

        glBindVertexArray(this.ID);
        vertexBuffer.bind();

        // Enable the buffer attributes pointers
        List<VertexBufferElement> elements = vertexBuffer.getLayout().getElements();
        for (int i = 0; i < elements.size(); i++) {
            switch (elements.get(i).getType()) {
                case Float, Float2, Float3, Float4, Mat3, Mat4 -> {
                    glEnableVertexAttribArray(i);
                    glVertexAttribPointer(i, OpenGLConversions.getComponentCount(elements.get(i).getType()),
                            OpenGLConversions.shaderDataTypeToOpenGLBaseType(elements.get(i).getType()), elements.get(i).isNormalized(),
                            vertexBuffer.getLayout().getStride(), elements.get(i).getOffset());
                }
                case Int, Int2, Int3, Int4, Bool -> {
                    glEnableVertexAttribArray(i);
                    glVertexAttribIPointer(i, OpenGLConversions.getComponentCount(elements.get(i).getType()),
                            OpenGLConversions.shaderDataTypeToOpenGLBaseType(elements.get(i).getType()),
                            vertexBuffer.getLayout().getStride(), elements.get(i).getOffset());
                }
                default -> throw new IllegalStateException(String.format("Unknown ShaderData type - '%s'", elements.get(i).getType().name()));
            }
        }
        this.vertexBuffer = vertexBuffer;
    }

    public void setIndexBuffer(IndexBuffer indexBuffer) {
        glBindVertexArray(this.ID);
        indexBuffer.bind();

        this.indexBuffer = indexBuffer;
    }

    public VertexBuffer getVertexBuffer() { return this.vertexBuffer; }

    public IndexBuffer getIndexBuffer() { return this.indexBuffer; }
}
