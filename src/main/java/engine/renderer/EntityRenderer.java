package engine.renderer;

import engine.entity.GameObject;
import engine.renderer.buffers.VertexArray;
import engine.renderer.renderer2D.SpriteMasterRenderer;
import engine.renderer.renderer2D.SpriteRenderer;
import engine.renderer.renderer3D.MeshMasterRenderer;
import engine.renderer.renderer3D.MeshRenderer;
import engine.renderer.shader.Shader;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30.*;

public class EntityRenderer {

    private static Shader currentShader;
    private static int textureSlotsCount;

    private static int drawCalls = 0;

    public static void init() {
        glEnable(GL_BLEND | GL_DEPTH_TEST);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        // Initialize texture slots count variable to GPU texture slots count
        IntBuffer buffer = BufferUtils.createIntBuffer(1);
        glGetIntegerv(GL_MAX_TEXTURE_IMAGE_UNITS, buffer);
        textureSlotsCount = buffer.get(0);
    }

    public static void render(Matrix4f projectionMatrix, Matrix4f viewMatrix) {
        SpriteMasterRenderer.render(projectionMatrix, viewMatrix);
        MeshMasterRenderer.render(projectionMatrix, viewMatrix);
    }

    public static void render_SingleShader(Matrix4f projectionMatrix, Matrix4f viewMatrix, Shader shader) {
        SpriteMasterRenderer.render_SingleShader(projectionMatrix, viewMatrix, shader);
        MeshMasterRenderer.render_SingleShader(projectionMatrix, viewMatrix, shader);
    }

    public static void beginScene() { } // TODO SEND GLOBAL DATA TO SHADER ONCE BEGIN OF FRAME, NOT AVERY TIME TO RENDER OBJECT

    public static void submit(VertexArray vao) {
        vao.bind();
        RenderCommand.drawIndexed(vao);
        vao.unbind();
    }

    public static void submit(VertexArray vao, int verticesCount) {
        vao.bind();
        RenderCommand.drawIndexed(verticesCount);
        vao.unbind();
    }

    public static void endScene() { }

    public static Shader getCurrentShader() { return currentShader; }

    public static void setShader(Shader shader) { currentShader = shader; }

    public static void add(GameObject obj) {
        if (obj.hasComponent(SpriteRenderer.class))
            SpriteMasterRenderer.add(obj);
        if (obj.hasComponent(MeshRenderer.class))
            MeshMasterRenderer.add(obj);
    }

    public static void destroyGameObject(GameObject obj) {
        if (obj.hasComponent(SpriteRenderer.class))
            SpriteMasterRenderer.destroyGameObject(obj);
        if (obj.hasComponent(MeshRenderer.class))
            MeshMasterRenderer.destroyGameObject(obj);
    }

    public static void clear() {
        SpriteMasterRenderer.clear();
        MeshMasterRenderer.clear();
    }

    public static int getTextureSlotsCount() { return textureSlotsCount; }

    public static int getDrawCalls() { return drawCalls; }

    public static void addDrawCall() { drawCalls++; }

    public static void resetStats() {
        drawCalls = 0;
//        SpriteMasterRenderer.resetStats();
    }
}
