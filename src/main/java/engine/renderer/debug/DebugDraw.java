package engine.renderer.debug;

import engine.assets.AssetPool;
import engine.profiling.Profiler;
import engine.renderer.EntityRenderer;
import engine.renderer.camera.ed_EditorCamera;
import engine.renderer.shader.Shader;
import engine.scenes.SceneManager;
import engine.stuff.Maths;
import engine.stuff.customVariables.Color;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class DebugDraw {

    private static final int MAX_LINES = 5000;

    private static final List<DebugLine> lines = new ArrayList<>();
    // 6 floats per vertex, 2 vertices per line
    private static final float[] vertexArray =  new float[MAX_LINES * 6 * 2];
    private static final Shader shader = AssetPool.getShader("editorFiles/shaders/debug/debugLineShader.glsl");

    private static int vaoID;
    private static int vboID;

    private static boolean started = false;

    public static void start() {
        // Generate the vao
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Create the vbo and buffer some memory
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, (long) vertexArray.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // Enable the vertex array attributes
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glLineWidth(3.0f);
    }

    public static void beginFrame() {
        if (!started) {
            start();
            started = true;
        }

        // Remove deadlines
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).beginFrame() < 0) {
                lines.remove(i);
                i--;
            }
        }
    }

    public static void draw() {
        if (lines.size() == 0)
            return;

        Profiler.startTimer("Debug Render");
        int index = 0;
        for (DebugLine line : lines) {
            for (int i = 0; i < 2; i++) {
                Vector3f position = i == 0 ? line.getFrom() : line.getTo();
                Color color = line.getColor();

                // Load position
                vertexArray[index] = position.x;
                vertexArray[index + 1] = position.y;
                vertexArray[index + 2] = position.z;

                // Load the color
                vertexArray[index + 3] = color.r / 255.0f;
                vertexArray[index + 4] = color.g / 255.0f;
                vertexArray[index + 5] = color.b / 255.0f;
                index += 6;
            }
        }

//        glClear(GL_DEPTH_BUFFER_BIT); // TODO MAKE IT FOR USER

        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertexArray);
//        glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertexArray, 0, lines.size() * 6 * 2));

        // =============================================================================================================
        // Rendering
        // =============================================================================================================

        // Bind shader
        EntityRenderer.setShader(shader);
        shader.bind();
        shader.uploadInt("u_EntityID", -99);
        shader.uploadMat4f("u_ProjectionMatrix", SceneManager.getCurrentScene().getEditorCamera().getProjectionMatrix());
        shader.uploadMat4f("u_ViewMatrix", SceneManager.getCurrentScene().getEditorCamera().getViewMatrix());

        // Bind the vao
        glBindVertexArray(vaoID);
//        glEnableVertexAttribArray(0);
//        glEnableVertexAttribArray(1);

        // Draw the batch
        glDrawArrays(GL_LINES, 0, lines.size());

        // Disable location
