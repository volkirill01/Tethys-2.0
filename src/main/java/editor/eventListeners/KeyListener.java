package editor.eventListeners;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class KeyListener {

    private static final int KEYS_COUNT = 350;
    private static final boolean[] keyDown = new boolean[KEYS_COUNT];
    private static final boolean[] keyClick = new boolean[KEYS_COUNT];

    public static void endFrame() { Arrays.fill(keyClick, false); }

    public static void keyCallback(long window, int key, int scancode, int action, int mods) {
        if (action == GLFW_PRESS) {
            keyDown[key] = true;
            keyClick[key] = true;
        } else if (action == GLFW_RELEASE) {
            keyDown[key] = false;
        }
    }

    protected static boolean isKeyDown(int keyCode) {
        if (keyCode > KEYS_COUNT)
            throw new IndexOutOfBoundsException("'" + keyCode + "' - key out of range.");

        return keyDown[keyCode];
    }

    protected static boolean isKeyClick(int keyCode) {
        if (keyCode > KEYS_COUNT)
            throw new IndexOutOfBoundsException("'" + keyCode + "' - key out of range.");

        return keyClick[keyCode];
    }
}
