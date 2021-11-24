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

import java.util.List;
import java.util.Map;

public abstract class ConcreteLogic implements ILogic {

    private static final float MOUSE_SENSITIVITY = 0.2f;
    private static final float CAMERA_POS_STEP = 0.05f;

    private final Vector3f cameraInc;
    private final Renderer renderer;
    private final Camera camera;

    protected Scene scene;

    public ConcreteLogic() {
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
        camera.movePosition(0, 10, 0);
        camera.moveRotation(30, 90+45, -30);
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);

        scene = new Scene();

        SceneLight sceneLight = new SceneLight();
        scene.setSceneLight(sceneLight);
        sceneLight.setAmbientLight(new Vector3f(.8f, .8f, .8f));
        sceneLight.setPointLightList(new PointLight[]{
                new PointLight(new Vector3f(.5f, .5f, 0.25f), new Vector3f(1000, 1000, 0), .5f),
        });

        SkyBox skyBox = new SkyBox("/models/skybox.obj", "textures/skybox.png");
        skyBox.setScale(200f);
        scene.setSkyBox(skyBox);
    }

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

    @Override
    public void update(Window window, float interval, MouseInput mouseInput) {

    }

    @Override
    public void updateCamera(Window window, float percent, MouseInput mouseInput) {
        // Update camera position
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);

        // Update camera based on mouse
        if (mouseInput.isLeftButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        }
    }

    @Override
    public void render(Window window) {
        renderer.render(window, camera, scene);
    }

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
