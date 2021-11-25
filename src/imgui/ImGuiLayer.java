package imgui;

import engine.rendering.Engine;
import simulation.Simulation;
import settings.SettingsContainer;

/**
 * This class represent the main ImGui Layer, in charge of rendering every subsequent Layers
 */
public class ImGuiLayer {

    private final Simulation simulation;
    private final SettingsLayer settingsLayer;
    private final MapLayer mapLayer;
    private Engine engine;

    /**
     * Create a new ImGui Layer
     * @param simulation the Simulation to link to
     * @param settingsContainer the SettingsContainer to link to
     */
    public ImGuiLayer(Simulation simulation, SettingsContainer settingsContainer) {
        this.simulation = simulation;
        settingsLayer = new SettingsLayer(simulation, settingsContainer);
        mapLayer = new MapLayer(simulation);
    }

    /**
     * Initialize the subsequents Layers
     */
    public void init() {
        mapLayer.initTexture();
    }

    public void render(float elapsedTime, int nbUpdates) {
        ImGui.beginMainMenuBar();
        if (ImGui.beginMenu("Options")) {
            if (ImGui.menuItem("Pause")) {
                simulation.pause();
            }
            if (ImGui.menuItem("Run")) {
                simulation.resume();
            }
            ImGui.separator();
            if (ImGui.menuItem("Reset")) {
                simulation.reset();
                engine.prepareImGuiTexture();
            }
            ImGui.endMenu();
        }
        if (ImGui.beginMenu("Settings")) {
            settingsLayer.setVisible(!settingsLayer.isVisible());
            ImGui.endMenu();
        }
        if (ImGui.beginMenu("2D Map")) {
            mapLayer.setVisible(!mapLayer.isVisible());
            ImGui.endMenu();
        }
        ImGui.textColored(255, 255, 0, 255, nbUpdates + " updates");
        ImGui.sameLine();
        ImGui.textColored(255, 255, 0, 255, "     " + ((int)(100f/elapsedTime)/100f) + " fps   ");
        ImGui.endMainMenuBar();

        if (settingsLayer.isVisible())
            settingsLayer.render();
        if (mapLayer.isVisible())
            mapLayer.render();
    }

    /**
     * Link an Engine to the Layers
     * @param engine the Engine to link
     */
    public void linkEngine(Engine engine) {
        this.engine = engine;
        settingsLayer.linkEngine(engine);
    }
}
