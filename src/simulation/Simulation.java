package simulation;

import engine.utils.MouseInput;
import engine.rendering.Window;
import engine.rendering.Material;
import engine.rendering.Mesh;
import engine.utils.OBJLoader;
import engine.rendering.Texture;
import org.joml.Vector2i;
import simulation.objects.Agent;
import simulation.objects.Object;
import simulation.objects.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Simulation extends ConcreteLogic {

    public static final int MAX_MOVE_DIST = 1;
    public static final float K_PLUS = 0.1f;
    public static final float K_MINUS = 0.3f;
    public static final int A_COUNT = 200;
    public static final int B_COUNT = 200;
    public static final int AGENT_COUNT = 20;
    public static final int MEMORY_SIZE = 10;
    public static final float ERROR_RATE = 0.1f;

    private List<Agent> agents;

    @Override
    public void init(Window window) throws Exception {
        super.init(window);

        agents = new ArrayList<>();

        Mesh worldMesh = OBJLoader.loadMesh("/models/cube.obj").setMaterial(new Material(new Texture("textures/grassblock.png"), 1f));
        Mesh aMesh = OBJLoader.loadMesh("/models/sphere.obj").setMaterial(new Material(new Texture("textures/red.png"), 1f));
        Mesh bMesh = OBJLoader.loadMesh("/models/sphere.obj").setMaterial(new Material(new Texture("textures/blue.png"), 1f));
        Mesh agentMesh = OBJLoader.loadMesh("/models/agent.obj").setMaterial(new Material(new Texture("textures/agent.png"), 1f));

        World world = new World(50, 50);
        world.init(worldMesh);
        scene.setWorld(world);

        generateAgents(agentMesh, AGENT_COUNT);
        generateObjects(aMesh, A_COUNT, Object.Type.A);
        generateObjects(bMesh, B_COUNT, Object.Type.B);
        //init
    }

    private void generateAgents(Mesh mesh, int count) {
        Random rand = new Random();
        for (int i = 0; i < count; i++) {
            Agent a = new Agent(scene.getWorld(), mesh);
            int x, z;
            do {
                x = rand.nextInt(scene.getWorld().getSizeX());
                z = rand.nextInt(scene.getWorld().getSizeZ());
            } while (!scene.getWorld().canMove(a, new Vector2i(x, z)));

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
            int x, z;
            do {
                x = rand.nextInt(scene.getWorld().getSizeX());
                z = rand.nextInt(scene.getWorld().getSizeZ());
            } while (scene.getWorld().hasObject(new Vector2i(x, z)));

            o.setPosition(x+.5f, 1, z+.5f);
            scene.getWorld().put(o, new Vector2i(x, z));
            scene.registerItem(o);
        }
    }

    @Override
    public void update(Window window, float elapsedTime, MouseInput mouseInput) {
        super.update(window, elapsedTime, mouseInput);
        for (Agent agent : agents)
            agent.update();
    }

    @Override
    public void updateCamera(Window window, float percent, MouseInput mouseInput) {
        super.updateCamera(window, percent, mouseInput);
        scene.getWorld().updateAnim(percent);
    }
}
