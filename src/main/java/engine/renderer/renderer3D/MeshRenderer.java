package engine.renderer.renderer3D;

import engine.assets.AssetPool;
import engine.renderer.EntityRenderer;
import engine.renderer.renderer2D.ed_Renderer;
import engine.renderer.renderer3D.mesh.Mesh;

public class MeshRenderer extends ed_Renderer {

    private Mesh mesh;

    @Override
    public void start() {
        super.start();

        // Load Meshes from AssetPool and replacing saved meshes because Gson loads Meshes and creates separate Object, with broken data
        if (this.mesh != null)
            this.mesh = AssetPool.getMesh(this.mesh.getFilepath());
    }

    @Override
    public void destroy() { EntityRenderer.destroyGameObject(this.gameObject, MeshRenderer.class); }

    @Override
    public void reset() { this.mesh = null; }

    public Mesh getMesh() { return this.mesh; }

    public void setMesh(Mesh mesh) { this.mesh = mesh; }
}
