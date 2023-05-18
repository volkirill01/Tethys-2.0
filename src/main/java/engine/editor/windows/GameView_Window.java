package engine.editor.windows;

import engine.assets.AssetPool;
import engine.editor.gui.EditorImGuiWindow;
import engine.editor.gui.EngineGuiLayer;
import engine.editor.gui.ImGuiLayer_old;
import engine.entity.GameObject;
import engine.eventListeners.MouseListener;
import engine.observers.EventSystem;
import engine.observers.Observer;
import engine.observers.events.Event;
import engine.observers.events.EventType;
import engine.scenes.SceneManager;
import engine.stuff.Window;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;

public class GameView_Window extends EditorImGuiWindow implements Observer {

    public GameView_Window() {
        super("\uEA32 Game", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);
        EventSystem.addObserver(this);
    }

    @Override
    public void drawWindow() {
        ImGui.setCursorPos(ImGui.getCursorPosX(), ImGui.getCursorPosY());
        ImVec2 viewportSize = getLargestSizeForViewport();
        ImVec2 viewportPos = getCenteredPositionForViewport(viewportSize);
        ImGui.setCursorPos(viewportPos.x, viewportPos.y);

        ImVec2 topLeft = new ImVec2();
        ImGui.getCursorScreenPos(topLeft);

        int textureID = SceneManager.getCurrentScene().getMainCamera() != null ? SceneManager.getCurrentScene().getMainCamera().getOutputFob().getColorTexture() : AssetPool.getTexture("editorFiles/images/noMainCameraInScene.png").getTextureID();
        ImVec2 start = new ImVec2();
        ImGui.getCursorPos(start);
        ImGui.image(textureID, viewportSize.x, viewportSize.y, 0, 1, 1, 0);

        MouseListener.setGameViewportPos(topLeft);
        MouseListener.setGameViewportSize(viewportSize);
    }

    private ImVec2 getLargestSizeForViewport() {
        ImVec2 viewportSize = ImGui.getContentRegionAvail();

        float aspectWidth = viewportSize.x;
        float aspectHeight = aspectWidth / Window.getTargetAspectRatio();

        if (aspectHeight > viewportSize.y) {
            // We must switch to pillar box mode (black bars on left and right sides of screen)
            aspectHeight = viewportSize.y;
            aspectWidth = aspectHeight * Window.getTargetAspectRatio();
        }

        return new ImVec2(aspectWidth, aspectHeight);
    }

    private ImVec2 getCenteredPositionForViewport(ImVec2 aspectSize) {
        ImVec2 viewportSize = ImGui.getContentRegionAvail();

        float viewportPositionX = (viewportSize.x / 2.0f) - (aspectSize.x / 2.0f);
        float viewportPositionY = (viewportSize.y / 2.0f) - (aspectSize.y / 2.0f);

        return new ImVec2(viewportPositionX + ImGui.getCursorPosX(), viewportPositionY + ImGui.getCursorPosY());
    }

    @Override
    public void onNotify(GameObject object, Event event) {
        if (event.type == EventType.GameEngine_StartPlay)
            EngineGuiLayer.selectWindow(this);
    }
}
