package engine.renderer.renderer2D;

import engine.entity.GameObject;
import engine.renderer.EntityRenderer;
import engine.renderer.Texture2D;
import engine.renderer.buffers.VertexArray;
import engine.renderer.buffers.bufferLayout.BufferElement;
import engine.renderer.buffers.bufferLayout.BufferLayout;
import engine.renderer.buffers.bufferLayout.ShaderDataType;
import engine.renderer.shader.Shader;
import engine.renderer.buffers.IndexBuffer;
import engine.renderer.buffers.VertexBuffer;
import engine.stuff.customVariables.Color;
import org.joml.*;
import org.joml.Math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;

public class RenderBatch implements Comparable<RenderBatch> {

    // Vertex
    // ========
    // Pos                     // Color                        // Texture coordinates      // Texture ID       // Entity ID
    // float, float, float,    float, float, float, float,     float, float,               float,              float
    private final int POS_SIZE = 3;
    private final int COLOR_SIZE = 4;
    private final int TEXTURE_COORDINATES_SIZE = 2;
    private final int TEXTURE_ID_SIZE = 1;
    private final int ENTITY_ID_SIZE = 1;

    private final int VERTEX_SIZE = POS_SIZE + COLOR_SIZE + TEXTURE_COORDINATES_SIZE + TEXTURE_ID_SIZE + ENTITY_ID_SIZE;

    private final SpriteMasterRenderer renderer;

    private final SpriteRenderer[] sprites;
    private int numberOfSprites;
    private boolean hasRoom;
    private final float[] vertices;
    private final int[] texSlots = { 0, 1, 2, 3, 4, 5, 6, 7 };

    private final List<Texture2D> textures = new ArrayList<>();;
    private VertexArray vao;
    private final int maxBathSize;
    private final int zIndex;

    public RenderBatch(int maxBatchSize, int zIndex, SpriteMasterRenderer renderer) {
        this.renderer = renderer;

        this.maxBathSize = maxBatchSize;
        this.sprites = new SpriteRenderer[this.maxBathSize];
        this.zIndex = zIndex;

        // 4 vertices per quad
        this.vertices = new float[this.maxBathSize * 4 * VERTEX_SIZE];

        this.numberOfSprites = 0;
        this.hasRoom = true;
    }

    public void start() {
        // Generate and bind a Vertex Array Object (VAO)
        this.vao = new VertexArray();

        // Create and upload vertices buffer (VBO)
        VertexBuffer vbo = new VertexBuffer(this.vertices);
        BufferLayout layout = new BufferLayout(Arrays.asList(
                new BufferElement(ShaderDataType.Float3, "a_Position"),
                new BufferElement(ShaderDataType.Float4, "a_Color"),
                new BufferElement(ShaderDataType.Float2, "a_TextureCoordinates"),
                new BufferElement(ShaderDataType.Float, "a_TextureID"),
                new BufferElement(ShaderDataType.Float, "a_EntityID")
        ));
        vbo.setLayout(layout);
        this.vao.setVertexBuffer(vbo);

        // Create and upload elements buffer (EBO)
        int[] elements = generateIndices();
        IndexBuffer ebo = new IndexBuffer(elements, elements.length);
        this.vao.setIndexBuffer(ebo);

        this.vao.unbind();
    }

    public void addSprite(SpriteRenderer renderer) {
        // Get index and add render object
        int index = this.numberOfSprites;
        this.sprites[index] = renderer;
        this.numberOfSprites++;

        if (renderer.getTexture() != null)
            if (!textures.contains(renderer.getTexture()))
                textures.add(renderer.getTexture());

        // Add properties to local vertices array
        loadVertexProperties(index);

        if (this.numberOfSprites >= this.maxBathSize)
            this.hasRoom = false;
    }

