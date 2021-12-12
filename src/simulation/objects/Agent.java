package simulation.objects;

import engine.objects.RenderableItem;
import engine.rendering.Mesh;
import settings.SettingsInterface;

import java.util.Map;
import java.util.Random;

/**
 * Thisb class represent an Agent that can interact with the World
 */
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

    /**
     * Create a new Agent
     * @param world the World where the Agent lives in
     * @param mesh the Mesh used by the Agent
     */
    public Agent(World world, Mesh mesh) {
        super(mesh);
        this.world = world;
        this.memory = "";
        for (int i = 0; i < SettingsInterface.MEMORY_SIZE; i++)
            this.memory += "0";
    }

    /**
     * Update the Agent
     * @param elapsedTime time elapsed since last update
     */
    public void update(double elapsedTime) {
        //If the Agent isn't a slave to another one
        if (isNotBusy()) {
            //Percepts, Act and Update the memory
            Perception perception = percepts();
            action(perception);
            updateMemory(perception);

            //If the Agent carries an Object of type C and doesn't has help, update timers
            if (!hasHelp()) {
                markerCooldown -= elapsedTime;
                giveUpCooldown -= elapsedTime;
            }
        }
    }

    /**
     * Return whether the Agent is helped by another or not
     * @return is the Agent currently helped by another
     */
    public boolean hasHelp() {
        return slave != null;
    }

    /**
     * Return whether the Agent is asking for help or not
     * @return is the Agent asking for help
     */
    public boolean isAskingForHelp() {
        return carriedObject!= null && carriedObject.getType() == Object.Type.C && giveUpCooldown > 0 && !hasHelp() && isNotBusy();
    }

    /**
     * Return whether the Agent is helping another one or not
     * @return is the Agent not helping any other one
     */
    public boolean isNotBusy() {
        return master == null;
    }

    /**
     * Create a Perception of the local surrounding from the World
     * @return the local Perception of the Agent
     */
    public Perception percepts() {
        return world.getPerception(this);
    }

    /**
     * The common behaviour of the Agent
     * @param p the local Perception of the Agent
     */
    public void action(Perception p) {
        //If there is an object on the ground
        if (p.object() != null) {
            //If the agent isn't carrying anything, try picking it up
            if (carriedObject == null) {
                //If the agent can't pick up the Object or doesn't want to, just move
                if (!pickUp(p))
                    move(p);
            } else {
                //Else if the agent carries an Object of type C
                if (carriedObject.getType() == Object.Type.C)
                    handleTypeC(p);
                //Otherwise, juste move
                else
                    move(p);
            }
        } else {
            //If the agent is carrying an object
            if (carriedObject != null) {
                //If the agent can't or doesn't want to put down the item
                if (!putDown(false))
                    //If the Agent carries an Object of type C
                    if (carriedObject.getType() == Object.Type.C)
                        handleTypeC(p);
                    //Otherwise, just move
                    else
                        move(p);
            //If the agent can't pick up the Object or doesn't want to, just move
            } else {
                move(p);
            }
        }
    }

    /**
     * Handle the case when the Agent carries an Object of type C
     * @param p the local Perception of the Agent
     */
    private void handleTypeC(Perception p) {
        //Is the Agent helped by another one
        if (hasHelp()) {
            //If so, move the Agent and it's slave
            Direction dir;
            int dist;
            int attempts = 0;
            do {
                //Try to find a Direction where the 2 agent can move
                dir = Direction.random(rand);
                dist = rand.nextInt(SettingsInterface.MAX_MOVE_DIST) + 1;
                //Give up after 3 unsuccessfully attempts
                if (++attempts > 3) {
                    return;
                }
            } while(!world.canMove(this, dir, dist) || !world.canMove(slave, dir, dist));

            //Move the Agent, and it's slave to the selected direction
            world.move(this, dir, dist);
            world.move(slave, dir, dist);
        //Otherwise, try asking for help or just give up
        } else {
            //If it's time to ask for help
            if (markerCooldown <= 0) {
                //Put marker on the world and update timer
                world.putMarker(this);
                markerCooldown = SettingsInterface.MARKER_COOLDOWN;
            }
            //If it's time to give up
            if (giveUpCooldown <= 0) {
                //Put down the item and move
                if (putDown(true)) {
                    releaseSlave();
                    world.removeMarker(this);
                    markerCooldown = 0;
                    giveUpCooldown = 0;
                    move(p);
                }
            }
        }
    }

    /**
     * Release an enslaved Agent
     */
    private void releaseSlave() {
        if (slave != null) {
            slave.master = null;
            slave = null;
        }
    }

    /**
     * Make the Agent put down its Object according to the calculated probability
     * @param force force probability to 1
     * @return has the Agent put down its Object
     */
    private boolean putDown(boolean force) {
        float freq = getFrequency(carriedObject);
        float prob = freq / (SettingsInterface.K_MINUS + freq);
        prob *= prob;
        if (rand.nextFloat() < prob || force) { //If putting it down
            if (world.putDown(this, carriedObject)) {
                //If the Agent puts down an Object of type C, release the slave
                if (carriedObject.getType() == Object.Type.C)
                    releaseSlave();
                carriedObject = null;
                return true;
            }
        }
        return false;
    }

    /**
     * Make the Agent pick an Object on the ground according to the calculated probability
     * @param p the local Perception of the Agent
     * @return has the Agent picked up the Object
     */
    private boolean pickUp(Perception p) {
        float freq = getFrequency(p.object());
        float prob = SettingsInterface.K_PLUS / (SettingsInterface.K_PLUS + freq);
        prob *= prob;
        if (rand.nextFloat() < prob) { //If picking it up
            carriedObject = world.pickUp(this);
            if (carriedObject.getType() == Object.Type.C) {
                markerCooldown = SettingsInterface.MARKER_COOLDOWN;
                world.putMarker(this);
                giveUpCooldown = SettingsInterface.GIVE_UP_COOLDOWN;
            }
            return true;
        }
        return false;
    }

    /**
     * Move an agent to a random Direction for a random distance
     * @param p the local Perception of the Agent
     */
    private void move(Perception p) {
        //Look for an Agent asking for help in the direct neighbourhood of the Agent (radius 1), if so help him and return
        if (lookForAgentAskingHelp())
            return;
        //If there is marker on the ground, move to the adjacent Chunk with the most marker, then return
        if (p.helpMarker() > 0 && tryMoveToMarker() && carriedObject == null)
            return;

        //Otherwise, juste generate a Direction and distance and move according to that
        Direction dir;
        int dist;
        int attempts = 0;
        do {
            dir = Direction.random(rand);
            dist = rand.nextInt(SettingsInterface.MAX_MOVE_DIST) + 1;
            //Stop if no suitable Direction and distance found after 3 attempts
            if (++attempts > 5) {
                return;
            }
        } while(!world.canMove(this, dir, dist));

        world.move(this, dir, dist);
    }

    /**
     * Move the Agent to the adjacent Chunk with the most marker
     * @return has the Agent succeeded in its movement
     */
    private boolean tryMoveToMarker() {
        //Get the ordered list of Direction, ordered by marker value, most first
        Map<Float, Direction> markers = world.getMarkers(this);

        //For each Direction, try to move, return if success, go to next if not
        for (Map.Entry<Float, Direction> entry : markers.entrySet()) {
            if (world.canMove(this, entry.getValue(), 1)) {
                world.move(this, entry.getValue(), 1);
                return true;
            }
        }
        return false;
    }

    /**
     * Return whether an Agent is asking for help in the immediate surrounding of the Agent
     * if found, the Agent will help it
     * @return has the Agent enslaved himself
     */
    private boolean lookForAgentAskingHelp() {
        Agent asking = world.lookForAgentInNeed(this);
        if (asking != null && master == null && slave == null && carriedObject == null) {
            asking.slave = this;
            master = asking;
            world.removeMarker(master);
            return true;
        }
        return false;
    }

    /**
     * Get the frequency of an Object in the Agent's memory
     * @param object the Object to calculate the frequency of
     * @return the calculated frequency
     */
    private float getFrequency(Object object) {
        float count = 0;
        if (object != null)
            for (char c : memory.toCharArray())
                if (c != '0' && Object.Type.valueOf(String.valueOf(c)) == object.getType())
                    count++;
        return count / SettingsInterface.MEMORY_SIZE;
    }

    /**
     * Update the memory of the Agent
     * @param p the local perception of the Agent
     */
    private void updateMemory(Perception p) {
        //Error checking
        Object.Type recognizedType = p.object() != null ? p.object().getType() : null;
        if (recognizedType != null && new Random().nextFloat() < SettingsInterface.ERROR_RATE)
            recognizedType = Object.Type.change(recognizedType);

        StringBuilder newMem = new StringBuilder(recognizedType != null ? p.object().getType().name() : "0");
        for (int i = 0; i < SettingsInterface.MEMORY_SIZE - 1; i++)
            newMem.append(memory.length() > i ? memory.charAt(i) : '0');
        memory = newMem.toString();
    }

    /**
     * Make the carried item follow the Agent (just for rendering purposes)
     */
    public void anim() {
        if (carriedObject != null)
            carriedObject.follow(this);
    }

    /**
     * Return the Object currently carried by the Agent
     * @return the carried Object, null is absent
     */
    public Object getCarriedObject() {
        return carriedObject;
    }
}
