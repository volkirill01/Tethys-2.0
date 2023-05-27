package engine.editor.gui;

import engine.profiling.Profiler;
import imgui.ImGui;
import imgui.ImVec4;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;

public abstract class EditorGuiWindow {

    protected int id;
    protected final String windowTitle;
    protected final String actualWindowTitle;
    protected final int windowFlags;

    private ImBoolean isOpen = new ImBoolean(true);
    private boolean isVisible = true;
    private boolean isSelected = false;
    private boolean isHover = false;
    private boolean isClicked = false;

    public EditorGuiWindow(String windowTitle) {
        this.id = EngineGuiLayer.getNextWindowId() - 1;
        this.windowTitle = windowTitle;
        this.windowFlags = ImGuiWindowFlags.None;

        this.actualWindowTitle = " " + this.windowTitle + " ##" + this.id;
    }

    public EditorGuiWindow(String windowTitle, int windowFlags) {
        this.id = EngineGuiLayer.getNextWindowId() - 1;
        this.windowTitle = windowTitle;
        this.windowFlags = windowFlags;

        this.actualWindowTitle = " " + this.windowTitle + " ##" + this.id;
    }

    public void imgui(ImBoolean isOpen) {
        Profiler.startTimer("Draw EditorImGui Window");
        this.isOpen = isOpen;
        this.isClicked = false;

        ImVec4 tabColor = ImGui.getStyle().getColor(ImGuiCol.Tab);
        ImVec4 tabHoverColor = ImGui.getStyle().getColor(ImGuiCol.TabHovered);
        ImVec4 tabUnfocusedColor = ImGui.getStyle().getColor(ImGuiCol.TabUnfocusedActive);

        if (!this.windowTitle.equals("__Custom__")) {
            ImGui.pushStyleColor(ImGuiCol.Tab, 0.0f, 0.0f, 0.0f, 0.0f);
            ImGui.pushStyleColor(ImGuiCol.TabUnfocused, 0.0f, 0.0f, 0.0f, 0.0f);
            ImGui.pushStyleColor(ImGuiCol.TabUnfocusedActive, tabUnfocusedColor.x, tabUnfocusedColor.y, tabUnfocusedColor.z, tabUnfocusedColor.w);
            ImGui.pushStyleColor(ImGuiCol.TabActive, tabColor.x, tabColor.y, tabColor.z, tabColor.w);
            ImGui.pushStyleColor(ImGuiCol.TabHovered, tabHoverColor.x, tabHoverColor.y, tabHoverColor.z, tabHoverColor.w);
            if (ImGui.begin(this.actualWindowTitle, this.isOpen, this.windowFlags)) {
                ImGui.popStyleColor(5);
                if (ImGui.isItemHovered())
                    if (ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left)) {
                        if (EngineGuiLayer.getWindowOnFullscreen() == null)
                            setOnFullscreen(this);
                        else
                            setOnFullscreen(null);
                    }
//                ImGui.text("" + this.id);

                if (ImGui.isWindowHovered()) {
                    if (ImGui.isMouseClicked(ImGuiMouseButton.Right) || ImGui.isMouseClicked(ImGuiMouseButton.Middle))
                        ImGui.setWindowFocus();

                    this.isHover = true;
                    if (ImGui.isMouseClicked(ImGuiMouseButton.Left)) {
                        this.isClicked = true;
                        EngineGuiLayer.selectWindow(this);
                    } else
                        this.isClicked = false;
                } else
                    this.isHover = false;

                this.isSelected = ImGui.isWindowFocused();

                this.isVisible = true;
                drawWindow();
            } else {
                ImGui.popStyleColor(5);
                this.isVisible = false;
                this.isHover = false;
                this.isClicked = false;
            }
            ImGui.end();
        } else {
            if (ImGui.isItemHovered())
                if (ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left)) {
                    if (EngineGuiLayer.getWindowOnFullscreen() == null)
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
                    EngineGuiLayer.selectWindow(this);
                } else
                    this.isClicked = false;
            }

            this.isSelected = ImGui.isWindowFocused();

            this.isVisible = true;
            drawWindow();
        }
        Profiler.stopTimer("Draw EditorImGui Window");
    }

    public abstract void drawWindow();

    public int getId() { return this.id; }

    public void setOnFullscreen(EditorGuiWindow window) { EngineGuiLayer.setWindowOnFullscreen(window); }

    public void close() { this.isOpen.set(false); }

    public boolean isOpen() { return this.isOpen.get(); }

    public boolean isVisible() { return this.isVisible; }

    public boolean isSelected() { return this.isSelected; }

    public boolean isHover() { return this.isHover; }

    public boolean isClicked() { return this.isClicked; }
}
