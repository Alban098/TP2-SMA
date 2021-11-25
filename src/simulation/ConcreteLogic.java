package simulation;

import engine.objects.*;
import engine.rendering.ILogic;
import engine.rendering.Mesh;
import engine.rendering.Renderer;
import engine.rendering.Window;
import engine.utils.MouseInput;
import org.joml.Vector2f;
import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.GL11;
import settings.SettingsInterface;

import java.util.List;
import java.util.Map;

/**
 * This class implements base methods of a Logic that can be run by the engine
 */
public abstract class ConcreteLogic implements ILogic {

    private final Vector3f cameraInc;
    private final Renderer renderer;
    private final Camera camera;

    protected Scene scene;

    /**
     * Create a new Logic
     * Initialize Camera and Renderer
     */
    public ConcreteLogic() {
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
        camera.movePosition(0, 10, 0);
        camera.moveRotation(30, 135, -30);
    }

    /**
     * Initialize the Logic by creating the scene, the lights and the skybox
     * @param window the Window to render to
     * @throws Exception thrown if the skybox model or texture couldn't be loaded
     */
    @Override
    public void init(Window window) throws Exception {
        renderer.init();

        scene = new Scene();

        SceneLight sceneLight = new SceneLight();
        scene.setSceneLight(sceneLight);
        sceneLight.setAmbientLight(new Vector3f(.5f, .5f, .5f));
        sceneLight.setPointLightList(new PointLight[]{
                new PointLight(new Vector3f(1, 1, 0.8f), new Vector3f(-1000, 10000, -1000), 0.7f),
        });

        SkyBox skyBox = new SkyBox("/models/skybox.obj", "textures/skybox.png");
        skyBox.setScale(300f);
        scene.setSkyBox(skyBox);
    }

    /**
     * Update the Camera movement variables
     * @param window the Window where the scene is renderer to
     * @param mouseInput the MouseInput containing cursor information
     */
    @Override
    public void input(Window window, MouseInput mouseInput) {
        cameraInc.set(0, 0, 0);
        if (window.isKeyPressed(GLFW_KEY_W)) {
            cameraInc.z = -10;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            cameraInc.z = 10;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            cameraInc.x = -10;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            cameraInc.x = 10;
        }
        if (window.isKeyPressed(GLFW_KEY_SPACE)) {
            cameraInc.y = 10;
        } else if (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            cameraInc.y = -10;
        }
        if (window.isKeyPressed(GLFW_KEY_ENTER)) {
            glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
            glDisable(GL_TEXTURE_2D);
        } else {
            glPolygonMode(GL11.GL_FRONT_AND_BACK, GL_FILL);
            glEnable(GL_TEXTURE_2D);
        }
    }

    /**
     * Update the Camera's position and rotation
     * @param window the windows where the scene is rendered to
     * @param mouseInput the MouseInput to use for camera rotation
     */
    @Override
    public void updateCamera(Window window, MouseInput mouseInput) {
        // Update camera position
        camera.movePosition(cameraInc.x * SettingsInterface.CAMERA_POS_STEP, cameraInc.y * SettingsInterface.CAMERA_POS_STEP, cameraInc.z * SettingsInterface.CAMERA_POS_STEP);

        // Update camera based on mouse
        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * SettingsInterface.MOUSE_SENSITIVITY, rotVec.y * SettingsInterface.MOUSE_SENSITIVITY, 0);
        }
    }

    /**
     * Render the scene to the screen, called once every frame
     * @param window the Window ro render to
     */
    @Override
    public void render(Window window) {
        renderer.render(window, camera, scene);
    }

    /**
     * Clear the memory used by the scene, and it's meshes
     */
    @Override
    public void cleanup() {
        renderer.cleanup();
        Map<Mesh, List<RenderableItem>> mapMeshes = scene.getMeshes();
        for (Mesh mesh : mapMeshes.keySet()) {
            mesh.cleanUp();
        }
        scene.getWorld().cleanup();
    }
}
