package engine.renderer.shader;

import engine.logging.DebugLog;
import engine.profiling.Profiler;
import engine.renderer.shader.uniforms.UniformBuffer;
import engine.stuff.Settings;
import engine.stuff.customVariables.Color;
import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.lwjgl.opengl.GL20.*;

public class Shader {

    private int shaderProgramID;
    private int vertexShaderID, fragmentShaderID;
    private boolean beingUsed = false;

    private String vertexSource;
    private String fragmentSource;
    private final String filepath;

    private final Map<String, Integer> uniformLocations = new HashMap<>();
    private final Map<String, UniformBuffer> uniformBuffers = new HashMap<>();

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
                this.vertexSource = "#version " + Settings.SHADER_VERSION + "\n" + splitString[1];
            else if (firstPattern.equals("fragment"))
                this.fragmentSource = "#version " + Settings.SHADER_VERSION + "\n" + splitString[1];
            else
                throw new IOException(String.format("Unexpected token '%s'", firstPattern));

            if (secondPattern.equals("vertex"))
                this.vertexSource = "#version " + Settings.SHADER_VERSION + "\n" + splitString[2];
            else if (secondPattern.equals("fragment"))
                this.fragmentSource = "#version " + Settings.SHADER_VERSION + "\n" + splitString[2];
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
        // TODO CACHE SHADERS BINARY AND LOAD FROM FILES, TO NOT COMPILE IT EVERY TIME THE ENGINE STARTS
//        glShaderBinary(new int[2] // two shaders, vertex, and fragment, GL_SHADER_COMPILER, ByteBuffer);

        DebugLog.logInfo("Shader:Compile: ", this.filepath);

        Profiler.startTimer(String.format("Compile Shader - '%s'", this.filepath));
        // ============================================================
        // Compile and link Shaders
        // ============================================================
        // First compile the Vertex Shader
        this.vertexShaderID = glCreateShader(GL_VERTEX_SHADER);
        // Pass the shader source code to the GPU
        glShaderSource(this.vertexShaderID, this.vertexSource);
        glCompileShader(this.vertexShaderID);

        // Check for errors in compilation
        int success = glGetShaderi(this.vertexShaderID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(this.vertexShaderID, GL_INFO_LOG_LENGTH);
            throw new RuntimeException(String.format("'%s'\n\tVertex shader compilation failed.\n%s", this.filepath, glGetShaderInfoLog(this.vertexShaderID, len)));
        }

        // First compile the Vertex Shader
        this.fragmentShaderID = glCreateShader(GL_FRAGMENT_SHADER);
        // Pass the shader source code to the GPU
        glShaderSource(this.fragmentShaderID, this.fragmentSource);
        glCompileShader(this.fragmentShaderID);

        // Check for errors in compilation
        success = glGetShaderi(this.fragmentShaderID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(this.fragmentShaderID, GL_INFO_LOG_LENGTH);
            throw new RuntimeException(String.format("'%s'\n\tFragment shader compilation failed.\n%s", this.filepath, glGetShaderInfoLog(this.fragmentShaderID, len)));
        }

