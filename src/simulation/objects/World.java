package simulation.objects;

import engine.objects.RenderableItem;
import engine.rendering.Mesh;
import org.joml.Vector2i;
import org.joml.Vector4i;
import simulation.Constants;

import java.nio.ByteBuffer;
import java.util.*;

public class World {

    private final int sizeX;
    private final int sizeZ;
    private final Chunk[] chunks;
    private final Map<Agent, Chunk> positions;
    private final Map<Agent, Float> rotations;
    private final Map<Agent, Chunk> lastPosition;
    private final Map<Agent, Float> lastRotation;

    public World(int sizeX, int sizeZ) {
        this.sizeX = sizeX;
        this.sizeZ = sizeZ;
        chunks = new Chunk[sizeX * sizeZ];
        positions = new HashMap<>();
        lastPosition = new HashMap<>();
        rotations = new HashMap<>();
        lastRotation = new HashMap<>();
    }

    public void init(Mesh chunkMesh) {
        for (int i = 0; i < sizeX * sizeZ; i++) {
            Chunk chunk = new Chunk(chunkMesh, i % sizeX, (i / sizeX));
            chunk.setPosition(i % sizeX, 0, (i / sizeX));
            chunks[i] = chunk;
        }
    }

    public Chunk getChunk(int x, int z) {
        if (x < 0 || z < 0 || x >= sizeX || z >= sizeZ)
            return null;
        return chunks[sizeX * z + x];
    }

    public void cleanup() {
        for (RenderableItem chunk : chunks) {
            chunk.getMesh().cleanUp();
        }
    }

    public boolean move(Agent agent, Direction dir, int distance) {
        int x = (positions.get(agent).getX() + dir.getDisplacement(distance).x);
        int z = (positions.get(agent).getZ() + dir.getDisplacement(distance).y);
        lastPosition.put(agent, positions.get(agent));
        boolean moved = put(agent, new Vector2i(x, z));

        //Update visual variables
        Chunk lastPos = lastPosition.get(agent);
        Chunk nextPos = positions.get(agent);
        Direction direction = Direction.get(lastPos.getX() - nextPos.getX(), lastPos.getZ() - nextPos.getZ());
        lastRotation.put(agent, rotations.get(agent));
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
        return moved;
    }

    public boolean put(Agent agent, Vector2i pos) {
        if (canMove(agent, pos)) {
            if (positions.get(agent) != null)
                positions.get(agent).setAgent(null);
            positions.put(agent, getChunk(pos.x, pos.y));
            getChunk(pos.x, pos.y).setAgent(agent);
            agent.setPosition(pos.x+.5f, 1, pos.y+.5f);
            return true;
        }
        return false;
    }

    public Object removeObject(Vector2i pos) {
        Object o = pos != null ? getChunk(pos.x, pos.y).getObject() : null;
        if (pos != null && hasObject(pos))
            getChunk(pos.x, pos.y).setObject(null);
        return o;
    }

    public boolean put(Object o, Vector2i pos) {
        if (!hasObject(pos) && o != null) {
            getChunk(pos.x, pos.y).setObject(o);
            o.setPosition(pos.x+.5f, 1, pos.y+.5f);
            return true;
        }
        return false;
    }

    public boolean canMove(Agent agent, Vector2i pos) {
        Chunk chunk = getChunk(pos.x, pos.y);
        return chunk != null && chunk.getAgent() == null;
    }

    public boolean canMove(Agent agent, Direction dir, int distance) {
        int x = (positions.get(agent).getX() + dir.getDisplacement(distance).x);
        int z = (positions.get(agent).getZ() + dir.getDisplacement(distance).y);
        return canMove(agent, new Vector2i(x, z));
    }

