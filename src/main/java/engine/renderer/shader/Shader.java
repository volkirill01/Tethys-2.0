package engine.renderer.shader;

import engine.profiling.Profiler;
import engine.stuff.Settings;
import engine.stuff.customVariables.Color;
import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static org.lwjgl.opengl.GL20.*;

public class Shader {

    private int shaderProgramID;
    private boolean beingUsed = false;

    private String vertexSource;
    private String fragmentSource;
    private final String filepath;

    public Shader(String filepath) {
        Profiler.startTimer(String.format("Create new Shader - '%s'", filepath));
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
                this.vertexSource = "#version " + Settings.shaderVersion + "\n" + splitString[1];
            else if (firstPattern.equals("fragment"))
                this.fragmentSource = "#version " + Settings.shaderVersion + "\n" + splitString[1];
            else
                throw new IOException(String.format("Unexpected token '%s'", firstPattern));

            if (secondPattern.equals("vertex"))
                this.vertexSource = "#version " + Settings.shaderVersion + "\n" + splitString[2];
            else if (secondPattern.equals("fragment"))
                this.fragmentSource = "#version " + Settings.shaderVersion + "\n" + splitString[2];
            else
                throw new IOException(String.format("Unexpected token '%s'", secondPattern));

            this.fragmentSource = buildFragmentShaderSource(Objects.requireNonNull(this.fragmentSource));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        compile();
        Profiler.stopTimer(String.format("Create new Shader - '%s'", filepath));
    }

    private String buildFragmentShaderSource(String fragmentSource) {
        String[] splitFragmentSource = fragmentSource.split("( )*(void)( )*(\n)*( )*(main)( )*(\n)*( )*(\\()( )*(\n)*( )*(\\))( )*(\n)*( )*");
        String partWithoutBracket = splitFragmentSource[1].split("\\{", 2)[1];
        String finalFragmentSource;

        try {
            String fragmentShaderTemplateSource = new String(Files.readAllBytes(Paths.get("editorFiles/shaders/stuff/fragmentShaderTemplate.glsl")));

            fragmentShaderTemplateSource = fragmentShaderTemplateSource.replace("#sourceFirst", splitFragmentSource[0]);
            fragmentShaderTemplateSource = fragmentShaderTemplateSource.replace("#sourceSecond", partWithoutBracket);

            if (!fragmentSource.contains("v_EntityID")) {
                int firstIndex = fragmentShaderTemplateSource.indexOf("#else block");
                int secondIndex = fragmentShaderTemplateSource.indexOf("#end else block");

                finalFragmentSource = fragmentShaderTemplateSource.substring(0, firstIndex);
                finalFragmentSource += fragmentShaderTemplateSource.substring(fragmentShaderTemplateSource.indexOf("\r\n", secondIndex));
            } else
                finalFragmentSource = fragmentShaderTemplateSource.replace("#else block", "").replace("#end else block", "");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return finalFragmentSource;
    }

    public void compile() {
        Profiler.startTimer(String.format("Compile Shader - '%s'", this.filepath));
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
            throw new RuntimeException(String.format("'%s'\n\tVertex shader compilation failed.\n%s", this.filepath, glGetShaderInfoLog(vertexID, len)));
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
            throw new RuntimeException(String.format("'%s'\n\tFragment shader compilation failed.\n%s", this.filepath, glGetShaderInfoLog(fragmentID, len)));
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
            throw new RuntimeException(String.format("'%s'\n\tLinking of shaders failed.\n%s", this.filepath, glGetProgramInfoLog(this.shaderProgramID, len)));
        }
        Profiler.stopTimer(String.format("Compile Shader - '%s'", this.filepath));
    }

    public void bind() {
        if (!this.beingUsed) {
            // Bind shader program
            glUseProgram(this.shaderProgramID);
            this.beingUsed = true;
        }
    }

    public void unbind() {
        // Bind nothing
        glUseProgram(0);
        this.beingUsed = false;
    }

    public String getFilepath() { return this.filepath; }

    private int uploadVariable(String variableName) {
        if (!this.beingUsed) {
            bind();
//            throw new RuntimeException("'" + filepath + "' Shader not used for uploading variable '" + variableName + "'");
        }

        return glGetUniformLocation(this.shaderProgramID, variableName);
    }

    public void uploadMat4f(String variableName, Matrix4f matrix) {
        int varLocation = uploadVariable(variableName);
        FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(4 * 4); // 4x4 Matrix
        matrix.get(matrixBuffer);
        glUniformMatrix4fv(varLocation, false, matrixBuffer);
    }

    public void uploadMat3f(String variableName, Matrix3f matrix) {
        int varLocation = uploadVariable(variableName);
        FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(3 * 3); // 3x3 Matrix
        matrix.get(matrixBuffer);
        glUniformMatrix3fv(varLocation, false, matrixBuffer);
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

    public void uploadIntArray(String variableName, int[] array) {
        int varLocation = uploadVariable(variableName);
        glUniform1iv(varLocation, array);
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

    public void uploadColor(String variableName, Color color) {
        int varLocation = uploadVariable(variableName);
        glUniform4f(varLocation, color.r / 255.0f, color.g / 255.0f, color.b / 255.0f, color.a / 255.0f);
    }
}
