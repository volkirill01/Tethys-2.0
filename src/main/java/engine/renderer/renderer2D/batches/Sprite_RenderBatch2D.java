package engine.renderer.renderer2D.batches;

import engine.profiling.Profiler;
import engine.renderer.EntityRenderer;
import engine.renderer.Texture2D;
import engine.renderer.buffers.bufferLayout.VertexBufferElement;
import engine.renderer.buffers.bufferLayout.BufferLayout;
import engine.renderer.renderer2D.MasterRenderer2D;
import engine.renderer.renderer2D.SpriteRenderer;
import engine.renderer.renderer2D.ed_Renderer;
import engine.stuff.openGL.ShaderDataType;
import engine.renderer.shader.Shader;
import engine.stuff.utils.Maths;
import engine.stuff.customVariables.Color;
import org.joml.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Sprite_RenderBatch2D extends RenderBatch2D {

    private final int[] texSlots;
    private final List<Texture2D> textures = new ArrayList<>();

    public Sprite_RenderBatch2D(int maxBatchSize, int zIndex) {
        super(maxBatchSize, zIndex);

        // Initialize texture slots array to maximum GPU texture slots
        this.texSlots = new int[EntityRenderer.getTextureSlotsCount()];
        for (int i = 0; i < this.texSlots.length; i++)
            this.texSlots[i] = i;
    }

    public void init() {
        super.init(new BufferLayout(Arrays.asList(
                new VertexBufferElement(ShaderDataType.Float3, "a_Position"),
                new VertexBufferElement(ShaderDataType.Float4, "a_Color"),
                new VertexBufferElement(ShaderDataType.Float2, "a_TextureCoordinates"),
                new VertexBufferElement(ShaderDataType.Float, "a_TextureID"),
                new VertexBufferElement(ShaderDataType.Int, "a_EntityID"),
                new VertexBufferElement(ShaderDataType.Float2, "a_Tiling")
        )));
    }

    @Override
    public void addQuad(ed_Renderer renderer) {
        SpriteRenderer _renderer = (SpriteRenderer) renderer;
        if (_renderer.getSprite().getTexture() != null)
            if (!this.textures.contains(_renderer.getSprite().getTexture()))
                this.textures.add(_renderer.getSprite().getTexture());
        super.addQuad(_renderer);
    }

    @Override
    public void render() {
        Profiler.startTimer("Render in RenderBatch2D");
        boolean rebufferData = false;
        for (int i = 0; i < this.numberOfQuads; i++) {
            if (this.quads[i].getClass() != SpriteRenderer.class)
                continue;

            SpriteRenderer renderer = (SpriteRenderer) this.quads[i];
            if (!hasTexture(renderer.getSprite().getTexture()) && renderer.getSprite().getTexture() != null) {
                MasterRenderer2D.destroyGameObject(renderer.gameObject, SpriteRenderer.class);
                MasterRenderer2D.add(renderer.gameObject);
            } else {
                loadVertexProperties(i);
                rebufferData = true;
            }

            // TODO GET BETTER SOLUTION FOR THIS
            if (renderer.gameObject.transform.getZIndex() != this.zIndex) {
                destroyIfExists(renderer.gameObject, SpriteRenderer.class);
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
        for (int i = 0; i < this.textures.size(); i++)
            this.textures.get(i).bind(i + 1);

        shader.uploadIntArray("u_TextureIDs", this.texSlots);

        EntityRenderer.submit(this.vao, this.numberOfQuads * 6); // 6 indices per quad

        this.vao.getVertexBuffer().unbind();

        for (Texture2D texture : textures)
            texture.unbind();

        Profiler.stopTimer("Render in RenderBatch2D");
    }

    @Override
    protected void loadVertexProperties(int index) {
        if (this.quads[index].getClass() != SpriteRenderer.class)
            return;

        SpriteRenderer sprite = (SpriteRenderer) this.quads[index];

        // Find offset within array (4 vertices per sprite)
        int offset = index * 4 * vertexSize;

        Color color = sprite.getColor();
        Vector2f[] textureCoordinates = sprite.getSprite().getTextureCoordinates();

        int textureID = 0;
        if (sprite.getSprite().getTexture() != null)
            for (int i = 0; i < this.textures.size(); i++)
                if (this.textures.get(i).equals(sprite.getSprite().getTexture())) {
                    textureID = i + 1;
                    break;
                }

        boolean isRotated = sprite.gameObject.transform.rotation.x != 0.0f || sprite.gameObject.transform.rotation.y != 0.0f || sprite.gameObject.transform.rotation.z != 0.0f;
        Matrix4f transformationMatrix = null;
        if (isRotated)
            transformationMatrix = Maths.createTransformationMatrix(sprite.gameObject.transform.position, sprite.gameObject.transform.rotation, sprite.gameObject.transform.scale);

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
                    sprite.gameObject.transform.position.x + (xAdd * sprite.gameObject.transform.scale.x),
                    sprite.gameObject.transform.position.y + (yAdd * sprite.gameObject.transform.scale.y),
                    sprite.gameObject.transform.position.z + (zAdd * sprite.gameObject.transform.scale.z),
                    1.0f
            );
            if (isRotated)
                currentPos = new Vector4f(xAdd, yAdd, zAdd, 1.0f).mul(transformationMatrix);

            // Load position
            vertices[offset + 0]    = currentPos.x;
            vertices[offset + 1]    = currentPos.y;
            vertices[offset + 2]    = currentPos.z;

            // Load color
            vertices[offset + 3]    = (color.r / 255.0f);
            vertices[offset + 4]    = (color.g / 255.0f);
            vertices[offset + 5]    = (color.b / 255.0f);
            vertices[offset + 6]    = (color.a / 255.0f);

            // Load texture coordinates
            vertices[offset + 7]    = textureCoordinates[i].x;
            vertices[offset + 8]    = textureCoordinates[i].y;

            // Load texture ID
            vertices[offset + 9]    = textureID;

            // Load entity ID
            vertices[offset + 10]   = sprite.gameObject.getIncrementedID() + 1; // We used 0 id for invalid object

            // Load texture tiling
            vertices[offset + 11]   = sprite.getTiling().x;
            vertices[offset + 12]   = sprite.getTiling().y;

            offset += vertexSize;
        }
    }

    public boolean hasTextureRoom() { return this.textures.size() < 8; }

    public boolean hasTexture(Texture2D texture) { return this.textures.contains(texture); }
}
