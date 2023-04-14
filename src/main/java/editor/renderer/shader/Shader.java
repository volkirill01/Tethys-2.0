package editor.renderer.shader;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader {

    private int shaderProgramID;
    private boolean beingUsed = false;

    private String vertexSource;
    private String fragmentSource;
    private final String filepath;

    public Shader(String filepath) {
        this.filepath = filepath;
        try {
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

            // Find the first pattern after #type 'pattern'
            int index = source.indexOf("#type") + 6;
            int endOfLine = source.indexOf("\r\n", index);
            String firstPattern = source.substring(index, endOfLine).trim();

            // Find the second pattern after #type 'pattern'
            index = source.indexOf("#type", endOfLine) + 6;
            endOfLine = source.indexOf("\r\n", index);
            String secondPattern = source.substring(index, endOfLine).trim();

            if (firstPattern.equals("vertex"))
                this.vertexSource = splitString[1];
            else if (firstPattern.equals("fragment"))
                this.fragmentSource = splitString[1];
            else
                throw new IOException("Unexpected token '" + firstPattern + "'.");

            if (secondPattern.equals("vertex"))
                this.vertexSource = splitString[2];
            else if (secondPattern.equals("fragment"))
                this.fragmentSource = splitString[2];
            else
                throw new IOException("Unexpected token '" + secondPattern + "'.");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        compile();
    }


    public void compile() {
        // ============================================================
        // Compile and link Shaders
        // ============================================================
        int vertexID, fragmentID;

        // First compile the Vertex Shader
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        // Pass the shader source code to the GPU
        glShaderSource(vertexID, this.vertexSource);
        glCompileShader(vertexID);

        // Check for errors in compilation
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            throw new RuntimeException("'" + this.filepath + "'\n\tVertex shader compilation failed.\n" + glGetShaderInfoLog(vertexID, len));
        }

        // First compile the Vertex Shader
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        // Pass the shader source code to the GPU
        glShaderSource(fragmentID, this.fragmentSource);
        glCompileShader(fragmentID);

        // Check for errors in compilation
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            throw new RuntimeException("'" + this.filepath + "'\n\tFragment shader compilation failed.\n" + glGetShaderInfoLog(fragmentID, len));
        }

        // Link shaders and check for errors
        this.shaderProgramID = glCreateProgram();
        glAttachShader(this.shaderProgramID, vertexID);
        glAttachShader(this.shaderProgramID, fragmentID);
        glLinkProgram(this.shaderProgramID);

        // Check for linking errors
        success = glGetProgrami(this.shaderProgramID, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(this.shaderProgramID, GL_INFO_LOG_LENGTH);
            throw new RuntimeException("'" + this.filepath + "'\n\tLinking of shaders failed.\n" + glGetProgramInfoLog(this.shaderProgramID, len));
        }
    }

    public void use() {
        if (!this.beingUsed) {
            // Bind shader program
            glUseProgram(this.shaderProgramID);
            this.beingUsed = true;
        }
    }

    public void detach() {
        // Bind nothing
        glUseProgram(0);
        this.beingUsed = false;
    }

    private int uploadVariable(String variableName) {
        if (!this.beingUsed)
            throw new RuntimeException("'" + filepath + "' Shader not used for uploading variable '" + variableName + "'");

        return glGetUniformLocation(this.shaderProgramID, variableName);
    }

    public void uploadMat4f(String variableName, Matrix4f matrix) {
        int varLocation = uploadVariable(variableName);
        FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(4 * 4); // 4x4 Matrix
        matrix.get(matrixBuffer);
        glUniformMatrix4fv(varLocation, false, matrixBuffer);
    }

    public void uploadFloat(String variableName, float value) {
        int varLocation = uploadVariable(variableName);
        glUniform1f(varLocation, value);
    }

    public void uploadInt(String variableName, int value) {
        int varLocation = uploadVariable(variableName);
        glUniform1i(varLocation, value);
    }

    public void uploadTexture(String variableName, int textureSlot) {
        int varLocation = uploadVariable(variableName);
        glUniform1i(varLocation, textureSlot);
    }

    public void uploadVec2f(String variableName, Vector2f vector) {
        int varLocation = uploadVariable(variableName);
        glUniform2f(varLocation, vector.x, vector.y);
    }

    public void uploadVec3f(String variableName, Vector3f vector) {
        int varLocation = uploadVariable(variableName);
        glUniform3f(varLocation, vector.x, vector.y, vector.z);
    }

    public void uploadVec4f(String variableName, Vector4f vector) {
        int varLocation = uploadVariable(variableName);
        glUniform4f(varLocation, vector.x, vector.y, vector.z, vector.w);
    }
}
