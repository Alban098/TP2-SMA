package engine.objects;

import engine.rendering.Material;
import engine.rendering.Mesh;
import engine.rendering.Texture;
import engine.utils.OBJLoader;

/**
 * This class represent the Skybox used in the Engine
 */
public class SkyBox extends RenderableItem {

    /**
     * Create a new Skybox
     * @param objModel the Skybox model file path
     * @param textureFile the Skybox texture file
     * @throws Exception thrown when model or texture couldn't be loaded or opened
     */
    public SkyBox(String objModel, String textureFile) throws Exception {
        super();
        Mesh mesh = OBJLoader.loadMesh(objModel);
        Texture texture = new Texture(textureFile);
        mesh.setMaterial(new Material(texture, null, 0.0f));
        setMesh(mesh);
        setPosition(0, 0, 0);
    }
}