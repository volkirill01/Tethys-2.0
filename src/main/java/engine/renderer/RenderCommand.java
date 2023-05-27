package engine.renderer;

import engine.profiling.Profiler;
import engine.renderer.buffers.VertexArray;
import engine.stuff.customVariables.Color;

import static org.lwjgl.opengl.GL30.*;

public class RenderCommand {

    public enum BufferBit {
        COLOR_BUFFER,
        DEPTH_BUFFER,
        COLOR_AND_DEPTH_BUFFER,
    }

    public static void setClearColor(Color color) {
        Profiler.startTimer("RendererCommand SetClearColor");
        glClearColor(color.r / 255.0f, color.g / 255.0f, color.b / 255.0f, color.a / 255.0f);
        Profiler.stopTimer("RendererCommand SetClearColor");
    }

    public static void clear(BufferBit bufferBit) {
        Profiler.startTimer("RendererCommand Clear");
        switch (bufferBit) {
            case COLOR_BUFFER -> glClear(GL_COLOR_BUFFER_BIT);
            case DEPTH_BUFFER -> glClear(GL_DEPTH_BUFFER_BIT);
            case COLOR_AND_DEPTH_BUFFER -> glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            default -> throw new IllegalStateException(String.format("Unknown Buffer bit - '%s'", bufferBit.name()));
        }
        Profiler.stopTimer("RendererCommand Clear");
    }

    public static void drawIndexed(VertexArray vao) {
        Profiler.startTimer("RendererCommand DrawIndexed");
        glDrawElements(GL_TRIANGLES, vao.getIndexBuffer().getCount(), GL_UNSIGNED_INT, 0);
        EntityRenderer.addDrawCall();
        Profiler.stopTimer("RendererCommand DrawIndexed");
    }

    public static void drawIndexed(int indicesCount) {
        Profiler.startTimer("RendererCommand DrawIndexed(Count)");
        glDrawElements(GL_TRIANGLES, indicesCount, GL_UNSIGNED_INT, 0);
        EntityRenderer.addDrawCall();
        Profiler.stopTimer("RendererCommand DrawIndexed(Count)");
    }
}
