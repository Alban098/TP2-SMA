package simulation;

import engine.rendering.Window;
import engine.rendering.Material;
import engine.rendering.Mesh;
import engine.utils.OBJLoader;
import engine.rendering.Texture;
import org.joml.Vector2i;
import org.lwjgl.BufferUtils;
import settings.SettingsInterface;
import simulation.objects.Agent;
import simulation.objects.Object;
import simulation.objects.World;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Simulation extends ConcreteLogic {

    private List<Agent> agents;
    private boolean paused = false;
    private Mesh worldMesh;
    private Mesh agentMesh;
    private Mesh aMesh;
    private Mesh bMesh;
    private Mesh cMesh;

    private ByteBuffer[] worldBuffers;

    private double animationPercent;

    /**
     * Initialize meshes, models and generate the scene of the simulation
     * @param window the Window when the Simulation will be rendered
     * @throws Exception thrown when models or textures can't be loaded
     */
    @Override
    public void init(Window window) throws Exception {
        super.init(window);
        animationPercent = 0;
        agents = new ArrayList<>();
        worldMesh = OBJLoader.loadMesh("/models/quad.obj").setMaterial(new Material(new Texture("textures/Ground_04.png"), new Texture("textures/Ground_04_Nrm.png"), 1f));
        aMesh = OBJLoader.loadMesh("/models/sphere.obj").setMaterial(new Material(SettingsInterface.A_COLOR, 1f));
        bMesh = OBJLoader.loadMesh("/models/sphere.obj").setMaterial(new Material(SettingsInterface.B_COLOR, 1f));
        cMesh = OBJLoader.loadMesh("/models/sphere.obj").setMaterial(new Material(SettingsInterface.C_COLOR, 1f));
        agentMesh = OBJLoader.loadMesh("/models/agent.obj").setMaterial(new Material(new Texture("textures/agent.png"), null, 1f));
        generateScene();
    }

    /**
     * Generate the scene (Agents, objects and World)
     */
    private void generateScene() {
        World world = new World(SettingsInterface.WORLD_SIZE, worldMesh);
        scene.setWorld(world);

        generateAgents(agentMesh, SettingsInterface.AGENT_COUNT);
        generateObjects(aMesh, SettingsInterface.A_COUNT, Object.Type.A);
        generateObjects(bMesh, SettingsInterface.B_COUNT, Object.Type.B);
        generateObjects(cMesh, SettingsInterface.C_COUNT, Object.Type.C);

        worldBuffers = new ByteBuffer[]{
                BufferUtils.createByteBuffer(SettingsInterface.WORLD_SIZE * SettingsInterface.WORLD_SIZE * 4),
                BufferUtils.createByteBuffer(SettingsInterface.WORLD_SIZE * SettingsInterface.WORLD_SIZE * 4),
                BufferUtils.createByteBuffer(SettingsInterface.WORLD_SIZE * SettingsInterface.WORLD_SIZE * 4)
        };
    }

    /**
     * Generate a set number of agents, less will be generated if not enough space
     * @param mesh the mesh used by the agents for rendering
     * @param count the target number of Agent to generate
     */
    private void generateAgents(Mesh mesh, int count) {
        Random rand = new Random();
        for (int i = 0; i < count; i++) {
            Agent a = new Agent(scene.getWorld(), mesh);
            int x, z, attempts = 0;
            //Try to place the agent, give up after 10 unsuccessful attempts
            do {
                attempts++;
                x = rand.nextInt(scene.getWorld().getSize());
                z = rand.nextInt(scene.getWorld().getSize());
            } while (attempts <= 10 && !scene.getWorld().canMove(a, new Vector2i(x, z)));
            if (attempts >= 10)
                continue;

            //Set the rendering position
            a.setPosition(x+.5f, 1, z+.5f);

            //Add it to the world and register it to the renderer
            agents.add(a);
            scene.getWorld().put(a, new Vector2i(x, z));
            scene.registerItem(a);
        }
    }

    /**
     * Generate a set number of objects, less will be generated if not enough space
     * @param mesh the mesh used by the Objects for rendering
     * @param count the target number of Object to generate
     * @param type the type of Object to generate
     */
    private void generateObjects(Mesh mesh, int count, Object.Type type) {
        Random rand = new Random();
        for (int i = 0; i < count; i++) {
            Object o = new Object(mesh, type);
            int x, z, attempts = 0;
            //Try to place the object, give up after 10 unsuccessful attempts
            do {
                attempts++;
                x = rand.nextInt(scene.getWorld().getSize());
                z = rand.nextInt(scene.getWorld().getSize());
            } while (attempts <= 10 && scene.getWorld().hasObject(new Vector2i(x, z)));
            if (attempts >= 10)
                continue;

            //Set the rendering position
            o.setPosition(x+.5f, 1, z+.5f);

            //Add it to the world and register it to the renderer
            scene.getWorld().put(o, new Vector2i(x, z));
            scene.registerItem(o);
        }
    }

    /**
     * Update the simulation, called once every (1/TARGET_UPS sec, see settings package)
     * @param window the Window where the simulation is rendered
     * @param elapsedTime time elapsed since last update in seconds
     */
    @Override
    public void update(Window window, double elapsedTime) {
        //If the simulation is running, update all objects
        if (!paused) {
            //Update the world (for help marker attenuation) and stop previous animations
            scene.getWorld().update();

            //Update all the Agents
            for (Agent agent : agents)
                agent.update(elapsedTime);
        }
    }

    /**
     * Called once every Frame
     * Compute sub-states positions and rotations
     * Update the material colors
     * Render the simulation to the screen
     * @param window the Window to render to
     */
    @Override
    public void render(Window window) {
        //If the simulation is running, compute animation
        if (!paused)
            scene.getWorld().animate(SettingsInterface.ANIMATION ? animationPercent : 1);
        scene.getWorld().renderMarkers();
        //Update the material colors
        aMesh.getMaterial().setAmbientColour(SettingsInterface.A_COLOR);
        bMesh.getMaterial().setAmbientColour(SettingsInterface.B_COLOR);
        cMesh.getMaterial().setAmbientColour(SettingsInterface.C_COLOR);
        super.render(window);
    }

    /**
     * Pause the simulation
     */
    @Override
    public void pause() {
        paused = true;
    }

    /**
     * Resume the simulation
     */
    @Override
    public void resume() {
        paused = false;
    }

    /**
     * Reset the simulation
     * Regenerate the scene (Objects, Agent and World)
     */
    @Override
    public void reset() {
        scene.reset();
        agents.clear();
        generateScene();
    }

    /**
     * Set the current animation percentage
     * @param percent the current animation percentage
     */
    @Override
    public void setPercent(double percent) {
        this.animationPercent = percent;
    }

    /**
     * Return the world rendered in some ByteBuffers
     * the buffers are already flipped and ready to be fed to Texture Samplers
     * @return an array of flipped ByteBuffer containing the rendered World [Objects, Agents, Help Markers]
     */
    public ByteBuffer[] getWorldBuffers() {
        scene.getWorld().fillBuffers(worldBuffers[0], worldBuffers[1], worldBuffers[2]);
        return worldBuffers;
    }
}
