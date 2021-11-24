package simulation;

import org.joml.Vector4f;
import simulation.settings.SettingIdentifiers;

public class Constants {
    public static boolean VSYNC = false;
    public static int MAX_MOVE_DIST = 1;
    public static float K_PLUS = 0.1f;
    public static float K_MINUS = 0.3f;
    public static int A_COUNT = 1000;
    public static int B_COUNT = 1000;
    public static int C_COUNT = 250;
    public static int AGENT_COUNT = 300;
    public static int MEMORY_SIZE = 20;
    public static float ERROR_RATE = 0f;
    public static float GIVE_UP_COOLDOWN = 15;
    public static float MARKER_COOLDOWN = 5;
    public static int MARKER_RADIUS = 30;
    public static float MARKER_ATTENUATION = 0.99f;
    public static int WORLD_SIZE = 100;
    public static boolean SHOW_MARKERS = true;
    public static Vector4f A_COLOR = new Vector4f(1f, 0f, 0f, 1f);
    public static Vector4f B_COLOR = new Vector4f(0f, 0f, 1f, 1f);
    public static Vector4f C_COLOR = new Vector4f(1f, 0f, 1f, 1f);

    public static int TARGET_FPS = 60;
    public static int TARGET_UPS = 60;
}
