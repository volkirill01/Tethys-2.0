package editor.stuff;

import editor.assets.AssetPool;
import editor.entity.GameObject;
import editor.entity.component.components.SpriteRenderer;
import editor.eventListeners.Input;
import editor.eventListeners.KeyCode;
import editor.eventListeners.MouseListener;
import editor.gui.ImGuiLayer;
import editor.renderer.Framebuffer;
import editor.renderer.MasterRenderer;
import editor.renderer.debug.DebugDraw;
import editor.renderer.debug.DebugGrid;
import editor.renderer.shader.Shader;
import editor.renderer.stuff.PickingTexture;
import editor.scenes.SceneManager;
import editor.stuff.customVariables.Color;
import editor.stuff.utils.Time;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import java.awt.*;
import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.GL_MAX_TEXTURE_IMAGE_UNITS;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

//    private static int start_width, start_height;
    private static int width, height;
    private static final String title = "Tethys";

    private static long glfwWindow;

    private static ImGuiLayer imguiLayer;

    private static Framebuffer framebuffer;
    private static PickingTexture pickingTexture;

    public static float r = 1.0f;
    public static float g = 1.0f;
    public static float b = 1.0f;
    public static float a = 1.0f;

    public static void run() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//        start_width = (int) screenSize.getWidth();
//        start_height = (int) screenSize.getHeight();
//        width = start_width;
//        height = start_height;

        width = 2560;
        height = 1080;

        init();
        loop();

        // If Window should be closed, runs this method
        closeWindow();
    }

    private static void init() {
        // Setup an Error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit())
            throw new IllegalStateException("Unable to Initialize GLFW.");

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // Create window but make it invisible before setting it
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE); // TODO FIX IMGUI CURSOR OFFSET AND SET THIS PROPERLY //GLFW_TRUE
//        glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE); // TODO FIX IMGUI CURSOR OFFSET AND SET THIS PROPERLY //GLFW_TRUE

        // Create the window
//        glfwWindow = glfwCreateWindow((int) (start_width / 1.2f), (int) (start_height / 1.2f), title, NULL, NULL); // TODO FIX IMGUI CURSOR OFFSET AND SET THIS PROPERLY //((int) (width / 1.2f), (int) (height / 1.2f))
        glfwWindow = glfwCreateWindow((int) (width / 1.2f), (int) (height / 1.2f), title, NULL, NULL); // TODO FIX IMGUI CURSOR OFFSET AND SET THIS PROPERLY //((int) (width / 1.2f), (int) (height / 1.2f))
        if (glfwWindow == NULL)
            throw new IllegalStateException("Failed to create GLFW Window.");

        // Setup callbacks
        Input.setInputCallbacks();
        glfwSetWindowSizeCallback(glfwWindow, Window::windowResizeCallback);

        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the Window visible
        glfwShowWindow(glfwWindow);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        imguiLayer = new ImGuiLayer(glfwWindow);
        imguiLayer.initImGui();

        framebuffer = new Framebuffer(2560, 1080);
        pickingTexture = new PickingTexture(2560, 1080);
        glViewport(0, 0, 2560, 1080);

        SceneManager.changeScene(0); // Load default Scene

        printMachineInfo();

//        glfwSetWindowSize(glfwWindow, (int) (start_width / 1.2f), (int) (start_height / 1.2f)); // TODO FIX IMGUI CURSOR OFFSET AND SET THIS PROPERLY //DELETE THIS
//        glfwMaximizeWindow(glfwWindow); // TODO FIX IMGUI CURSOR OFFSET AND SET THIS PROPERLY //DELETE THIS
    }

    private static void loop() {
        float beginTime = Time.getTime();
        float endTime;

        Shader defaultShader = AssetPool.getShader("editorFiles/shaders/default.glsl");
        Shader pickingShader = AssetPool.getShader("editorFiles/shaders/stuff/pickingShader.glsl");

        while (!glfwWindowShouldClose(glfwWindow)) {
            glfwWindowResizeCallback();

            // Poll events
            glfwPollEvents();

            // Render pass 1. Render to picking texture
            glDisable(GL_BLEND);
            pickingTexture.bind();

            glViewport(0, 0, 2560, 1080);
            glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            MasterRenderer.bindShader(pickingShader);
            SceneManager.getCurrentScene().render();

            pickingTexture.unbind();
            glEnable(GL_BLEND);

            // Render pass 2. Render actual scene
            DebugDraw.addBox2D(new Vector3f(50.0f, 150.0f, 0.0f), new Vector2f(100.0f, 200.0f));
            DebugDraw.addCube(new Vector3f(150.0f, 250.0f, -20.0f), new Vector3f(100.0f, 200.0f, 100.0f), new Vector3f(45.0f, 50.0f, 10.0f), Color.BLUE);

            DebugDraw.beginFrame();

            glfwSetWindowTitle(glfwWindow, title + " FPS: " + (1.0f / Time.deltaTime()));

            framebuffer.bind();
            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT);

            if (Time.deltaTime() >= 0.0f) {
                DebugGrid.draw();

                SceneManager.getCurrentScene().update();

                MasterRenderer.bindShader(defaultShader);
                SceneManager.getCurrentScene().render();

                DebugDraw.draw();
            }
            framebuffer.unbind();

            // Display Editors GUI
            imguiLayer.update();

            glfwSwapBuffers(glfwWindow);

            MouseListener.endFrame();

            endTime = Time.getTime();
            Time.setDeltaTime(endTime - beginTime);
            beginTime = endTime;
        }

        SceneManager.getCurrentScene().save();
    }

    private static void glfwWindowResizeCallback() {
        int[] width = { 0 };
        int[] height = { 0 };
        glfwGetWindowSize(glfwWindow, width, height);
        windowResizeCallback(glfwWindow, width[0], height[0]);
    }

    private static void windowResizeCallback(long w, int newWidth, int newHeight) {
        Window.setWidth(newWidth);
        Window.setHeight(newHeight);
    }

    private static void closeWindow() {
        // Free the memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    private static void printMachineInfo() {
        System.out.printf(ColoredText.GREEN + "\nEngine starts:\n");
        System.out.printf((ColoredText.YELLOW + "  Resolution: " + ColoredText.RESET + "%d x %d\n"), width, height);
        System.out.printf((ColoredText.YELLOW + "  LWJGL Version: " + ColoredText.RESET + "%s\n"), Version.getVersion());
        IntBuffer buffer = BufferUtils.createIntBuffer(1);
        glGetIntegerv(GL_MAX_TEXTURE_IMAGE_UNITS, buffer);
        System.out.printf((ColoredText.YELLOW + "  Texture Slots count: " + ColoredText.RESET + "%d\n"), buffer.get(0));
    }

    public static boolean isClose() { return glfwWindowShouldClose(glfwWindow); }

    public static long getGlfwWindow() { return glfwWindow; }

    public static String getTitle() { return title; }

//    public static int getStartWidth() { return start_width; }

    public static int getWidth() { return width; }

    public static void setWidth(int width) { Window.width = width; }

//    public static int getStartHeight() { return start_height; }

    public static int getHeight() { return height; }

    public static void setHeight(int height) { Window.height = height; }

    public static Framebuffer getFramebuffer() { return Window.framebuffer; }

    public static float getTargetAspectRatio() { return 16.0f / 9.0f; }

    public static PickingTexture getPickingTexture() { return Window.pickingTexture; }
}
