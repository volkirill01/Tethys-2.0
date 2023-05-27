package engine.renderer.shader.uniforms;

import engine.profiling.Profiler;
import engine.stuff.customVariables.Color;
import org.joml.*;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL44.GL_DYNAMIC_STORAGE_BIT;
import static org.lwjgl.opengl.GL45.glCreateBuffers;
import static org.lwjgl.opengl.GL45.glNamedBufferStorage;

public class UniformBuffer {

    private final int uboID;
    private final int uboBlockIndex;
    private final int uboBlockSize;
    private final String uniformBufferName;
    private final UniformBufferElement[] elements;
    private final float[] data;

    public UniformBuffer(String uniformBufferVariableName, int shaderProgramID, UniformBufferElement... elements) {
        this.uniformBufferName = uniformBufferVariableName;
        Profiler.startTimer(String.format("UniformBuffer Constructor - '%s'", this.uniformBufferName));
        this.elements = elements;

        // Creating uniform buffer block
        this.uboBlockIndex = glGetUniformBlockIndex(shaderProgramID, this.uniformBufferName);
        int[] size = new int[1];
        glGetActiveUniformBlockiv(shaderProgramID, this.uboBlockIndex, GL_UNIFORM_BLOCK_DATA_SIZE, size);
        this.uboBlockSize = size[0];

        if (this.uboBlockSize == 0)
            throw new IllegalStateException(String.format("%s - Uniform buffer not contains any variables/does not exist", this.uniformBufferName));

        this.data = new float[this.uboBlockSize / Float.BYTES];

        // Get uniform variables names from UniformBuffer
        CharSequence[] uniformNames = new CharSequence[elements.length];
        for (int i = 0; i < uniformNames.length; i++)
            uniformNames[i] = elements[i].getName();

        // Get uniform variables indices from UniformBuffer
        IntBuffer uniformIndices = BufferUtils.createIntBuffer(elements.length);
        glGetUniformIndices(shaderProgramID, uniformNames, uniformIndices);

        // Get uniform variables offsets from UniformBuffer
        IntBuffer offsets = BufferUtils.createIntBuffer(elements.length);
        glGetActiveUniformsiv(shaderProgramID, uniformIndices, GL_UNIFORM_OFFSET, offsets);
        for (int i = 0; i < this.elements.length; i++)
            this.elements[i].setOffset(offsets.get(i));

        // Creating UniformBufferObject
        this.uboID = glCreateBuffers();
        glNamedBufferStorage(this.uboID, this.uboBlockSize, GL_DYNAMIC_STORAGE_BIT);
        glBindBufferRange(GL_UNIFORM_BUFFER, this.uboBlockIndex, this.uboID, 0, this.uboBlockSize);

        // Free temporary memory
        uniformIndices.flip();
        offsets.flip();
        Profiler.stopTimer(String.format("UniformBuffer Constructor - '%s'", this.uniformBufferName));
    }

    private void uploadDataU(String uniformVariableName, Object data) {
        Profiler.startTimer(String.format("UniformBuffer UploadData(%s, data: %s) - '%s'", uniformVariableName, "" + data, this.uniformBufferName));
        UniformBufferElement element = getBufferElement(uniformVariableName);
        if (element != null) {
            if (element.getTypeClass() != data.getClass())
                throw new IllegalStateException(String.format("Uniform buffer element type is '%s', not '%s'", element.getTypeClass().getName(), data.getClass().getName()));

            element.setData(data);
            if (data.getClass().isAssignableFrom(Color.class)) {
                Color tmpColor = (Color) data;
                element.setData(new Vector4f(tmpColor.r / 255.0f, tmpColor.g / 255.0f, tmpColor.b / 255.0f, tmpColor.a / 255.0f));
            }

            updateDataArray();
            uploadDataToGPU();
        }
        Profiler.stopTimer(String.format("UniformBuffer UploadData(%s, data: %s) - '%s'", uniformVariableName, "" + data, this.uniformBufferName));
    }

    private void uploadDataToGPU() {
        Profiler.startTimer(String.format("UniformBuffer UploadDataToGPU - '%s'", this.uniformBufferName));
        glBufferSubData(GL_UNIFORM_BUFFER, 0, this.data);
        Profiler.stopTimer(String.format("UniformBuffer UploadDataToGPU - '%s'", this.uniformBufferName));
    }

