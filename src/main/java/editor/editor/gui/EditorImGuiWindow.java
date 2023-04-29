package editor.editor.gui;

import imgui.ImGui;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;

public abstract class EditorImGuiWindow {

    protected int id;
    protected String windowTitle;
    protected int windowFlags;

    private ImBoolean isOpen = new ImBoolean(true);
    private boolean isVisible = true;
    private boolean isSelected = false;
    private boolean isHover = false;
    private boolean isClicked = false;

    public EditorImGuiWindow(String windowTitle) {
        this.id = ImGuiLayer.getNextWindowId() - 1;
        this.windowTitle = windowTitle;
        this.windowFlags = ImGuiWindowFlags.None;
    }

    public EditorImGuiWindow(String windowTitle, int windowFlags) {
        this.id = ImGuiLayer.getNextWindowId() - 1;
        this.windowTitle = windowTitle;
        this.windowFlags = windowFlags;
    }

    public void imgui(ImBoolean isOpen) {
        this.isOpen = isOpen;

        if (!this.windowTitle.equals("__Custom__")) {
            if (ImGui.begin(" " + this.windowTitle + " ##" + this.id, this.isOpen, this.windowFlags)) {
//              ImGui.text("" + this.id);
                if (ImGui.isItemHovered())
                    if (ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left)) {
                        if (ImGuiLayer.getWindowOnFullscreen() == null)
                            setOnFullscreen(this);
                        else
                            setOnFullscreen(null);
                    }

                if (ImGui.isWindowHovered()) {
                    if (ImGui.isMouseClicked(ImGuiMouseButton.Right) || ImGui.isMouseClicked(ImGuiMouseButton.Middle))
                        ImGui.setWindowFocus();

                    this.isHover = true;
                    if (ImGui.isMouseClicked(ImGuiMouseButton.Left)) {
                        this.isClicked = true;
                        ImGuiLayer.selectWindow(this);
                    } else
                        this.isClicked = false;
                }

                this.isSelected = ImGui.isWindowFocused();

                this.isVisible = true;
                drawWindow();
            } else {
                this.isVisible = false;
                this.isHover = false;
                this.isClicked = false;
            }
            ImGui.end();
        } else {
            if (ImGui.isItemHovered())
                if (ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left)) {
                    if (ImGuiLayer.getWindowOnFullscreen() == null)
                        setOnFullscreen(this);
                    else
                        setOnFullscreen(null);
                }

            if (ImGui.isWindowHovered()) {
                if (ImGui.isMouseClicked(ImGuiMouseButton.Right) || ImGui.isMouseClicked(ImGuiMouseButton.Middle))
                    ImGui.setWindowFocus();

                this.isHover = true;
                if (ImGui.isMouseClicked(ImGuiMouseButton.Left)) {
                    this.isClicked = true;
                    ImGuiLayer.selectWindow(this);
                } else
                    this.isClicked = false;
            }

            this.isSelected = ImGui.isWindowFocused();

            this.isVisible = true;
            drawWindow();
        }
    }

    public abstract void drawWindow();

    public int getId() { return this.id; }

    public void setOnFullscreen(EditorImGuiWindow window) { ImGuiLayer.setWindowOnFullscreen(window); }

    public void close() { this.isOpen.set(false); }

    public boolean isOpen() { return this.isOpen.get(); }

    public boolean isVisible() { return this.isVisible; }

    public boolean isSelected() { return this.isSelected; }

    public boolean isHover() { return this.isHover; }

    public boolean isClicked() { return this.isClicked; }
}
