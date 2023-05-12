package engine.assets;

import engine.parsers.ModelParser;
import engine.audio.Sound;
import engine.renderer.Texture2D;
import engine.renderer.renderer2D.sprite.SpriteSheet;
import engine.renderer.renderer3D.mesh.Mesh;
import engine.renderer.shader.Shader;

import java.util.*;

import static org.lwjgl.opengl.GL11.*;

public class AssetPool {

    private static final Map<String, Texture2D> textures2D = new HashMap<>();
    private static final Map<String, Shader> shaders = new HashMap<>();
    private static final Map<String, SpriteSheet> spriteSheets = new HashMap<>();
    private static final Map<String, Sound> sounds = new HashMap<>();

    private static final int whiteTexture;
    static {
        // Generate white OpenGL texture.
        whiteTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, whiteTexture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, 1, 1, 0, GL_RGB, GL_UNSIGNED_BYTE, new int[]{ 0xFFFFFFFF });
    }

    public static Texture2D getTexture(String filepath) {
        if (textures2D.containsKey(filepath))
            return textures2D.get(filepath);

        Texture2D newTexture = new Texture2D(filepath);
        textures2D.put(filepath, newTexture);
        return newTexture;
    }

    public static int getWhiteTexture() { return whiteTexture; }

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
