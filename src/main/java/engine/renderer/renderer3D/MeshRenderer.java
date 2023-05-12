package engine.renderer.renderer3D;

import engine.assets.AssetPool;
import engine.entity.component.Component;
import engine.renderer.renderer3D.mesh.Mesh;

public class MeshRenderer extends Component { // TODO FIX BUG(IF USER ADDS COMPONENT, MESH NOT DISPLAY BEFORE USER ENTER PLAY MODE)

    private Mesh mesh;

    public MeshRenderer() { this.mesh = AssetPool.getMesh("Assets/pbrTest/pbr-sphere-test.obj"); }

    public Mesh getMesh() { return this.mesh; }

    public void setMesh(Mesh mesh) { this.mesh = mesh; }
}
