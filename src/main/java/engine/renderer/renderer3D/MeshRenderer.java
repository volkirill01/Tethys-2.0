package engine.renderer.renderer3D;

import engine.renderer.EntityRenderer;
import engine.renderer.renderer2D.ed_Renderer;
import engine.renderer.renderer3D.mesh.Mesh;

public class MeshRenderer extends ed_Renderer {

    private Mesh mesh;

    @Override
    public void destroy() { EntityRenderer.destroyGameObject(this.gameObject, MeshRenderer.class); }

    public Mesh getMesh() { return this.mesh; }

    public void setMesh(Mesh mesh) { this.mesh = mesh; }
}
