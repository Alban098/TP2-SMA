package engine.objects;

import engine.rendering.Mesh;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class RenderableItem {

    private Mesh mesh;

    private final Vector3f position;

    private float scale;

    private final Vector3f rotation;

    private final Vector4f addedColor;

    public RenderableItem() {
        position = new Vector3f();
        scale = 1;
        rotation = new Vector3f();
        addedColor = new Vector4f();
    }

    public RenderableItem(Mesh mesh) {
        this();
        this.mesh = mesh;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    public Vector4f getAddedColor() {
        return addedColor;
    }
}