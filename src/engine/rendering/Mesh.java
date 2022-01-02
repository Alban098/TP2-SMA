package engine.rendering;

import engine.objects.RenderableItem;
import org.lwjgl.system.MemoryUtil;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * This class represent a 3D Mesh that can be rendered by the Engine
 */
public class Mesh {

    private final int vaoId;
    private final List<Integer> vboIdList;
    private final int vertexCount;
    private Material material;
    private final Map<Integer, Texture> extra_samplers;

    /**
     * Create a new Mesh from RAW data
     * @param positions Vertex Position (3 floats)
     * @param textCoords Vertex UVs (2 floats)
     * @param normals Vertex Normals (3 floats)
     * @param indices Triangle indices (1 int)
     */
    public Mesh(float[] positions, float[] textCoords, float[] normals, int[] indices) {
        FloatBuffer posBuffer = null;
        FloatBuffer textCoordsBuffer = null;
        FloatBuffer vecNormalsBuffer = null;
        IntBuffer indicesBuffer = null;
        try {
            vertexCount = indices.length;
            vboIdList = new ArrayList<>();

            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            // Position VBO
            int vboId = glGenBuffers();
            vboIdList.add(vboId);
            posBuffer = MemoryUtil.memAllocFloat(positions.length);
            posBuffer.put(positions).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, posBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            // Texture coordinates VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            textCoordsBuffer = MemoryUtil.memAllocFloat(textCoords.length);
            textCoordsBuffer.put(textCoords).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

            // Vertex normals VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            vecNormalsBuffer = MemoryUtil.memAllocFloat(normals.length);
            vecNormalsBuffer.put(normals).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, vecNormalsBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(2);
            glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

            // Index VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        } finally {
            if (posBuffer != null) {
                MemoryUtil.memFree(posBuffer);
            }
            if (textCoordsBuffer != null) {
                MemoryUtil.memFree(textCoordsBuffer);
            }
            if (vecNormalsBuffer != null) {
                MemoryUtil.memFree(vecNormalsBuffer);
            }
            if (indicesBuffer != null) {
                MemoryUtil.memFree(indicesBuffer);
            }
        }
        extra_samplers = new HashMap<>();
    }

    /**
     * Return the Mesh Material
     * @return the Mesh Material
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Set the Mesh Material
     * @param material the new Mesh Material
     * @return the Mesh, to chain method calls
     */
    public Mesh setMaterial(Material material) {
        this.material = material;
        return this;
    }

    /**
     * Return the Mesh VAO id
     * @return the Mesh VAO id
     */
    public int getVaoId() {
        return vaoId;
    }

    /**
     * Return the number of Vertices of the Mesh
     * @return the number of Vertices of the Mesh
     */
    public int getVertexCount() {
        return vertexCount;
    }

    /**
     * Initialize the renderer to render the Mesh
     * by binding Textures and VAO
     */
    private void initRender() {
        Texture texture = material.getTexture();
        Texture normal = material.getNormal();
        if (texture != null) {
            // Activate firs texture bank
            glActiveTexture(GL_TEXTURE0);
            // Bind the texture
            texture.bind();
        }

        if (normal != null) {
            // Activate firs texture bank
            glActiveTexture(GL_TEXTURE1);
            // Bind the texture
            normal.bind();
        }
        for (Map.Entry<Integer, Texture> entry : extra_samplers.entrySet()) {
            glActiveTexture(entry.getKey());
            entry.getValue().bind();
        }

        // Draw the mesh
        glBindVertexArray(getVaoId());
    }

    /**
     * End the renderer for this Mesh
     * by unbinding Texture and VAO
     */
    private void endRender() {
        // Restore state
        glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    /**
     * Render the Mesh
     */
    public void render() {
        initRender();
        glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
        endRender();
    }

    /**
     * Render a list of Items that are represented by this Mesh
     * @param gameItems the list of Items to render
     * @param consumer a Function taking the current Item called before rendering (usually for loading uniforms)
     */
    public void renderList(List<RenderableItem> gameItems, Consumer<RenderableItem> consumer) {
        initRender();

        for (RenderableItem gameItem : gameItems) {
            // Set up data required by GameItem
            consumer.accept(gameItem);
            // Render this game item
            glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
        }

        endRender();
    }

    /**
     * Clean up the Mesh and its Textures/VAO/VBOs
     */
    public void cleanUp() {
        glDisableVertexAttribArray(0);

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for (int vboId : vboIdList) {
            glDeleteBuffers(vboId);
        }

        // Delete the texture
        Texture texture = material.getTexture();
        if (texture != null) {
            texture.cleanup();
        }

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }

    /**
     * Add a new Texture to the model along with its texture location (location from GLSL shaders)
     * @param textureUnit the Texture location (from GLSL shaders)
     * @param texture the Texture to add
     */
    public void addTexture(int textureUnit, Texture texture) {
        extra_samplers.put(textureUnit, texture);
    }

    /**
     * Clear the Texture map
     */
    public void clearSamplers() {
        extra_samplers.clear();
    }
}
