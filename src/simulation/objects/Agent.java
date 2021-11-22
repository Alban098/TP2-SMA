package simulation.objects;

import engine.objects.RenderableItem;
import engine.rendering.Mesh;
import simulation.Simulation;

import java.util.Random;

public class Agent extends RenderableItem {

    private static final Random rand = new Random();

    private final World world;

    private Object carriedObject;
    private String memory;

    public Agent(World world, Mesh mesh) {
        super(mesh);
        this.world = world;
        this.memory = "";
        for (int i = 0; i < Simulation.MEMORY_SIZE; i++)
            this.memory += "0";
    }

    public void update() {
        Perception perception = percepts();
        action(perception);
        updateMemory(perception);
    }

    public Perception percepts() {
        return world.getPerception(this);
    }

    public void action(Perception p) {
        if (p.getObject() != null) { //If there is an object on the ground
            if (carriedObject == null) { //If the agent isn't carrying anything, try picking it up
                if (!pickUp(p))
                    move();
            } else {
                move();
            }
        } else { //Otherwise
            if (carriedObject != null) { //If the agent is carrying an object
                if (!putDown(p))
                    move();
            } else {
                move();
            }
        }
    }

    private boolean putDown(Perception p) {
        float freq = getFreq(carriedObject);
        float prob = freq / (Simulation.K_MINUS + freq);
        prob *= prob;
        if (rand.nextFloat() < prob) { //If putting it down
            if (world.putDown(this, carriedObject)) {
                carriedObject = null;
                world.move(this, Direction.NONE, 0); //Yet another anim hack
                return true;
            }
        }
        return false;
    }

    private boolean pickUp(Perception p) {
        float freq = getFreq(p.getObject());
        float prob = Simulation.K_PLUS / (Simulation.K_PLUS + freq);
        prob *= prob;
        if (rand.nextFloat() < prob) { //If picking it up
            carriedObject = world.pickUp(this);
            world.move(this, Direction.NONE, 0); // Just an anim hack
            return true;
        }
        return false;
    }

    private void move() {
        Direction dir;
        int dist;
        do {
            dir = Direction.random(rand);
            dist = rand.nextInt(Simulation.MAX_MOVE_DIST) + 1;
        } while(!world.canMove(this, dir, dist));

        world.move(this, dir, dist);
    }

    private float getFreq(Object object) {
        float count = 0;
        if (object != null)
            for (char c : memory.toCharArray())
                if (c != '0' && Object.Type.valueOf(String.valueOf(c)) == object.getType())
                    count++;
        return count / Simulation.MEMORY_SIZE;
    }

    private void updateMemory(Perception p) {
        //Error checking
        Object.Type recognizedType = p.getObject() != null ? p.getObject().getType() : null;
        if (recognizedType != null && new Random().nextFloat() < Simulation.ERROR_RATE)
            recognizedType = Object.Type.change(recognizedType);

        StringBuilder newMem = new StringBuilder(recognizedType != null ? p.getObject().getType().name() : "0");
        for (int i = 0; i < Simulation.MEMORY_SIZE - 1; i++)
            newMem.append(memory.charAt(i));
        memory = newMem.toString();
    }

    public void anim() {
        if (carriedObject != null)
            carriedObject.update(this);
    }
}
