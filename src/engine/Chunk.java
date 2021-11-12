package engine;

import engine.RenderableItem;
import engine.graph.Mesh;

public class Chunk extends RenderableItem {

    private int x;
    private int z;
    private RenderableItem object;
    private RenderableItem agent;

    public Chunk(Mesh mesh, int x, int z) {
        super(mesh);
        this.x = x;
        this.z = z;
    }
}
