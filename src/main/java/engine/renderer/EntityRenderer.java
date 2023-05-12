package engine.renderer;

import engine.entity.GameObject;
import engine.renderer.buffers.VertexArray;
import engine.renderer.renderer2D.SpriteMasterRenderer;
import engine.renderer.renderer2D.SpriteRenderer;
import engine.renderer.renderer3D.MeshMasterRenderer;
import engine.renderer.renderer3D.MeshRenderer;
import engine.renderer.shader.Shader;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;

public class EntityRenderer {

    private static Shader currentShader;

    private static final SpriteMasterRenderer spriteRenderer = new SpriteMasterRenderer();
    private static final MeshMasterRenderer meshRenderer = new MeshMasterRenderer();

    public static void init() {
        glEnable(GL_BLEND | GL_DEPTH_TEST);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void render(Matrix4f projectionMatrix, Matrix4f viewMatrix) {
        spriteRenderer.render(projectionMatrix, viewMatrix);
        meshRenderer.render(projectionMatrix, viewMatrix);
    }

    public static void render_SingleShader(Matrix4f projectionMatrix, Matrix4f viewMatrix, Shader shader) {
        spriteRenderer.render_SingleShader(projectionMatrix, viewMatrix, shader);
        meshRenderer.render_SingleShader(projectionMatrix, viewMatrix, shader);
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
            spriteRenderer.add(obj);
        if (obj.hasComponent(MeshRenderer.class))
            meshRenderer.add(obj);
    }

    public static void destroyGameObject(GameObject obj) {
        if (obj.hasComponent(SpriteRenderer.class))
            spriteRenderer.destroyGameObject(obj);
        if (obj.hasComponent(MeshRenderer.class))
            meshRenderer.destroyGameObject(obj);
    }

    public static void clear() {
        spriteRenderer.clear();
        meshRenderer.clear();
    }
}
