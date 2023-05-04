package editor.renderer;

import editor.renderer.buffers.VertexArray;
import editor.stuff.customVariables.Color;

import static org.lwjgl.opengl.GL11.*;

public class RenderCommand {

    public enum BufferBit {
        ColorBuffer,
        DepthBuffer,
        ColorAndDepthBuffer,
    }

    public static void setClearColor(Color color) {
        glClearColor(color.r / 255.0f, color.g / 255.0f, color.b / 255.0f, color.a / 255.0f);
    }

    public static void clear(BufferBit bufferBit) {
        switch (bufferBit) {
            case ColorBuffer -> glClear(GL_COLOR_BUFFER_BIT);
            case DepthBuffer -> glClear(GL_DEPTH_BUFFER_BIT);
            case ColorAndDepthBuffer -> glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            default -> throw new IllegalStateException(String.format("Unknown Buffer bit - '%s'", bufferBit.name()));
        }
    }

    public static void drawIndexed(VertexArray vao) {
        glDrawElements(GL_TRIANGLES, vao.getIndexBuffer().getCount(), GL_UNSIGNED_INT, 0);
    }

    public static void drawIndexed(int verticesCount) {
        glDrawElements(GL_TRIANGLES, verticesCount, GL_UNSIGNED_INT, 0);
    }
}
