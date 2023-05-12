package engine.renderer.buffers.bufferLayout;

public class BufferElement {

    private final String name;
    private final ShaderDataType type;
    private int offset = 0;
    private final int size;
    private final boolean isNormalized;

    public BufferElement(ShaderDataType type, String name) {
        this.name = name;
        this.type = type;
        this.size = shaderDataTypeSize(this.type);
        this.isNormalized = false;
    }

    public BufferElement(ShaderDataType type, String name, boolean isNormalized) {
        this.name = name;
        this.type = type;
        this.size = shaderDataTypeSize(this.type);
        this.isNormalized = isNormalized;
    }

    private int shaderDataTypeSize(ShaderDataType type) {
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

    public String getName() { return this.name; }

    public ShaderDataType getType() { return this.type; }

    public int getOffset() { return this.offset; }

    public void setOffset(int offset) { this.offset = offset; }

    public int getSize() { return this.size; }

    public boolean isNormalized() { return this.isNormalized; }

    public int getComponentCount() {
        return switch (this.type) {
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