    public Chunk[] getChunks() {
        return chunks;
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeZ() {
        return sizeZ;
    }

    public void updateAnim(float percent) {
        for (Agent a : positions.keySet()) {
            Chunk lastPos = lastPosition.get(a);
            Chunk nextPos = positions.get(a);

            Float lastRot = lastRotation.get(a);
            Float nextRot = rotations.get(a);
            if (lastPos != null && nextPos != null && (lastPos.getX() != nextPos.getX() || lastPos.getZ() != nextPos.getZ())) {
                float x = (1 - percent) * lastPos.getX() + percent * nextPos.getX();
                float z = (1 - percent) * lastPos.getZ() + percent * nextPos.getZ();
                a.setPosition(x+.5f, 1, z+.5f);
            } else if (nextPos != null){
                a.setPosition(nextPos.getX() + 0.5f, 1, nextPos.getZ() + 0.5f);
            }

            float scaledPercent = Math.min(percent * 5, 1);
            if (lastRot != null && nextRot != null && (!lastRot.equals(nextRot))) {
                float dRot = nextRot - lastRot;
                //Just for the agent to turn toward the fastest direction
                if (dRot > 180)
                    dRot -= 360;
                else if (dRot < -180)
                    dRot += 360;
                a.setRotation(0, lastRot + scaledPercent * dRot, 0);
            } else if (nextRot != null) {
                a.setRotation(0, nextRot, 0);
            }
            a.anim();
            if (percent >= 0.99f) { //Stop the animation if finished (prevent looping when not moving)
                lastPosition.put(a, positions.get(a));
                lastRotation.put(a, rotations.get(a));
            }
        }
    }

    public boolean hasObject(Vector2i pos) {
        Chunk chunk = getChunk(pos.x, pos.y);
        return chunk != null && chunk.getObject() != null;
    }

    public Perception getPerception(Agent source) {
        int marker = 0;
        return new Perception(source, positions.get(source).getObject(), marker);
    }

    public Object pickUp(Agent agent) {
        return removeObject(new Vector2i(positions.get(agent).getX(), positions.get(agent).getZ()));
    }

    public boolean putDown(Agent agent, Object object) {
        return put(object, new Vector2i(positions.get(agent).getX(), positions.get(agent).getZ()));
    }

    public void update() {
        for (Chunk c : chunks) {
            c.setMarker(c.getMarker() * Constants.MARKER_ATTENUATION);
            if (c.getMarker() < .05)
                c.setMarker(0);
            if (Constants.SHOW_MARKERS)
                c.getAddedColor().x = c.getMarker();
            else
                c.getAddedColor().x = 0;
        }
    }

    public void putMarker(Agent source) {
        Chunk sourceChunk = positions.get(source);
        for (int i = -Constants.MARKER_RADIUS; i <= Constants.MARKER_RADIUS; i++) {
            for (int j = -Constants.MARKER_RADIUS; j <= Constants.MARKER_RADIUS; j++) {
                Chunk chunk = getChunk(sourceChunk.getX() + i, sourceChunk.getZ() + j);
                float dist = i*i + j*j;
                dist = (float) Math.sqrt(dist) + 1;
                if (chunk != null) {
                    chunk.setMarker(1f / dist);
                }
            }
        }
    }

    public Agent lookForAgentInNeed(Agent source) {
        Chunk sourceChunk = positions.get(source);
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                Chunk chunk = getChunk(sourceChunk.getX() + i, sourceChunk.getZ() + j);
                if (chunk != null) {
                    Agent agent = chunk.getAgent();
                    if (agent != null && agent != source && agent.isAskingForHelp())
                        return agent;
                }
            }
        }
        return null;
    }

    public Map<Float, Direction> getMarkers(Agent source) {
        Map<Float, Direction> markers = new TreeMap<>(Comparator.reverseOrder());
        Chunk sourceChunk = positions.get(source);
        for (Direction dir : Direction.values()) {
            if (dir == Direction.NONE)
                continue;
            Vector2i displacement = dir.getDisplacement(1);
            markers.put(getChunk(sourceChunk.getX() + displacement.x, sourceChunk.getZ() + displacement.y).getMarker(), dir);
        }
        return markers;
    }

    public void fillBuffer(ByteBuffer buffer) {
        buffer.clear();
        Vector4i color = new Vector4i(128, 128, 128, 255);
        for (Chunk chunk : chunks) {
            color.set(128, 128, 128, 255);
            if (chunk.getObject() != null) {
                switch (chunk.getObject().getType()) {
                    case A -> color.set((int) (Constants.A_COLOR.x * 255), (int) (Constants.A_COLOR.y * 255), (int) (Constants.A_COLOR.z * 255), (int) (Constants.A_COLOR.w * 255));
                    case B -> color.set((int) (Constants.B_COLOR.x * 255), (int) (Constants.B_COLOR.y * 255), (int) (Constants.B_COLOR.z * 255), (int) (Constants.B_COLOR.w * 255));
                    case C -> color.set((int) (Constants.C_COLOR.x * 255), (int) (Constants.C_COLOR.y * 255), (int) (Constants.C_COLOR.z * 255), (int) (Constants.C_COLOR.w * 255));
                }
            }
            buffer.put((byte) color.x);
            buffer.put((byte) color.y);
            buffer.put((byte) color.z);
            buffer.put((byte) color.w);
        }
        buffer.flip();
    }
}
