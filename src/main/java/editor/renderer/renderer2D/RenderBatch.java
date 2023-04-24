package editor.renderer.renderer2D;

import editor.assets.AssetPool;
import editor.entity.component.components.SpriteRenderer;
import editor.renderer.MasterRenderer;
import editor.renderer.Texture;
import editor.renderer.shader.Shader;
import editor.scenes.SceneManager;
import editor.stuff.customVariables.Color;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

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

    private final int POS_OFFSET = 0;
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    private final int TEXTURE_COORDINATES_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
    private final int TEXTURE_ID_OFFSET = TEXTURE_COORDINATES_OFFSET + TEXTURE_COORDINATES_SIZE * Float.BYTES;
    private final int ENTITY_ID_OFFSET = TEXTURE_ID_OFFSET + TEXTURE_ID_SIZE * Float.BYTES;
    private final int VERTEX_SIZE = POS_SIZE + COLOR_SIZE + TEXTURE_COORDINATES_SIZE + TEXTURE_ID_SIZE + ENTITY_ID_SIZE;

    private final SpriteRenderer[] sprites;
    private int numberOfSprites;
    private boolean hasRoom;
    private final float[] vertices;
    private final int[] texSlots = { 0, 1, 2, 3, 4, 5, 6, 7 };

    private final List<Texture> textures;
    private int vaoID, vboID;
    private final int maxBathSize;
    private final int zIndex;

    public RenderBatch(int maxBatchSize, int zIndex) {
        this.maxBathSize = maxBatchSize;
        this.sprites = new SpriteRenderer[this.maxBathSize];
        this.zIndex = zIndex;

        // 4 vertices per quad
        this.vertices = new float[this.maxBathSize * 4 * VERTEX_SIZE];

        this.numberOfSprites = 0;
        this.hasRoom = true;
        this.textures = new ArrayList<>();
    }

    public void start() {
        // Generate and bind a Vertex Array Object (VAO)
        this.vaoID = glGenVertexArrays();
        glBindVertexArray(this.vaoID);

        // Allocate space for vertices
        this.vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, this.vboID);
        glBufferData(GL_ARRAY_BUFFER, this.vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // Create and upload indices buffer
        int eboID = glGenBuffers();
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // Enable the buffer attributes pointers
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE * Float.BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE * Float.BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, TEXTURE_COORDINATES_SIZE, GL_FLOAT, false, VERTEX_SIZE * Float.BYTES, TEXTURE_COORDINATES_OFFSET);
        glEnableVertexAttribArray(2);

        glVertexAttribPointer(3, TEXTURE_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE * Float.BYTES, TEXTURE_ID_OFFSET);
        glEnableVertexAttribArray(3);

        glVertexAttribPointer(4, ENTITY_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE * Float.BYTES, ENTITY_ID_OFFSET);
        glEnableVertexAttribArray(4);
    }

    public void addSprite(SpriteRenderer spr) {
        // Get index and add render object
        int index = this.numberOfSprites;
        this.sprites[index] = spr;
        this.numberOfSprites++;

        if (spr.getTexture() != null)
            if (!textures.contains(spr.getTexture()))
                textures.add(spr.getTexture());

        // Add properties to local vertices array
        loadVertexProperties(index);

        if (this.numberOfSprites >= this.maxBathSize)
            this.hasRoom = false;
    }

    public void render() {
        boolean rebufferData = false;
        for (int i = 0; i < this.numberOfSprites; i++) {
            SpriteRenderer spr = this.sprites[i];
            if (spr.isDirty()) {
                loadVertexProperties(i);
                spr.setDirty(false);
                rebufferData = true;
            }
        }

        // Send data tu GPU only if data is changed
        if (rebufferData) {
            // TODO SEND ONLY CHANGED DATA, NOT ALL DATA
            glBindBuffer(GL_ARRAY_BUFFER, this.vboID);
            glBufferSubData(GL_ARRAY_BUFFER, 0, this.vertices);
        }

        // Use shader
        Shader shader = MasterRenderer.getCurrentShader();
        shader.uploadMat4f("uProjectionMatrix", SceneManager.getCurrentScene().getCamera().getProjectionMatrix());
        shader.uploadMat4f("uViewMatrix", SceneManager.getCurrentScene().getCamera().getViewMatrix());
        for (int i = 0; i < this.textures.size(); i++) {
            // TODO CHANGE CONSTANT 8 TEXTURE SLOTS, TO USERS GPU TEXTURES SLOTS COUNT
//            IntBuffer buffer = BufferUtils.createIntBuffer(1);
//            glGetIntegerv(GL_MAX_TEXTURE_IMAGE_UNITS, buffer);

            glActiveTexture(GL_TEXTURE0 + i + 1);
            this.textures.get(i).bind();
        }
        shader.uploadIntArray("uTextureIDs", this.texSlots);

        glBindVertexArray(this.vaoID);
//        glEnableVertexAttribArray(0);
//        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, this.numberOfSprites * 6, GL_UNSIGNED_INT, 0); // 6 indices per quad

//        glDisableVertexAttribArray(0);
//        glDisableVertexAttribArray(1);
        glBindVertexArray(0); // Bind nothing

        for (Texture texture : textures)
            texture.unbind();

        shader.detach();
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

        boolean isRotated = sprite.gameObject.transform.rotation != 0.0f;
        Matrix4f transformationMatrix = new Matrix4f().identity();
        if (isRotated) {
            transformationMatrix.translate(sprite.gameObject.transform.position);
            transformationMatrix.rotate(Math.toRadians(sprite.gameObject.transform.rotation), 0.0f, 0.0f, 1.0f);
            transformationMatrix.scale(sprite.gameObject.transform.scale);
        }


        // Add vertices with the appropriate properties
        float xAdd = 1.0f;
        float yAdd = 1.0f;
        float zAdd = 0.0f;

        for (int i = 0; i < 4; i++) { // 4 vertices per sprite
            if (i == 1)
                yAdd = 0.0f;
            else if (i == 2)
                xAdd = 0.0f;
            else if (i == 3)
                yAdd = 1.0f;

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

    public boolean isHasRoom() { return this.hasRoom; }

    public boolean hasTextureRoom() { return this.textures.size() < 8; }

    public boolean hasTexture(Texture texture) { return this.textures.contains(texture); }

    public int getZIndex() { return this.zIndex; }

    @Override
    public int compareTo(RenderBatch o) { return Integer.compare(this.zIndex, o.getZIndex()); }
}
