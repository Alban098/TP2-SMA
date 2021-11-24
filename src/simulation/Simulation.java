package simulation;

import engine.utils.MouseInput;
import engine.rendering.Window;
import engine.rendering.Material;
import engine.rendering.Mesh;
import engine.utils.OBJLoader;
import engine.rendering.Texture;
import org.joml.Vector2i;
import org.lwjgl.BufferUtils;
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

    private ByteBuffer mapBuffer;

    @Override
    public void init(Window window) throws Exception {
        super.init(window);
        agents = new ArrayList<>();
        worldMesh = OBJLoader.loadMesh("/models/cube.obj").setMaterial(new Material(new Texture("textures/grassblock.png"), 1f));
        aMesh = OBJLoader.loadMesh("/models/sphere.obj").setMaterial(new Material(Constants.A_COLOR, 1f));
        bMesh = OBJLoader.loadMesh("/models/sphere.obj").setMaterial(new Material(Constants.B_COLOR, 1f));
        cMesh = OBJLoader.loadMesh("/models/sphere.obj").setMaterial(new Material(Constants.C_COLOR, 1f));
        agentMesh = OBJLoader.loadMesh("/models/agent.obj").setMaterial(new Material(new Texture("textures/agent.png"), 1f));
        generateScene();
    }

    private void generateScene() {
        World world = new World(Constants.WORLD_SIZE, Constants.WORLD_SIZE);
        world.init(worldMesh);
        scene.setWorld(world);

        generateAgents(agentMesh, Constants.AGENT_COUNT);
        generateObjects(aMesh, Constants.A_COUNT, Object.Type.A);
        generateObjects(bMesh, Constants.B_COUNT, Object.Type.B);
        generateObjects(cMesh, Constants.C_COUNT, Object.Type.C);

        mapBuffer = BufferUtils.createByteBuffer(Constants.WORLD_SIZE * Constants.WORLD_SIZE * 4);
    }


    private void generateAgents(Mesh mesh, int count) {
        Random rand = new Random();
        for (int i = 0; i < count; i++) {
            Agent a = new Agent(scene.getWorld(), mesh);
            int x, z, attempts = 0;
            do {
                attempts++;

                x = rand.nextInt(scene.getWorld().getSizeX());
                z = rand.nextInt(scene.getWorld().getSizeZ());
            } while (attempts <= 10 && !scene.getWorld().canMove(a, new Vector2i(x, z)));
            if (attempts >= 10)
                continue;
            a.setPosition(x+.5f, 1, z+.5f);
            agents.add(a);
            scene.getWorld().put(a, new Vector2i(x, z));
            scene.registerItem(a);
        }
    }

    private void generateObjects(Mesh mesh, int count, Object.Type type) {
        Random rand = new Random();
        for (int i = 0; i < count; i++) {
            Object o = new Object(mesh, type);
            int x, z, attempts = 0;
            do {
                attempts++;
                x = rand.nextInt(scene.getWorld().getSizeX());
                z = rand.nextInt(scene.getWorld().getSizeZ());
            } while (attempts <= 10 && scene.getWorld().hasObject(new Vector2i(x, z)));
            if (attempts >= 10)
                continue;
            o.setPosition(x+.5f, 1, z+.5f);
            scene.getWorld().put(o, new Vector2i(x, z));
            scene.registerItem(o);
        }
    }

    @Override
    public void update(Window window, float elapsedTime, MouseInput mouseInput) {
        super.update(window, elapsedTime, mouseInput);
        if (!paused) {
            scene.getWorld().update(elapsedTime);
            for (Agent agent : agents)
                agent.update(elapsedTime);
        }
    }

    @Override
    public void updateCamera(Window window, float percent, MouseInput mouseInput) {
        super.updateCamera(window, percent, mouseInput);
        if (!paused)
            scene.getWorld().updateAnim(percent);
        aMesh.getMaterial().setAmbientColour(Constants.A_COLOR);
        bMesh.getMaterial().setAmbientColour(Constants.B_COLOR);
        cMesh.getMaterial().setAmbientColour(Constants.C_COLOR);
    }

    @Override
    public void pause() {
        paused = true;
    }

    @Override
    public void resume() {
        paused = false;
    }

    @Override
    public void reset() {
        scene.reset();
        agents.clear();
        generateScene();

    }

    public ByteBuffer getMapBuffer() {
        mapBuffer.clear();
        scene.getWorld().fillBuffer(mapBuffer);
        return mapBuffer;
    }
}
