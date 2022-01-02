package simulation.objects;

import engine.objects.RenderableItem;
import engine.rendering.Mesh;
import engine.rendering.Texture;
import org.joml.Vector4i;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL13C;
import settings.SettingsInterface;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains the methods used for rendering and animating the world
 */
public class RenderableWorld extends RenderableItem{

    protected final Map<Agent, Chunk> lastPosition;
    protected final Map<Agent, Float> lastRotation;

    private final ByteBuffer help_marker_buffer;
    private final Texture help_marker_texture;

    protected final Map<Agent, Chunk> positions;
    protected final Map<Agent, Float> rotations;
    protected final Chunk[] chunks;
    protected int size;

    /**
     * Create a new RenderableWorld
     * @param size the size of the world
     * @param mesh the meshed used by the world
     */
    public RenderableWorld(int size, Mesh mesh) {
        super(mesh);
        setPosition(0, 1, 0);
        positions = new HashMap<>();
        rotations = new HashMap<>();
        lastPosition = new HashMap<>();
        lastRotation = new HashMap<>();
        chunks = new Chunk[size * size];
        this.size = size;
        for (int i = 0; i < size * size; i++) {
            Chunk chunk = new Chunk(i % size, (i / size));
            chunks[i] = chunk;
        }
        help_marker_buffer = BufferUtils.createByteBuffer(size * size * 4);
        help_marker_texture = new Texture(size, size);
        mesh.addTexture(GL13C.GL_TEXTURE2, help_marker_texture);
    }

    /**
     * The mesh is scaled according to the size of the world so scale = size
     * @return the size of the world
     */
    @Override
    public float getScale() {
        return size;
    }

    /**
     * Update the visual variable of an agent
     * this is only used for animation between updates
     * @param agent the Agent to update
     */
    protected void updateVisual(Agent agent) {
        Chunk nextPos = positions.get(agent);
        Chunk lastPos = lastPosition.get(agent) == null ? nextPos : lastPosition.get(agent);
        Direction direction = Direction.get(lastPos.getX() - nextPos.getX(), lastPos.getZ() - nextPos.getZ());
        switch (direction) {
            case NORTH -> rotations.put(agent, 0f);
            case SOUTH -> rotations.put(agent, 180f);
            case EAST -> rotations.put(agent, 270f);
            case WEST -> rotations.put(agent, 90f);
            case NORTH_EAST -> rotations.put(agent, 315f);
            case NORTH_WEST -> rotations.put(agent, 45f);
            case SOUTH_EAST -> rotations.put(agent, 225f);
            case SOUTH_WEST -> rotations.put(agent, 135f);
        }
    }

    /**
     * Update the animation of the Agents
     * @param percent the progress of animation (between 2 updates) from 0 to 1
     */
    public void animate(double percent) {
        for (Agent a : positions.keySet()) {
            Chunk lastPos = lastPosition.get(a);
            Chunk nextPos = positions.get(a);
            Float lastRot = lastRotation.get(a);
            Float nextRot = rotations.get(a);

            //Linear interpolation between the 2 positions
            if (lastPos != null && nextPos != null && (lastPos.getX() != nextPos.getX() || lastPos.getZ() != nextPos.getZ())) {
                float x = (float) ((1 - percent) * lastPos.getX() + percent * nextPos.getX());
                float z = (float) ((1 - percent) * lastPos.getZ() + percent * nextPos.getZ());
                a.setPosition(x+.5f, 1, z+.5f);
            } else if (nextPos != null){
                a.setPosition(nextPos.getX() + 0.5f, 1, nextPos.getZ() + 0.5f);
            }

            //Linear interpolation between the 2 rotation
            float scaledPercent = (float) Math.min(percent * 5, 1);
            if (lastRot != null && nextRot != null && (!lastRot.equals(nextRot))) {
                float dRot = nextRot - lastRot;
                //Find the rotation direction that is the fastest
                if (dRot > 180)
                    dRot -= 360;
                else if (dRot < -180)
                    dRot += 360;
                a.setRotation(0, lastRot + scaledPercent * dRot, 0);
            } else if (nextRot != null) {
                a.setRotation(0, nextRot, 0);
            }

            //update the carried object if it exist
            a.anim();

            //Prevent the animation from looping (normally not useful, but just in case for the sake of it)
            if (percent >= 0.99f) {
                lastPosition.put(a, nextPos);
                lastRotation.put(a, nextRot);
            }
        }
    }

