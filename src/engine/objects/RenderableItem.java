package engine.objects;

import engine.rendering.Mesh;
import org.joml.Vector3f;

/**
 * This class represent an Item that can be rendered by the Engine
 */
public class RenderableItem {

    private Mesh mesh;
    private final Vector3f position;
    private float scale;
    private final Vector3f rotation;

    /**
     * Create an empty Item, without mesh
     */
    public RenderableItem() {
        position = new Vector3f();
        scale = 1;
        rotation = new Vector3f();
    }

    /**
     * Create a new Item
     * @param mesh the Item's Mesh
     */
    public RenderableItem(Mesh mesh) {
        this();
        this.mesh = mesh;
    }

    /**
     * Return the Item's current position
     * @return the Item's position
     */
    public Vector3f getPosition() {
        return position;
    }

    /**
     * Set the Item's position
     * @param x x coords
     * @param y y coords
     * @param z z coords
     */
    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    /**
     * Return the Item's scaling factor
     * @return the Item's scaling factor
     */
    public float getScale() {
        return scale;
    }

    /**
     * Set the Item's scaling factor
     * @param scale the new scaling factor
     */
    public void setScale(float scale) {
        this.scale = scale;
    }

    /**
     * Return the Item's current rotation
     * @return the Item's rotation
     */
    public Vector3f getRotation() {
        return rotation;
    }

    /**
     * Set the item's rotation
     * @param x angle around X axis in degrees
     * @param y angle around Y axis in degrees
     * @param z angle around Z axis in degrees
     */
    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }

    /**
     * Return the Item's Mesh
     * @return the Item's Mesh
     */
    public Mesh getMesh() {
        return mesh;
    }

    /**
     * Set the Item's Mesh
     * @param mesh the new Item's Mesh
     */
    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }
}