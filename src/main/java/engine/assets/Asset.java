package engine.assets;

import engine.renderer.Texture;

import java.io.File;

public class Asset {

    public enum AssetType {
        All,

        Folder,

        Scene,
        Texture,
        Mesh,

        Other
    }

    private final String filepath;
    private final String name;
    private final AssetType type;
    private final Texture icon;

    public Asset(String filepath, AssetType type, Texture icon) {
        this.filepath = filepath;
        this.name = new File(filepath).getName();
        this.type = type;
        this.icon = icon;
    }

    public String getFilepath() { return this.filepath; }

    public String getName() { return this.name; }

    public AssetType getType() { return this.type; }

    public Texture getIcon() { return this.icon; }
}