//        glDisableVertexAttribArray(0);
//        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        // Unbind shader
        shader.unbind();
        Profiler.stopTimer("Debug Render");
    }

    // =================================================================================================================
    // Add line methods
    // =================================================================================================================
    public static void addLine(Vector3f from, Vector3f to) { addLine(from, to, Color.GREEN, 1); }
    public static void addLine(Vector3f from, Vector3f to, Color color) { addLine(from, to, color, 1); }
    /** @param lifetime in frames */
    public static void addLine(Vector3f from, Vector3f to, Color color, int lifetime) {
        ed_EditorCamera camera = SceneManager.getCurrentScene().getEditorCamera();

        Vector2f cameraProjectionSize = new Vector2f(camera.getOrthographicProjectionSize()).mul(camera.getZoom());

        Vector2f cameraLeft = new Vector2f(camera.getPosition().x - cameraProjectionSize.x / 2, camera.getPosition().y - cameraProjectionSize.y / 2).sub(2.0f, 2.0f);
        Vector2f cameraRight = new Vector2f(camera.getPosition().x + cameraProjectionSize.x / 2, camera.getPosition().y + cameraProjectionSize.y / 2).add(4.0f, 4.0f);
        boolean lineInView = ((from.x >= cameraLeft.x && from.x <= cameraRight.x) && (from.y >= cameraLeft.y && from.y <= cameraRight.y)) ||
                ((to.x >= cameraLeft.x && to.x <= cameraRight.x) && (to.y >= cameraLeft.y && to.y <= cameraRight.y));

        if (lines.size() >= MAX_LINES || !lineInView)
            return;

        DebugDraw.lines.add(new DebugLine(from, to, color, lifetime));
    }

    // =================================================================================================================
    // Add box2D methods
    // =================================================================================================================
    public static void addBox2D(Vector3f center, Vector2f size) { addBox2D(center, size, new Vector3f(0.0f), Color.GREEN, 1); }
    public static void addBox2D(Vector3f center, Vector2f size, Vector3f rotation) { addBox2D(center, size, rotation, Color.GREEN, 1); }
    public static void addBox2D(Vector3f center, Vector2f size, Color color) { addBox2D(center, size, new Vector3f(0.0f), color, 1); }
    public static void addBox2D(Vector3f center, Vector2f size, Vector3f rotation, Color color) { addBox2D(center, size, rotation, color, 1); }
    /** @param lifetime in frames */
    public static void addBox2D(Vector3f center, Vector2f size, Vector3f rotation, Color color, int lifetime) {
        Vector3f min = new Vector3f(center).sub(size.x / 2.0f, size.y / 2.0f, 0.0f);
        Vector3f max = new Vector3f(center).add(size.x / 2.0f, size.y / 2.0f, 0.0f);

        Vector3f[] vertices = {
                new Vector3f(min.x, min.y, min.z), // bottom
                new Vector3f(min.x, max.y, min.z), // top left
                new Vector3f(max.x, max.y, min.z), // top right
                new Vector3f(max.x, min.y, min.z), // bottom right
        };

        Maths.rotate3DVertices(vertices, center, rotation);

        addLine(vertices[0], vertices[1], color, lifetime); // back left
        addLine(vertices[0], vertices[3], color, lifetime); // back bottom
        addLine(vertices[1], vertices[2], color, lifetime); // back top
        addLine(vertices[2], vertices[3], color, lifetime); // back right
    }

    // =================================================================================================================
    // Add cube methods
    // =================================================================================================================
    public static void addCube(Vector3f center, Vector3f size) { addCube(center, size, new Vector3f(0.0f), Color.GREEN, 1); }
    public static void addCube(Vector3f center, Vector3f size, Vector3f rotation) { addCube(center, size, rotation, Color.GREEN, 1); }
    public static void addCube(Vector3f center, Vector3f size, Color color) { addCube(center, size, new Vector3f(0.0f), color, 1); }
    public static void addCube(Vector3f center, Vector3f size, Vector3f rotation, Color color) { addCube(center, size, rotation, color, 1); }
    /** @param lifetime in frames */
    public static void addCube(Vector3f center, Vector3f size, Vector3f rotation, Color color, int lifetime) {
        Vector3f halfSize = new Vector3f(size).div(2.0f);
        Vector3f min = new Vector3f(center).sub(halfSize);
        Vector3f max = new Vector3f(center).add(halfSize);

        Vector3f[] vertices = {
                new Vector3f(min.x, min.y, min.z), // bottom left back
                new Vector3f(min.x, max.y, min.z), // top left back
                new Vector3f(max.x, max.y, min.z), // top right back
                new Vector3f(max.x, min.y, min.z), // bottom right back
                new Vector3f(min.x, min.y, max.z), // bottom left front
                new Vector3f(min.x, max.y, max.z), // top left front
                new Vector3f(max.x, max.y, max.z), // top right front
                new Vector3f(max.x, min.y, max.z)  // bottom right front
        };
        Maths.rotate3DVertices(vertices, center, rotation);

        addLine(vertices[0], vertices[1], color, lifetime); // back left
        addLine(vertices[0], vertices[3], color, lifetime); // back bottom
        addLine(vertices[1], vertices[2], color, lifetime); // back top
        addLine(vertices[2], vertices[3], color, lifetime); // back right

        addLine(vertices[4], vertices[5], color, lifetime); // front left
        addLine(vertices[4], vertices[7], color, lifetime); // front bottom
        addLine(vertices[5], vertices[6], color, lifetime); // front top
        addLine(vertices[6], vertices[7], color, lifetime); // front right

        addLine(vertices[5], vertices[1], color, lifetime); // top left

        addLine(vertices[6], vertices[2], color, lifetime); // top right

        addLine(vertices[4], vertices[0], color, lifetime); // bottom left

        addLine(vertices[7], vertices[3], color, lifetime); // bottom right
    }

    // =================================================================================================================
    // Add circle2D methods
    // =================================================================================================================
    public static void addCircle2D(Vector3f center, float radius) { addCircle2D(center, radius, new Vector3f(0.0f), Color.GREEN, 1); }
    public static void addCircle2D(Vector3f center, float radius, Color color) { addCircle2D(center, radius, new Vector3f(0.0f), color, 1); }
    public static void addCircle2D(Vector3f center, float radius, Vector3f rotation) { addCircle2D(center, radius, rotation, Color.GREEN, 1); }
    public static void addCircle2D(Vector3f center, float radius, Vector3f rotation, Color color) { addCircle2D(center, radius, rotation, color, 1); }
    /** @param lifetime in frames */
    public static void addCircle2D(Vector3f center, float radius, Vector3f rotation, Color color, int lifetime) {
        Vector3f[] points = new Vector3f[30];
        int increment = 360 / points.length;
        int currentAngle = 0;

        for (int i = 0; i < points.length; i++) {
            Vector2f tmp = new Vector2f(radius / 2.0f + 0.001f, 0.0f);
            Maths.rotate(tmp, currentAngle, new Vector2f(0.0f));
            Vector3f point = new Vector3f(tmp.x, tmp.y, 0.0f);
            Maths.rotate3D(point, rotation);
            points[i] = point.add(center);

            if (i > 0)
                addLine(points[i - 1], points[i], color, lifetime);

            currentAngle += increment;
        }

        addLine(points[points.length - 1], points[0], color, lifetime);
    }

    // =================================================================================================================
    // Add sphere methods
    // =================================================================================================================
    public static void addSphere(Vector3f center, float radius) { addSphere(center, radius, new Vector3f(0.0f), Color.GREEN, 1); }
    public static void addSphere(Vector3f center, float radius, Color color) { addSphere(center, radius, new Vector3f(0.0f), color, 1); }
    public static void addSphere(Vector3f center, float radius, Vector3f rotation) { addSphere(center, radius, rotation, Color.GREEN, 1); }
    public static void addSphere(Vector3f center, float radius, Vector3f rotation, Color color) { addSphere(center, radius, rotation, color, 1); }
    /** @param lifetime in frames */
    public static void addSphere(Vector3f center, float radius, Vector3f rotation, Color color, int lifetime) {
        Vector3f[] points = new Vector3f[90];
        int increment = 360 / 30;
        int currentAngle = 0;

        for (int i = 0; i < 30; i++) {
            Vector2f tmp = new Vector2f(radius / 2.0f + 0.001f, 0.0f);
            Maths.rotate(tmp, currentAngle, new Vector2f(0.0f));
            Vector3f point = new Vector3f(tmp.x, tmp.y, 0.0f);
            points[i] = point.add(center);

            if (i > 0)
                addLine(points[i - 1], points[i], color, lifetime);

            currentAngle += increment;
        }
        addLine(points[29], points[0], color, lifetime);

        currentAngle = 0;
        for (int i = 30; i < 60; i++) {
            Vector2f tmp = new Vector2f(radius / 2.0f + 0.001f, 0.0f);
            Maths.rotate(tmp, currentAngle, new Vector2f(0.0f));
            Vector3f point = new Vector3f(tmp.x, tmp.y, 0.0f);
            Maths.rotateY(point, 90.0f);
            points[i] = point.add(center);

            if (i > 30)
                addLine(points[i - 1], points[i], color, lifetime);

            currentAngle += increment;
        }
        addLine(points[59], points[30], color, lifetime);

        currentAngle = 0;
        for (int i = 60; i < 90; i++) {
            Vector2f tmp = new Vector2f(radius / 2.0f + 0.001f, 0.0f);
            Maths.rotate(tmp, currentAngle, new Vector2f(0.0f));
            Vector3f point = new Vector3f(tmp.x, tmp.y, 0.0f);
            Maths.rotateX(point, 90.0f);
            points[i] = point.add(center);

            if (i > 60)
                addLine(points[i - 1], points[i], color, lifetime);

            currentAngle += increment;
        }
        addLine(points[89], points[60], color, lifetime);

        Maths.rotate3DVertices(points, center, rotation);
    }

    public static void clearLines() {
        lines.clear();
        Arrays.fill(vertexArray, 0.0f);
    }
}
