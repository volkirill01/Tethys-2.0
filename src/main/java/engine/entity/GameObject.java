package engine.entity;

import com.google.gson.Gson;
import engine.TestFieldsWindow;
import engine.assets.AssetPool;
import engine.editor.console.Console;
import engine.editor.gui.EditorGUI;
import engine.entity.component.Component;
import engine.entity.component.TagComponent;
import engine.logging.DebugLog;
import engine.profiling.Profiler;
import engine.renderer.renderer2D.SpriteRenderer;
import engine.entity.component.Transform;
import engine.stuff.UUID;
import engine.stuff.utils.EditorGson;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiTreeNodeFlags;

import java.util.ArrayList;
import java.util.List;

public class GameObject {

    private static int ID_COUNTER = 1;
    private transient int incrementedID;

    public transient Transform transform;
    public TagComponent tagComponent = new TagComponent();
    private final List<Component> components = new ArrayList<>();
    private boolean clickable = true;

    private transient boolean doSerialization = true;
    private transient boolean isDeath = false;

    public GameObject(String name) {
        this.tagComponent.name = name;
        this.incrementedID = ID_COUNTER++;
    }

    public <T extends Component> boolean hasComponent(Class<T> componentClass) { return getComponent(componentClass) != null; }

    public <T extends Component> T getComponent(Class<T> componentClass) {
        for (Component c : this.components) {
            if (componentClass.isAssignableFrom(c.getClass()))
                try {
                    return componentClass.cast(c);
                } catch (ClassCastException e) {
                    throw new ClassCastException(String.format("Error of casting component - '%s' to '%s'", componentClass.getName(), c.getClass().getName()));
                }
        }
        return null;
    }

    public <T extends Component> void removeComponent(Class<T> componentClass) {
        DebugLog.log("GameObject:RemoveComponent: ", getName(), ", component: ", componentClass.getName());
        for (int i = 0; i < this.components.size(); i++) {
            Component c = this.components.get(i);

            if (componentClass.isAssignableFrom(c.getClass())) {
                this.components.get(i).destroy();
                this.components.remove(i);
                return;
            }
        }
        DebugLog.logError("GameObject:RemoveComponent: ", getName(), ", not has Component: ", componentClass.getName());
        Console.logError(String.format("GameObject (%s) not has component - '%s'", getName(), componentClass.getName()));
    }

    public void addComponent(Component c) {
        DebugLog.log("GameObject:AddComponent: ", getName(), ", component: ", c.getClass().getName());

        c.generateID();
        this.components.add(c);
        c.gameObject = this;
    }

    public void imgui() {
        setName(EditorGUI.textFieldNoLabel("GameObject_Name_" + this.tagComponent.id.toString(), getName(), "Name", ImGui.getContentRegionAvailX() / 1.5f));
        ImGui.sameLine();

        //<editor-fold desc="Add Component Button">
        float width = (ImGui.getContentRegionAvailX() / 2.0f) - (ImGui.calcTextSize("Add component").x / 2.0f);
        float buttonCenter = ImGui.getCursorScreenPosX() + width + ImGui.getStyle().getFramePaddingX();
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, width, ImGui.getStyle().getFramePaddingY());
        if (ImGui.button("Add component"))
            ImGui.openPopup("ComponentAdder");
        ImGui.popStyleVar();
        //</editor-fold>

        //<editor-fold desc="Add Component Popup">
        ImVec2 popupPosition = new ImVec2(buttonCenter - ImGui.getStateStorage().getFloat(ImGui.getID("addComponentPopupWidth")), ImGui.getWindowPosY() + ImGui.getCursorPosY());
        if (ImGui.isPopupOpen("ComponentAdder"))
            ImGui.setNextWindowPos(popupPosition.x, popupPosition.y);
        if (ImGui.beginPopup("ComponentAdder")) {
            for (Component c : Component.allComponents()) {
                if (!hasComponent(c.getClass()))
                    if (ImGui.menuItem(c.getClass().getSimpleName())) {
                        Gson gson = EditorGson.getGsonBuilder();
                        String gsonString = gson.toJson(c, Component.class);
                        Component copy = gson.fromJson(gsonString, Component.class);
                        addComponent(copy);
                    }
            }
            ImGui.getStateStorage().setFloat(ImGui.getID("addComponentPopupWidth"), ImGui.getContentRegionMaxX());
            ImGui.endPopup();
        }
        //</editor-fold>

        ImGui.setCursorPosY(ImGui.getCursorPosY() + ImGui.getStyle().getItemSpacingY());

