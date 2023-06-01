package engine.renderer.renderer2D;

import engine.assets.AssetPool;
import engine.entity.GameObject;
import engine.profiling.Profiler;
import engine.renderer.EntityRenderer;
import engine.renderer.Texture2D;
import engine.renderer.camera.ed_BaseCamera;
import engine.renderer.renderer2D.batches.Circle_RenderBatch2D;
import engine.renderer.renderer2D.batches.RenderBatch2D;
import engine.renderer.renderer2D.batches.Sprite_RenderBatch2D;
import engine.renderer.shader.Shader;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.glEnable;

public class MasterRenderer2D {

    private static final int MAX_BATCH_SIZE = 10_000;
    private static final List<RenderBatch2D> batches = new ArrayList<>();

    private static int quadsCount = 0;

    private static final Shader spriteShader = AssetPool.getShader("editorFiles/shaders/2D/defaultSprite.glsl", true);
    private static final Shader circleShader = AssetPool.getShader("editorFiles/shaders/2D/circle.glsl", true);

    public static void add(GameObject go) { add(go.getComponent(ed_Renderer.class)); }

    private static void add(ed_Renderer renderer) {
        Profiler.startTimer(String.format("SpriteMasterRenderer Add GameObject. Obj Name - '%s'", renderer.gameObject.getName()));
        boolean added = false;
        for (RenderBatch2D batch : batches) {
            if (batch.getClass() == Sprite_RenderBatch2D.class && renderer.getClass() == SpriteRenderer.class) {
                Sprite_RenderBatch2D _batch = (Sprite_RenderBatch2D) batch;

                if (_batch.hasRoom() && _batch.getZIndex() == renderer.gameObject.transform.getZIndex()) {
                    Texture2D texture = ((SpriteRenderer) renderer).getSprite().getTexture();
                    if (texture == null || (_batch.hasTexture(texture) || _batch.hasTextureRoom())) {
                        _batch.addQuad(renderer);
                        added = true;
                        break;
                    }
                }
            } else if (batch.getClass() == Circle_RenderBatch2D.class && renderer.getClass() == ShapeRenderer2D.class) {
                Circle_RenderBatch2D _batch = (Circle_RenderBatch2D) batch;

                if (_batch.hasRoom() && _batch.getZIndex() == renderer.gameObject.transform.getZIndex()) {
                    _batch.addQuad(renderer);
                    added = true;
                    break;
                }
            }
        }

        if (!added) {
            if (renderer.getClass() == SpriteRenderer.class) {
                Sprite_RenderBatch2D newBatch = new Sprite_RenderBatch2D(MAX_BATCH_SIZE, renderer.gameObject.transform.getZIndex());
                newBatch.init();
                batches.add(newBatch);
                newBatch.addQuad(renderer);
                Collections.sort(batches);
            } else if (renderer.getClass() == ShapeRenderer2D.class) {
                Circle_RenderBatch2D newBatch = new Circle_RenderBatch2D(MAX_BATCH_SIZE, renderer.gameObject.transform.getZIndex());
                newBatch.init();
                batches.add(newBatch);
                newBatch.addQuad(renderer);
                Collections.sort(batches);
            }
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

        for (int i = 0; i < batches.size(); i++) {
            if (batches.get(i).getClass() == Sprite_RenderBatch2D.class) { // TODO REDUCE SWAP OF SHADERS
                EntityRenderer.setShader(spriteShader);
                spriteShader.bind();
                EntityRenderer.uploadSceneData(camera);
                spriteShader.uploadMat4f("u_TransformationMatrix", new Matrix4f().identity());
            } else if (batches.get(i).getClass() == Circle_RenderBatch2D.class) {
                EntityRenderer.setShader(circleShader);
                circleShader.bind();
                EntityRenderer.uploadSceneData(camera);
                circleShader.uploadMat4f("u_TransformationMatrix", new Matrix4f().identity());
            }

            batches.get(i).render();
            if (batches.get(i).isEmpty()) {
                batches.remove(i);
                i--;
            }

            if (batches.size() != 0) {
                if (batches.get(i).getClass() == Sprite_RenderBatch2D.class)
                    spriteShader.unbind();
                else if (batches.get(i).getClass() == Circle_RenderBatch2D.class)
                    circleShader.unbind();
            }
        }

        Profiler.stopTimer("SpriteMasterRenderer Render");
    }

    public static void clear() { batches.clear(); }

    public static int getQuadsCount() { return quadsCount; }

    public static int getVerticesCount() { return quadsCount * 4; }

    public static int getIndicesCount() { return quadsCount * 6; }

    public static int getBatchCount() { return batches.size(); }

//    public static void resetStats() { quadsCount = 0; }
}
