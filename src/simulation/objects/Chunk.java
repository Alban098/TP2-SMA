package simulation.objects;

/**
 * This class represent a Chunk that can contain an Object, an Agent and a marker value
 */
public class Chunk {

    private final int x;
    private final int z;

    private Object object;
    private Agent agent;
    private float marker;

    /**
     * Create a new Chunk
     * @param x the Chunk position on the X axis
     * @param z the Chunk position on the Z Axis
     */
    public Chunk(int x, int z) {
        this.x = x;
        this.z = z;
    }

    /**
     * Return the Chunk's X position
     * @return the Chunk's X position
     */
    public int getX() {
        return x;
    }

    /**
     * Return the Chunk's Z position
     * @return the Chunk's Z position
     */
    public int getZ() {
        return z;
    }

    /**
     * Return the Object present in this Chunk
     * @return the Object in the Chunk, null if absent
     */
    public Object getObject() {
        return object;
    }

    /**
     * Set the Object in the Chunk
     * @param object the Object to put in the Chunk
     */
    public void setObject(Object object) {
        this.object = object;
    }

    /**
     * Return the Agent present in this Chunk
     * @return the Agent in the Chunk, null if absent
     */
    public Agent getAgent() {
        return agent;
    }

    /**
     * Set the Agent in the Chunk
     * @param agent the Agent to put in the Chunk
     */
    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    /**
     * Set the marker value of the Chunk
     * @param marker the new marker value of the Chunk
     */
    public void setMarker(float marker) {
        this.marker = marker;
    }

    /**
     * Return the current marker value of the Chunk
     * @return the current marker value of the Chunk
     */
    public float getMarker() {
        return marker;
    }
}
