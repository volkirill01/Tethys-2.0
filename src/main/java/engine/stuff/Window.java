package engine.stuff;

import engine.editor.console.Console;
import engine.editor.console.ConsoleMessage;
import engine.editor.console.LogType;
import engine.editor.windows.Outliner_Window;
import engine.entity.GameObject;
import engine.eventListeners.Input;
import engine.eventListeners.KeyCode;
import engine.eventListeners.KeyListener;
import engine.eventListeners.MouseListener;
import engine.editor.gui.EngineGuiLayer;
import engine.layerStack.EngineLayer;
import engine.layerStack.LayerStack;
import engine.observers.EventSystem;
import engine.observers.Observer;
import engine.observers.events.Event;
import engine.observers.events.EventType;
import engine.physics.physics2D.Physics2D;
import engine.profiling.Profiler;
import engine.profiling.SessionReplay;
import engine.renderer.EntityRenderer;
import engine.renderer.RenderCommand;
import engine.renderer.shader.Shader;
import engine.renderer.stuff.Fbo;
import engine.scenes.SceneManager;
import engine.stuff.customVariables.Color;
import engine.stuff.utils.Time;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.lwjgl.BufferUtils;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.opengl.GL;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window implements Observer {

    private static int screen_width, screen_height;

    private static int width, height;
    private static final Vector2i position = new Vector2i();
    private static final String title = "Tethys";
    private static final String windowIniFilepath = "window.ini";

    private static boolean isMinimized = false;
    private static boolean isClosed = false;

    private static long glfwWindow;

    private static long audioContext;
    private static long audioDevice;

    private static boolean runtimePlaying = false;

    public static boolean debugMode = false; // TODO DELETE THIS

    private void putLayersInStack() {
        LayerStack.attachLayer(new EngineGuiLayer());
        LayerStack.attachLayer(new EngineLayer());
    }

    public void run() {
        EventSystem.addObserver(this);

        loadIniFile();

        SessionReplay.beginSession("Startup", String.format("%sProfile-Startup.json", Window.getTitle()));
        init();
        SessionReplay.endSession();
        SessionReplay.beginSession("Runtime", String.format("%sProfile-Runtime.json", Window.getTitle()));
        loop();
        SessionReplay.endSession();

        // If Window should be closed, runs this method
        SessionReplay.beginSession("Shutdown", String.format("%sProfile-Shutdown.json", Window.getTitle()));
        closeWindow();
        SessionReplay.endSession();
    }

    private void loadIniFile() {
        // Set default window parameters
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screen_width = (int) screenSize.getWidth();
        screen_height = (int) screenSize.getHeight();

        String inFile = "";
        try {
            inFile = new String(Files.readAllBytes(Paths.get(windowIniFilepath)));
        } catch (IOException e) {
            System.out.printf("%sNo Window ini file.%s\n", ColoredText.RED, ColoredText.RESET);
        }

        if (!inFile.equals("")) {
            width = Integer.parseInt(inFile.split("size = ")[1].split(", ")[0]);
            height = Integer.parseInt(inFile.split("size = ")[1].split(", ")[1].split("\n")[0]);

            position.x = Integer.parseInt(inFile.split("position = ")[1].split(", ")[0].split("\n")[0]);
            position.y = Integer.parseInt(inFile.split("position = ")[1].split(", ")[1].split("\n")[0]);

            isMinimized = !Boolean.parseBoolean(inFile.split("isMaximized = ")[1]);
            return;
        }

        width = screen_width;
        height = screen_height;

        position.x = 0;
        position.y = 0;

        isMinimized = false;
    }

    private void init() {
        Profiler.startTimer("Window Init");
        // Set up an Error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit())
            throw new IllegalStateException("Unable to Initialize GLFW.");

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // Create window but make it invisible before setting it
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, isMinimized ? GLFW_FALSE : GLFW_TRUE);

        // Create the window
        glfwWindow = glfwCreateWindow(width, height, title, NULL, NULL);
        if (glfwWindow == NULL)
            throw new IllegalStateException("Failed to create GLFW Window.");

        if (position.x != 0 && position.y != 0)
            glfwSetWindowPos(glfwWindow, position.x, position.y);

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

        EntityRenderer.init();

        putLayersInStack();
        LayerStack.init();

        glViewport(0, 0, Window.getScreenWidth(), Window.getScreenHeight());

        SceneManager.changeScene("level.scene"); // Load default Scene

        printMachineInfo();
        Profiler.stopTimer("Window Init");
    }

    private void loop() {
        Profiler.startTimer("Window Loop");

        float beginTime = Time.getTime();
        float endTime;

        while (!glfwWindowShouldClose(glfwWindow)) {
            if (isClosed)
                break;

            Profiler.startTimer("Engine Update");
            glfwWindowResizeCallback();

            // Poll events
            glfwPollEvents();

            glfwSetWindowTitle(glfwWindow, title + " FPS: " + Time.getFPS());

            LayerStack.update();

            if (Input.buttonDown(KeyCode.Left_Alt) && Input.buttonDown(KeyCode.Left_Shift) && Input.buttonClick(KeyCode.B)) // TODO DELETE THIS
                debugMode = !debugMode;

            glfwSwapBuffers(glfwWindow);

            MouseListener.endFrame();
            KeyListener.endFrame();

            endTime = Time.getTime();
            Time.setDeltaTime(endTime - beginTime);
            beginTime = endTime;

            Profiler.stopTimer("Engine Update");
        }

        SceneManager.getCurrentScene().saveAs("level.scene");
        Profiler.startTimer("Window Loop");
    }

    private static void renderPass(Matrix4f projectionMatrix, Matrix4f viewMatrix, Color backgroundColor) {
        RenderCommand.setClearColor(backgroundColor);
        RenderCommand.clear(RenderCommand.BufferBit.ColorAndDepthBuffer);
        EntityRenderer.render(projectionMatrix, viewMatrix);
    }

    private static void renderPass_SingleShader(Matrix4f projectionMatrix, Matrix4f viewMatrix, Color backgroundColor, Shader shader) {
        RenderCommand.setClearColor(backgroundColor);
        RenderCommand.clear(RenderCommand.BufferBit.ColorAndDepthBuffer);
        EntityRenderer.setShader(shader);
        EntityRenderer.render_SingleShader(projectionMatrix, viewMatrix, shader);
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

        saveWindowIniFile();

        // Free the memory
        LayerStack.cleanUp();
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
        System.out.printf((ColoredText.YELLOW + "  Texture Slots count: " + ColoredText.RESET + "%d\n\n"), buffer.get(0));
    }

    private static void saveWindowIniFile() {
        try {
            FileOutputStream outputStream = new FileOutputStream(windowIniFilepath);

            int[] tmpPositionX = new int[1];
            int[] tmpPositionY = new int[1];
            glfwGetWindowPos(glfwWindow, tmpPositionX, tmpPositionY);

            int[] tmpSizeX = new int[1];
            int[] tmpSizeY = new int[1];
            glfwGetWindowSize(glfwWindow, tmpSizeX, tmpSizeY);

            outputStream.write(String.format("position = %d, %d\n", tmpPositionX[0], tmpPositionY[0]).getBytes());
            outputStream.write(String.format("size = %d, %d\n", tmpSizeX[0], tmpSizeY[0]).getBytes());
            outputStream.write(String.format("isMaximized = %b", glfwGetWindowAttrib(glfwWindow, GLFW_MAXIMIZED) == 1).getBytes());
            outputStream.flush();

            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public static boolean isMinimized() { return isMinimized; }

    public static void minimize() { } // TODO MINIMIZE WINDOW

    public static void maximize() { glfwMaximizeWindow(glfwWindow); }

    public static void close() { isClosed = true; }

    public static Fbo getScreenFramebuffer() { return SceneManager.getCurrentScene().getEditorCamera().getOutputFob(); }

    public static float getTargetAspectRatio() { return 16.0f / 9.0f; }

    public static boolean isRuntimePlaying() { return Window.runtimePlaying; }

    public static void setRuntimePlaying(boolean runtimePlaying) { Window.runtimePlaying = runtimePlaying; }

    public static Physics2D getPhysics2D() { return SceneManager.getCurrentScene().getPhysics2D(); }

    @Override
    public void onNotify(GameObject object, Event event) {
        if (Console.isStopOnError() && runtimePlaying)
            if (event.type == EventType.ConsoleSendMessage && ((ConsoleMessage) event.data).type == LogType.Error) {
                EventSystem.notify(null, new Event(EventType.GameEngine_StopPlay));
                return;
            }

        switch (event.type) {
            case GameEngine_StartPlay -> {
                runtimePlaying = true;
                SceneManager.getCurrentScene().saveAs("tmpRuntimeScene.scene"); // Save to tmp scene file, to provide user not save scene before he starts playing
                SceneManager.changeScene("tmpRuntimeScene.scene");
                Outliner_Window.clearSelected();
            }
            case GameEngine_StopPlay -> {
                runtimePlaying = false;
                SceneManager.changeScene("tmpRuntimeScene.scene"); // Load from tmp scene file, to provide user not save scene before he starts playing
                Outliner_Window.clearSelected();
            }
            case GameEngine_SaveScene -> SceneManager.getCurrentScene().saveAs("level.scene");
            case GameEngine_ReloadScene -> SceneManager.changeScene("level.scene");
        }
    }
}
