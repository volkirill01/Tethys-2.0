package editor.stuff;

import editor.assets.AssetPool;
import editor.entity.GameObject;
import editor.eventListeners.Input;
import editor.eventListeners.KeyListener;
import editor.eventListeners.MouseListener;
import editor.editor.gui.ImGuiLayer;
import editor.observers.EventSystem;
import editor.observers.Observer;
import editor.observers.events.Event;
import editor.renderer.Framebuffer;
import editor.renderer.MasterRenderer;
import editor.renderer.debug.DebugDraw;
import editor.renderer.debug.DebugGrid;
import editor.renderer.shader.Shader;
import editor.renderer.stuff.PickingTexture;
import editor.scenes.EngineSceneInitializer;
import editor.scenes.SceneManager;
import editor.stuff.inputActions.KeyboardControls;
import editor.stuff.inputActions.MouseControls;
import editor.stuff.utils.Time;
import org.lwjgl.BufferUtils;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.opengl.GL;

import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.GL_MAX_TEXTURE_IMAGE_UNITS;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window implements Observer {

    private static int width, height;
    private static final String title = "Tethys";

    private static long glfwWindow;

    private static long audioContext;
    private static long audioDevice;

    private static ImGuiLayer imguiLayer;

    private static Framebuffer framebuffer;
    private static PickingTexture pickingTexture;

    private static boolean runtimePlaying = false;

    public void run() {
        EventSystem.addObserver(this);

//        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
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

    private void init() {
        // Setup an Error callback
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

        framebuffer = new Framebuffer(2560, 1080);
        pickingTexture = new PickingTexture(2560, 1080);
        glViewport(0, 0, 2560, 1080);

        SceneManager.changeScene(new EngineSceneInitializer()); // Load default Scene

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

            // Update Listeners
            KeyboardControls.update();
            MouseControls.update();

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
            DebugDraw.beginFrame();

            glfwSetWindowTitle(glfwWindow, title + " FPS: " + (1.0f / Time.deltaTime()));

            framebuffer.bind();
            glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            if (Time.deltaTime() >= 0.0f) {
                DebugGrid.draw();

                if (runtimePlaying)
                    SceneManager.getCurrentScene().update();
                else
                    SceneManager.getCurrentScene().editorUpdate();

                MasterRenderer.bindShader(defaultShader);
                SceneManager.getCurrentScene().render();

                DebugDraw.draw();
            }
            framebuffer.unbind();

            // Display Editors GUI
            imguiLayer.update();

            glfwSwapBuffers(glfwWindow);

            MouseListener.endFrame();
            KeyListener.endFrame();

            endTime = Time.getTime();
            Time.setDeltaTime(endTime - beginTime);
            beginTime = endTime;
        }

        SceneManager.getCurrentScene().saveAs("level.txt");
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

    public static int getWidth() { return width; }

    public static void setWidth(int width) { Window.width = width; }

    public static int getHeight() { return height; }

    public static void setHeight(int height) { Window.height = height; }

    public static Framebuffer getFramebuffer() { return Window.framebuffer; }

    public static float getTargetAspectRatio() { return 16.0f / 9.0f; }

    public static PickingTexture getPickingTexture() { return Window.pickingTexture; }

    public static boolean isRuntimePlaying() { return Window.runtimePlaying; }

    public static void setRuntimePlaying(boolean runtimePlaying) { Window.runtimePlaying = runtimePlaying; }

    @Override
    public void onNotify(GameObject object, Event event) {
        switch (event.type) {
            case GameEngine_StartPlay -> {
                runtimePlaying = true;
                SceneManager.getCurrentScene().saveAs("level.txt"); // TODO SAVE TO TMP SCENE FILE, TO PROVIDE USER NOT SAVE SCENE BEFORE HE STAT PLAYING
                SceneManager.changeScene(new EngineSceneInitializer());
            }
            case GameEngine_StopPlay -> {
                runtimePlaying = false;
                SceneManager.changeScene(new EngineSceneInitializer()); // TODO LOAD FROM TMP SCENE FILE, TO PROVIDE USER NOT SAVE SCENE BEFORE HE STAT PLAYING
            }
            case GameEngine_SaveScene -> {
                SceneManager.getCurrentScene().saveAs("level.txt");
            }
            case GameEngine_LoadScene -> {
                SceneManager.changeScene(new EngineSceneInitializer());
            }
        }
    }
}
