package editor.scenes;

import TMP_MARIO_STUFF.Prefabs;
import editor.assets.AssetPool;
import editor.audio.Sound;
import editor.entity.GameObject;
import editor.entity.component.components.SpriteRenderer;
import editor.eventListeners.Input;
import editor.eventListeners.MouseListener;
import editor.renderer.renderer2D.sprite.Sprite;
import editor.renderer.renderer2D.sprite.SpriteSheet;
import editor.stuff.Settings;
import editor.stuff.inputActions.MouseControls;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;

import java.util.Collection;

public class EngineSceneInitializer extends SceneInitializer {

    private SpriteSheet sprites;

    @Override
    public void init(Scene scene) { sprites = AssetPool.getSpriteSheet("Assets/decorationsAndBlocks.png"); }

    @Override
    public void loadResources(Scene scene) {
        AssetPool.addSpriteSheet("Assets/decorationsAndBlocks.png",
                new SpriteSheet(AssetPool.getTexture("Assets/decorationsAndBlocks.png"),
                        16, 16, 81, 0, 0, 0, 0));

        AssetPool.addSpriteSheet("editorFiles/gizmos.png",
                new SpriteSheet(AssetPool.getTexture("editorFiles/gizmos.png"),
                        24, 48, 3, 0, 0, 0, 0));

        AssetPool.addSound("Assets/sounds/main-theme-overworld.ogg", true);
        AssetPool.addSound("Assets/sounds/flagpole.ogg", false);
        AssetPool.addSound("Assets/sounds/break_block.ogg", false);
        AssetPool.addSound("Assets/sounds/bump.ogg", false);
        AssetPool.addSound("Assets/sounds/coin.ogg", false);
        AssetPool.addSound("Assets/sounds/gameover.ogg", false);
        AssetPool.addSound("Assets/sounds/jump-small.ogg", false);
        AssetPool.addSound("Assets/sounds/mario_die.ogg", false);
        AssetPool.addSound("Assets/sounds/pipe.ogg", false);
        AssetPool.addSound("Assets/sounds/powerup.ogg", false);
        AssetPool.addSound("Assets/sounds/powerup_appears.ogg", false);
        AssetPool.addSound("Assets/sounds/stage_clear.ogg", false);
        AssetPool.addSound("Assets/sounds/stomp.ogg", false);
        AssetPool.addSound("Assets/sounds/kick.ogg", false);
        AssetPool.addSound("Assets/sounds/invincible.ogg", false);

        for (GameObject go : scene.getAllGameObjects()) {
            if (go.hasComponent(SpriteRenderer.class)) {
                SpriteRenderer renderer = go.getComponent(SpriteRenderer.class);
                if (renderer.getTexture() != null)
                    // Load Textures from AssetPool and replacing saved textures because Gson loads Textures and creates separate Object, with broken data
                    renderer.setTexture(AssetPool.getTexture(renderer.getTexture().getFilepath()));
            }
        }
    }

    @Override
    public void imgui() {
        ImGui.begin("Test Window");

        ImVec2 windowPos = new ImVec2();
        ImGui.getWindowPos(windowPos);
        ImVec2 windowSize = new ImVec2();
        ImGui.getWindowContentRegionMax(windowSize);
        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        float windowX2 = windowPos.x + windowSize.x;
        ImGui.beginTabBar("Objects");

        if (ImGui.beginTabItem("Tiles")) {
            for (int i = 0; i < sprites.size(); i++) {
                Sprite sprite = sprites.getSprite(i);
                float spriteWidth = sprite.getWidth() * 3;
                float spriteHeight = sprite.getHeight() * 3;
                int id = sprite.getTextureID();
                Vector2f[] texCoordinates = sprite.getTextureCoordinates();

                ImGui.pushID("TileButton_" + i);
                if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoordinates[2].x, texCoordinates[0].y, texCoordinates[0].x, texCoordinates[2].y)) {
                    GameObject obj = Prefabs.generateSpriteObject(sprite, Settings.GRID_WIDTH, Settings.GRID_HEIGHT);
                    MouseControls.pickUpObject(obj);
                }
                ImGui.popID();

                ImVec2 lasButtonPos = new ImVec2();
                ImGui.getItemRectMax(lasButtonPos);
                float lastButtonX2 = lasButtonPos.x;
                float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
                if (i + 1 < sprites.size() && nextButtonX2 < windowX2)
                    ImGui.sameLine();
            }
            ImGui.endTabItem();
        }
        if (ImGui.beginTabItem("Sounds")) {
            Collection<Sound> sounds = AssetPool.getAllSounds();

            int i = 0;
            for (Sound sound : sounds) {
                String soundName = sound.getFilepath().split("/")[sound.getFilepath().split("/").length - 1];

                float spriteWidth = ImGui.calcTextSize(soundName).x + 16.0f * 2;
                float spriteHeight = 16.0f * 3;
                int id = ImGui.getID(sound.getFilepath());

                ImGui.pushID("SoundButton_" + id);
                if (ImGui.button(soundName, spriteWidth, spriteHeight)) {
                    if (sound.isPlaying())
                        sound.stop();
                    else
                        sound.play();
                }
                ImGui.popID();

                ImVec2 lasButtonPos = new ImVec2();
                ImGui.getItemRectMax(lasButtonPos);
                float lastButtonX2 = lasButtonPos.x;
                float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
                if (i + 1 < sounds.size() && nextButtonX2 < windowX2)
                    ImGui.sameLine();
                i++;
            }
            ImGui.endTabItem();
        }
        ImGui.endTabBar();

        ImGui.end();
    }
}
