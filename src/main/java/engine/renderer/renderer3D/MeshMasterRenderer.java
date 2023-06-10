package engine.renderer.renderer3D;

import engine.assets.AssetPool;
import engine.entity.GameObject;
import engine.profiling.Profiler;
import engine.renderer.EntityRenderer;
import engine.renderer.camera.ed_BaseCamera;
import engine.renderer.renderer3D.mesh.RawModel;
import engine.renderer.shader.Shader;
import engine.stuff.utils.Maths;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class MeshMasterRenderer {

    private static final List<MeshRenderer> meshes = new ArrayList<>();
    private static final Shader shader = AssetPool.getShader("Resources/shaders/3D/pbrTest.glsl", true);

    public static void add(GameObject go) { add(go.getComponent(MeshRenderer.class)); }

    private static void add(MeshRenderer renderer) {
        if (!meshes.contains(renderer))
            meshes.add(renderer);
    }

    public static void destroyGameObject(GameObject obj) {
        Profiler.startTimer(String.format("Destroy GameObject in MeshMasterRenderer - '%s'", obj.getName()));
        meshes.removeIf(renderer -> renderer == obj.getComponent(MeshRenderer.class));
        Profiler.stopTimer(String.format("Destroy GameObject in MeshMasterRenderer - '%s'", obj.getName()));
    }

    public static void render(ed_BaseCamera camera) {
        Profiler.startTimer("Render in MeshMasterRenderer");
        EntityRenderer.setShader(shader);
        shader.bind();
        EntityRenderer.uploadSceneData(camera);
//        glClear(GL_DEPTH_BUFFER_BIT);
//        glEnable(GL_DEPTH_TEST | GL_CULL_FACE);
//        glCullFace(GL_BACK);

//                shader.uploadTexture("u_Albedo", 0);

        // Send data tu GPU only if data is changed
        for (MeshRenderer render : meshes) {
            if (render.getMesh() == null)
                continue;

            if (render.getMesh().getModels() != null)
                for (RawModel rawModel : render.getMesh().getModels()) {
    //                rawModel.getVao().getVertexBuffer().bind();

                    Matrix4f transformationMatrix = Maths.createTransformationMatrix(render.gameObject.transform.position, render.gameObject.transform.rotation, render.gameObject.transform.scale);

                    shader.uploadInt("u_EntityID", render.gameObject.getIncrementedID() + 1);
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
