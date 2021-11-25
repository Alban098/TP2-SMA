import engine.rendering.Engine;
import imgui.ImGuiLayer;
import settings.SettingsContainer;
import simulation.Simulation;

public class Main {
 
    public static void main(String[] args) {
        try {
            Simulation logic = new Simulation();
            SettingsContainer settingsContainer = new SettingsContainer("config.ini");
            ImGuiLayer imgui = new ImGuiLayer(logic, settingsContainer);
            Engine engine = new Engine("SMA", 600, 480, logic, imgui);
            engine.run();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}