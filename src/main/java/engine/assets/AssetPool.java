package engine.assets;

import engine.parsers.ModelParser;
import engine.audio.Sound;
import engine.renderer.Texture;
import engine.renderer.Texture2D;
import engine.renderer.renderer2D.SubTexture2D;
import engine.renderer.renderer2D.sprite.Sprite;
import engine.renderer.renderer2D.sprite.SpriteSheet;
import engine.renderer.renderer3D.mesh.Mesh;
import engine.renderer.shader.Shader;
import org.joml.Vector2f;

import java.util.*;

import static org.lwjgl.opengl.GL11.*;

public class AssetPool {

    private static final Map<String, Texture2D> textures2D = new HashMap<>();
    private static final Map<String, Shader> shaders = new HashMap<>();
    private static final Map<String, SpriteSheet> spriteSheets = new HashMap<>();
    private static final Map<String, Sound> sounds = new HashMap<>();

    private static final Texture2D whiteTexture;
    private static final Sprite defaultSprite;
    static {
        // Generate white OpenGL texture.
        whiteTexture = new Texture2D(1, 1, new int[]{ 0xFFFFFFFF }, false);

        defaultSprite = new Sprite();
        defaultSprite.setTexture(whiteTexture);
        defaultSprite.setWidth(1);
        defaultSprite.setHeight(1);
    }

    public static Texture2D getTexture(String filepath) {
        if (textures2D.containsKey(filepath))
            return textures2D.get(filepath);

        Texture2D newTexture = new Texture2D(filepath);
        textures2D.put(filepath, newTexture);
        return newTexture;
    }

    public static Texture2D getWhiteTexture() { return whiteTexture; }

    public static Shader getShader(String filepath) {
        if (shaders.containsKey(filepath))
            return shaders.get(filepath);

        Shader newShader = new Shader(filepath);
        shaders.put(filepath, newShader);
        return newShader;
    }

    public static void addSpriteSheet(String filepath, SpriteSheet spriteSheet) {
        if (!spriteSheets.containsKey(filepath))
            spriteSheets.put(filepath, spriteSheet);
    }

    public static SpriteSheet getSpriteSheet(String filepath) {
        if (!spriteSheets.containsKey(filepath))
            throw new NullPointerException(String.format("SpriteSheet not found - '%s'", filepath));
        return spriteSheets.getOrDefault(filepath, null);
    }

    public static Collection<Sound> getAllSounds() { return sounds.values(); }

    public static Sound getSound(String filepath) {
        if (sounds.containsKey(filepath))
            return sounds.get(filepath);

        Sound newSound = new Sound(filepath, false);
        sounds.put(filepath, newSound);
        return newSound;
    }

    public static Sound addSound(String filepath, boolean loops) {
        if (sounds.containsKey(filepath))
            return sounds.get(filepath);

        Sound newSound = new Sound(filepath, loops);
        sounds.put(filepath, newSound);
        return newSound;
    }

    public static Mesh getMesh(String filepath) { return ModelParser.loadFromFile(filepath); }

    public static Sprite getDefaultSprite() { return defaultSprite; }
}