    public void render(Matrix4f projectionMatrix, Matrix4f viewMatrix) {
        boolean rebufferData = false;
        for (int i = 0; i < this.numberOfSprites; i++) {
            SpriteRenderer renderer = this.sprites[i];
            if (renderer.isDirty()) {
                if (!hasTexture(renderer.getTexture()) && renderer.getTexture() != null) {
                    this.renderer.destroyGameObject(renderer.gameObject);
                    this.renderer.add(renderer.gameObject);
                } else {
                    loadVertexProperties(i);
                    renderer.setDirty(false);
                    rebufferData = true;
                }
            }

            // TODO GET BETTER SOLUTION FOR THIS
            if (renderer.gameObject.transform.getZIndex() != this.zIndex) {
                destroyIfExists(renderer.gameObject);
                this.renderer.add(renderer.gameObject);
                i--;
            }
        }

        // Send data tu GPU only if data is changed
        if (rebufferData) {
            // TODO SEND ONLY CHANGED DATA, NOT ALL DATA
            this.vao.getVertexBuffer().bind();
            glBufferSubData(GL_ARRAY_BUFFER, 0, this.vertices);
        }

        Shader shader = EntityRenderer.getCurrentShader();
        shader.uploadInt("u_EntityID", -1);
        shader.uploadMat4f("u_TransformationMatrix", new Matrix4f().identity());
        shader.uploadMat4f("u_ProjectionMatrix", projectionMatrix);
        shader.uploadMat4f("u_ViewMatrix", viewMatrix);
        for (int i = 0; i < this.textures.size(); i++) {
            // TODO CHANGE CONSTANT 8 TEXTURE SLOTS, TO USERS GPU TEXTURES SLOTS COUNT
//            IntBuffer buffer = BufferUtils.createIntBuffer(1);
//            glGetIntegerv(GL_MAX_TEXTURE_IMAGE_UNITS, buffer);

            glActiveTexture(GL_TEXTURE0 + i + 1);
            this.textures.get(i).bind();
        }
        shader.uploadIntArray("u_TextureIDs", this.texSlots);

        EntityRenderer.submit(this.vao, this.numberOfSprites * 6); // 6 indices per quad

        this.vao.getVertexBuffer().unbind();

        for (Texture2D texture : textures)
            texture.unbind();

        shader.unbind();
    }

    public boolean destroyIfExists(GameObject obj) {
        SpriteRenderer renderer = obj.getComponent(SpriteRenderer.class);
        for (int i = 0; i < this.numberOfSprites; i++) {
            if (this.sprites[i] == renderer) {
                for (int j = i; j < this.numberOfSprites - 1; j++) {
                    this.sprites[j] = this.sprites[j + 1];
                    this.sprites[j].setDirty(true);
                }
                this.numberOfSprites--;
                return true;
            }
        }
        return false;
    }

    private void loadVertexProperties(int index) {
        SpriteRenderer sprite = this.sprites[index];

        // Find offset within array (4 vertices per sprite)
        int offset = index * 4 * VERTEX_SIZE;

        Color color = sprite.getColor();
        Vector2f[] textureCoordinates = sprite.getTextureCoordinates();

        int textureID = 0;
        if (sprite.getTexture() != null)
            for (int i = 0; i < this.textures.size(); i++)
                if (this.textures.get(i).equals(sprite.getTexture())) {
                    textureID = i + 1;
                    break;
                }

        boolean isRotated = sprite.gameObject.transform.rotation.x != 0.0f || sprite.gameObject.transform.rotation.y != 0.0f || sprite.gameObject.transform.rotation.z != 0.0f;
        Matrix4f transformationMatrix = new Matrix4f().identity();
        if (isRotated) {
            transformationMatrix.translate(sprite.gameObject.transform.position);
            transformationMatrix.rotate(Math.toRadians(sprite.gameObject.transform.rotation.x), 1.0f, 0.0f, 0.0f);
            transformationMatrix.rotate(Math.toRadians(sprite.gameObject.transform.rotation.y), 0.0f, 1.0f, 0.0f);
            transformationMatrix.rotate(Math.toRadians(sprite.gameObject.transform.rotation.z), 0.0f, 0.0f, 1.0f);
            transformationMatrix.scale(sprite.gameObject.transform.scale);
        }

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
            vertices[offset]        = currentPos.x;
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

            offset += VERTEX_SIZE;
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
        elements[offsetArrayIndex]      = offset + 3;
        elements[offsetArrayIndex + 1]  = offset + 2;
        elements[offsetArrayIndex + 2]  = offset;

        // Triangle 2
        elements[offsetArrayIndex + 3]  = offset;
        elements[offsetArrayIndex + 4]  = offset + 2;
        elements[offsetArrayIndex + 5]  = offset + 1;
    }

    public boolean hasRoom() { return this.hasRoom; }

    public boolean hasTextureRoom() { return this.textures.size() < 8; }

    public boolean hasTexture(Texture2D texture) { return this.textures.contains(texture); }

    public int getZIndex() { return this.zIndex; }

    @Override
    public int compareTo(RenderBatch o) { return Integer.compare(this.zIndex, o.getZIndex()); }
}
