package editor.renderer.buffers;

import static org.lwjgl.opengl.GL15.*;

public class IndexBuffer {

    private final int ID;
    private final int count;

    public IndexBuffer(int[] indices, int count) {
        this.ID = glGenBuffers();
        this.count = count;
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.ID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
    }

    public void bind() { glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.ID); }

    public void unbind() { glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0); } // Bind nothing

    public void delete() { glDeleteBuffers(this.ID); }

    public int getID() { return this.ID; }

    public int getCount() { return this.count; }
}
