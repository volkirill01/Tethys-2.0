package engine.eventListeners;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class KeyListener {

    private static final int KEYS_COUNT = 350;
    private static final boolean[] keysDown = new boolean[KEYS_COUNT];
    private static final boolean[] keysClick = new boolean[KEYS_COUNT];

    public static void endFrame() { Arrays.fill(keysClick, false); }

    public static void keyCallback(long window, int key, int scancode, int action, int mods) {
        if (action == GLFW_PRESS) {
            keysDown[key] = true;
            keysClick[key] = true;
        } else if (action == GLFW_RELEASE) {
            keysDown[key] = false;
        }
    }

    protected static boolean isKeyDown(int keyCode) {
        if (keyCode > KEYS_COUNT)
            throw new IndexOutOfBoundsException(String.format("'%d' - key out of range.", keyCode));

        return keysDown[keyCode];
    }

    protected static boolean isKeyClick(int keyCode) {
        if (keyCode > KEYS_COUNT)
            throw new IndexOutOfBoundsException(String.format("'%d' - key out of range.", keyCode));

        return keysClick[keyCode];
    }

    protected static boolean isAnyKeyDown() {
        for (boolean key : keysDown)
            if (key)
                return true;
        return false;
    }
}
