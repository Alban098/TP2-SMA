package engine.rendering;

import engine.objects.Scene;
import engine.utils.Transformation;
import engine.utils.Utils;
import engine.objects.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import settings.SettingsInterface;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

/**
 * This class represent a Renderer used to render everything in the Engine
 */
public class Renderer {

    private static final int MAX_POINT_LIGHTS = 5;
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.f;

    private final Transformation transformation;

    private ShaderProgram shaderProgram;
    private ShaderProgram skyBoxShaderProgram;
    private ShaderProgram worldShaderProgram;

    private final float specularPower;

    /**
     * Create a new Renderer
     */
    public Renderer() {
        transformation = new Transformation();
        specularPower = 10f;
    }

    /**
     * Initialize the Renderer, create Shaders and Uniforms
     * @throws Exception thrown when unable to link shaders or to load resources
     */
    public void init() throws Exception {
        // Create shader
        shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(Utils.loadResource("/shaders/entities/vertex.glsl"));
        shaderProgram.createFragmentShader(Utils.loadResource("/shaders/entities/fragment.glsl"));
        shaderProgram.link();

        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("modelViewMatrix");
        shaderProgram.createUniform("texture_sampler");
        shaderProgram.createUniform("normal_sampler");
        shaderProgram.createMaterialUniform("material");
        shaderProgram.createUniform("specularPower");
        shaderProgram.createUniform("ambientLight");
        shaderProgram.createPointLightListUniform("pointLights", MAX_POINT_LIGHTS);

        worldShaderProgram = new ShaderProgram();
        worldShaderProgram.createVertexShader(Utils.loadResource("/shaders/world/vertex.glsl"));
        worldShaderProgram.createFragmentShader(Utils.loadResource("/shaders/world/fragment.glsl"));
        worldShaderProgram.link();

        worldShaderProgram.createUniform("projectionMatrix");
        worldShaderProgram.createUniform("modelViewMatrix");
        worldShaderProgram.createUniform("texture_sampler");
        worldShaderProgram.createUniform("normal_sampler");
        worldShaderProgram.createUniform("help_marker_sampler");
        worldShaderProgram.createMaterialUniform("material");
        worldShaderProgram.createUniform("specularPower");
        worldShaderProgram.createUniform("scale");
        worldShaderProgram.createUniform("ambientLight");
        worldShaderProgram.createPointLightListUniform("pointLights", MAX_POINT_LIGHTS);

        skyBoxShaderProgram = new ShaderProgram();
        skyBoxShaderProgram.createVertexShader(Utils.loadResource("/shaders/skybox/vertex.glsl"));
        skyBoxShaderProgram.createFragmentShader(Utils.loadResource("/shaders/skybox/fragment.glsl"));
        skyBoxShaderProgram.link();

        skyBoxShaderProgram.createUniform("projectionMatrix");
        skyBoxShaderProgram.createUniform("modelViewMatrix");
        skyBoxShaderProgram.createUniform("texture_sampler");
        skyBoxShaderProgram.createUniform("ambientLight");
    }

    /**
     * Clear the color of the screen
     */
    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    /**
     * Render the scene to the screen
     * @param window the Window to render to
     * @param camera the Camera to render from
     * @param scene the Scene to render
     */
    public void render(Window window, Camera camera, Scene scene) {
        clear();

        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }
        transformation.updateProjectionMatrix(SettingsInterface.FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        transformation.updateViewMatrix(camera);

        renderScene(scene);
        renderWorld(scene);
        renderSkyBox(window, camera, scene);
    }

    /**
     * Render the world to the screen
     * @param scene the Scene to render to World of
     */
    private void renderWorld(Scene scene) {
        worldShaderProgram.bind();

        Matrix4f projectionMatrix = transformation.getProjectionMatrix();

        Matrix4f viewMatrix = transformation.getViewMatrix();
        Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(scene.getWorld(), viewMatrix);

        worldShaderProgram.setUniform("projectionMatrix", projectionMatrix);
        worldShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);

        SceneLight sceneLight = scene.getSceneLight();
        renderLights(viewMatrix, sceneLight, worldShaderProgram);

