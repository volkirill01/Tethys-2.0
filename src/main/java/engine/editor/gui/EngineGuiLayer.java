package engine.editor.gui;

import engine.TestFieldsWindow;
import engine.editor.console.Console_Window;
import engine.editor.windows.*;
import engine.eventListeners.Input;
import engine.eventListeners.KeyCode;
import engine.eventListeners.KeyListener;
import engine.eventListeners.MouseListener;
import engine.layerStack.Layer;
import engine.observers.events.Event;
import engine.profiling.Profiler;
import engine.profiling.Profiler_Window;
import engine.scenes.SceneManager;
import engine.stuff.Maths;
import engine.stuff.Settings;
import engine.stuff.Window;
import engine.stuff.utils.Time;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.callback.ImStrConsumer;
import imgui.callback.ImStrSupplier;
import imgui.flag.*;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImBoolean;
import imgui.type.ImInt;

import java.util.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL30.*;

public class EngineGuiLayer extends Layer {

    // Mouse cursors provided by GLFW
    private final long[] mouseCursors = new long[ImGuiMouseCursor.COUNT];

    // LWJGL3 renderer (SHOULD be initialized)
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();

    private static int windowId = 0;
    private static final Map<Integer, Map.Entry<ImBoolean, EditorImGuiWindow>> windows = new LinkedHashMap<>();
    private static EditorImGuiWindow windowOnFullscreen = null;

    @Override
    public void init() {
        createEditorWindows();

        Profiler.startTimer("Init ImGui");
        // IMPORTANT!!
        // This line is critical for Dear ImGui to work.
        ImGui.createContext();

        // ------------------------------------------------------------
        // Initialize ImGuiIO config
        final ImGuiIO io = ImGui.getIO();

        io.setIniFilename("gui.ini");
//        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);   // Navigation with keyboard
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);       // Enable Docking for windows
        // TODO FIX IMGUI VIEWPORTS
//        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);     // Enable ImGUi Viewports(multiple windows)
        io.setBackendFlags(ImGuiBackendFlags.HasMouseCursors);   // Mouse cursors to display while resizing windows etc.
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
        mouseCursors[ImGuiMouseCursor.Hand] = glfwCreateStandardCursor(GLFW_HAND_CURSOR);
        mouseCursors[ImGuiMouseCursor.NotAllowed] = glfwCreateStandardCursor(GLFW_NOT_ALLOWED_CURSOR);
        mouseCursors[ImGuiMouseCursor.Arrow] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
        mouseCursors[ImGuiMouseCursor.ResizeAll] = glfwCreateStandardCursor(GLFW_RESIZE_ALL_CURSOR);
        mouseCursors[ImGuiMouseCursor.ResizeEW] = glfwCreateStandardCursor(GLFW_RESIZE_EW_CURSOR);
        mouseCursors[ImGuiMouseCursor.ResizeNESW] = glfwCreateStandardCursor(GLFW_RESIZE_NESW_CURSOR);
        mouseCursors[ImGuiMouseCursor.ResizeNS] = glfwCreateStandardCursor(GLFW_RESIZE_NS_CURSOR);
        mouseCursors[ImGuiMouseCursor.ResizeNWSE] = glfwCreateStandardCursor(GLFW_RESIZE_NWSE_CURSOR);
        mouseCursors[ImGuiMouseCursor.TextInput] = glfwCreateStandardCursor(GLFW_IBEAM_CURSOR);