        for (Component c : this.components) {
            if (drawComponent(c))
                return;
        }
    }

    private boolean drawComponent(Component c) {
        ImGui.pushID("ComponentGUI_" + c);

        int treeNodeColor = ImGuiCol.FrameBg;
        if (ImGui.isMouseHoveringRect(
                ImGui.getCursorScreenPosX(),
                ImGui.getCursorScreenPosY(),
                ImGui.getCursorScreenPosX() + ImGui.getContentRegionAvailX() + TestFieldsWindow.getFloats[0],
                ImGui.getCursorScreenPosY() + ImGui.getFrameHeight() + TestFieldsWindow.getFloats[1]
        )) {
            treeNodeColor = ImGuiCol.FrameBgHovered;
            if (ImGui.isMouseDown(ImGuiMouseButton.Left))
                treeNodeColor = ImGuiCol.FrameBgActive;
        }

        ImGui.getWindowDrawList().addRectFilled(
                ImGui.getCursorScreenPosX(),
                ImGui.getCursorScreenPosY(),
                ImGui.getCursorScreenPosX() + ImGui.getContentRegionAvailX(),
                ImGui.getCursorScreenPosY() + ImGui.getFrameHeight(),
                ImGui.getColorU32(treeNodeColor)
        );
        // Black line on top of tree node
        ImGui.getWindowDrawList().addRectFilled(
                ImGui.getCursorScreenPosX(),
                ImGui.getCursorScreenPosY(),
                ImGui.getCursorScreenPosX() + ImGui.getContentRegionAvailX(),
                ImGui.getCursorScreenPosY() + ImGui.getFrameHeight() / 10,
                ImGui.getColorU32(ImGui.getStyle().getColor(ImGuiCol.TitleBg).x, ImGui.getStyle().getColor(ImGuiCol.TitleBg).y, ImGui.getStyle().getColor(ImGuiCol.TitleBg).z, ImGui.getStyle().getColor(ImGuiCol.TitleBg).w / 4)
        );

        ImGui.pushStyleColor(ImGuiCol.Header, 0.0f, 0.0f, 0.0f, 0.0f);
        ImGui.pushStyleColor(ImGuiCol.HeaderHovered, 0.0f, 0.0f, 0.0f, 0.0f);
        ImGui.pushStyleColor(ImGuiCol.HeaderActive, 0.0f, 0.0f, 0.0f, 0.0f);
        ImGui.pushStyleColor(ImGuiCol.Border, 0.0f, 0.0f, 0.0f, 0.0f);
        boolean treeNodeOpen = EditorGUI.beginCollapsingHeader(c.getClass().getSimpleName(), (c.getClass() == Transform.class ? ImGuiTreeNodeFlags.DefaultOpen : ImGuiTreeNodeFlags.None) | ImGuiTreeNodeFlags.AllowItemOverlap);
        boolean removeComponent = false;
        ImGui.popStyleColor(4);
        if (ImGui.beginPopupContextItem("Component_Context_Popup_" + c)) {
            if (c.getClass() != Transform.class) {
                if (ImGui.selectable("Remove"))
                    removeComponent = true;
            }
            ImGui.endPopup();
        }

        ImGui.sameLine();
        ImGui.setCursorPosX(ImGui.getCursorStartPosX() + ImGui.getContentRegionMaxX() - ImGui.getFrameHeight() - ImGui.getStyle().getWindowPaddingX());
        if (ImGui.invisibleButton("Component_Context_Button_" + c, ImGui.getFrameHeight(), ImGui.getFrameHeight())) {
            ImGui.openPopup("Component_Context_Popup_" + c);
        }
        ImGui.sameLine();
        ImGui.setCursorPosX(ImGui.getCursorStartPosX() + ImGui.getContentRegionMaxX() - ImGui.getFrameHeight() - ImGui.getStyle().getWindowPaddingX() + ImGui.getStyle().getFramePaddingY());
        ImGui.text("\uEFE1"); // \uEFA2 \uEFE1 \uEFE2

        if (treeNodeOpen) {
            c.imgui();
            EditorGUI.endCollapsingHeader();
        }
        ImGui.popID();

        if (removeComponent) {
            removeComponent(c.getClass());
            return true;
        }
        return false;
    }

    public void start() {
        for (Component c : this.components)
            c.start();
    }

    public void editorUpdate() {
        for (Component c : this.components)
            c.editorUpdate();
    }

    public void update() {
        for (Component c : this.components)
            c.update();
    }

    public void destroy() {
        DebugLog.log("GameObject:Destroy: ", getName());

        Profiler.startTimer(String.format("Destroy GameObject - '%s'", getName()));
        this.isDeath = true;
        for (Component component : this.components)
            component.destroy();
        Profiler.stopTimer(String.format("Destroy GameObject - '%s'", getName()));
    }

    public GameObject copy() {
        DebugLog.log("GameObject:Copy: ", getName());

        Profiler.startTimer(String.format("Copy GameObject - '%s'", getName()));
        Gson gson = EditorGson.getGsonBuilder();

        String objAsGson = gson.toJson(this);
        GameObject copy = gson.fromJson(objAsGson, GameObject.class);

        copy.tagComponent.id = new UUID();
        copy.generateIncrementedID();
        for (Component c : copy.components)
            c.generateID();

        if (copy.hasComponent(SpriteRenderer.class)) {
            SpriteRenderer renderer = copy.getComponent(SpriteRenderer.class);
            if (renderer.getSprite().getTexture() != null)
                renderer.getSprite().setTexture(AssetPool.getTexture(renderer.getSprite().getTexture().getFilepath()));
        }
        Profiler.stopTimer(String.format("Copy GameObject - '%s'", getName()));
        return copy;
    }

    public String getName() { return this.tagComponent.name; }

    public void setName(String name) { this.tagComponent.name = name; }

    public void setSerialize(boolean serialize) { this.doSerialization = serialize; }

    public boolean isDoSerialization() { return this.doSerialization; }

    public static void init(int maxID) { ID_COUNTER = maxID; }

    public long getUUID() { return this.tagComponent.id.get(); }

    public int getIncrementedID() { return this.incrementedID; }

    private void generateIncrementedID() { this.incrementedID = ID_COUNTER++; }

    public List<Component> getAllComponents() { return this.components; }

    public boolean isClickable() { return this.clickable; }

    public void setClickable(boolean clickable) { this.clickable = clickable; }

    public boolean isDeath() { return this.isDeath; }
}
