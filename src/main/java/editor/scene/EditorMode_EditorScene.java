package editor.scene;


import editor.entity.GameObject;
import editor.entity.component.components.SpriteRenderer;
import editor.renderer.Camera;
import editor.renderer.Texture;
import editor.renderer.shader.Shader;
import editor.stuff.customVariables.Color;
import editor.stuff.utils.Time;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class EditorMode_EditorScene extends EditorScene {

//    private float[] vertexArray = {
//             // position                // color                    // UV Coordinates
//             100.5f, -100.5f, 0.0f,     1.0f, 0.0f, 0.0f, 1.0f,     1.0f, 0.0f,  // Bottom right   (0)
//            -100.5f,  100.5f, 0.0f,     0.0f, 1.0f, 0.0f, 1.0f,     0.0f, 1.0f,  // Top left       (1)
//             100.5f,  100.5f, 0.0f,     0.0f, 0.0f, 1.0f, 1.0f,     1.0f, 1.0f,  // Top right      (2)
//            -100.5f, -100.5f, 0.0f,     1.0f, 1.0f, 0.0f, 1.0f,     0.0f, 0.0f   // Bottom left    (3)
//    };
//
//    // IMPORTANT: Must be in counter-clockwise order
//    private int[] elementArray = {
//            2, 1, 0, // Top right Triangle
//            0, 1, 3  // Bottom left Triangle
//    };
//
//    private int vaoID, vboID, eboID;
//
//    private final Shader defaultShader = new Shader("editorFiles/shaders/default.glsl");
//    private Texture testTexture;
//
//    private GameObject testObj;

    public EditorMode_EditorScene() {

    }

    @Override
    public void init() {
        this.camera = new Camera(new Vector3f(0.0f));

        int xOffset = 10;
        int yOffset = 10;

        float totalWidth = (float) (600 - xOffset * 2);
        float totalHeight = (float) (300 - yOffset * 2);
        float sizeX = totalWidth / 100.0f;
        float sizeY = totalHeight / 100.0f;

        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 100; y++) {
                float xPos = xOffset + (x * sizeX);
                float yPos = yOffset + (y * sizeY);

                GameObject go = new GameObject("Obj " + x + ", " + y);
                go.transform.position.set(xPos, yPos);
                go.transform.scale.set(sizeX, sizeY);
                go.addComponent(new SpriteRenderer(new Color(xPos / totalWidth, yPos / totalWidth, 255.0f, 255.0f)));
                this.addGameObjectToScene(go);
            }
        }

//        this.testTexture = new Texture("Assets/test.png");
//
//        System.out.println("Creating test object");
//        this.testObj = new GameObject("test obj");
//        this.testObj.addComponent(new SpriteRenderer());
//        this.addGameObjectToScene(this.testObj);

//        // ============================================================
//        // Generate VAO, VBO and EBO buffer Objects, and send to GPU
//        // ============================================================
//        vaoID = glGenVertexArrays();
//        glBindVertexArray(vaoID);
//
//        // Create a float buffer of vertices
//        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
//        vertexBuffer.put(vertexArray);
//        vertexBuffer.flip();
//
//        // Create VBO upload the vertex buffer
//        vboID = glGenBuffers();
//        glBindBuffer(GL_ARRAY_BUFFER, vboID);
//        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
//
//        // Create the indices and upload
//        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
//        elementBuffer.put(elementArray);
//        elementBuffer.flip();
//
//        eboID = glGenBuffers();
//        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
//        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);
//
//        // Add the vertex attribute pointers
//        int positionSize = 3;   // x, y, z
//        int colorSize = 4;      // r, g, b, a
//        int uvSize = 2;      // x, y
//        int vertexSizeBytes = (positionSize + colorSize + uvSize) * Float.BYTES;
//        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeBytes, 0);
//        glEnableVertexAttribArray(0);
//
//        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionSize * Float.BYTES);
//        glEnableVertexAttribArray(1);
//
//        glVertexAttribPointer(2, uvSize, GL_FLOAT, false, vertexSizeBytes, (positionSize + colorSize) * Float.BYTES);
//        glEnableVertexAttribArray(2);
    }

    @Override
    public void update() {
        for (GameObject go : this.gameObjects) {
            go.update();
        }

        this.spriteRenderer.render();
    }
}
