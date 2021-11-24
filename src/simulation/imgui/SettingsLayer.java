package simulation.imgui;


import engine.rendering.Engine;
import imgui.ImGui;
import simulation.Simulation;
import simulation.settings.SettingIdentifiers;
import simulation.settings.SettingsContainer;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;

/**
 * This class represent the Setting window
 * allowing user to change the emulator's behaviour
 */
public class SettingsLayer extends Layer {

    private final SettingsContainer settingsContainer;
    private final Simulation sim;
    private Engine engine;

    /**
     * Create a new instance of the Layer
     * @param settingsContainer the container linked to the layer
     */
    public SettingsLayer(Simulation sim, SettingsContainer settingsContainer) {
        super();
        this.sim = sim;
        this.settingsContainer = settingsContainer;
    }

    /**
     * Render the layer to the screen
     * and propagate user inputs to the emulator
     */
    public void render() {
        try {
            ImGui.begin("Settings");
            ImGui.setWindowSize(530, 500);
            if (ImGui.button("Save Settings")) {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "Settings file (.ini)", "ini", ".ini");
                chooser.setFileFilter(filter);
                chooser.setCurrentDirectory(new File(new File(".").getCanonicalPath()));

                int returnVal = chooser.showSaveDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    String file = chooser.getSelectedFile().getAbsolutePath();
                    settingsContainer.saveFile(file.endsWith(".ini") ? file : file + ".ini");
                }
                engine.resetFrameTimer();

            }
            ImGui.sameLine();
            if (ImGui.button("Load Settings")) {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "Settings file (.ini)", "ini", ".ini");
                chooser.setFileFilter(filter);
                chooser.setCurrentDirectory(new File(new File(".").getCanonicalPath()));
                int returnVal = chooser.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    settingsContainer.loadFile(chooser.getSelectedFile().getAbsolutePath());
                    sim.reset();
                    engine.prepareImGuiTexture();
                }
                engine.resetFrameTimer();
            }
            ImGui.sameLine(350);
            if (ImGui.button("Reset")) {
                sim.reset();
                engine.prepareImGuiTexture();
            }
            ImGui.sameLine(485);
            if (ImGui.button("Exit"))
                setVisible(false);
            ImGui.separator();
            if (ImGui.beginTabBar("tab")) {
                if (ImGui.beginTabItem("Simulation")) {
                    ImGui.setNextItemOpen(true);
                    if (ImGui.treeNode("Counts")) {
                        settingsContainer.getSetting(SettingIdentifiers.A_COUNT).process();
                        settingsContainer.getSetting(SettingIdentifiers.B_COUNT).process();
                        settingsContainer.getSetting(SettingIdentifiers.C_COUNT).process();
                        settingsContainer.getSetting(SettingIdentifiers.AGENT_COUNT).process();
                        ImGui.treePop();
                    }
                    ImGui.separator();
                    ImGui.setNextItemOpen(true);
                    if (ImGui.treeNode("Parameters")) {
                        settingsContainer.getSetting(SettingIdentifiers.MAX_MOVE_DIST).process();
                        settingsContainer.getSetting(SettingIdentifiers.K_PLUS).process();
                        settingsContainer.getSetting(SettingIdentifiers.K_MINUS).process();
                        settingsContainer.getSetting(SettingIdentifiers.MEMORY_SIZE).process();
                        settingsContainer.getSetting(SettingIdentifiers.ERROR_RATE).process();
                        ImGui.treePop();
                    }
                    ImGui.separator();
                    ImGui.setNextItemOpen(true);
                    if (ImGui.treeNode("Parameters (Part 2)")) {
                        settingsContainer.getSetting(SettingIdentifiers.MARKER_RADIUS).process();
                        settingsContainer.getSetting(SettingIdentifiers.MARKER_ATTENUATION).process();
                        settingsContainer.getSetting(SettingIdentifiers.MARKER_COOLDOWN).process();
                        settingsContainer.getSetting(SettingIdentifiers.GIVE_UP_COOLDOWN).process();
                        ImGui.treePop();
                    }
                    ImGui.separator();
                    ImGui.setNextItemOpen(true);
                    if (ImGui.treeNode("World")) {
                        settingsContainer.getSetting(SettingIdentifiers.WORLD_SIZE).process();
                        ImGui.treePop();
                    }
                    ImGui.endTabItem();
                }
                if (ImGui.beginTabItem("Graphics")) {
                    ImGui.setNextItemOpen(true);
                    if (ImGui.treeNode("Misc")) {
                        settingsContainer.getSetting(SettingIdentifiers.A_COLOR).process();
                        settingsContainer.getSetting(SettingIdentifiers.B_COLOR).process();
                        settingsContainer.getSetting(SettingIdentifiers.C_COLOR).process();
                        ImGui.treePop();
                    }
                    ImGui.setNextItemOpen(true);
                    if (ImGui.treeNode("Misc")) {
                        settingsContainer.getSetting(SettingIdentifiers.SHOW_MARKERS).process();
                        settingsContainer.getSetting(SettingIdentifiers.SPEED).process();
                        settingsContainer.getSetting(SettingIdentifiers.FPS_TARGET).process();
                        settingsContainer.getSetting(SettingIdentifiers.VSYNC).process();
                        ImGui.treePop();
                    }
                    ImGui.endTabItem();
                }
            }
            ImGui.endTabBar();

            ImGui.end();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void linkEngine(Engine engine) {
        this.engine = engine;
    }
}