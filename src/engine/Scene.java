package engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import engine.graph.Mesh;

public class Scene {

    private Map<Mesh, List<RenderableItem>> meshMap;
    
    private SkyBox skyBox;
    private World world;
    private SceneLight sceneLight;

    public Scene() {
        meshMap = new HashMap();
    }
    
    public Map<Mesh, List<RenderableItem>> getMeshes() {
        return meshMap;
    }

    public void registerItems(RenderableItem[] gameItems) {
        int numGameItems = gameItems != null ? gameItems.length : 0;
        for (int i=0; i<numGameItems; i++) {
            RenderableItem gameItem = gameItems[i];
            registerItem(gameItem);
        }
    }

    public void registerItem(RenderableItem item) {
        Mesh mesh = item.getMesh();
        List<RenderableItem> list = meshMap.computeIfAbsent(mesh, k -> new ArrayList<>());
        list.add(item);
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
        registerItems(world.getChunks());
    }

    public SkyBox getSkyBox() {
        return skyBox;
    }

    public void setSkyBox(SkyBox skyBox) {
        this.skyBox = skyBox;
    }

    public SceneLight getSceneLight() {
        return sceneLight;
    }

    public void setSceneLight(SceneLight sceneLight) {
        this.sceneLight = sceneLight;
    }
    
}
