package editor.renderer;

import editor.entity.GameObject;
import editor.entity.component.components.SpriteRenderer;

import java.util.ArrayList;
import java.util.List;

public class SpriteMasterRenderer {

    private final int MAX_BATCH_SIZE = 1000;
    private List<RenderBatch> batches = new ArrayList<>();

    public SpriteMasterRenderer() {

    }

    public void add(GameObject go) {
        if (go.hasComponent(SpriteRenderer.class))
            add(go.getComponent(SpriteRenderer.class));
    }

    private void add(SpriteRenderer sprite) {
        boolean added = false;
        for (RenderBatch batch : this.batches) {
            if (batch.isHasRoom()) {
                batch.addSprite(sprite);
                added = true;
                break;
            }
        }

        if (!added) {
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE);
            newBatch.start();
            this.batches.add(newBatch);
            newBatch.addSprite(sprite);
        }
    }

    public void render() {
        for (RenderBatch batch : this.batches)
            batch.render();
    }
}
