package simulation.objects;

import engine.objects.RenderableItem;
import engine.rendering.Mesh;

import java.util.Random;

public class Object extends RenderableItem {

    private final Type type;

    public Object(Mesh mesh, Type type) {
        super(mesh);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public void update(Agent carrier) {
        this.setPosition(carrier.getPosition().x, carrier.getPosition().y + .5f, carrier.getPosition().z);
        this.setRotation(carrier.getRotation().x, carrier.getRotation().y + .5f, carrier.getRotation().z);
    }

    public enum Type {
        A,
        B,
        C;

        public static Type change(Type initial) {
            Type newType;

            do {
                newType = values()[new Random().nextInt(values().length)];
            } while (newType.equals(initial));

            return newType;
        }
    }
}
