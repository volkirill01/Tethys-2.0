package engine.renderer;

import engine.entity.GameObject;
import engine.profiling.Profiler;
import engine.renderer.buffers.VertexArray;
import engine.renderer.camera.ed_BaseCamera;
import engine.renderer.renderer2D.*;
import engine.renderer.renderer3D.MeshMasterRenderer;
import engine.renderer.renderer3D.MeshRenderer;
import engine.renderer.shader.Shader;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30.*;

public class EntityRenderer {

    private static Shader currentShader;
    private static int textureSlotsCount;

    private static int drawCalls = 0;

    public static void init() {
        Profiler.startTimer("EntityRenderer Init");
        glEnable(GL_BLEND | GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        // Initialize texture slots count variable to GPU texture slots count
        IntBuffer buffer = BufferUtils.createIntBuffer(1);
        glGetIntegerv(GL_MAX_TEXTURE_IMAGE_UNITS, buffer);
        textureSlotsCount = buffer.get(0);
        Profiler.stopTimer("EntityRenderer Init");
    }

    public static void render(ed_BaseCamera camera) {
        Profiler.startTimer("EntityRenderer Render");
        MasterRenderer2D.render(camera);
        MeshMasterRenderer.render(camera);
        Profiler.stopTimer("EntityRenderer Render");
    }

    public static void beginScene() { } // TODO SEND GLOBAL DATA TO SHADER ONCE BEGIN OF FRAME, NOT AVERY TIME TO RENDER OBJECT

    public static void uploadSceneData(ed_BaseCamera camera) {
        Profiler.startTimer("EntityRenderer Upload SceneData");
        currentShader.getUniformBuffer("u_SceneData").uploadData("u_ProjectionMatrix", camera.getProjectionMatrix()); // TODO UPLOAD THIS FROM ALL CAMERAS, NOT STATIC EDITOR CAMERA
        currentShader.getUniformBuffer("u_SceneData").uploadData("u_ViewMatrix", camera.getViewMatrix());
        Profiler.stopTimer("EntityRenderer Upload SceneData");
    }

    public static void submit(VertexArray vao) {
        Profiler.startTimer("EntityRenderer Submit");
        vao.bind();
        RenderCommand.drawIndexed(vao);
        vao.unbind();
        Profiler.stopTimer("EntityRenderer Submit");
    }

    public static void submit(VertexArray vao, int verticesCount) {
        Profiler.startTimer("EntityRenderer Submit");
        vao.bind();
        RenderCommand.drawIndexed(verticesCount);
        vao.unbind();
        Profiler.stopTimer("EntityRenderer Submit");
    }

    public static void endScene() { }

    public static Shader getCurrentShader() { return currentShader; }

    public static void setShader(Shader shader) { currentShader = shader; }

    public static void add(GameObject obj) { // TODO FIX BUG ADDING COMPONENT TO GAME OBJECT NOT ADD IT TO RENDERERS
        Profiler.startTimer("EntityRenderer Add GameObject");
        if (obj.hasComponent(ed_Renderer.class))
            MasterRenderer2D.add(obj);
        if (obj.hasComponent(MeshRenderer.class))
            MeshMasterRenderer.add(obj);
        Profiler.stopTimer("EntityRenderer Add GameObject");
    }

    public static void destroyGameObject(GameObject obj) {
        Profiler.startTimer("EntityRenderer Destroy GameObject");
        if (obj.hasComponent(ed_Renderer.class))
            MasterRenderer2D.destroyGameObject(obj);
        if (obj.hasComponent(MeshRenderer.class))
            MeshMasterRenderer.destroyGameObject(obj);
        Profiler.stopTimer("EntityRenderer Destroy GameObject");
    }

    public static void clear() {
        Profiler.startTimer("EntityRenderer Clear");
        MasterRenderer2D.clear();
        MeshMasterRenderer.clear();
        Profiler.stopTimer("EntityRenderer Clear");
    }

    public static int getTextureSlotsCount() { return textureSlotsCount; }

    public static int getDrawCalls() { return drawCalls; }

    public static void addDrawCall() { drawCalls++; }

    public static void resetStats() {
        drawCalls = 0;
//        SpriteMasterRenderer.resetStats();
    }
}