        // ------------------------------------------------------------
        // GLFW callbacks to handle user input
        org.lwjgl.glfw.GLFW.glfwSetKeyCallback(Window.getGlfwWindow(), (w, key, scancode, action, mods) -> {
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

        glfwSetCharCallback(Window.getGlfwWindow(), (w, c) -> {
            if (c != GLFW_KEY_DELETE)
                io.addInputCharacter(c);
        });

        glfwSetMouseButtonCallback(Window.getGlfwWindow(), (w, button, action, mods) -> {
            final boolean[] mouseDown = new boolean[5];

            mouseDown[0] = button == GLFW_MOUSE_BUTTON_1 && action != GLFW_RELEASE;
            mouseDown[1] = button == GLFW_MOUSE_BUTTON_2 && action != GLFW_RELEASE;
            mouseDown[2] = button == GLFW_MOUSE_BUTTON_3 && action != GLFW_RELEASE;
            mouseDown[3] = button == GLFW_MOUSE_BUTTON_4 && action != GLFW_RELEASE;
            mouseDown[4] = button == GLFW_MOUSE_BUTTON_5 && action != GLFW_RELEASE;

            io.setMouseDown(mouseDown);

            if (!io.getWantCaptureMouse() && mouseDown[1])
                ImGui.setWindowFocus(null);

            if (!io.getWantCaptureMouse() || getWantCaptureMouse())
                MouseListener.mouseButtonCallback(w, button, action, mods);
        });

        glfwSetScrollCallback(Window.getGlfwWindow(), (w, xOffset, yOffset) -> {
            io.setMouseWheelH(io.getMouseWheelH() + (float) xOffset);
            io.setMouseWheel(io.getMouseWheel() + (float) yOffset);

            if ((!io.getWantCaptureMouse() || getWantCaptureMouse()))
                MouseListener.mouseScrollCallback(w, xOffset, yOffset);
        });

        io.setSetClipboardTextFn(new ImStrConsumer() {
            @Override
            public void accept(final String s) {
                glfwSetClipboardString(Window.getGlfwWindow(), s);
            }
        });

        io.setGetClipboardTextFn(new ImStrSupplier() {
            @Override
            public String get() {
                final String clipboardString = glfwGetClipboardString(Window.getGlfwWindow());
                return Objects.requireNonNullElse(clipboardString, "");
            }
        });

        GuiFont.init(io);

        // Method initializes LWJGL3 renderer.
        // This method SHOULD be called after you've initialized your ImGui configuration (fonts and so on).
        // ImGui context should be created as well.
        imGuiGlfw.init(Window.getGlfwWindow(), false);
        imGuiGl3.init("#version " + Settings.shaderVersion);

        EditorThemeSystem.setDarkTheme();

        Profiler.stopTimer("Init ImGui");
    }

    private void createEditorWindows() {
        Profiler.startTimer("Create EditorImGui Windows");

        windows.put(getNextWindowId(), new AbstractMap.SimpleEntry<>(new ImBoolean(true), new GameView_Window()));
        windows.put(getNextWindowId(), new AbstractMap.SimpleEntry<>(new ImBoolean(true), new SceneView_Window()));
        windows.put(getNextWindowId(), new AbstractMap.SimpleEntry<>(new ImBoolean(true), new Outliner_Window()));
        windows.put(getNextWindowId(), new AbstractMap.SimpleEntry<>(new ImBoolean(true), new SceneHierarchy_Window()));
        windows.put(getNextWindowId(), new AbstractMap.SimpleEntry<>(new ImBoolean(true), new Console_Window()));

        windows.put(getNextWindowId(), new AbstractMap.SimpleEntry<>(new ImBoolean(false), new Profiler_Window()));

        Profiler.stopTimer("Create EditorImGui Windows");
    }

    private final ImInt testTextureID = new ImInt(0);
    @Override
    public void update() {
        Profiler.startTimer("Update ImGui");
        startFrame(Time.deltaTime());

        if (windowOnFullscreen == null) {
            float menuBarHeight = MainMenuBar.imgui();
            float footerHeight = Footer.imgui();
            setupDockSpace(menuBarHeight, footerHeight);
            // TODO DELETE DRAWING OF CURRENT SCENE
            SceneManager.getCurrentScene().imgui();
            if (Window.debugMode) { // TODO Delete this
                ThemeChanger_Window.imgui();

                ImGui.begin("_Debug_");
                ImGui.inputInt("Test Texture ID", testTextureID);
                testTextureID.set(Maths.clamp(testTextureID.get(), 0, Integer.MAX_VALUE));
                ImGui.image(testTextureID.get(), ImGui.getContentRegionAvailX(), ImGui.getContentRegionAvailX(), 0, 1, 1, 0);
                ImGui.text("Hover GameObject ID: " + ((int) Window.getScreenFramebuffer().readPixel((int) Input.getMouseScreenPositionX(), (int) Input.getMouseScreenPositionY(), GL_COLOR_ATTACHMENT1) - 1));
                ImGui.end();

                ImGui.showDemoWindow();
                ImGui.showStackToolWindow();
                ImGui.showMetricsWindow();
                TestFieldsWindow.imgui();
            }

            try {
                for (int windowId : windows.keySet())
                    if (windows.get(windowId).getKey().get())
                        windows.get(windowId).getValue().imgui(windows.get(windowId).getKey());
            } catch (ConcurrentModificationException ignored) { }

        } else {
//            float menuBarHeight = MainMenuBar.imgui();
//            float footerHeight = Footer.imgui();
//            setupDockSpace(menuBarHeight, footerHeight);

//            ImGuiViewport viewport = ImGui.getMainViewport();
//            ImGui.setNextWindowPos(viewport.getWorkPos().x, viewport.getWorkPos().y + menuBarHeight);
//            ImGui.setNextWindowSize(viewport.getWorkSize().x, viewport.getWorkSize().y - menuBarHeight - footerHeight);

            windowOnFullscreen.imgui(new ImBoolean(true));

            if (Input.buttonClick(KeyCode.C))
                windowOnFullscreen = null;
        }
        endFrame();
        Profiler.stopTimer("Update ImGui");
    }

    private void startFrame(final float deltaTime) {
        Profiler.startTimer("ImGui StartFrame");

        imGuiGlfw.newFrame();

        // Get window properties and mouse position
        float[] winWidth = { Window.getWidth() };
        float[] winHeight = { Window.getHeight() };
        double[] mousePosX = { 0 };
        double[] mousePosY = { 0 };
        glfwGetCursorPos(Window.getGlfwWindow(), mousePosX, mousePosY);

        // We SHOULD call those methods to update Dear ImGui state for the current frame
        final ImGuiIO io = ImGui.getIO();
        io.setDisplaySize(winWidth[0], winHeight[0]);
        io.setDisplayFramebufferScale(1.0f, 1.0f);
        io.setMousePos((float) mousePosX[0], (float) mousePosY[0]);
        io.setDeltaTime(deltaTime);

        // Update the mouse cursor
        final int imguiCursor = ImGui.getMouseCursor();
        glfwSetCursor(Window.getGlfwWindow(), mouseCursors[imguiCursor]);
        glfwSetInputMode(Window.getGlfwWindow(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        ImGui.newFrame();

        Profiler.stopTimer("ImGui StartFrame");
    }

    private void endFrame() {
        Profiler.startTimer("ImGui EndFrame");

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, Window.getWidth(), Window.getHeight());
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);

        // After Dear ImGui prepared a draw data, we use it in the LWJGL3 renderer.
        // At that moment ImGui will be rendered to the current OpenGL context.
        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());

        // TODO FIX IMGUI VIEWPORTS
//        long backupWindowPointer = glfwGetCurrentContext();
//        ImGui.updatePlatformWindows();
//        ImGui.renderPlatformWindowsDefault();
//        glfwMakeContextCurrent(backupWindowPointer);
        Profiler.stopTimer("ImGui EndFrame");
    }

