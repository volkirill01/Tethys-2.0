package editor.renderer.renderer2D;

import editor.entity.GameObject;
import editor.renderer.EntityRenderer;
import editor.renderer.Texture;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpriteMasterRenderer {

    private static final int MAX_BATCH_SIZE = 1_000;
    private final List<RenderBatch> batches = new ArrayList<>();

    public void add(GameObject go) { add(go.getComponent(SpriteRenderer.class)); }

    private void add(SpriteRenderer renderer) {
        boolean added = false;
        for (RenderBatch batch : this.batches) {
            if (batch.hasRoom() && batch.getZIndex() == renderer.gameObject.transform.getZIndex()) {
                Texture texture = renderer.getTexture();
                if (texture == null || (batch.hasTexture(texture) || batch.hasTextureRoom())) {
                    batch.addSprite(renderer);
                    added = true;
                    break;
                }
            }
        }

        if (!added) {
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE, renderer.gameObject.transform.getZIndex(), this);
            newBatch.start();
            this.batches.add(newBatch);
            newBatch.addSprite(renderer);
            Collections.sort(batches);
        }
    }

    public void destroyGameObject(GameObject obj) {
        for (RenderBatch batch : this.batches)
            if (batch.destroyIfExists(obj))
                return;
    }

    public void render(Matrix4f projectionMatrix, Matrix4f viewMatrix) {
        EntityRenderer.getCurrentShader().bind();
        for (int i = 0; i < this.batches.size(); i++)
            this.batches.get(i).render(projectionMatrix, viewMatrix);
        EntityRenderer.getCurrentShader().unbind();
    }
}
