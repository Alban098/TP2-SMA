package engine.utils;

import engine.objects.Camera;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import engine.objects.RenderableItem;

/**
 * This class represent a Transformation used to render Item to the right spot on the Scene
 */
public class Transformation {

    private final Matrix4f projectionMatrix;
    private final Matrix4f modelMatrix;
    private final Matrix4f modelViewMatrix;
    private final Matrix4f viewMatrix;

    /**
     * Create a new empty Transformation
     */
    public Transformation() {
        projectionMatrix = new Matrix4f();
        modelMatrix = new Matrix4f();
        modelViewMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
    }

    /**
     * Return the projection matrix, used to see from the camera's perspective (not accounting for position and rotation)
     * @return the current projection matrix
     */
    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    /**
     * Compute the current projection matrix
     * @param fov the Field of View in radians
     * @param width width of the rendering target
     * @param height height of the rendering target
     * @param zNear near clip plane
     * @param zFar far clip plane
     * @return the calculated projection matrix
     */
    public Matrix4f updateProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        projectionMatrix.identity();
        return projectionMatrix.setPerspective(fov, width / height, zNear, zFar);
    }

    /**
     * the view matrix, used to see from the camera's position and rotation
     * @return the current view matric
     */
    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    /**
     * Compute the current view matrix
     * @param camera the camera to render from
     * @return the calculated view matrix
     */
    public Matrix4f updateViewMatrix(Camera camera) {
        Vector3f cameraPos = camera.getPosition();
        Vector3f rotation = camera.getRotation();

        viewMatrix.identity();
        // First do the rotation so camera rotates over its position
        viewMatrix.rotate((float)Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
                .rotate((float)Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
        // Then do the translation
        viewMatrix.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        return viewMatrix;
    }

    /**
     * Calculate the model view matrix, used to place the object on the correct place in the Camera's FOV and render it with the correct shape and size
     * @param gameItem the item to render
     * @param viewMatrix the view matric of the camera
     * @return the complete transformation matrix for this item
     */
    public Matrix4f buildModelViewMatrix(RenderableItem gameItem, Matrix4f viewMatrix) {
        Vector3f rotation = gameItem.getRotation();
        modelMatrix.identity().translate(gameItem.getPosition()).
                rotateX((float)Math.toRadians(-rotation.x)).
                rotateY((float)Math.toRadians(-rotation.y)).
                rotateZ((float)Math.toRadians(-rotation.z)).
                scale(gameItem.getScale());
        modelViewMatrix.set(viewMatrix);
        return modelViewMatrix.mul(modelMatrix);
    }
}