package simulation;

import org.joml.Vector4f;

public class Constants {
    public static final int MAX_MOVE_DIST = 1;
    public static final float K_PLUS = 0.1f;
    public static final float K_MINUS = 0.3f;
    public static final int A_COUNT = 200;
    public static final int B_COUNT = 200;
    public static final int C_COUNT = 50;
    public static final int AGENT_COUNT = 20;
    public static final int MEMORY_SIZE = 10;
    public static final float ERROR_RATE = 0f;
    public static final int GIVE_UP_COOLDOWN = 15;
    public static final int MARKER_COOLDOWN = 5;
    public static final int MARKER_RADIUS = 3;
    public static final float MARKER_ATTENUATION = 0.99f;
    public static final int WORLD_SIZE = 50;
    public static final Vector4f RED = new Vector4f(1f, 0f, 0f, 1f);
    public static final Vector4f PURPLE = new Vector4f(1f, 0f, 1f, 1f);
    public static final Vector4f BLUE = new Vector4f(0f, 0f, 1f, 1f);
}
