package engine.renderer.renderer2D;

import engine.assets.AssetPool;
import engine.entity.GameObject;
import engine.profiling.Profiler;
import engine.renderer.EntityRenderer;
import engine.renderer.Texture2D;
import engine.renderer.camera.ed_BaseCamera;
import engine.renderer.shader.Shader;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.glEnable;

public class SpriteMasterRenderer {

    private static final int MAX_BATCH_SIZE = 10_000;
    private static final List<RenderBatch2D> batches = new ArrayList<>();

    private static int quadsCount = 0;

    private static final Shader shader = AssetPool.getShader("editorFiles/shaders/2D/defaultSprite.glsl", true);

    public static void add(GameObject go) { add(go.getComponent(SpriteRenderer.class)); }

    private static void add(SpriteRenderer renderer) {
        Profiler.startTimer(String.format("SpriteMasterRenderer Add GameObject. Obj Name - '%s'", renderer.gameObject.getName()));
        boolean added = false;
        for (RenderBatch2D batch : batches) {
            if (batch.hasRoom() && batch.getZIndex() == renderer.gameObject.transform.getZIndex()) {
                Texture2D texture = renderer.getSprite().getTexture();
                if (texture == null || (batch.hasTexture(texture) || batch.hasTextureRoom())) {
                    batch.addSprite(renderer);
                    added = true;
                    break;
                }
            }
        }

        if (!added) {
            RenderBatch2D newBatch = new RenderBatch2D(MAX_BATCH_SIZE, renderer.gameObject.transform.getZIndex());
            newBatch.init();
            batches.add(newBatch);
            newBatch.addSprite(renderer);
            Collections.sort(batches);
        }
        Profiler.stopTimer(String.format("SpriteMasterRenderer Add GameObject. Obj Name - '%s'", renderer.gameObject.getName()));

        quadsCount++;
    }

    public static void destroyGameObject(GameObject obj) {
        Profiler.startTimer(String.format("SpriteMasterRenderer Destroy GameObject - '%s'", obj.getName()));
        for (RenderBatch2D batch : batches)
            if (batch.destroyIfExists(obj))
                break;
        quadsCount--;
        Profiler.stopTimer(String.format("SpriteMasterRenderer Destroy GameObject - '%s'", obj.getName()));
    }

    public static void render(ed_BaseCamera camera) {
        Profiler.startTimer("SpriteMasterRenderer Render");
        glEnable(GL_BLEND);

        EntityRenderer.setShader(shader);
        shader.bind();
        EntityRenderer.uploadSceneData(camera);
        shader.uploadMat4f("u_TransformationMatrix", new Matrix4f().identity());

        for (int i = 0; i < batches.size(); i++) {
            batches.get(i).render();
            if (batches.get(i).isEmpty()) {
                batches.remove(i);
                i--;
            }
        }

        shader.unbind();
        Profiler.stopTimer("SpriteMasterRenderer Render");
    }

    public static void clear() { batches.clear(); }

    public static int getQuadsCount() { return quadsCount; }

    public static int getVerticesCount() { return quadsCount * 4; }

    public static int getIndicesCount() { return quadsCount * 6; }

    public static int getBatchCount() { return batches.size(); }

//    public static void resetStats() { quadsCount = 0; }
}
