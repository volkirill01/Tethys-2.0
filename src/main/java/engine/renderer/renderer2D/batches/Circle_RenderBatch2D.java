package engine.renderer.renderer2D.batches;

import engine.profiling.Profiler;
import engine.renderer.EntityRenderer;
import engine.renderer.buffers.bufferLayout.BufferLayout;
import engine.renderer.buffers.bufferLayout.VertexBufferElement;
import engine.renderer.renderer2D.ShapeRenderer2D;
import engine.renderer.renderer2D.MasterRenderer2D;
import engine.renderer.renderer2D.ed_Renderer;
import engine.renderer.shader.Shader;
import engine.stuff.Maths;
import engine.stuff.customVariables.Color;
import engine.stuff.openGL.ShaderDataType;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.Arrays;

public class Circle_RenderBatch2D extends RenderBatch2D {

    public Circle_RenderBatch2D(int maxBatchSize, int zIndex) { super(maxBatchSize, zIndex); }

    public void init() {
        super.init(new BufferLayout(Arrays.asList(
                new VertexBufferElement(ShaderDataType.Float3, "a_Position"),
                new VertexBufferElement(ShaderDataType.Float2, "a_UV"),
                new VertexBufferElement(ShaderDataType.Float4, "a_Color"),
                new VertexBufferElement(ShaderDataType.Float, "a_Thickness"),
                new VertexBufferElement(ShaderDataType.Float, "a_Fade"),
                new VertexBufferElement(ShaderDataType.Int, "a_EntityID")
        )));
    }

    @Override
    public void addQuad(ed_Renderer renderer) { super.addQuad(renderer); }

    @Override
    public void render() {
        Profiler.startTimer("Render in Circle_RenderBatch2D");
        boolean rebufferData = false;
        for (int i = 0; i < this.numberOfQuads; i++) {
            if (this.quads[i].getClass() != ShapeRenderer2D.class)
                continue;

            loadVertexProperties(i);
            rebufferData = true;

            // TODO GET BETTER SOLUTION FOR THIS
            ShapeRenderer2D renderer = (ShapeRenderer2D) this.quads[i];
            if (renderer.gameObject.transform.getZIndex() != this.zIndex) {
                destroyIfExists(renderer.gameObject, ShapeRenderer2D.class);
                MasterRenderer2D.add(renderer.gameObject);
                i--;
            }
        }

        // Send data tu GPU only if data is changed
        if (rebufferData) {
            // TODO SEND ONLY CHANGED DATA, NOT ALL DATA
            this.vao.getVertexBuffer().setData(this.vertices);
        }

        Shader shader = EntityRenderer.getCurrentShader();
        shader.uploadInt("u_EntityID", -1);

        EntityRenderer.submit(this.vao, this.numberOfQuads * 6); // 6 indices per quad

        this.vao.getVertexBuffer().unbind();

        Profiler.stopTimer("Render in Circle_RenderBatch2D");
    }

    @Override
    protected void loadVertexProperties(int index) {
        if (this.quads[index].getClass() != ShapeRenderer2D.class)
            return;

        ShapeRenderer2D shape = (ShapeRenderer2D) this.quads[index];

        // Find offset within array (4 vertices per sprite)
        int offset = index * 4 * vertexSize;

        Color color = shape.getColor();

        boolean isRotated = shape.gameObject.transform.rotation.x != 0.0f || shape.gameObject.transform.rotation.y != 0.0f || shape.gameObject.transform.rotation.z != 0.0f;
        Matrix4f transformationMatrix = null;
        if (isRotated)
            transformationMatrix = Maths.createTransformationMatrix(shape.gameObject.transform.position, shape.gameObject.transform.rotation, shape.gameObject.transform.scale);

        // Add vertices with the appropriate properties
        float xAdd = 0.5f;
        float yAdd = 0.5f;
        float zAdd = 0.0f;

        for (int i = 0; i < 4; i++) { // 4 vertices per sprite
            if (i == 1)
                yAdd = -0.5f;
            else if (i == 2)
                xAdd = -0.5f;
            else if (i == 3)
                yAdd = 0.5f;

            Vector4f currentPos = new Vector4f(
                    shape.gameObject.transform.position.x + (xAdd * shape.gameObject.transform.scale.x * (shape.getRadius() * 2.0f)),
                    shape.gameObject.transform.position.y + (yAdd * shape.gameObject.transform.scale.y * (shape.getRadius() * 2.0f)),
                    shape.gameObject.transform.position.z + (zAdd * shape.gameObject.transform.scale.z * (shape.getRadius() * 2.0f)),
                    1.0f
            );
            if (isRotated)
                currentPos = new Vector4f(xAdd, yAdd, zAdd, 1.0f).mul(transformationMatrix);

            // Load position
            vertices[offset + 0]    = currentPos.x;
            vertices[offset + 1]    = currentPos.y;
            vertices[offset + 2]    = currentPos.z;

            // Load UV
            vertices[offset + 3]    = xAdd * 2.0f;
            vertices[offset + 4]    = yAdd * 2.0f;

            // Load color
            vertices[offset + 5]    = (color.r / 255.0f);
            vertices[offset + 6]    = (color.g / 255.0f);
            vertices[offset + 7]    = (color.b / 255.0f);
            vertices[offset + 8]    = (color.a / 255.0f);

            // Load circle parameters
            vertices[offset + 9]    = shape.getThickness();
            vertices[offset + 10]   = shape.getFade();

            // Load entity ID
            vertices[offset + 11]   = shape.gameObject.getIncrementedID() + 1; // We used 0 id for invalid object

            offset += vertexSize;
        }
    }
}
