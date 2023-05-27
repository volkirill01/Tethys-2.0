package engine.stuff.openGL;

public class BufferElement {

    private final String name;
    private final ShaderDataType type;
    private int offset = 0;
    private final int size;

    public BufferElement(ShaderDataType type, String name) {
        this.name = name;
        this.type = type;
        this.size = OpenGLConversions.shaderDataTypeSize(this.type);
    }

    public String getName() { return this.name; }

    public ShaderDataType getType() { return this.type; }

    public int getOffset() { return this.offset; }

    public void setOffset(int offset) { this.offset = offset; }

    public int getSize() { return this.size; }
}
