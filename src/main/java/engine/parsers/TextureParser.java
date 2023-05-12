package engine.parsers;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class TextureParser {

    public static int loadFromFile(String filepath, IntBuffer width, IntBuffer height) {
        // Generate texture on GPU
        int textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);

        // Set texture parameters
        // Repeat image in both directions
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        // When stretching the image, pixelate it
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        // When shrinking an image, pixelate it
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        IntBuffer channels = BufferUtils.createIntBuffer(1);
        stbi_set_flip_vertically_on_load(true);
        ByteBuffer image = stbi_load(filepath, width, height, channels, 0);

        if (image == null)
            throw new NullPointerException(String.format("Could not load image - '%s'", filepath));

        switch (channels.get(0)) {
            case 3 -> glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB8, width.get(0), height.get(0), 0, GL_RGB, GL_UNSIGNED_BYTE, image);
            case 4 -> glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width.get(0), height.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            default -> throw new IllegalStateException(String.format("Unknown number of channels: (%d) - '%s'", channels.get(0), filepath));
        }

        stbi_image_free(image);
        return textureID;
    }
}
