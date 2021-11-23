package simulation.objects;

import engine.objects.RenderableItem;
import engine.rendering.Mesh;

public class Chunk extends RenderableItem {

    private final int x;
    private final int z;
    private Object object;
    private Agent agent;
    private float marker;

    public Chunk(Mesh mesh, int x, int z) {
        super(mesh);
        this.x = x;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public void setMarker(float marker) {
        this.marker = marker;
    }

    public float getMarker() {
        return marker;
    }
}
