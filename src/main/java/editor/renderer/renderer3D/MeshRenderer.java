package editor.renderer.renderer3D;

import editor.assets.AssetPool;
import editor.entity.component.Component;
import editor.renderer.renderer3D.mesh.Mesh;

public class MeshRenderer extends Component {

    private Mesh mesh;

    public MeshRenderer() {
        this.mesh = AssetPool.getMesh("Assets/defaultCapsule.obj");
    }

    public Mesh getMesh() { return this.mesh; }

    public void setMesh(Mesh mesh) { this.mesh = mesh; }
}
