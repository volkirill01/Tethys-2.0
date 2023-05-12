package engine.renderer;

public abstract class Texture {

    protected transient int textureID;
    private final String filepath;
    protected int width, height;

    public Texture(String filepath) { this.filepath = filepath; }

    public Texture(String filepath, int width, int height) {
        this.filepath = filepath;
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) return false;
        if (!(object instanceof Texture t)) return false;

        if (object == this) return true;

        return t.getWidth() == this.getWidth() && t.getHeight() == this.getHeight() && t.getTextureID() == this.getTextureID() && t.getFilepath().equals(this.getFilepath());
    }

    public int getTextureID() { return this.textureID; }

    public String getFilepath() { return this.filepath; }

    public int getWidth() { return this.width; }

    public int getHeight() { return this.height; }

    public abstract void bind();

    public abstract void unbind();
}
