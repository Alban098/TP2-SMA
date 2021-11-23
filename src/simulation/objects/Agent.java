package simulation.objects;

import engine.objects.RenderableItem;
import engine.rendering.Mesh;
import simulation.Constants;
import simulation.Simulation;

import java.util.Map;
import java.util.Random;

public class Agent extends RenderableItem {

    private static final Random rand = new Random();

    private final World world;

    private Object carriedObject;
    private String memory;

    //Part 2
    private float markerCooldown = 0;
    private float giveUpCooldown = 0;
    private Agent slave;
    private Agent master;

    public Agent(World world, Mesh mesh) {
        super(mesh);
        this.world = world;
        this.memory = "";
        for (int i = 0; i < Constants.MEMORY_SIZE; i++)
            this.memory += "0";
    }

    public void update(float elapsedTime) {
        if (!isHelping()) {
            Perception perception = percepts();
            action(perception);
            updateMemory(perception);

            if (!hasHelp()) {
                markerCooldown -= elapsedTime;
                giveUpCooldown -= elapsedTime;
            }
        } else {
            if (rand.nextFloat() < 0.05 && master != null) {
                master.slave = null;
                master = null;
            }
        }
    }

    public boolean hasHelp() {
        return slave != null;
    }

    public boolean isAskingForHelp() {
        return carriedObject!= null && carriedObject.getType() == Object.Type.C && giveUpCooldown > 0 && !hasHelp() && !isHelping();
    }

    public boolean isHelping() {
        return master != null;
    }

    public Perception percepts() {
        return world.getPerception(this);
    }

    public void action(Perception p) {
        if (p.getObject() != null) { //If there is an object on the ground
            if (carriedObject == null) { //If the agent isn't carrying anything, try picking it up
                if (!pickUp(p))
                    move(p);
            } else {
                if (carriedObject.getType() == Object.Type.C)
                    handleTypeC(p);
                else
                    move(p);
            }
        } else { //Otherwise
            if (carriedObject != null) { //If the agent is carrying an object
                if (!putDown(p, false))
                    if (carriedObject.getType() == Object.Type.C)
                        handleTypeC(p);
                    else
                        move(p);
            } else {
                move(p);
            }
        }
    }

    private void handleTypeC(Perception p) {
        if (hasHelp()) {
            Direction dir;
            int dist;
            do {
                dir = Direction.random(rand);
                dist = rand.nextInt(Constants.MAX_MOVE_DIST) + 1;
            } while(!world.canMove(this, dir, dist) || !world.canMove(slave, dir, dist));

            world.move(this, dir, dist);
            world.move(slave, dir, dist);
        } else {
            if (markerCooldown <= 0) {
                world.putMarker(this);
                markerCooldown = Constants.MARKER_COOLDOWN;
            }
            if (giveUpCooldown <= 0) {
                if (putDown(p, true)) {
                    releaseSlave();
                    markerCooldown = 0;
                    giveUpCooldown = 0;
                    move(p);
                }
            }
        }
    }

    private void releaseSlave() {
        if (slave != null) {
            slave.master = null;
            world.move(slave, Direction.NONE, 0);
            slave = null;
        }
    }

    private boolean putDown(Perception p, boolean force) {
        float freq = getFreq(carriedObject);
        float prob = freq / (Constants.K_MINUS + freq);
        prob *= prob;
        if (rand.nextFloat() < prob || force) { //If putting it down
            if (world.putDown(this, carriedObject)) {
                if (carriedObject.getType() == Object.Type.C)
                    releaseSlave();
                carriedObject = null;
                world.move(this, Direction.NONE, 0); //Yet another anim hack
                return true;
            }
        }
        return false;
    }

    private boolean pickUp(Perception p) {
        float freq = getFreq(p.getObject());
        float prob = Constants.K_PLUS / (Constants.K_PLUS + freq);
        prob *= prob;
        if (rand.nextFloat() < prob) { //If picking it up
            carriedObject = world.pickUp(this);
            world.move(this, Direction.NONE, 0); // Just an anim hack
            if (carriedObject.getType() == Object.Type.C) {
                markerCooldown = Constants.MARKER_COOLDOWN;
                world.putMarker(this);
                giveUpCooldown = Constants.GIVE_UP_COOLDOWN;
            }
            return true;
        }
        return false;
    }

    private void move(Perception p) {
        if (lookForAgentAskingHelp(p))
            return;
        if (p.getHelpMarker() > 0 && tryMoveToMarker())
            return;
        Direction dir;
        int dist;
        do {
            dir = Direction.random(rand);
            dist = rand.nextInt(Constants.MAX_MOVE_DIST) + 1;
        } while(!world.canMove(this, dir, dist));

        world.move(this, dir, dist);
    }

    private boolean tryMoveToMarker() {
        Map<Float, Direction> markers = world.getMarkers(this);
        for (Map.Entry<Float, Direction> entry : markers.entrySet()) {
            if (world.canMove(this, entry.getValue(), 1)) {
                world.move(this, entry.getValue(), 1);
                return true;
            }
        }
        return false;
    }

    private boolean lookForAgentAskingHelp(Perception p) {
        Agent asking = world.lookForAgentInNeed(this);
        if (asking != null && master == null && slave == null && carriedObject == null) {
            asking.slave = this;
            master = asking;
            return true;
        }
        return false;
    }

    private float getFreq(Object object) {
        float count = 0;
        if (object != null)
            for (char c : memory.toCharArray())
                if (c != '0' && Object.Type.valueOf(String.valueOf(c)) == object.getType())
                    count++;
        return count / Constants.MEMORY_SIZE;
    }

    private void updateMemory(Perception p) {
        //Error checking
        Object.Type recognizedType = p.getObject() != null ? p.getObject().getType() : null;
        if (recognizedType != null && new Random().nextFloat() < Constants.ERROR_RATE)
            recognizedType = Object.Type.change(recognizedType);

        StringBuilder newMem = new StringBuilder(recognizedType != null ? p.getObject().getType().name() : "0");
        for (int i = 0; i < Constants.MEMORY_SIZE - 1; i++)
            newMem.append(memory.charAt(i));
        memory = newMem.toString();
    }

    public void anim() {
        if (carriedObject != null)
            carriedObject.update(this);
    }
}
