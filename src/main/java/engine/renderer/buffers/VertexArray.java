package engine.renderer.buffers;

import engine.renderer.buffers.bufferLayout.BufferElement;
import engine.renderer.buffers.bufferLayout.ShaderDataType;

import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class VertexArray {

    private final int ID;
    private VertexBuffer vertexBuffer = null;
    private IndexBuffer indexBuffer = null;

    public VertexArray() { this.ID = glGenVertexArrays(); }

    public void bind() { glBindVertexArray(this.ID); }

    public void unbind() { glBindVertexArray(0); } // Bind nothing

    public void delete() { glDeleteVertexArrays(this.ID); }

    public int getID() { return this.ID; }

    public void setVertexBuffer(VertexBuffer vertexBuffer) {
        if (vertexBuffer.getLayout() == null || vertexBuffer.getLayout().getElements() == null || vertexBuffer.getLayout().getElements().size() == 0)
            throw new NullPointerException("Vertex Buffer has no layout.");

        glBindVertexArray(this.ID);
        vertexBuffer.bind();

        // Enable the buffer attributes pointers
        List<BufferElement> elements = vertexBuffer.getLayout().getElements();
        for (int i = 0; i < elements.size(); i++) {
            glVertexAttribPointer(i, elements.get(i).getComponentCount(),
                    shaderDataTypeToOpenGLBaseType(elements.get(i).getType()), elements.get(i).isNormalized(),
                    vertexBuffer.getLayout().getStride(), elements.get(i).getOffset());
            glEnableVertexAttribArray(i);
        }
        this.vertexBuffer = vertexBuffer;
    }

    public void setIndexBuffer(IndexBuffer indexBuffer) {
        glBindVertexArray(this.ID);
        indexBuffer.bind();

        this.indexBuffer = indexBuffer;
    }

    private int shaderDataTypeToOpenGLBaseType(ShaderDataType type) {
        return switch (type) {
            case None                                       -> GL_NONE;
            case Float, Float2, Float3, Float4, Mat3, Mat4  -> GL_FLOAT;
            case Int, Int2, Int3, Int4                      -> GL_INT;
            case Bool                                       -> GL_BOOL;
            default -> throw new IllegalStateException(String.format("Unknown ShaderData type - '%s'", type.name()));
        };
    }

    public VertexBuffer getVertexBuffer() { return this.vertexBuffer; }

    public IndexBuffer getIndexBuffer() { return this.indexBuffer; }
}
