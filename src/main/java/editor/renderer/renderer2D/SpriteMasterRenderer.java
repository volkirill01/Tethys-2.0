package editor.renderer.renderer2D;

import editor.entity.GameObject;
import editor.entity.component.components.SpriteRenderer;
import editor.renderer.MasterRenderer;
import editor.renderer.Texture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpriteMasterRenderer {

    private final int MAX_BATCH_SIZE = 1_000;
    private final List<RenderBatch> batches = new ArrayList<>();

    public SpriteMasterRenderer() {

    }

    public void add(GameObject go) {
        if (go.hasComponent(SpriteRenderer.class))
            add(go.getComponent(SpriteRenderer.class));
    }

    private void add(SpriteRenderer sprite) {
        boolean added = false;
        for (RenderBatch batch : this.batches) {
            // TODO IF OBJECTS Z INDEX CHANGES, DELETE IT FROM CURRENT BATCH, AND MOVE TO ANOTHER

            if (batch.isHasRoom() && batch.getZIndex() == sprite.gameObject.getZIndex()) {
                Texture texture = sprite.getTexture();
                if (texture == null || (batch.hasTexture(texture) || batch.hasTextureRoom())) {
                    batch.addSprite(sprite);
                    added = true;
                    break;
                }
            }
        }

        if (!added) {
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE, sprite.gameObject.getZIndex());
            newBatch.start();
            this.batches.add(newBatch);
            newBatch.addSprite(sprite);
            Collections.sort(batches);
        }
    }

    public void render() {
        MasterRenderer.getCurrentShader().use();
        for (RenderBatch batch : this.batches)
            batch.render();
        MasterRenderer.getCurrentShader().detach();
    }
}
