package engine.renderer.buffers;

import engine.renderer.buffers.bufferLayout.BufferLayout;

import static org.lwjgl.opengl.GL15.*;

public class VertexBuffer {

    private final int ID;
    private BufferLayout layout;

    public VertexBuffer(int size) {
        this.ID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, this.ID);
        glBufferData(GL_ARRAY_BUFFER, size, GL_DYNAMIC_DRAW);
    }

    public VertexBuffer(float[] vertices) {
        this.ID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, this.ID);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_DYNAMIC_DRAW);
    }

    public void setData(float[] data) {
        glBindBuffer(GL_ARRAY_BUFFER, this.ID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, data);
    }

    public void bind() { glBindBuffer(GL_ARRAY_BUFFER, this.ID); }

    public void unbind() { glBindBuffer(GL_ARRAY_BUFFER, 0); } // Bind nothing

    public void delete() { glDeleteBuffers(this.ID); }

    public int getID() { return this.ID; }

    public BufferLayout getLayout() { return this.layout; }

    public void setLayout(BufferLayout layout) { this.layout = layout; }
}
