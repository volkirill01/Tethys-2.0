package engine.stuff;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import engine.assets.AssetPool;
import engine.editor.console.Console;
import engine.editor.console.ConsoleMessage;
import engine.editor.windows.Outliner_Window;
import engine.eventListeners.Input;
import engine.eventListeners.KeyCode;
import engine.eventListeners.KeyListener;
import engine.eventListeners.MouseListener;
import engine.editor.gui.EngineGuiLayer;
import engine.layerStack.EngineLayer;
import engine.layerStack.LayerStack;
import engine.logging.DebugLog;
import engine.observers.EventSystem;
import engine.observers.Observer;
import engine.observers.events.Event;
import engine.observers.events.EventType;
import engine.profiling.Profiler;
import engine.profiling.SessionReplay;
import engine.renderer.EntityRenderer;
import engine.renderer.frameBuffer.Framebuffer;
import engine.scenes.SceneManager;
import engine.stuff.fileDialogs.FileDialogs;
import engine.stuff.fileDialogs.FileTypeFilter;
import engine.stuff.utils.Time;
import org.joml.Vector2f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.opengl.GL;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window implements Observer {

    private static int screen_width, screen_height;

    private static int width, height;
    private static int positionX;
    private static int positionY;
    private static final String title = "Tethys";
    private static final String windowIniFilepath = "window_ini";
    private static final String engineConfigFilepath = "engine_conf";

    private static boolean isMinimized = false;
    private static boolean isClosed = false;

    private static long glfwWindow;

    private static long audioContext;
    private static long audioDevice;

    private static boolean runtimePlaying = false; // TODO MAYBE REWRITE THIS BUTCH OF BOOLEANS IN TO STATE MACHINE
    private static boolean runtimePause = false;
    private static boolean nextFrame = false;
    private static String lastOpenedScenePath = "";

    public static boolean debugMode = false; // TODO DELETE THIS

    private void putLayersInStack() {
        new LayerStack(); // only for initialization of layer stack

        LayerStack.attachLayer(new EngineGuiLayer());
        LayerStack.attachLayer(new EngineLayer());
    }

    public void run() {
        EventSystem.addObserver(this);

        loadWindowIniFile();

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

        if (positionX != 0 && positionY != 0)
            glfwSetWindowPos(glfwWindow, positionX, positionY);

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

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        loadEngineConfigFile();

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

            String sceneRelativePath = "Assets" + lastOpenedScenePath.split("Assets")[1];
            glfwSetWindowTitle(glfwWindow, title + " | Current Scene: " + sceneRelativePath + " | FPS: " + Time.getFPS());

            if (Input.buttonDown(KeyCode.Left_Alt) && Input.buttonDown(KeyCode.Left_Shift) && Input.buttonClick(KeyCode.B)) // TODO DELETE THIS
                debugMode = !debugMode;

            LayerStack.update();
            nextFrame = false;

            glfwSwapBuffers(glfwWindow);

            MouseListener.endFrame();
            KeyListener.endFrame();

            endTime = Time.getTime();
            Time.setDeltaTime(endTime - beginTime);
            beginTime = endTime;

            Profiler.stopTimer("Engine Update");
        }

        if (runtimePlaying)
            EventSystem.notify(new Event(EventType.Engine_StopPlay));
//        SceneManager.getCurrentScene().save(); // TODO SHOW SAVE DIALOG IF SCENE EDITED BUT NOT SAVED
        Profiler.startTimer("Window Loop");
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
        DebugLog.log("Close Window.");
        Profiler.startTimer("Close Window");
        // Delete tmp files
        try {
            Files.delete(new File("tmpRuntimeScene.scene").toPath());
        } catch (IOException ignored) { }

        saveWindowIniFile();
        saveEngineConfigFile();

        // Free the memory
        AssetPool.freeMemory();
        LayerStack.freeMemory();
        alcDestroyContext(audioContext);
        alcCloseDevice(audioDevice);

        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
        Profiler.stopTimer("Close Window");
    }

    private static void printMachineInfo() {
        System.out.println();
        DebugLog.logInfo("Engine starts:");
        DebugLog.logWarning_WithoutTime("  Resolution: ", ColoredText.CLEAR, width, "x", height);
        DebugLog.logWarning_WithoutTime("  LWJGL Version: ", ColoredText.CLEAR, Version.getVersion());
        DebugLog.logWarning_WithoutTime("  Texture Slots count: ", ColoredText.CLEAR, EntityRenderer.getTextureSlotsCount());
        System.out.println();
    }

    private void loadWindowIniFile() {
        DebugLog.logInfo("Load Window ini file.");

        // Set default window parameters
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screen_width = (int) screenSize.getWidth();
        screen_height = (int) screenSize.getHeight();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String inFile = "";
        try {
            inFile = new String(Files.readAllBytes(Paths.get(windowIniFilepath)));
        } catch (IOException e) {
            DebugLog.logError("No Window ini file: ", windowIniFilepath);
        }

        if (!inFile.equals("")) {
            try {
                Type typeOfHashMap = new TypeToken<Map<String, Object>>() { }.getType();
                Map<String, Object> newMap = gson.fromJson(inFile, typeOfHashMap); // This type must match TypeToken
                LinkedTreeMap<String, Object> tmpLinkedMap = (LinkedTreeMap<String, Object>) newMap.get("position");
                Vector2f tmpVector = new Vector2f((float) (double) tmpLinkedMap.get("x"), (float) (double) tmpLinkedMap.get("y"));
                positionX = (int) tmpVector.x;
                positionY = (int) tmpVector.y;

                tmpLinkedMap = (LinkedTreeMap<String, Object>) newMap.get("size");
                tmpVector = new Vector2f((float) (double) tmpLinkedMap.get("x"), (float) (double) tmpLinkedMap.get("y"));
                width = (int) tmpVector.x;
                height = (int) tmpVector.y;

                isMinimized = !(boolean) newMap.get("isMaximized");
                return;
            } catch (NullPointerException ignored) { }
        }

        width = screen_width;
        height = screen_height;

        positionX = 0;
        positionY = 0;

        isMinimized = false;
    }

    private void loadEngineConfigFile() {
        DebugLog.logInfo("Load Engine config file.");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String inFile = "";
        try {
            inFile = new String(Files.readAllBytes(Paths.get(engineConfigFilepath)));
        } catch (IOException e) {
            DebugLog.logError("No Engine config file: ", engineConfigFilepath);
        }

        if (!inFile.equals("")) {
            try {
                Type typeOfHashMap = new TypeToken<Map<String, Object>>() {
                }.getType();
                Map<String, Object> newMap = gson.fromJson(inFile, typeOfHashMap); // This type must match TypeToken

                SceneManager.changeScene((String) newMap.get("currentScene"));

                lastOpenedScenePath = SceneManager.getCurrentScene().getFilepath();

                return;
            } catch (NullPointerException ignored) { }
        }

        SceneManager.changeScene("Assets/defaultScene.scene"); // Load default Scene
        lastOpenedScenePath = SceneManager.getCurrentScene().getFilepath();
    }

    private static void saveWindowIniFile() {
        DebugLog.logInfo("Save Window ini file.");

        int[] tmpPositionX = new int[1];
        int[] tmpPositionY = new int[1];
        glfwGetWindowPos(glfwWindow, tmpPositionX, tmpPositionY);
        int[] tmpSizeX = new int[1];
        int[] tmpSizeY = new int[1];
        glfwGetWindowSize(glfwWindow, tmpSizeX, tmpSizeY);

        Map<String, Object> windowParametersMap = new HashMap<>();
        windowParametersMap.put("position", new Vector2f(tmpPositionX[0], tmpPositionY[0]));
        windowParametersMap.put("size", new Vector2f(tmpSizeX[0], tmpSizeY[0]));
        windowParametersMap.put("isMaximized", glfwGetWindowAttrib(glfwWindow, GLFW_MAXIMIZED) == 1);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(windowParametersMap);

        try {
            FileWriter writer = new FileWriter(windowIniFilepath);
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(String.format("Error in saving Window ini file. Filepath - '%s'", windowIniFilepath), e);
        }
    }

    private static void saveEngineConfigFile() {
        DebugLog.logInfo("Save Engine config file.");

        Map<String, Object> engineParametersMap = new HashMap<>();
        engineParametersMap.put("currentScene", SceneManager.getCurrentScene().getFilepath().replace("\\", "/"));

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(engineParametersMap);

        try {
            FileWriter writer = new FileWriter(engineConfigFilepath);
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(String.format("Error in saving Engine config file. Filepath - '%s'", engineConfigFilepath), e);
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

    public static void minimize() {
        DebugLog.log("Window minimize.");
    } // TODO MINIMIZE WINDOW

    public static void maximize() {
        DebugLog.log("Window maximize.");
        glfwMaximizeWindow(glfwWindow);
    }

    public static void close() { isClosed = true; }

    public static Framebuffer getScreenFramebuffer() { return SceneManager.getCurrentScene().getEditorCamera().getOutputFob(); }

    public static float getTargetAspectRatio() { return 16.0f / 9.0f; }

    public static boolean isRuntimePlaying() { return runtimePlaying; }

    public static boolean isRuntimePause() { return runtimePause; }

    public static boolean isNextFrame() { return nextFrame; }

    @Override
    public void onNotify(Event event) {
        if (Console.isStopOnError() && runtimePlaying)
            if (event.type == EventType.Console_SendMessage && ((ConsoleMessage) event.data).type == ConsoleMessage.LogType.Error) {
                EventSystem.notify(new Event(EventType.Engine_StopPlay));
                return;
            }

        switch (event.type) {
            case Engine_StartPlay -> {
                runtimePlaying = true;
                nextFrame = true;
                lastOpenedScenePath = SceneManager.getCurrentScene().getFilepath();
                SceneManager.getCurrentScene().saveAs("tmpRuntimeScene.scene"); // Save to tmp scene file, to provide user not save scene before he starts playing
                SceneManager.changeScene("tmpRuntimeScene.scene");
                Outliner_Window.clearSelected();
            }
            case Engine_StopPlay -> {
                runtimePlaying = false;
                nextFrame = true;
                SceneManager.changeScene("tmpRuntimeScene.scene"); // Load from tmp scene file, to provide user not save scene before he starts playing
                SceneManager.getCurrentScene().saveAs(lastOpenedScenePath);
                SceneManager.changeScene(lastOpenedScenePath);
                Outliner_Window.clearSelected();
            }
            case Engine_Pause -> runtimePause = true;
            case Engine_Play -> runtimePause = false;
            case Engine_NextFrame -> nextFrame = true;
            case Engine_SaveScene -> SceneManager.getCurrentScene().save();
            case Engine_SaveSceneAs -> {
                String scenePath = FileDialogs.saveFile(new File(SceneManager.getCurrentScene().getFilepath()), FileTypeFilter.sceneFilter, SceneManager.getCurrentScene().getFilepath());
                if (scenePath != null && !scenePath.equals("")) {
                    SceneManager.getCurrentScene().saveAs(scenePath);
                    SceneManager.changeScene(scenePath);
                }
            }
            case Engine_OpenScene -> {
                String scenePath = FileDialogs.openFile(FileTypeFilter.sceneFilter, SceneManager.getCurrentScene().getFilepath());
                if (scenePath != null && !scenePath.equals(""))
                    SceneManager.changeScene(scenePath);
                lastOpenedScenePath = SceneManager.getCurrentScene().getFilepath();
            }
            case Engine_ReloadScene -> SceneManager.changeScene(SceneManager.getCurrentScene().getFilepath());
        }
    }
}
