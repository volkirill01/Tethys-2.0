package engine.stuff.openGL;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.GL_BOOL;

public class OpenGLConversions {

    public static int shaderDataTypeToOpenGLBaseType(ShaderDataType type) {
        return switch (type) {
            case None                                       -> GL_NONE;
            case Float, Float2, Float3, Float4, Mat3, Mat4  -> GL_FLOAT;
            case Int, Int2, Int3, Int4                      -> GL_INT;
            case Bool                                       -> GL_BOOL;
            default -> throw new IllegalStateException(String.format("Unknown ShaderData type - '%s'", type.name()));
        };
    }

    public static int shaderDataTypeSize(ShaderDataType type) {
        return switch (type) {
            case None       -> 0;
            case Float      -> Float.BYTES;
            case Float2     -> Float.BYTES * 2;
            case Float3     -> Float.BYTES * 3;
            case Float4     -> Float.BYTES * 4;
            case Int        -> Integer.BYTES;
            case Int2       -> Integer.BYTES * 2;
            case Int3       -> Integer.BYTES * 3;
            case Int4       -> Integer.BYTES * 4;
            case Mat3       -> Float.BYTES * 3 * 3;
            case Mat4       -> Float.BYTES * 4 * 4;
            case Bool       -> 1;
            default -> throw new IllegalStateException(String.format("Unknown ShaderData type - '%s'", type.name()));
        };
    }

    public static int getComponentCount(ShaderDataType type) {
        return switch (type) {
            case None               -> 0;
            case Float, Int, Bool   -> 1;
            case Float2, Int2       -> 2;
            case Float3, Int3       -> 3;
            case Float4, Int4       -> 4;
            case Mat3               -> 3 * 3;
            case Mat4               -> 4 * 4;
            default -> throw new IllegalStateException(String.format("Unknown ShaderData type - '%s'", type.name()));
        };
    }
}
