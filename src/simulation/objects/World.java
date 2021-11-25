package simulation.objects;

import engine.rendering.Mesh;
import org.joml.Vector2i;
import settings.SettingsInterface;

import java.util.*;

public class World extends RenderableWorld {

    public World(int size, Mesh mesh) {
        super(size, mesh);
    }

    /**
     * Get the chunk at specified position
     * @param x chunk's X position
     * @param z chunk's Z position
     * @return the chunk at (X,Z), null if out of range
     */
    public Chunk getChunk(int x, int z) {
        if (x < 0 || z < 0 || x >= size || z >= size)
            return null;
        return chunks[size * z + x];
    }

    /**
     * Move an Agent a specified distance in a direction
     * @param agent the Agent to move
     * @param dir the Direction to move to
     * @param distance the distance to move
     */
    public void move(Agent agent, Direction dir, int distance) {
        int x = (positions.get(agent).getX() + dir.getDisplacement(distance).x);
        int z = (positions.get(agent).getZ() + dir.getDisplacement(distance).y);
        Chunk lastChunk = positions.get(agent);
        put(agent, new Vector2i(x, z));

        updateVisual(agent, lastChunk);
    }

    /**
     * Put an Agent to a specified position
     * @param agent the Agent to put
     * @param pos the position to put it to
     */
    public void put(Agent agent, Vector2i pos) {
        if (canMove(agent, pos)) {
            // If this agent is present in another chunk, remove it
            if (positions.get(agent) != null)
                positions.get(agent).setAgent(null);
            positions.put(agent, getChunk(pos.x, pos.y));
            getChunk(pos.x, pos.y).setAgent(agent);
            //Center the agent in the chunk, for rendering
            agent.setPosition(pos.x+.5f, 1, pos.y+.5f);
        }
    }

    /**
     * Put an Object to a specified position
     * @param o the Object to put
     * @param pos the position to put it to
     */
    public boolean put(Object o, Vector2i pos) {
        if (!hasObject(pos) && o != null) {
            getChunk(pos.x, pos.y).setObject(o);
            o.setPosition(pos.x+.5f, 1, pos.y+.5f);
            return true;
        }
        return false;
    }

    /**
     * Remove an object at specified position if any
     * @param pos the position to remove the object from
     * @return the removed Object, null otherwise
     */
    public Object removeObject(Vector2i pos) {
        Object o = pos != null ? getChunk(pos.x, pos.y).getObject() : null;
        if (pos != null && hasObject(pos))
            getChunk(pos.x, pos.y).setObject(null);
        return o;
    }

    /**
     * Return whether an Agent can move to a specified position or not
     * @param agent the Agent to move
     * @param pos the position to move it to
     * @return can the Agent move to the specified position
     */
    public boolean canMove(Agent agent, Vector2i pos) {
        Chunk chunk = getChunk(pos.x, pos.y);
        return chunk != null && chunk.getAgent() == null && agent != chunk.getAgent();
    }

    /**
     * Return whether an Agent can move a specified distance in a direction or not
     * @param agent the Agent to move
     * @param dir the Direction to move to
     * @param distance the distance to move
     * @return can the Agent move
     */
    public boolean canMove(Agent agent, Direction dir, int distance) {
        int x = (positions.get(agent).getX() + dir.getDisplacement(distance).x);
        int z = (positions.get(agent).getZ() + dir.getDisplacement(distance).y);
        return canMove(agent, new Vector2i(x, z));
    }

    /**
     * Return whether there is an Object at a specified position or nor
     * @param pos the position to check
     * @return is there an Object at the specified position
     */
    public boolean hasObject(Vector2i pos) {
        Chunk chunk = getChunk(pos.x, pos.y);
        return chunk != null && chunk.getObject() != null;
    }

    /**
     * Return a local perception from the point of view of an Agent
     * @param source the Agent to percepts from
     * @return the local perception of the Agent
     */
    public Perception getPerception(Agent source) {
        return new Perception(positions.get(source).getObject(), positions.get(source).getMarker());
    }

    /**
     * Remove an Object from the World
     * @param agent the Agent picking the Object
     * @return the picked up Object
     */
    public Object pickUp(Agent agent) {
        return removeObject(new Vector2i(positions.get(agent).getX(), positions.get(agent).getZ()));
    }

    /**
     * Put down an object to the world at the Agent position
     * @param agent the Agent putting the Object down
     * @param object the Object to put down
     * @return has the Object been put down
     */
    public boolean putDown(Agent agent, Object object) {
        return put(object, new Vector2i(positions.get(agent).getX(), positions.get(agent).getZ()));
    }

    /**
     * Update every Chunks for help markers
     */
    public void update() {
        for (Chunk c : chunks) {
            c.setMarker(c.getMarker() * SettingsInterface.MARKER_ATTENUATION);
            if (c.getMarker() < .05)
                c.setMarker(0);
        }
    }

    /**
     * Spawn a help marker to the world and spreads it out
     * @param source the Agent putting down the marker
     */
    public void putMarker(Agent source) {
        Chunk sourceChunk = positions.get(source);
        for (int i = -SettingsInterface.MARKER_RADIUS; i <= SettingsInterface.MARKER_RADIUS; i++) {
            for (int j = -SettingsInterface.MARKER_RADIUS; j <= SettingsInterface.MARKER_RADIUS; j++) {
                Chunk chunk = getChunk(sourceChunk.getX() + i, sourceChunk.getZ() + j);
                float dist = i*i + j*j;
                dist = (float) Math.sqrt(dist) + 1;
                if (chunk != null) {
                    chunk.setMarker(1f / dist);
                }
            }
        }
    }

    /**
     * Get an Agent asking for help in the immediate neighborhood of the source Agent
     * @param source the agent looking
     * @return an Agent asking for help and adjacent to the source Agent, null otherwise
     */
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

    /**
     * Return a Map of the help markers value in the local surrounding of the agent
     * the map is Ordered from most to least markers, indexed by Direction
     * @param source the Agent doing the perception
     * @return the ordered Map of Direction and help markers
     */
    public Map<Float, Direction> getMarkers(Agent source) {
        Map<Float, Direction> markers = new TreeMap<>(Comparator.reverseOrder());
        Chunk sourceChunk = positions.get(source);
        for (Direction dir : Direction.values()) {
            if (dir == Direction.NONE)
                continue;
            Vector2i displacement = dir.getDisplacement(1);
            Chunk chunk = getChunk(sourceChunk.getX() + displacement.x, sourceChunk.getZ() + displacement.y);
            if (chunk != null)
                markers.put(chunk.getMarker(), dir);
        }
        return markers;
    }
}
