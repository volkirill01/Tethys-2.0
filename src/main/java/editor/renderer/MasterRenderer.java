package editor.renderer;

import editor.renderer.shader.Shader;

public class MasterRenderer {

    private static Shader currentShader;

    public static Shader getCurrentShader() { return MasterRenderer.currentShader; }

    public static void bindShader(Shader shader) {
        currentShader = shader;
    }
}
