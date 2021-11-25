package simulation.objects;

import org.joml.Vector2i;
import java.util.Random;

/**
 * This enum contains all possible Directions that an Agent can move to
 */
public enum Direction {
    NORTH(0, 1),
    SOUTH(0, -1),
    EAST(1, 0),
    WEST(-1, 0),
    NORTH_EAST(1, 1),
    NORTH_WEST(-1, 1),
    SOUTH_EAST(1, -1),
    SOUTH_WEST(-1, -1),
    NONE(0, 0);

    private final int xDir;
    private final int zDir;

    Direction(int xDir, int zDir) {
        this.xDir = xDir;
        this.zDir = zDir;
    }

    /**
     * Return a vector representing the displacement in X and Z of the Direction
     * @param amount the distance to move
     * @return a scaled Vector representing the displacement
     */
    public Vector2i getDisplacement(int amount) {
        return new Vector2i(xDir * amount, zDir * amount);
    }

    /**
     * Return a random direction
     * @param rand a seeded Random used to generate the direction
     * @return the random Direction
     */
    public static Direction random(Random rand) {
        return values()[rand.nextInt(values().length)];
    }

    /**
     * Get the Direction representing a displacement
     * @param x X direction
     * @param z Z direction
     * @return the Direction depicted by the displacement
     */
    public static Direction get(int x, int z) {
        x = Integer.compare(x, 0);
        z = Integer.compare(z, 0);
        for (Direction direction : values())
            if (direction.xDir == x && direction.zDir == z)
                return direction;
        return NONE;
    }
}
