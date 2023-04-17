package editor.gui;

import editor.editor.windows.SceneView_Window;
import editor.eventListeners.KeyCode;
import editor.eventListeners.KeyListener;
import editor.eventListeners.MouseListener;
import editor.scenes.SceneManager;
import editor.stuff.Window;
import editor.stuff.utils.Time;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.callback.ImStrConsumer;
import imgui.callback.ImStrSupplier;
import imgui.flag.*;
import imgui.gl3.ImGuiImplGl3;
import imgui.type.ImBoolean;

import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;

public class ImGuiLayer {

    private final long glfwWindow;

    // Mouse cursors provided by GLFW
    private final long[] mouseCursors = new long[ImGuiMouseCursor.COUNT];

    // LWJGL3 renderer (SHOULD be initialized)
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

    public ImGuiLayer(long glfwWindow) { this.glfwWindow = glfwWindow; }

    // Initialize Dear ImGui.
    public void initImGui() {
        // IMPORTANT!!
        // This line is critical for Dear ImGui to work.
        ImGui.createContext();

        // ------------------------------------------------------------
        // Initialize ImGuiIO config
        final ImGuiIO io = ImGui.getIO();

        io.setIniFilename("gui.ini");
        io.setConfigFlags(ImGuiConfigFlags.NavEnableKeyboard); // Navigation with keyboard
        io.setConfigFlags(ImGuiConfigFlags.DockingEnable);
        io.setBackendFlags(ImGuiBackendFlags.HasMouseCursors); // Mouse cursors to display while resizing windows etc.
        io.setBackendPlatformName("imgui_java_impl_glfw");

        // ------------------------------------------------------------
        // Keyboard mapping. ImGui will use those indices to peek into the io.KeysDown[] array.
        final int[] keyMap = new int[ImGuiKey.COUNT];
        keyMap[ImGuiKey.Tab] = KeyCode.Tab;
        keyMap[ImGuiKey.LeftArrow] = KeyCode.Arrow_Left;
        keyMap[ImGuiKey.RightArrow] = KeyCode.Arrow_Right;
        keyMap[ImGuiKey.UpArrow] = KeyCode.Arrow_Up;
        keyMap[ImGuiKey.DownArrow] = KeyCode.Arrow_Down;
        keyMap[ImGuiKey.PageUp] = KeyCode.Page_Up;
        keyMap[ImGuiKey.PageDown] = KeyCode.Page_Down;
        keyMap[ImGuiKey.Home] = KeyCode.Home;
        keyMap[ImGuiKey.End] = KeyCode.End;
        keyMap[ImGuiKey.Insert] = KeyCode.Insert;
        keyMap[ImGuiKey.Delete] = KeyCode.Insert;
        keyMap[ImGuiKey.Backspace] = KeyCode.Backspace;
        keyMap[ImGuiKey.Space] = KeyCode.Space;
        keyMap[ImGuiKey.Enter] = KeyCode.Enter;
        keyMap[ImGuiKey.Escape] = KeyCode.Escape;
        keyMap[ImGuiKey.KeyPadEnter] = KeyCode.KP_Enter;
        keyMap[ImGuiKey.A] = KeyCode.A;
        keyMap[ImGuiKey.C] = KeyCode.C;
        keyMap[ImGuiKey.V] = KeyCode.V;
        keyMap[ImGuiKey.X] = KeyCode.X;
        keyMap[ImGuiKey.Y] = KeyCode.Y;
        keyMap[ImGuiKey.Z] = KeyCode.Z;
        io.setKeyMap(keyMap);

        // ------------------------------------------------------------
        // Mouse cursors mapping
        mouseCursors[ImGuiMouseCursor.Arrow] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
        mouseCursors[ImGuiMouseCursor.TextInput] = glfwCreateStandardCursor(GLFW_IBEAM_CURSOR);
        mouseCursors[ImGuiMouseCursor.ResizeAll] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
        mouseCursors[ImGuiMouseCursor.ResizeNS] = glfwCreateStandardCursor(GLFW_VRESIZE_CURSOR);
        mouseCursors[ImGuiMouseCursor.ResizeEW] = glfwCreateStandardCursor(GLFW_HRESIZE_CURSOR);
        mouseCursors[ImGuiMouseCursor.ResizeNESW] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
        mouseCursors[ImGuiMouseCursor.ResizeNWSE] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
        mouseCursors[ImGuiMouseCursor.Hand] = glfwCreateStandardCursor(GLFW_HAND_CURSOR);
        mouseCursors[ImGuiMouseCursor.NotAllowed] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);

        // ------------------------------------------------------------
        // GLFW callbacks to handle user input

