package engine.rendering;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

/**
 * This class represent a Texture
 */
public class Texture {

    private final int id;
    private final int width;
    private final int height;

    /**
     * Create a new Texture from a file
     * @param fileName Path to the texture file
     * @throws Exception thrown when unable to load the file
     */
    public Texture(String fileName) throws Exception {
        this(loadTexture(fileName));
    }

    /**
     * Create a new empty Texture from attributes
     * @param attrib an Array of int {Texture id, width in pixels, height in pixels}
     */
    public Texture(int[] attrib) {
        this.id = attrib[0];
        this.width = attrib[1];
        this.height = attrib[2];
    }

    /**
     * Create a new empty Texture
     * @param width the Texture width in pixels
     * @param height the Texture height in pixels
     */
    public Texture(int width, int height) {
        this.width = width;
        this.height = height;
        //Generate the texture
        id = glGenTextures();
        bind();
        //Set the filtering mode
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        //Load the buffer in VRAM
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, BufferUtils.createByteBuffer(width * height * 4));
    }

    /**
     * Bind the texture for rendering
     */
    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    /**
     * Return the Texture id
     * @return the Texture id
     */
    public int getId() {
        return id;
    }

    /**
     * Load a texture from a file
     * @param fileName the Path to the texture file
     * @return an Array containing {Texture ID, width in pixels, height in pixels}
     * @throws Exception thrown when unable to load the file
     */
    private static int[] loadTexture(String fileName) throws Exception {
        int width;
        int height;
        ByteBuffer buf;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            buf = stbi_load("resources/" + fileName, w, h, channels, 4);
            if (buf == null) {
                throw new Exception("Image file [" + fileName  + "] not loaded: " + stbi_failure_reason());
            }

            width = w.get();
            height = h.get();
        }

        // Create a new OpenGL texture
        int textureId = glGenTextures();
        // Bind the texture
        glBindTexture(GL_TEXTURE_2D, textureId);

        // Tell OpenGL how to unpack the RGBA bytes. Each component is 1 byte size
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        // Upload the texture data
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
        // Generate Mip Map
        glGenerateMipmap(GL_TEXTURE_2D);

        stbi_image_free(buf);

        return new int[] {textureId, width, height};
    }

    /**
     * Load a byte buffer in the texture
     * @param buf the buffer to load
     */
    public void load(ByteBuffer buf) {
        bind();
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
    }

    /**
     * Cleanup the Texture
     */
    public void cleanup() {
        glDeleteTextures(id);
    }
}
