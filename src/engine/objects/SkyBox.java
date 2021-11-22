package engine.objects;

import engine.rendering.Material;
import engine.rendering.Mesh;
import engine.rendering.Texture;
import engine.utils.OBJLoader;

public class SkyBox extends RenderableItem {

    public SkyBox(String objModel, String textureFile) throws Exception {
        super();
        Mesh mesh = OBJLoader.loadMesh(objModel);
        Texture texture = new Texture(textureFile);
        mesh.setMaterial(new Material(texture, 0.0f));
        setMesh(mesh);
        setPosition(0, 0, 0);
    }
}