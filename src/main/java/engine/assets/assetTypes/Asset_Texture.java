package engine.assets.assetTypes;

import engine.assets.Asset;
import engine.assets.AssetPool;
import engine.renderer.Texture;

public class Asset_Texture extends Asset {

    public Asset_Texture(String filepath, Texture icon) { super(filepath, AssetType.Texture, icon); }
}
