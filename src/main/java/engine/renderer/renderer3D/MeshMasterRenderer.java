package engine.renderer.renderer3D;

import engine.assets.AssetPool;
import engine.entity.GameObject;
import engine.profiling.Profiler;
import engine.renderer.EntityRenderer;
import engine.renderer.Texture;
import engine.renderer.renderer3D.mesh.RawModel;
import engine.renderer.shader.Shader;
import engine.scenes.SceneManager;
import engine.stuff.Maths;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class MeshMasterRenderer {

    private static final List<MeshRenderer> meshes = new ArrayList<>();

    public static void add(GameObject go) { add(go.getComponent(MeshRenderer.class)); }

    private static void add(MeshRenderer renderer) { meshes.add(renderer); }

    public static void destroyGameObject(GameObject obj) {
        Profiler.startTimer(String.format("Destroy GameObject in MeshMasterRenderer - '%s'", obj.name));
        meshes.removeIf(renderer -> renderer == obj.getComponent(MeshRenderer.class));
        Profiler.stopTimer(String.format("Destroy GameObject in MeshMasterRenderer - '%s'", obj.name));
    }

    public static void render(Matrix4f projectionMatrix, Matrix4f viewMatrix) {
        Shader shader = AssetPool.getShader("editorFiles/shaders/3D/pbrTest.glsl");
        render_SingleShader(projectionMatrix, viewMatrix, shader);
    }

    public static void render_SingleShader(Matrix4f projectionMatrix, Matrix4f viewMatrix, Shader shader) {
        Profiler.startTimer("Render in MeshMasterRenderer");
        EntityRenderer.setShader(shader);
        shader.bind();
//        glClear(GL_DEPTH_BUFFER_BIT);
//        glEnable(GL_DEPTH_TEST | GL_CULL_FACE);
//        glCullFace(GL_BACK);

//                shader.uploadTexture("u_Albedo", 0);
        shader.uploadMat4f("u_ProjectionMatrix", projectionMatrix);
        shader.uploadMat4f("u_ViewMatrix", viewMatrix);

        // Send data tu GPU only if data is changed
        for (MeshRenderer render : meshes) {
            for (RawModel rawModel : render.getMesh().getModels()) {
//                rawModel.getVao().getVertexBuffer().bind();

                Matrix4f transformationMatrix = Maths.createTransformationMatrix(render.gameObject.transform.position, render.gameObject.transform.rotation, render.gameObject.transform.scale);

                shader.uploadInt("u_EntityID", render.gameObject.getUid() + 1);
                shader.uploadMat4f("u_TransformationMatrix", transformationMatrix);

//                // PBR TEST
//                Texture albedo = AssetPool.getTexture("Assets/pbrTest/sphere_Base_Color.png");
//                glActiveTexture(GL_TEXTURE0);
//                albedo.bind();
//                shader.uploadVec3f("u_CameraPosition", SceneManager.getCurrentScene().getEditorCamera().getPosition());

                EntityRenderer.submit(rawModel.getVao());
//                albedo.unbind();

//                rawModel.getVao().getVertexBuffer().unbind();
            }
        }
//        glDisable(GL_CULL_FACE | GL_DEPTH_TEST);
        EntityRenderer.getCurrentShader().unbind();
        Profiler.stopTimer("Render in MeshMasterRenderer");
    }

    public static void clear() { meshes.clear(); }
}
