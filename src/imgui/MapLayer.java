package imgui;

import engine.rendering.Texture;
import settings.SettingsInterface;
import simulation.Simulation;

import java.nio.ByteBuffer;

/**
 * This class represent the 2D Maps window
 */
public class MapLayer extends Layer {

    private final Simulation sim;
    private Texture objectTexture;
    private Texture agentTexture;
    private Texture markerTexture;

    /**
     * Create a new instance of the Layer
     * @param sim the Simulation the Layer is linked to
     */
    public MapLayer(Simulation sim) {
        super();
        this.sim = sim;
    }

    /**
     * Initialize the texture used by the Layer
     */
    public void initTexture() {
        objectTexture = new Texture(SettingsInterface.WORLD_SIZE, SettingsInterface.WORLD_SIZE);
        agentTexture = new Texture(SettingsInterface.WORLD_SIZE, SettingsInterface.WORLD_SIZE);
        markerTexture = new Texture(SettingsInterface.WORLD_SIZE, SettingsInterface.WORLD_SIZE);
    }

    /**
     * Render the layer to the screen
     */
    public void render() {
        ImGui.begin("2D Maps");
        ImGui.setWindowSize(517, 582);
        ImGui.newLine();
        ImGui.sameLine(470);
        ByteBuffer[] buffers = sim.getWorldBuffers();
        if (ImGui.button("Exit"))
            setVisible(false);
        if (ImGui.beginTabBar("tab")) {
            if (ImGui.beginTabItem("Objects")) {
                objectTexture.load(buffers[0]);
                ImGui.image(objectTexture.getId(), 500, 500);
                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("Agents")) {
                agentTexture.load(buffers[1]);
                ImGui.image(agentTexture.getId(), 500, 500);
                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("Markers")) {
                markerTexture.load(buffers[2]);
                ImGui.image(markerTexture.getId(), 500, 500);
                ImGui.endTabItem();
            }
        }
        ImGui.endTabBar();
        ImGui.end();
    }
}