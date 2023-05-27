package engine.renderer.shader.uniforms;

import engine.stuff.openGL.BufferElement;
import engine.stuff.openGL.ShaderDataType;
import org.joml.*;

public class UniformBufferElement extends BufferElement {

    private Object data;

    public UniformBufferElement(ShaderDataType type, String name) { super(type, name); }

    public Object getData() { return this.data; }

    public void setData(Object data) { this.data = data; }

    public Class<?> getTypeClass() {
        return switch (getType()) {
            case None -> null;
            case Float -> Float.class;
            case Float2 -> Vector2f.class;
            case Float3 -> Vector3f.class;
            case Float4 -> Vector4f.class;
            case Int -> Integer.class;
            case Int2 -> Vector2i.class;
            case Int3 -> Vector3i.class;
            case Int4 -> Vector4i.class;
            case Mat3 -> Matrix3f.class;
            case Mat4 -> Matrix4f.class;
            case Bool -> Boolean.class;
            default -> throw new IllegalStateException(String.format("Unknown ShaderData type - '%s'", getType().name()));
        };
    }
}