        worldShaderProgram.setUniform("texture_sampler", 0);
        worldShaderProgram.setUniform("normal_sampler", 1);
        worldShaderProgram.setUniform("help_marker_sampler", 2);
        worldShaderProgram.setUniform("scale", scene.getWorld().getScale());
        // Render each mesh with the associated game Items
        worldShaderProgram.setUniform("material", scene.getWorld().getMesh().getMaterial());
        scene.getWorld().getMesh().render();

        worldShaderProgram.unbind();
    }

    /**
     * Render the Lights by loading them to the GPU
     * @param viewMatrix the view matrix of the Scene
     * @param sceneLight the Light to render
     * @param shader the Shader used to render
     */
    private void renderLights(Matrix4f viewMatrix, SceneLight sceneLight, ShaderProgram shader) {
        shader.setUniform("ambientLight", sceneLight.getAmbientLight());
        shader.setUniform("specularPower", specularPower);

        // Process Point Lights
        PointLight[] pointLightList = sceneLight.getPointLightList();
        int numLights = pointLightList != null ? pointLightList.length : 0;
        for (int i = 0; i < numLights; i++) {
            // Get a copy of the point light object and transform its position to view coordinates
            PointLight currPointLight = new PointLight(pointLightList[i]);
            Vector3f lightPos = currPointLight.getPosition();
            Vector4f aux = new Vector4f(lightPos, 1);
            aux.mul(viewMatrix);
            lightPos.x = aux.x;
            lightPos.y = aux.y;
            lightPos.z = aux.z;
            shader.setUniform("pointLights", currPointLight, i);
        }
    }

    /**
     * Render the Items of the Scene to the screen
     * @param scene the Scene to render the Items of
     */
    public void renderScene(Scene scene) {
        shaderProgram.bind();

        Matrix4f projectionMatrix = transformation.getProjectionMatrix();
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);

        Matrix4f viewMatrix = transformation.getViewMatrix();

        SceneLight sceneLight = scene.getSceneLight();
        renderLights(viewMatrix, sceneLight, shaderProgram);

        shaderProgram.setUniform("texture_sampler", 0);
        shaderProgram.setUniform("normal_sampler", 1);
        // Render each mesh with the associated game Items
        Map<Mesh, List<RenderableItem>> mapMeshes = scene.getMeshes();
        for (Mesh mesh : mapMeshes.keySet()) {
            shaderProgram.setUniform("material", mesh.getMaterial());
            mesh.renderList(mapMeshes.get(mesh), (RenderableItem gameItem) -> {
                        Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(gameItem, viewMatrix);
                        shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
                    }
            );
        }
        shaderProgram.unbind();
    }

    /**
     * Render the Skybox to the screen
     * @param window the Window to render to
     * @param camera the Camera to render from
     * @param scene the Scene to render the Skybox of
     */
    private void renderSkyBox(Window window, Camera camera, Scene scene) {
        skyBoxShaderProgram.bind();

        skyBoxShaderProgram.setUniform("texture_sampler", 0);

        // Update projection Matrix
        Matrix4f projectionMatrix = transformation.updateProjectionMatrix(SettingsInterface.FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        skyBoxShaderProgram.setUniform("projectionMatrix", projectionMatrix);
        SkyBox skyBox = scene.getSkyBox();
        Matrix4f viewMatrix = transformation.updateViewMatrix(camera);
        viewMatrix.m30(0);
        viewMatrix.m31(0);
        viewMatrix.m32(0);
        Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(skyBox, viewMatrix);
        skyBoxShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
        skyBoxShaderProgram.setUniform("ambientLight", scene.getSceneLight().getAmbientLight());

        scene.getSkyBox().getMesh().render();

        skyBoxShaderProgram.unbind();
    }

    /**
     * Cleanup the renderer by cleaning up the shaders
     */
    public void cleanup() {
        if (shaderProgram != null)
            shaderProgram.cleanup();

        if (skyBoxShaderProgram != null)
            skyBoxShaderProgram.cleanup();
    }
}