    /**
     * Fill the 3 ByteBuffers with the world's information
     * @param objectBuffer the buffer containing the object map
     * @param agentBuffer the buffer containing the agent map
     * @param markerBuffer the buffer containing the marker map
     */
    public void fillBuffers(ByteBuffer objectBuffer, ByteBuffer agentBuffer, ByteBuffer markerBuffer) {
        objectBuffer.clear();
        agentBuffer.clear();
        markerBuffer.clear();
        Vector4i objectColor = new Vector4i();
        Vector4i agentColor = new Vector4i();
        Vector4i markerColor = new Vector4i();
        for (Chunk chunk : chunks) {
            objectColor.set(128, 128, 128, 255);
            agentColor.set(128, 128, 128, 255);
            markerColor.set(128, 128, 128, 255);
            //If the Chunk contains an Object, set it's color to the Object's color
            if (chunk.getObject() != null) {
                switch (chunk.getObject().getType()) {
                    case A -> objectColor.set((int) (SettingsInterface.A_COLOR.x * 255), (int) (SettingsInterface.A_COLOR.y * 255), (int) (SettingsInterface.A_COLOR.z * 255), (int) (SettingsInterface.A_COLOR.w * 255));
                    case B -> objectColor.set((int) (SettingsInterface.B_COLOR.x * 255), (int) (SettingsInterface.B_COLOR.y * 255), (int) (SettingsInterface.B_COLOR.z * 255), (int) (SettingsInterface.B_COLOR.w * 255));
                    case C -> objectColor.set((int) (SettingsInterface.C_COLOR.x * 255), (int) (SettingsInterface.C_COLOR.y * 255), (int) (SettingsInterface.C_COLOR.z * 255), (int) (SettingsInterface.C_COLOR.w * 255));
                }
            }
            //If the Chunk contains an Agent, set it's color to the Agent's carried Object's color
            if (chunk.getAgent() != null) {
                if (chunk.getAgent().getCarriedObject() != null) {
                    switch (chunk.getAgent().getCarriedObject().getType()) {
                        case A -> agentColor.set((int) (SettingsInterface.A_COLOR.x * 255), (int) (SettingsInterface.A_COLOR.y * 255), (int) (SettingsInterface.A_COLOR.z * 255), (int) (SettingsInterface.A_COLOR.w * 255));
                        case B -> agentColor.set((int) (SettingsInterface.B_COLOR.x * 255), (int) (SettingsInterface.B_COLOR.y * 255), (int) (SettingsInterface.B_COLOR.z * 255), (int) (SettingsInterface.B_COLOR.w * 255));
                        case C -> agentColor.set((int) (SettingsInterface.C_COLOR.x * 255), (int) (SettingsInterface.C_COLOR.y * 255), (int) (SettingsInterface.C_COLOR.z * 255), (int) (SettingsInterface.C_COLOR.w * 255));
                    }
                } else {
                    agentColor.set(255, 255, 255, 255);
                }
            }

            //Set the red level according to the Chunk's marker level
            markerColor.x += Math.min(chunk.getMarker() * 127, 127);

            objectBuffer.put((byte) objectColor.x);
            objectBuffer.put((byte) objectColor.y);
            objectBuffer.put((byte) objectColor.z);
            objectBuffer.put((byte) objectColor.w);

            agentBuffer.put((byte) agentColor.x);
            agentBuffer.put((byte) agentColor.y);
            agentBuffer.put((byte) agentColor.z);
            agentBuffer.put((byte) agentColor.w);

            markerBuffer.put((byte) markerColor.x);
            markerBuffer.put((byte) markerColor.y);
            markerBuffer.put((byte) markerColor.z);
            markerBuffer.put((byte) markerColor.w);
        }

        //Flip the buffers to render then in a texture sampler later
        objectBuffer.flip();
        agentBuffer.flip();
        markerBuffer.flip();
    }

    /**
     * Render the marker texture sampler used to render them on the World
     */
    public void renderMarkers() {
        for (Chunk c : chunks) {
            help_marker_buffer.put((byte) ((int)((SettingsInterface.SHOW_MARKERS ? c.getMarker() : 0) * 255) & 0xFF));
            help_marker_buffer.put((byte) 0);
            help_marker_buffer.put((byte) 0);
            help_marker_buffer.put((byte) 0);
        }
        help_marker_buffer.flip();
        help_marker_texture.load(help_marker_buffer);
        help_marker_buffer.clear();
    }

    /**
     * Return the size of the World
     * @return the World's size
     */
    public int getSize() {
        return size;
    }

    /**
     * Clean up the memory used by the World's mesh
     */
    public void cleanup() {
        getMesh().cleanUp();
        getMesh().clearSamplers();
    }
}
