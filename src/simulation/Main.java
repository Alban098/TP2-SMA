package simulation;

import engine.rendering.Engine;
import simulation.imgui.ImGuiLayer;
import simulation.settings.SettingsContainer;

public class Main {
 
    public static void main(String[] args) {
        try {
            Simulation logic = new Simulation();
            SettingsContainer settingsContainer = new SettingsContainer("config.ini");
            ImGuiLayer imgui = new ImGuiLayer(logic, settingsContainer);
            Engine engine = new Engine("SMA", 600, 480, logic, imgui);
            engine.run();
        } catch (Exception excp) {
            excp.printStackTrace();
            System.exit(-1);
        }
    }
}