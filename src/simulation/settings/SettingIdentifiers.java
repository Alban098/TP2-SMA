package simulation.settings;

public enum SettingIdentifiers {
    MAX_MOVE_DIST("Max displacement"),
    K_PLUS("K+"),
    K_MINUS("K-"),
    A_COUNT("Number of A"),
    B_COUNT("Number of B"),
    C_COUNT ("Number of C"),
    AGENT_COUNT("Number of Agents"),
    MEMORY_SIZE("Agent's memory"),
    ERROR_RATE("Error rate"),
    GIVE_UP_COOLDOWN("Give up delay"),
    MARKER_COOLDOWN("Marker update delay"),
    MARKER_RADIUS("Marker radius"),
    MARKER_ATTENUATION("Marker attenuation"),
    WORLD_SIZE("World size"),
    SHOW_MARKERS("Show Help Markers"),
    A_COLOR("A Objects color"),
    B_COLOR("B Objects color"),
    C_COLOR("C Objects color"),
    SPEED("Simulation speed (Update/FPS)"),
    VSYNC("Use V-Sync"),
    FPS_TARGET("Target FPS (Used for Update)");

    private final String description;

    SettingIdentifiers(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}