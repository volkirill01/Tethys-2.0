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

            if (batch.hasRoom() && batch.getZIndex() == sprite.gameObject.transform.getZIndex()) {
                Texture texture = sprite.getTexture();
                if (texture == null || (batch.hasTexture(texture) || batch.hasTextureRoom())) {
                    batch.addSprite(sprite);
                    added = true;
                    break;
                }
            }
        }

        if (!added) {
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE, sprite.gameObject.transform.getZIndex(), this);
            newBatch.start();
            this.batches.add(newBatch);
            newBatch.addSprite(sprite);
            Collections.sort(batches);
        }
    }

    public void destroyGameObject(GameObject obj) {
        if (!obj.hasComponent(SpriteRenderer.class)) return;
        for (RenderBatch batch :
                this.batches) {
            if (batch.destroyIfExists(obj))
                return;
        }
    }

    public void render() {
        MasterRenderer.getCurrentShader().use();
        for (int i = 0; i < this.batches.size(); i++)
            this.batches.get(i).render();
        MasterRenderer.getCurrentShader().detach();
    }
}
