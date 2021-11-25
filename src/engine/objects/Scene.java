package engine.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import engine.rendering.Mesh;
import simulation.objects.World;

/**
 * This class represent a Scene that is rendered by the Engine
 */
public class Scene {

    private final Map<Mesh, List<RenderableItem>> meshMap;
    
    private SkyBox skyBox;
    private World world;
    private SceneLight sceneLight;

    /**
     * Create an empty Scene
     */
    public Scene() {
        meshMap = new HashMap<>();
    }

    /**
     * Return the Map of Meshes and Item currently in the Scene
     * @return a Map of Items, indexed by Meshes
     */
    public Map<Mesh, List<RenderableItem>> getMeshes() {
        return meshMap;
    }

    /**
     * Add a group of Items to the Scene, for them to be rendered
     * @param gameItems an Array of Items
     */
    public void registerItems(RenderableItem[] gameItems) {
        int numGameItems = gameItems != null ? gameItems.length : 0;
        for (int i=0; i<numGameItems; i++) {
            RenderableItem gameItem = gameItems[i];
            registerItem(gameItem);
        }
    }

    /**
     * Add an Item to the Scene, for it to be rendered
     * @param item the Item to add
     */
    public void registerItem(RenderableItem item) {
        Mesh mesh = item.getMesh();
        List<RenderableItem> list = meshMap.computeIfAbsent(mesh, k -> new ArrayList<>());
        list.add(item);
    }

    /**
     * Return the current Scene World
     * @return the Scene World
     */
    public World getWorld() {
        return world;
    }

    /**
     * Set the Scene World
     * @param world teh new Scene World
     */
    public void setWorld(World world) {
        this.world = world;
    }

    /**
     * Return the current Scene's Skybox
     * @return the Scene's Skybox
     */
    public SkyBox getSkyBox() {
        return skyBox;
    }

    /**
     * Set the Scene's Skybox
     * @param skyBox the new Scene's Skybox
     */
    public void setSkyBox(SkyBox skyBox) {
        this.skyBox = skyBox;
    }

    /**
     * Return the current Scene Light
     * @return the current Scene Light
     */
    public SceneLight getSceneLight() {
        return sceneLight;
    }

    /**
     * Set the current SceneLight
     * @param sceneLight the new Scene Light
     */
    public void setSceneLight(SceneLight sceneLight) {
        this.sceneLight = sceneLight;
    }

    /**
     * Reset the Scene
     * Remove all Objects
     */
    public void reset() {
        meshMap.clear();
        world = null;
    }
}
