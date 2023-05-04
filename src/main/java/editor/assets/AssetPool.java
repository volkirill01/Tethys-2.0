package editor.assets;

import editor.parsers.ModelParser;
import editor.renderer.Texture;
import editor.audio.Sound;
import editor.renderer.renderer2D.sprite.SpriteSheet;
import editor.renderer.renderer3D.mesh.Mesh;
import editor.renderer.shader.Shader;

import java.util.*;

public class AssetPool {

    private static final Map<String, Texture> textures = new HashMap<>();
    private static final Map<String, Shader> shaders = new HashMap<>();
    private static final Map<String, SpriteSheet> spriteSheets = new HashMap<>();
    private static final Map<String, Sound> sounds = new HashMap<>();

    public static Texture getTexture(String filepath) {
        if (textures.containsKey(filepath))
            return textures.get(filepath);

        Texture newTexture = new Texture(filepath);
        textures.put(filepath, newTexture);
        return newTexture;
    }

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
}
