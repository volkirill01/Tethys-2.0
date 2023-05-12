package engine.renderer.renderer3D.mesh;

import java.util.List;

public class Mesh {

    private transient final List<RawModel> models;
    private final String filepath;

    public Mesh(List<RawModel> models, String filepath) {
        this.models = models;
        this.filepath = filepath;
    }

    public List<RawModel> getModels() { return this.models; }

    public String getFilepath() { return this.filepath; }
}
