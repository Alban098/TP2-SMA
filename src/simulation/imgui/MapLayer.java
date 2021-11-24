package simulation.imgui;

import engine.rendering.Texture;
import imgui.ImGui;
import simulation.Constants;
import simulation.Simulation;

/**
 * This class represent the Setting window
 * allowing user to change the emulator's behaviour
 */
public class MapLayer extends Layer {

    private final Simulation sim;
    private Texture mapTexture;
    /**
     * Create a new instance of the Layer
     */
    public MapLayer(Simulation sim) {
        super();
        this.sim = sim;
    }

    public void initTexture() {
        mapTexture = new Texture(Constants.WORLD_SIZE, Constants.WORLD_SIZE);
    }

    /**
     * Render the layer to the screen
     * and propagate user inputs to the emulator
     */
    public void render() {
        ImGui.begin("Map");
        ImGui.setWindowSize(517, 562);
        ImGui.newLine();
        ImGui.sameLine(470);
        if (ImGui.button("Exit"))
            setVisible(false);
        ImGui.separator();
        mapTexture.load(sim.getMapBuffer());
        ImGui.image(mapTexture.getId(), 500, 500);
        ImGui.end();
    }
}