        glfwSetKeyCallback(glfwWindow, (w, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS)
                io.setKeysDown(key, true);
            else if (action == GLFW_RELEASE)
                io.setKeysDown(key, false);

            io.setKeyCtrl(io.getKeysDown(GLFW_KEY_LEFT_CONTROL) || io.getKeysDown(GLFW_KEY_RIGHT_CONTROL));
            io.setKeyShift(io.getKeysDown(GLFW_KEY_LEFT_SHIFT) || io.getKeysDown(GLFW_KEY_RIGHT_SHIFT));
            io.setKeyAlt(io.getKeysDown(GLFW_KEY_LEFT_ALT) || io.getKeysDown(GLFW_KEY_RIGHT_ALT));
            io.setKeySuper(io.getKeysDown(GLFW_KEY_LEFT_SUPER) || io.getKeysDown(GLFW_KEY_RIGHT_SUPER));

            if (!io.getWantCaptureKeyboard())
                KeyListener.keyCallback(w, key, scancode, action, mods);
        });

        glfwSetCharCallback(glfwWindow, (w, c) -> {
            if (c != GLFW_KEY_DELETE)
                io.addInputCharacter(c);
        });

        glfwSetMouseButtonCallback(glfwWindow, (w, button, action, mods) -> {
            final boolean[] mouseDown = new boolean[5];

            mouseDown[0] = button == GLFW_MOUSE_BUTTON_1 && action != GLFW_RELEASE;
            mouseDown[1] = button == GLFW_MOUSE_BUTTON_2 && action != GLFW_RELEASE;
            mouseDown[2] = button == GLFW_MOUSE_BUTTON_3 && action != GLFW_RELEASE;
            mouseDown[3] = button == GLFW_MOUSE_BUTTON_4 && action != GLFW_RELEASE;
            mouseDown[4] = button == GLFW_MOUSE_BUTTON_5 && action != GLFW_RELEASE;

            io.setMouseDown(mouseDown);

            if (!io.getWantCaptureMouse() && mouseDown[1])
                ImGui.setWindowFocus(null);

            if (!io.getWantCaptureMouse() || SceneView_Window.getWantCaptureMouse())
                MouseListener.mouseButtonCallback(w, button, action, mods);
        });

        glfwSetScrollCallback(glfwWindow, (w, xOffset, yOffset) -> {
            io.setMouseWheelH(io.getMouseWheelH() + (float) xOffset);
            io.setMouseWheel(io.getMouseWheel() + (float) yOffset);

            if (!io.getWantCaptureMouse())
                MouseListener.mouseScrollCallback(w, io.getMouseWheelH() + (float) xOffset, io.getMouseWheel() + (float) yOffset);
        });

        io.setSetClipboardTextFn(new ImStrConsumer() {
            @Override
            public void accept(final String s) {
                glfwSetClipboardString(glfwWindow, s);
            }
        });

        io.setGetClipboardTextFn(new ImStrSupplier() {
            @Override
            public String get() {
                final String clipboardString = glfwGetClipboardString(glfwWindow);
                return Objects.requireNonNullElse(clipboardString, "");
            }
        });

        GuiFont.init(io);

        // Method initializes LWJGL3 renderer.
        // This method SHOULD be called after you've initialized your ImGui configuration (fonts and so on).
        // ImGui context should be created as well.
        imGuiGl3.init("#version 330 core");
    }

    public void update() {
        startFrame(Time.deltaTime());
        ImGui.newFrame();

        setupDockSpace();
        // TODO DELETE DRAWING OF CURRENT SCENE
        SceneManager.getCurrentScene().sceneImgui();
        ImGui.showDemoWindow();

        SceneView_Window.imgui(); // TODO REPLACE THIS WITH NON STATIC CLASS, ADD MULTIPLE WINDOW DUPLICATES
        ImGui.end(); // End of Editor DockSpace

        ImGui.render();
        endFrame();
    }

    private void startFrame(final float deltaTime) {
        // Get window properties and mouse position
        float[] winWidth = { Window.getWidth() };
        float[] winHeight = { Window.getHeight() };
        double[] mousePosX = { 0 };
        double[] mousePosY = { 0 };
        glfwGetCursorPos(glfwWindow, mousePosX, mousePosY);

        // We SHOULD call those methods to update Dear ImGui state for the current frame
        final ImGuiIO io = ImGui.getIO();
        io.setDisplaySize(winWidth[0], winHeight[0]);
        io.setDisplayFramebufferScale(1.0f, 1.0f);
        io.setMousePos((float) mousePosX[0], (float) mousePosY[0]);
        io.setDeltaTime(deltaTime);

        // Update the mouse cursor
        final int imguiCursor = ImGui.getMouseCursor();
        glfwSetCursor(glfwWindow, mouseCursors[imguiCursor]);
        glfwSetInputMode(glfwWindow, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
    }

    private void endFrame() {
        // After Dear ImGui prepared a draw data, we use it in the LWJGL3 renderer.
        // At that moment ImGui will be rendered to the current OpenGL context.
        imGuiGl3.render(ImGui.getDrawData());
    }

    // If you want to clean a room after yourself - do it by yourself
    private void destroyImGui() {
        imGuiGl3.dispose();
        ImGui.destroyContext();
    }

    private void setupDockSpace() {
        int windowFlags = ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoDocking | ImGuiWindowFlags.NoTitleBar |
                ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove |
                ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;

        ImGui.setNextWindowPos(0.0f, 0.0f, ImGuiCond.Always);
        ImGui.setNextWindowSize(Window.getWidth(), Window.getHeight()); // Set DockSpace window size
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0f, 0.0f);

        ImGui.begin("Editor DeckSpace", new ImBoolean(true), windowFlags);
        ImGui.popStyleVar(3);

        // Dockspace
        ImGui.dockSpace(ImGui.getID("DeckSpace"));
    }
}
