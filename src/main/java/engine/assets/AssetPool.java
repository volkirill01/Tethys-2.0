package engine.assets;

import engine.logging.DebugLog;
import engine.parsers.ModelParser;
import engine.audio.Sound;
import engine.profiling.Profiler;
import engine.renderer.Texture2D;
import engine.renderer.renderer2D.sprite.Sprite;
import engine.renderer.renderer2D.sprite.SpriteSheet;
import engine.renderer.renderer3D.mesh.Mesh;
import engine.renderer.shader.Shader;
import engine.renderer.shader.uniforms.UniformBuffer;
import engine.renderer.shader.uniforms.UniformBufferElement;
import engine.stuff.openGL.ShaderDataType;

import java.util.*;

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
        defaultSprite.setTexture(getTexture("tmp.png"));
        defaultSprite.setWidth(1);
        defaultSprite.setHeight(1);
    }

    public static Texture2D getTexture(String filepath) {
        if (textures2D.containsKey(filepath))
            return textures2D.get(filepath);

        DebugLog.logInfo("AssetPool:Create new Texture: ", filepath, ".");

        Texture2D newTexture = new Texture2D(filepath);
        textures2D.put(filepath, newTexture);
        return newTexture;
    }

    public static Texture2D getWhiteTexture() { return whiteTexture; }

    public static Shader getShader(String filepath, boolean addSceneDataBlock) {
        if (shaders.containsKey(filepath))
            return shaders.get(filepath);

        DebugLog.logInfo("AssetPool:Create new Shader: ", filepath, ".");

        Shader newShader = new Shader(filepath);
        if (addSceneDataBlock)
            newShader.addUniformBuffer(new UniformBuffer("u_SceneData", newShader.getShaderProgramID(),
                    new UniformBufferElement(ShaderDataType.Mat4, "u_ProjectionMatrix"),
                    new UniformBufferElement(ShaderDataType.Mat4, "u_ViewMatrix")
            ));
        shaders.put(filepath, newShader);
        return newShader;
    }

    public static void addSpriteSheet(String filepath, SpriteSheet spriteSheet) {
        DebugLog.logInfo("AssetPool:AddSpriteSheet: ", filepath, ".");

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

        DebugLog.logInfo("AssetPool:Create new Sound: ", filepath, ".");

        Sound newSound = new Sound(filepath, false);
        sounds.put(filepath, newSound);
        return newSound;
    }

    public static Sound addSound(String filepath, boolean loops) {
        if (sounds.containsKey(filepath))
            return sounds.get(filepath);

        DebugLog.logInfo("AssetPool:AddSound: ", filepath, ".");

        Sound newSound = new Sound(filepath, loops);
        sounds.put(filepath, newSound);
        return newSound;
    }

    public static Mesh getMesh(String filepath) { return ModelParser.loadFromFile(filepath); }

    public static Sprite getDefaultSprite() { return defaultSprite; }

    public static void freeMemory() {
        DebugLog.logInfo("AssetPool:FreeMemory.");
        Profiler.startTimer("AssetPool FreeMemory");
        for (Texture2D texture : textures2D.values())
            texture.freeMemory();
        for (Shader shader : shaders.values())
            shader.freeMemory();
        for (Sound sound : sounds.values())
            sound.freeMemory();
        Profiler.stopTimer("AssetPool FreeMemory");
    }
}
