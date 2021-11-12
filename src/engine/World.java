package engine;

import engine.graph.Mesh;

public class World {

    private final int sizeX;
    private final int sizeZ;
    private final Chunk[] chunks;

    public World(int sizeX, int sizeZ) {
        this.sizeX = sizeX;
        this.sizeZ = sizeZ;
        chunks = new Chunk[sizeX * sizeZ];
    }

    public void init(Mesh chunkMesh) {
        for (int i = 0; i < sizeX * sizeZ; i++) {
            Chunk chunk = new Chunk(chunkMesh, i % sizeX, (i / sizeX));
            chunk.setPosition(i % sizeX - sizeX/2, 0, (i / sizeX) - sizeZ / 2);
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

    public Chunk[] getChunks() {
        return chunks;
    }
}
