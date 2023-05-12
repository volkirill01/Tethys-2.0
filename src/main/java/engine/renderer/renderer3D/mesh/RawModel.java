package engine.renderer.renderer3D.mesh;

import engine.renderer.buffers.VertexArray;

public class RawModel {

    private final VertexArray vao;
    private final String materialGroup;

    public RawModel(VertexArray vao, String materialGroup) {
        this.vao = vao;
        this.materialGroup = materialGroup;
    }

    public VertexArray getVao() { return this.vao; }

    public String getMaterialGroup() { return this.materialGroup; }
}
