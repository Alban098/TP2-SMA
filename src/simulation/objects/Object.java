package simulation.objects;

import engine.objects.RenderableItem;
import engine.rendering.Mesh;

import java.util.Random;

/**
 * This class represent an Object that lives in the World
 */
public class Object extends RenderableItem {

    private final Type type;

    /**
     * Create a new Object
     * @param mesh the mesh used by the Object
     * @param type the type of the Object
     */
    public Object(Mesh mesh, Type type) {
        super(mesh);
        this.type = type;
    }

    /**
     * Get the Type of the Object
     * @return the Object's Type
     */
    public Type getType() {
        return type;
    }

    /**
     * Set the rendering coords of the Object to follow the carrying Agent
     * @param carrier the Agent carrying the Object
     */
    public void follow(Agent carrier) {
        this.setPosition(carrier.getPosition().x, carrier.getPosition().y + .5f, carrier.getPosition().z);
        this.setRotation(carrier.getRotation().x, carrier.getRotation().y + .5f, carrier.getRotation().z);
    }

    /**
     * This enum contains all possible Object Type
     */
    public enum Type {
        A,
        B,
        C;

        /**
         * Get a random Type that is different from the passed one
         * @param initial the original Type
         * @return a Type different from the passed one
         */
        public static Type change(Type initial) {
            Type newType;

            do {
                newType = values()[new Random().nextInt(values().length)];
            } while (newType.equals(initial));

            return newType;
        }
    }
}
