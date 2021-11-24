package simulation.imgui;

import engine.rendering.ILogic;
import imgui.ImGui;
import simulation.Simulation;
import simulation.settings.SettingsContainer;

public class ImGuiLayer {

    private final ILogic simulation;
    private final SettingsLayer settingsLayer;

    public ImGuiLayer(ILogic simulation, SettingsContainer settingsContainer) {
        this.simulation = simulation;
        settingsLayer = new SettingsLayer(simulation, settingsContainer);
    }

    public void render() {
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
            }
            ImGui.endMenu();
        }

        if (ImGui.beginMenu("Settings")) {
            settingsLayer.setVisible(!settingsLayer.isVisible());
            ImGui.endMenu();
        }
        ImGui.endMainMenuBar();

        if (settingsLayer.isVisible())
            settingsLayer.render();
    }

    public boolean hasLayerVisible() {
        return settingsLayer.isVisible();
    }
}
