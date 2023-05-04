package editor.renderer;

import editor.entity.GameObject;
import editor.renderer.buffers.VertexArray;
import editor.renderer.renderer2D.SpriteMasterRenderer;
import editor.renderer.renderer2D.SpriteRenderer;
import editor.renderer.renderer3D.MeshMasterRenderer;
import editor.renderer.renderer3D.MeshRenderer;
import editor.renderer.shader.Shader;
import editor.stuff.customVariables.Color;
import org.joml.Matrix4f;

public class EntityRenderer {

    private static Shader currentShader;

    private final SpriteMasterRenderer spriteRenderer = new SpriteMasterRenderer();
    private final MeshMasterRenderer meshRenderer = new MeshMasterRenderer();

    public void render(Matrix4f projectionMatrix, Matrix4f viewMatrix) {
        RenderCommand.setClearColor(new Color(50.0f, 50.0f, 50.0f));
        RenderCommand.clear(RenderCommand.BufferBit.ColorBuffer);
        this.spriteRenderer.render(projectionMatrix, viewMatrix);
        this.meshRenderer.render(projectionMatrix, viewMatrix);
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

    public void add(GameObject obj) {
        if (obj.hasComponent(SpriteRenderer.class))
            this.spriteRenderer.add(obj);
        if (obj.hasComponent(MeshRenderer.class))
            this.meshRenderer.add(obj);
    }

    public void destroyGameObject(GameObject obj) {
        if (obj.hasComponent(SpriteRenderer.class))
            this.spriteRenderer.destroyGameObject(obj);
        if (obj.hasComponent(MeshRenderer.class))
            this.meshRenderer.destroyGameObject(obj);
    }
}
