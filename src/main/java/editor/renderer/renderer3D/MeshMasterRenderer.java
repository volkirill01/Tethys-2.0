package editor.renderer.renderer3D;

import editor.assets.AssetPool;
import editor.entity.GameObject;
import editor.renderer.EntityRenderer;
import editor.renderer.renderer3D.mesh.RawModel;
import editor.renderer.shader.Shader;
import editor.stuff.Maths;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class MeshMasterRenderer {

    private final List<MeshRenderer> meshes = new ArrayList<>();

    public void add(GameObject go) { add(go.getComponent(MeshRenderer.class)); }

    private void add(MeshRenderer renderer) {
        meshes.add(renderer);
    }

    public void destroyGameObject(GameObject obj) {
        meshes.removeIf(renderer -> renderer == obj.getComponent(MeshRenderer.class));
    }

    public void render(Matrix4f projectionMatrix, Matrix4f viewMatrix) {
        if (EntityRenderer.getCurrentShader().getFilepath().contains("editorFiles/shaders/default.glsl"))
            EntityRenderer.setShader(AssetPool.getShader("editorFiles/shaders/test.glsl"));
        EntityRenderer.getCurrentShader().bind();

        // Send data tu GPU only if data is changed
        for (MeshRenderer render : this.meshes) {
            for (RawModel rawModel : render.getMesh().getModels()) {
                rawModel.getVao().getVertexBuffer().bind();

                Matrix4f transformationMatrix = Maths.createTransformationMatrix(render.gameObject.transform.position, render.gameObject.transform.rotation, render.gameObject.transform.scale);

                Shader shader = EntityRenderer.getCurrentShader();
                shader.uploadFloat("u_EntityID", render.gameObject.getUid() + 1);
                shader.uploadMat4f("u_TransformationMatrix", transformationMatrix);
                shader.uploadMat4f("u_ProjectionMatrix", projectionMatrix);
                shader.uploadMat4f("u_ViewMatrix", viewMatrix);

                EntityRenderer.submit(rawModel.getVao());

                rawModel.getVao().getVertexBuffer().unbind();
            }
        }

        EntityRenderer.getCurrentShader().unbind();
    }
}
