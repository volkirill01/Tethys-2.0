package editor.stuff;

import editor.assets.AssetPool;
import editor.editor.gui.EditorThemeSystem;
import editor.editor.windows.Outliner_Window;
import editor.entity.GameObject;
import editor.eventListeners.Input;
import editor.eventListeners.KeyCode;
import editor.eventListeners.KeyListener;
import editor.eventListeners.MouseListener;
import editor.editor.gui.ImGuiLayer;
import editor.observers.EventSystem;
import editor.observers.Observer;
import editor.observers.events.Event;
import editor.physics.physics2D.Physics2D;
import editor.renderer.EntityRenderer;
import editor.renderer.RenderCommand;
import editor.renderer.camera.Camera;
import editor.renderer.debug.DebugDraw;
import editor.renderer.shader.Shader;
import editor.renderer.stuff.Fbo;
import editor.renderer.stuff.PickingTexture;
import editor.scenes.SceneManager;
import editor.stuff.customVariables.Color;
import editor.stuff.utils.Time;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.opengl.GL;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.GL_MAX_TEXTURE_IMAGE_UNITS;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window implements Observer {

    private static int width, height;
    private static int screen_width, screen_height;
    private static final String title = "Tethys";
    private static boolean isMinimized = false;

    private static long glfwWindow;

    private static long audioContext;
    private static long audioDevice;

    private static ImGuiLayer imguiLayer;

    private static Fbo screenFramebuffer;
    private static PickingTexture pickingTexture;

    private static boolean runtimePlaying = false;

    public static boolean debugMode = false; // TODO DELETE THIS

    public void run() {
        EventSystem.addObserver(this);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screen_width = (int) screenSize.getWidth();
        screen_height = (int) screenSize.getHeight();

        width = screen_width;
        height = screen_height;

        init();
        loop();

        // If Window should be closed, runs this method
        closeWindow();
    }

    private void init() {
        // Set up an Error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit())
            throw new IllegalStateException("Unable to Initialize GLFW.");

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // Create window but make it invisible before setting it
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        // Create the window
        glfwWindow = glfwCreateWindow((int) (width / 1.2f), (int) (height / 1.2f), title, NULL, NULL);
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

        // Initialize AudioDevice
        String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        audioDevice = alcOpenDevice(defaultDeviceName);

        int[] attributes = { 0 };
        audioContext = alcCreateContext(audioDevice, attributes);
        alcMakeContextCurrent(audioContext);

        ALCCapabilities alcCapabilities = ALC.createCapabilities(audioDevice);
        ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);

        if (!alCapabilities.OpenAL10)
            throw new IllegalStateException("Audio Library not supported.");

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

        EditorThemeSystem.setDarkTheme();

        screenFramebuffer = new Fbo(Window.getScreenWidth(), Window.getScreenHeight(), Fbo.DEPTH_RENDER_BUFFER);
        pickingTexture = new PickingTexture(Window.getScreenWidth(), Window.getScreenHeight());
        glViewport(0, 0, Window.getScreenWidth(), Window.getScreenHeight());

        SceneManager.changeScene("level.scene"); // Load default Scene

        printMachineInfo();
    }

    private void loop() {
        float beginTime = Time.getTime();
        float endTime;

        Shader defaultShader = AssetPool.getShader("editorFiles/shaders/default.glsl");
        Shader pickingShader = AssetPool.getShader("editorFiles/shaders/stuff/pickingShader.glsl");

        while (!glfwWindowShouldClose(glfwWindow)) {
            glfwWindowResizeCallback();

            // Poll events
            glfwPollEvents();

            if (!isMinimized) {
                // Render pass 1. Render to picking texture
                glDisable(GL_BLEND);
                pickingTexture.bind();

                glViewport(0, 0, Window.getScreenWidth(), Window.getScreenHeight());
                RenderCommand.setClearColor(Color.BLACK);
                RenderCommand.clear(RenderCommand.BufferBit.ColorAndDepthBuffer);

                renderPass(pickingShader, SceneManager.getCurrentScene().getEditorCamera().getProjectionMatrix(), SceneManager.getCurrentScene().getEditorCamera().getViewMatrix());

                pickingTexture.unbind();
                glEnable(GL_BLEND);

                // Render pass 2. Render actual scene
                DebugDraw.beginFrame();

                glfwSetWindowTitle(glfwWindow, title + " FPS: " + (1.0f / Time.deltaTime()));

                screenFramebuffer.bind();

                if (Time.deltaTime() >= 0.0f) {
//                    DebugGrid.addGrid(); // TODO FIX GRID DRAWING

                    if (runtimePlaying)
                        SceneManager.getCurrentScene().update();
                    else
                        SceneManager.getCurrentScene().editorUpdate();

                    renderPass(defaultShader, SceneManager.getCurrentScene().getEditorCamera().getProjectionMatrix(), SceneManager.getCurrentScene().getEditorCamera().getViewMatrix());

                    DebugDraw.draw();
                }

                if (Input.buttonDown(KeyCode.Left_Alt) && Input.buttonDown(KeyCode.Left_Shift) && Input.buttonClick(KeyCode.B))
                    debugMode = !debugMode;

                screenFramebuffer.unbind();

                for (Camera c : SceneManager.getCurrentScene().getAllCameras()) {
                    c.getFob().bind();
                    glClearColor(c.getBackgroundColor().r / 255.0f, c.getBackgroundColor().g / 255.0f, c.getBackgroundColor().b / 255.0f, 1.0f);
                    glClear(GL_COLOR_BUFFER_BIT);
                    renderPass(defaultShader, c.getProjectionMatrix(), c.getViewMatrix());
                    c.getFob().unbind();
                }
            }

            // Display Editors GUI
            imguiLayer.update();

            glfwSwapBuffers(glfwWindow);

            MouseListener.endFrame();
            KeyListener.endFrame();

            endTime = Time.getTime();
            Time.setDeltaTime(endTime - beginTime);
            beginTime = endTime;
        }

        SceneManager.getCurrentScene().saveAs("level.scene");
    }

    private static void renderPass(Shader shader, Matrix4f projectionMatrix, Matrix4f viewMatrix) {
        EntityRenderer.setShader(shader);
        SceneManager.getCurrentScene().render(projectionMatrix, viewMatrix);
    }

    private static void glfwWindowResizeCallback() {
        int[] width = { 0 };
        int[] height = { 0 };
        glfwGetWindowSize(glfwWindow, width, height);
        windowResizeCallback(glfwWindow, width[0], height[0]);
    }

    private static void windowResizeCallback(long window, int newWidth, int newHeight) {
        Window.setWidth(newWidth);
        Window.setHeight(newHeight);

        isMinimized = width == 0 || height == 0;
    }

    private static void closeWindow() {
        // Delete tmp files
        try {
            Files.delete(new File("tmpRuntimeScene.scene").toPath());
        } catch (IOException ignored) { }

        // Free the memory
        imguiLayer.destroyImGui();
        alcDestroyContext(audioContext);
        alcCloseDevice(audioDevice);

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

    public static int getScreenWidth() { return screen_width; }

    public static int getScreenHeight() { return screen_height; }

    public static int getWidth() { return width; }

    public static void setWidth(int width) { Window.width = width; }

    public static int getHeight() { return height; }

    public static void setHeight(int height) { Window.height = height; }

    public static Fbo getScreenFramebuffer() { return Window.screenFramebuffer; }

    public static float getTargetAspectRatio() { return 16.0f / 9.0f; }

    public static PickingTexture getPickingTexture() { return Window.pickingTexture; }

    public static boolean isRuntimePlaying() { return Window.runtimePlaying; }

    public static void setRuntimePlaying(boolean runtimePlaying) { Window.runtimePlaying = runtimePlaying; }

    public static Physics2D getPhysics2D() { return SceneManager.getCurrentScene().getPhysics2D(); }

    @Override
    public void onNotify(GameObject object, Event event) {
        switch (event.type) {
            case GameEngine_StartPlay -> {
                runtimePlaying = true;
                SceneManager.getCurrentScene().saveAs("tmpRuntimeScene.scene"); // TODO SAVE TO TMP SCENE FILE, TO PROVIDE USER NOT SAVE SCENE BEFORE HE STAT PLAYING
                SceneManager.changeScene("tmpRuntimeScene.scene");
                Outliner_Window.clearSelected();
            }
            case GameEngine_StopPlay -> {
                runtimePlaying = false;
                SceneManager.changeScene("tmpRuntimeScene.scene"); // TODO LOAD FROM TMP SCENE FILE, TO PROVIDE USER NOT SAVE SCENE BEFORE HE STAT PLAYING
                Outliner_Window.clearSelected();
            }
            case GameEngine_SaveScene -> SceneManager.getCurrentScene().saveAs("level.scene");
            case GameEngine_ReloadScene -> SceneManager.changeScene("level.scene");
        }
    }
}