        // Link shaders and check for errors
        this.shaderProgramID = glCreateProgram();
        glAttachShader(this.shaderProgramID, this.vertexShaderID);
        glAttachShader(this.shaderProgramID, this.fragmentShaderID);
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
        Profiler.startTimer(String.format("Bind Shader - '%s'", this.filepath));
        if (!this.beingUsed) {
            // Bind shader program
            glUseProgram(this.shaderProgramID);
            this.beingUsed = true;
        }
        Profiler.stopTimer(String.format("Bind Shader - '%s'", this.filepath));
    }

    public void unbind() {
        Profiler.startTimer(String.format("Unbind Shader - '%s'", this.filepath));
        // Bind nothing
        glUseProgram(0);
        this.beingUsed = false;
        Profiler.stopTimer(String.format("Unbind Shader - '%s'", this.filepath));
    }

    public void freeMemory() {
        Profiler.startTimer(String.format("FreeMemory Shader - '%s'", this.filepath));
        glDeleteProgram(this.shaderProgramID);
        glDeleteShader(this.vertexShaderID);
        glDeleteShader(this.fragmentShaderID);
        for (UniformBuffer buffer : this.uniformBuffers.values())
            glDeleteBuffers(buffer.getBufferID());
        Profiler.stopTimer(String.format("FreeMemory Shader - '%s'", this.filepath));
    }

    public String getFilepath() { return this.filepath; }

    private int getUniformLocation(String variableName) {
        Profiler.startTimer(String.format("Get Uniform location Shader - '%s'", this.filepath));
        if (this.uniformLocations.containsKey(variableName))
            return this.uniformLocations.get(variableName);

        int uniformLocation = glGetUniformLocation(this.shaderProgramID, variableName);
        this.uniformLocations.put(variableName, uniformLocation);
        Profiler.stopTimer(String.format("Get Uniform location Shader - '%s'", this.filepath));
        return uniformLocation;
    }

    private int uploadVariable(String variableName) {
        if (!this.beingUsed) {
            bind();
//            throw new RuntimeException("'" + filepath + "' Shader not used for uploading variable '" + variableName + "'");
        }

        return getUniformLocation(variableName);
    }

    public void uploadMat4f(String variableName, Matrix4f matrix) {
        Profiler.startTimer(String.format("Upload Mat4 Shader - '%s'", this.filepath));
        int varLocation = uploadVariable(variableName);
        FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(4 * 4); // 4x4 Matrix
        matrix.get(matrixBuffer);
        glUniformMatrix4fv(varLocation, false, matrixBuffer);
        Profiler.stopTimer(String.format("Upload Mat4 Shader - '%s'", this.filepath));
    }

    public void uploadMat3f(String variableName, Matrix3f matrix) {
        Profiler.startTimer(String.format("Upload Mat3 Shader - '%s'", this.filepath));
        int varLocation = uploadVariable(variableName);
        FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(3 * 3); // 3x3 Matrix
        matrix.get(matrixBuffer);
        glUniformMatrix3fv(varLocation, false, matrixBuffer);
        Profiler.stopTimer(String.format("Upload Mat3 Shader - '%s'", this.filepath));
    }

    public void uploadFloat(String variableName, float value) {
        Profiler.startTimer(String.format("Upload Float Shader - '%s'", this.filepath));
        int varLocation = uploadVariable(variableName);
        glUniform1f(varLocation, value);
        Profiler.stopTimer(String.format("Upload Float Shader - '%s'", this.filepath));
    }

    public void uploadInt(String variableName, int value) {
        Profiler.startTimer(String.format("Upload Int Shader - '%s'", this.filepath));
        int varLocation = uploadVariable(variableName);
        glUniform1i(varLocation, value);
        Profiler.stopTimer(String.format("Upload Int Shader - '%s'", this.filepath));
    }

    public void uploadTexture(String variableName, int textureSlot) {
        Profiler.startTimer(String.format("Upload Texture Shader - '%s'", this.filepath));
        int varLocation = uploadVariable(variableName);
        glUniform1i(varLocation, textureSlot);
        Profiler.stopTimer(String.format("Upload Texture Shader - '%s'", this.filepath));
    }

    public void uploadIntArray(String variableName, int[] array) {
        Profiler.startTimer(String.format("Upload IntArray Shader - '%s'", this.filepath));
        int varLocation = uploadVariable(variableName);
        glUniform1iv(varLocation, array);
        Profiler.stopTimer(String.format("Upload IntArray Shader - '%s'", this.filepath));
    }

    public void uploadVec2f(String variableName, Vector2f vector) {
        Profiler.startTimer(String.format("Upload Vec2 Shader - '%s'", this.filepath));
        int varLocation = uploadVariable(variableName);
        glUniform2f(varLocation, vector.x, vector.y);
        Profiler.stopTimer(String.format("Upload Vec2 Shader - '%s'", this.filepath));
    }

    public void uploadVec3f(String variableName, Vector3f vector) {
        Profiler.startTimer(String.format("Upload Vec3 Shader - '%s'", this.filepath));
        int varLocation = uploadVariable(variableName);
        glUniform3f(varLocation, vector.x, vector.y, vector.z);
        Profiler.stopTimer(String.format("Upload Vec3 Shader - '%s'", this.filepath));
    }

    public void uploadVec4f(String variableName, Vector4f vector) {
        Profiler.startTimer(String.format("Upload Vec4 Shader - '%s'", this.filepath));
        int varLocation = uploadVariable(variableName);
        glUniform4f(varLocation, vector.x, vector.y, vector.z, vector.w);
        Profiler.stopTimer(String.format("Upload Vec4 Shader - '%s'", this.filepath));
    }

    public void uploadColor(String variableName, Color color) {
        Profiler.startTimer(String.format("Upload Color Shader - '%s'", this.filepath));
        int varLocation = uploadVariable(variableName);
        glUniform4f(varLocation, color.r / 255.0f, color.g / 255.0f, color.b / 255.0f, color.a / 255.0f);
        Profiler.stopTimer(String.format("Upload Color Shader - '%s'", this.filepath));
    }

    public int getShaderProgramID() { return this.shaderProgramID; }

    public void addUniformBuffer(UniformBuffer buffer) { this.uniformBuffers.put(buffer.getBufferName(), buffer); }

    public UniformBuffer getUniformBuffer(String bufferName) { return this.uniformBuffers.get(bufferName); }
}
