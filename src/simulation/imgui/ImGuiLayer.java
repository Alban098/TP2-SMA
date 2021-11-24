package simulation.imgui;

import engine.rendering.Engine;
import imgui.ImGui;
import simulation.Simulation;
import simulation.settings.SettingsContainer;

public class ImGuiLayer {

    private final Simulation simulation;
    private final SettingsLayer settingsLayer;
    private final MapLayer mapLayer;
    private Engine engine;

    public ImGuiLayer(Simulation simulation, SettingsContainer settingsContainer) {
        this.simulation = simulation;
        settingsLayer = new SettingsLayer(simulation, settingsContainer);
        mapLayer = new MapLayer(simulation);
    }

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

    public void linkEngine(Engine engine) {
        this.engine = engine;
        settingsLayer.linkEngine(engine);
    }
}
