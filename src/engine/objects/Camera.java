package engine.objects;

import org.joml.Vector3f;

/**
 * This class represent a Camera used in the Engine
 */
public class Camera {

    private final Vector3f position;
    private final Vector3f rotation;

    /**
     * Create a new Camera
     */
    public Camera() {
        position = new Vector3f();
        rotation = new Vector3f();
    }

    /**
     * Return the current Camera position
     * @return the current Camera position
     */
    public Vector3f getPosition() {
        return position;
    }

    /**
     * Move the camera a set amount
     * @param offsetX amount in the X direction
     * @param offsetY amount in the Y direction
     * @param offsetZ amount in the Z direction
     */
    public void movePosition(float offsetX, float offsetY, float offsetZ) {
        if (offsetZ != 0) {
            position.x += (float)Math.sin(Math.toRadians(rotation.y)) * -1.0f * offsetZ;
            position.z += (float)Math.cos(Math.toRadians(rotation.y)) * offsetZ;
        }
        if (offsetX != 0) {
            position.x += (float)Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * offsetX;
            position.z += (float)Math.cos(Math.toRadians(rotation.y - 90)) * offsetX;
        }
        position.y += offsetY;
    }

    /**
     * Return the current Camera rotation
     * @return the current Camera rotation
     */
    public Vector3f getRotation() {
        return rotation;
    }

    /**
     * Rotate the camera a set amount
     * @param offsetX amount around the X axis
     * @param offsetY amount around the Y axis
     * @param offsetZ amount around the Z axis
     */
    public void moveRotation(float offsetX, float offsetY, float offsetZ) {
        rotation.x += offsetX;
        rotation.y += offsetY;
        rotation.z += offsetZ;
    }
}