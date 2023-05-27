package engine.renderer.renderer2D;

import engine.entity.GameObject;
import engine.profiling.Profiler;
import engine.renderer.EntityRenderer;
import engine.renderer.Texture2D;
import engine.renderer.buffers.VertexArray;
import engine.renderer.buffers.bufferLayout.VertexBufferElement;
import engine.renderer.buffers.bufferLayout.BufferLayout;
import engine.stuff.openGL.ShaderDataType;
import engine.renderer.shader.Shader;
import engine.renderer.buffers.IndexBuffer;
import engine.renderer.buffers.VertexBuffer;
import engine.stuff.Maths;
import engine.stuff.customVariables.Color;
import org.joml.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RenderBatch2D implements Comparable<RenderBatch2D> {

    private final SpriteRenderer[] sprites;
    private int numberOfSprites;
    private boolean hasRoom;
    private float[] vertices;
    private final int[] texSlots;

    private final List<Texture2D> textures = new ArrayList<>();;
    private VertexArray vao;
    private int vertexSize;
    private final int maxBathSize;
    private final int zIndex;

    public RenderBatch2D(int maxBatchSize, int zIndex) {
        this.maxBathSize = maxBatchSize;
        this.sprites = new SpriteRenderer[this.maxBathSize];
        this.zIndex = zIndex;

        // Initialize texture slots array to maximum GPU texture slots
        this.texSlots = new int[EntityRenderer.getTextureSlotsCount()];
        for (int i = 0; i < this.texSlots.length; i++)
            this.texSlots[i] = i;

        this.numberOfSprites = 0;
        this.hasRoom = true;
    }

    public void init() {
        Profiler.startTimer("RenderBatch2D Init");
        // Generate and bind a Vertex Array Object (VAO)
        this.vao = new VertexArray();

        // Create and upload vertices buffer (VBO)
        BufferLayout layout = new BufferLayout(Arrays.asList(
                new VertexBufferElement(ShaderDataType.Float3, "a_Position"),
                new VertexBufferElement(ShaderDataType.Float4, "a_Color"),
                new VertexBufferElement(ShaderDataType.Float2, "a_TextureCoordinates"),
                new VertexBufferElement(ShaderDataType.Float, "a_TextureID"),
                new VertexBufferElement(ShaderDataType.Int, "a_EntityID"),
                new VertexBufferElement(ShaderDataType.Float2, "a_Tiling")
        ));
        this.vertexSize = layout.getStride() / Float.BYTES;
        // 4 vertices per quad
        this.vertices = new float[this.maxBathSize * 4 * this.vertexSize];
        VertexBuffer vbo = new VertexBuffer(this.vertices);
        vbo.setLayout(layout);
        this.vao.setVertexBuffer(vbo);

        // Create and upload elements buffer (EBO)
        int[] elements = generateIndices();
        IndexBuffer ebo = new IndexBuffer(elements, elements.length);
        this.vao.setIndexBuffer(ebo);

        this.vao.unbind();
        Profiler.stopTimer("RenderBatch2D Init");
    }

    public void addSprite(SpriteRenderer renderer) {
        Profiler.startTimer(String.format("Add Sprite to RenderBatch2D. Obj Name - '%s'", renderer.gameObject.getName()));
        // Get index and add render object
        int index = this.numberOfSprites;
        this.sprites[index] = renderer;
        this.numberOfSprites++;

        if (renderer.getSprite().getTexture() != null)
            if (!this.textures.contains(renderer.getSprite().getTexture()))
                this.textures.add(renderer.getSprite().getTexture());

        // Add properties to local vertices array
        loadVertexProperties(index);

        if (this.numberOfSprites >= this.maxBathSize)
            this.hasRoom = false;
        Profiler.stopTimer(String.format("Add Sprite to RenderBatch2D. Obj Name - '%s'", renderer.gameObject.getName()));
    }

    public void render() {
        Profiler.startTimer("Render in RenderBatch2D");
        boolean rebufferData = false;
        for (int i = 0; i < this.numberOfSprites; i++) {
            SpriteRenderer renderer = this.sprites[i];
            if (renderer.isDirty()) {
                if (!hasTexture(renderer.getSprite().getTexture()) && renderer.getSprite().getTexture() != null) {
                    SpriteMasterRenderer.destroyGameObject(renderer.gameObject);
                    SpriteMasterRenderer.add(renderer.gameObject);
                } else {
                    loadVertexProperties(i);
                    renderer.setDirty(false);
                    rebufferData = true;
                }
            }

            // TODO GET BETTER SOLUTION FOR THIS
            if (renderer.gameObject.transform.getZIndex() != this.zIndex) {
                destroyIfExists(renderer.gameObject);
                SpriteMasterRenderer.add(renderer.gameObject);
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

        EntityRenderer.submit(this.vao, this.numberOfSprites * 6); // 6 indices per quad

        this.vao.getVertexBuffer().unbind();

        for (Texture2D texture : textures)
            texture.unbind();

        Profiler.stopTimer("Render in RenderBatch2D");
    }

    public boolean destroyIfExists(GameObject obj) {
        Profiler.startTimer("Destroy in RenderBatch2D");
        SpriteRenderer renderer = obj.getComponent(SpriteRenderer.class);
        for (int i = 0; i < this.numberOfSprites; i++) {
            if (this.sprites[i] == renderer) {
                for (int j = i; j < this.numberOfSprites - 1; j++) {
                    this.sprites[j] = this.sprites[j + 1];
                    this.sprites[j].setDirty(true);
                }
                this.numberOfSprites--;
                Profiler.stopTimer("Destroy in RenderBatch2D");
                return true;
            }
        }
        Profiler.stopTimer("Destroy in RenderBatch2D");
        return false;
    }

    private void loadVertexProperties(int index) {
        SpriteRenderer sprite = this.sprites[index];

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
            vertices[offset + 0]        = currentPos.x;
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
            vertices[offset + 10]   = sprite.gameObject.getUid() + 1; // We used 0 id for invalid object

            // Load texture tiling
            vertices[offset + 11]   = sprite.getTiling().x;
            vertices[offset + 12]   = sprite.getTiling().y;

            offset += vertexSize;
        }
    }

    private int[] generateIndices() {
        // 6 indices per quad (3 per triangle)
        int[] elements = new int[6 * this.maxBathSize];
        for (int i = 0; i < this.maxBathSize; i++)
            loadElementIndices(elements, i);

        return elements;
    }

    private void loadElementIndices(int[] elements, int index) {
        // 6 indices per quad (3 per triangle)
        int offsetArrayIndex = 6 * index;
        int offset = 4 * index;

        // Triangle 1
        elements[offsetArrayIndex + 0]  = offset + 3;
        elements[offsetArrayIndex + 1]  = offset + 2;
        elements[offsetArrayIndex + 2]  = offset + 0;

        // Triangle 2
        elements[offsetArrayIndex + 3]  = offset + 0;
        elements[offsetArrayIndex + 4]  = offset + 2;
        elements[offsetArrayIndex + 5]  = offset + 1;
    }

    public boolean hasRoom() { return this.hasRoom; }

    public boolean hasTextureRoom() { return this.textures.size() < 8; }

    public boolean hasTexture(Texture2D texture) { return this.textures.contains(texture); }

    public int getZIndex() { return this.zIndex; }

    @Override
    public int compareTo(RenderBatch2D o) { return Integer.compare(this.zIndex, o.getZIndex()); }

    public boolean isEmpty() { return this.numberOfSprites == 0; }
}