    private UniformBufferElement getBufferElement(String uniformVariableName) {
        for (UniformBufferElement element : this.elements)
            if (element.getName().equals(uniformVariableName))
                return element;
        return null;
    }

    private void updateDataArray() {
        Profiler.startTimer(String.format("UniformBuffer UpdateDataArray - '%s'", this.uniformBufferName));
        Arrays.fill(this.data, 0.0f);

        int i;
        for (UniformBufferElement element : this.elements) {
            if (element.getData() == null)
                continue;

            i = element.getOffset() / Float.BYTES;
            switch (element.getType()) {
                case Float -> this.data[i] = (float) element.getData();
                case Float2 -> {
                    this.data[i + 0] = ((Vector2f) element.getData()).x;
                    this.data[i + 1] = ((Vector2f) element.getData()).y;
                }
                case Float3 -> {
                    this.data[i + 0] = ((Vector3f) element.getData()).x;
                    this.data[i + 1] = ((Vector3f) element.getData()).y;
                    this.data[i + 2] = ((Vector3f) element.getData()).z;
                }
                case Float4 -> {
                    this.data[i + 0] = ((Vector4f) element.getData()).x;
                    this.data[i + 1] = ((Vector4f) element.getData()).y;
                    this.data[i + 2] = ((Vector4f) element.getData()).z;
                    this.data[i + 3] = ((Vector4f) element.getData()).w;
                }
                case Int -> this.data[i] = (int) element.getData();
                case Int2 -> {
                    this.data[i + 0] = ((Vector2i) element.getData()).x;
                    this.data[i + 1] = ((Vector2i) element.getData()).y;
                }
                case Int3 -> {
                    this.data[i + 0] = ((Vector3i) element.getData()).x;
                    this.data[i + 1] = ((Vector3i) element.getData()).y;
                    this.data[i + 2] = ((Vector3i) element.getData()).z;
                }
                case Int4 -> {
                    this.data[i + 0] = ((Vector4i) element.getData()).x;
                    this.data[i + 1] = ((Vector4i) element.getData()).y;
                    this.data[i + 2] = ((Vector4i) element.getData()).z;
                    this.data[i + 3] = ((Vector4i) element.getData()).w;
                }
                case Mat3 -> {
                    float[] matrix = new float[3 * 3];
                    ((Matrix3f) element.getData()).get(matrix);
                    System.arraycopy(matrix, 0, this.data, i, matrix.length);
                }
                case Mat4 -> {
                    float[] matrix = new float[4 * 4];
                    ((Matrix4f) element.getData()).get(matrix);
                    System.arraycopy(matrix, 0, this.data, i, matrix.length);
                }
                case Bool -> this.data[i] = (boolean) element.getData() ? 1.0f : 0.0f;
            }
        }
        Profiler.stopTimer(String.format("UniformBuffer UpdateDataArray - '%s'", this.uniformBufferName));
    }

    public void uploadData(String uniformVariableName, float data) { uploadDataU(uniformVariableName, data); }

    public void uploadData(String uniformVariableName, int data) { uploadDataU(uniformVariableName, data); }

    public void uploadData(String uniformVariableName, boolean data) { uploadDataU(uniformVariableName, data); }

    public void uploadData(String uniformVariableName, Vector2f data) { uploadDataU(uniformVariableName, data); }

    public void uploadData(String uniformVariableName, Vector3f data) { uploadDataU(uniformVariableName, data); }

    public void uploadData(String uniformVariableName, Vector4f data) { uploadDataU(uniformVariableName, data); }

    public void uploadData(String uniformVariableName, Matrix3f data) { uploadDataU(uniformVariableName, data); }

    public void uploadData(String uniformVariableName, Matrix4f data) { uploadDataU(uniformVariableName, data); }

    public void uploadData(String uniformVariableName, Color data) { uploadDataU(uniformVariableName, data); }

    public String getBufferName() { return this.uniformBufferName; }

    public int getBufferID() { return this.uboID; }

    public UniformBufferElement[] getElements() { return this.elements; }
}