    private void setupDockSpace(float menuBarHeight, float footerHeight) {
        int windowFlags = ImGuiWindowFlags.NoDocking | ImGuiWindowFlags.NoTitleBar |
                ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove |
                ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;

        // TODO FIX IMGUI VIEWPORTS
//        ImGuiViewport mainViewport = ImGui.getMainViewport();
//        ImGui.setNextWindowPos(mainViewport.getWorkPosX(), mainViewport.getWorkPosY());
//        ImGui.setNextWindowSize(mainViewport.getWorkSizeX(), mainViewport.getWorkSizeY());
//        ImGui.setNextWindowViewport(mainViewport.getID());
        ImGui.setNextWindowPos(0.0f, menuBarHeight);
        ImGui.setNextWindowSize(Window.getWidth(), Window.getHeight() - menuBarHeight - footerHeight); // Set DockSpace window size
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0f, 0.0f);

        ImGui.begin("Editor DockSpace", new ImBoolean(true), windowFlags);
        ImGui.popStyleVar(3);

        // Dockspace
        ImGui.dockSpace(ImGui.getID("DockSpace"));

        ImGui.end(); // End of Editor DockSpace
    }

    public static void setWindowOnFullscreen(EditorImGuiWindow windowOnFullscreen) { // TODO FIX THIS
        if (windowOnFullscreen != null) {
//            Gson gson = new GsonBuilder()
//                    .setPrettyPrinting()
//                    .registerTypeAdapter(EditorImGuiWindow.class, new EditorImGuiWindowDeserializer())
//                    .enableComplexMapKeySerialization()
//                    .create();
//            String gsonString = gson.toJson(windowOnFullscreen, EditorImGuiWindow.class);
//            EditorImGuiWindow copy = gson.fromJson(gsonString, EditorImGuiWindow.class);
//            copy.id = getNextWindowId();
//
//            System.out.println(windowOnFullscreen);
//            System.out.println(copy);
        } else
            EngineGuiLayer.windowOnFullscreen = null;
    }

    public static EditorImGuiWindow getWindowOnFullscreen() { return windowOnFullscreen; }

    public static  <T extends EditorImGuiWindow> List<EditorImGuiWindow> getWindowsByType(Class<T> windowType, boolean includeNonVisible) {
        List<EditorImGuiWindow> result = new ArrayList<>();

        for (int windowId : windows.keySet())
            if (windows.get(windowId).getKey().get() && windows.get(windowId).getValue().getClass() == windowType)
                if (includeNonVisible) {
                    if (windows.get(windowId).getValue().isVisible())
                        result.add(windows.get(windowId).getValue());
                } else
                    result.add(windows.get(windowId).getValue());

        return result;
    }

    public static EditorImGuiWindow getWindow(int windowId) { return windows.get(windowId).getValue(); }

    public static <T extends EditorImGuiWindow> boolean isAnyWindowVisible(Class<T> windowType) {
        for (int windowId : windows.keySet())
            if (windows.get(windowId).getKey().get() && windows.get(windowId).getValue().getClass() == windowType)
                if (windows.get(windowId).getValue().isVisible())
                    return true;
        return false;
    }

    public static void selectWindow(EditorImGuiWindow window) { ImGui.setWindowFocus(window.actualWindowTitle); }

    public static  <T extends EditorImGuiWindow> void setWindowOpen(Class<T> window, boolean state) {
        for (int windowId : windows.keySet())
            if (windows.get(windowId).getValue().getClass() == window) {
                windows.get(windowId).getKey().set(state);
                return;
            }
    }

    public static int getNextWindowId() { return windowId++; }

    public static boolean getWantCaptureMouse() {
        for (EditorImGuiWindow window : getWindowsByType(SceneView_Window.class, false))
            if (((SceneView_Window) window).getWantCaptureMouse())
                return true;
        return false;
    }

    @Override
    public boolean onEvent(Event event) {
        return false;
    }

    @Override
    public void cleanUp() {
        imGuiGl3.dispose();
        ImGui.destroyContext();
    }
}
