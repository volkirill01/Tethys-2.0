package engine.renderer.renderer2D.batches;

import engine.entity.GameObject;
import engine.profiling.Profiler;
import engine.renderer.buffers.IndexBuffer;
import engine.renderer.buffers.VertexArray;
import engine.renderer.buffers.VertexBuffer;
import engine.renderer.buffers.bufferLayout.BufferLayout;
import engine.renderer.renderer2D.ed_Renderer;

public abstract class RenderBatch2D implements Comparable<RenderBatch2D> {

    protected final ed_Renderer[] quads;
    protected int numberOfQuads;
    protected boolean hasRoom;
    protected float[] vertices;

    protected VertexArray vao;
    protected int vertexSize;
    protected final int maxBathSize;
    protected final int zIndex;

    public RenderBatch2D(int maxBatchSize, int zIndex) {
        this.maxBathSize = maxBatchSize;
        this.quads = new ed_Renderer[this.maxBathSize];
        this.zIndex = zIndex;

        this.numberOfQuads = 0;
        this.hasRoom = true;
    }

    public void init(BufferLayout layout) {
        Profiler.startTimer("RenderBatch2D Init");
        // Generate and bind a Vertex Array Object (VAO)
        this.vao = new VertexArray();

        // Create and upload vertices buffer (VBO)
        this.vertexSize = layout.getStride() / Float.BYTES;
        // 4 vertices per quad
        this.vertices = new float[this.maxBathSize * 4 * this.vertexSize];
        VertexBuffer vbo = new VertexBuffer(this.vertices);
        vbo.setLayout(layout);
        this.vao.setVertexBuffer(vbo);

        // Create and upload elements buffer (EBO)
        int[] elements = generateIndices();
        IndexBuffer ebo = new IndexBuffer(elements, elements.length);
        this.vao.setIndexBuffer(ebo);

        this.vao.unbind();
        Profiler.stopTimer("RenderBatch2D Init");
    }

    public void addQuad(ed_Renderer renderer) {
        Profiler.startTimer(String.format("Add Sprite to RenderBatch2D. Obj Name - '%s'", renderer.gameObject.getName()));
        // Get index and add render object
        int index = this.numberOfQuads;
        this.quads[index] = renderer;
        this.numberOfQuads++;

        // Add properties to local vertices array
        loadVertexProperties(index);

        if (this.numberOfQuads >= this.maxBathSize)
            this.hasRoom = false;
        Profiler.stopTimer(String.format("Add Sprite to RenderBatch2D. Obj Name - '%s'", renderer.gameObject.getName()));
    }

    public abstract void render();

    public boolean destroyIfExists(GameObject obj) {
        Profiler.startTimer("Destroy in RenderBatch2D");
        ed_Renderer renderer = obj.getComponent(ed_Renderer.class);
        for (int i = 0; i < this.numberOfQuads; i++) {
            if (this.quads[i] == renderer) {
                for (int j = i; j < this.numberOfQuads - 1; j++) {
                    this.quads[j] = this.quads[j + 1];
                    this.quads[j].setDirty(true);
                }
                this.numberOfQuads--;
                Profiler.stopTimer("Destroy in RenderBatch2D");
                return true;
            }
        }
        Profiler.stopTimer("Destroy in RenderBatch2D");
        return false;
    }

    protected abstract void loadVertexProperties(int index);

    private int[] generateIndices() {
        // 6 indices per quad (3 per triangle)
        int[] elements = new int[6 * this.maxBathSize];
        for (int i = 0; i < this.maxBathSize; i++)
            loadElementIndices(elements, i);

        return elements;
    }

    private void loadElementIndices(int[] elements, int index) {
        // 6 indices per quad (3 per triangle)
        int offsetArrayIndex = 6 * index;
        int offset = 4 * index;

        // Triangle 1
        elements[offsetArrayIndex + 0]  = offset + 3;
        elements[offsetArrayIndex + 1]  = offset + 2;
        elements[offsetArrayIndex + 2]  = offset + 0;

        // Triangle 2
        elements[offsetArrayIndex + 3]  = offset + 0;
        elements[offsetArrayIndex + 4]  = offset + 2;
        elements[offsetArrayIndex + 5]  = offset + 1;
    }

    public boolean hasRoom() { return this.hasRoom; }

    public int getZIndex() { return this.zIndex; }

    @Override
    public int compareTo(RenderBatch2D o) { return Integer.compare(this.zIndex, o.getZIndex()); }

    public boolean isEmpty() { return this.numberOfQuads == 0; }
}
