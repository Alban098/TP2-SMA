package simulation.settings;

import engine.rendering.Engine;
import imgui.ImGui;
import imgui.flag.ImGuiColorEditFlags;
import imgui.type.ImBoolean;
import org.joml.Vector4f;
import simulation.Constants;
import simulation.Simulation;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SettingsContainer {

    private final String file;
    private final Map<SettingIdentifiers, Setting<?>> settings;

    public SettingsContainer(Simulation sim, String file) {
        this.settings = new HashMap<>();
        this.file = file;

        settings.put(SettingIdentifiers.MAX_MOVE_DIST, new Setting<>(SettingIdentifiers.MAX_MOVE_DIST, 1, (Setting<Integer> setting) -> {
            int[] tmp = {setting.getValue()};
            if (ImGui.sliderInt(setting.getIdentifier().getDescription(), tmp, 1, 15)) {
                setting.setValue(tmp[0]);
                Constants.MAX_MOVE_DIST = setting.getValue();
            }
        }));
        settings.put(SettingIdentifiers.FPS_TARGET, new Setting<>(SettingIdentifiers.FPS_TARGET, 60, (Setting<Integer> setting) -> {
            int[] tmp = {setting.getValue()};
            if (ImGui.sliderInt(setting.getIdentifier().getDescription(), tmp, 20, 240)) {
                setting.setValue(tmp[0]);
                Engine.TARGET_FPS = setting.getValue();
            }
        }));
        settings.put(SettingIdentifiers.K_PLUS, new Setting<>(SettingIdentifiers.K_PLUS, 0.1f, (Setting<Float> setting) -> {
            float[] tmp = {setting.getValue()};
            if (ImGui.sliderFloat(setting.getIdentifier().getDescription(), tmp, 0.001f, 0.999f)) {
                setting.setValue(tmp[0]);
                Constants.K_PLUS = setting.getValue();
            }
        }));
        settings.put(SettingIdentifiers.K_MINUS, new Setting<>(SettingIdentifiers.K_MINUS, 0.3f, (Setting<Float> setting) -> {
            float[] tmp = {setting.getValue()};
            if (ImGui.sliderFloat(setting.getIdentifier().getDescription(), tmp, 0.001f, 0.999f)) {
                setting.setValue(tmp[0]);
                Constants.K_MINUS = setting.getValue();
            }
        }));
        settings.put(SettingIdentifiers.A_COLOR, new Setting<>( SettingIdentifiers.A_COLOR, new Vector4f(1, 0, 0, 1), (Setting<Vector4f> setting) -> {
            float[] tmp = {setting.getValue().x, setting.getValue().y, setting.getValue().z};
            if (ImGui.colorEdit3(setting.getIdentifier().getDescription(), tmp, ImGuiColorEditFlags.PickerHueBar)) {
                setting.setValue(new Vector4f(tmp[0], tmp[1] , tmp[2], 1));
                Constants.A_COLOR = setting.getValue();
            }
        }));
        settings.put(SettingIdentifiers.B_COLOR, new Setting<>( SettingIdentifiers.B_COLOR, new Vector4f(0, 0, 1, 1), (Setting<Vector4f> setting) -> {
            float[] tmp = {setting.getValue().x, setting.getValue().y, setting.getValue().z};
            if (ImGui.colorEdit3(setting.getIdentifier().getDescription(), tmp, ImGuiColorEditFlags.PickerHueBar)) {
                setting.setValue(new Vector4f(tmp[0], tmp[1] , tmp[2], 1));
                Constants.B_COLOR = setting.getValue();

            }
        }));
        settings.put(SettingIdentifiers.C_COLOR, new Setting<>( SettingIdentifiers.C_COLOR, new Vector4f(1, 0, 1, 1), (Setting<Vector4f> setting) -> {
            float[] tmp = {setting.getValue().x, setting.getValue().y, setting.getValue().z};
            if (ImGui.colorEdit3(setting.getIdentifier().getDescription(), tmp, ImGuiColorEditFlags.PickerHueBar)) {
                setting.setValue(new Vector4f(tmp[0], tmp[1] , tmp[2], 1));
                Constants.C_COLOR = setting.getValue();
            }
        }));
        settings.put(SettingIdentifiers.A_COUNT, new Setting<>(SettingIdentifiers.A_COUNT, 200, (Setting<Integer> setting) -> {
            int[] tmp = {setting.getValue()};
            if (ImGui.sliderInt(setting.getIdentifier().getDescription(), tmp, 0, 5000)) {
                setting.setValue(tmp[0]);
                Constants.A_COUNT = setting.getValue();
            }
        }));
        settings.put(SettingIdentifiers.B_COUNT, new Setting<>(SettingIdentifiers.B_COUNT, 200, (Setting<Integer> setting) -> {
            int[] tmp = {setting.getValue()};
            if (ImGui.sliderInt(setting.getIdentifier().getDescription(), tmp, 0, 5000)) {
                setting.setValue(tmp[0]);
                Constants.B_COUNT = setting.getValue();
            }
        }));
        settings.put(SettingIdentifiers.C_COUNT, new Setting<>(SettingIdentifiers.C_COUNT, 0, (Setting<Integer> setting) -> {
            int[] tmp = {setting.getValue()};
            if (ImGui.sliderInt(setting.getIdentifier().getDescription(), tmp, 0, 5000)) {
                setting.setValue(tmp[0]);
                Constants.C_COUNT = setting.getValue();
            }
        }));
        settings.put(SettingIdentifiers.AGENT_COUNT, new Setting<>(SettingIdentifiers.AGENT_COUNT, 20, (Setting<Integer> setting) -> {
            int[] tmp = {setting.getValue()};
            if (ImGui.sliderInt(setting.getIdentifier().getDescription(), tmp, 1, 500)) {
                setting.setValue(tmp[0]);
                Constants.AGENT_COUNT = setting.getValue();
            }
        }));
        settings.put(SettingIdentifiers.MEMORY_SIZE, new Setting<>(SettingIdentifiers.MEMORY_SIZE, 10, (Setting<Integer> setting) -> {
            int[] tmp = {setting.getValue()};
            if (ImGui.sliderInt(setting.getIdentifier().getDescription(), tmp, 1, 25)) {
                setting.setValue(tmp[0]);
                Constants.MEMORY_SIZE = setting.getValue();
            }
        }));
        settings.put(SettingIdentifiers.MARKER_RADIUS, new Setting<>(SettingIdentifiers.MARKER_RADIUS, 2, (Setting<Integer> setting) -> {
            int[] tmp = {setting.getValue()};
            if (ImGui.sliderInt(setting.getIdentifier().getDescription(), tmp, 1, 20)) {
                setting.setValue(tmp[0]);
                Constants.MARKER_RADIUS = setting.getValue();
            }
        }));
        settings.put(SettingIdentifiers.WORLD_SIZE, new Setting<>(SettingIdentifiers.WORLD_SIZE, 50, (Setting<Integer> setting) -> {
            int[] tmp = {setting.getValue()};
            if (ImGui.sliderInt(setting.getIdentifier().getDescription(), tmp, 10, 200)) {
                setting.setValue(tmp[0]);
                Constants.WORLD_SIZE = setting.getValue();
            }
        }));
        settings.put(SettingIdentifiers.ERROR_RATE, new Setting<>(SettingIdentifiers.ERROR_RATE, 0f, (Setting<Float> setting) -> {
            float[] tmp = {setting.getValue()};
            if (ImGui.sliderFloat(setting.getIdentifier().getDescription(), tmp, 0f, 1f)) {
                setting.setValue(tmp[0]);
                Constants.ERROR_RATE = setting.getValue();
            }
        }));
        settings.put(SettingIdentifiers.GIVE_UP_COOLDOWN, new Setting<>(SettingIdentifiers.GIVE_UP_COOLDOWN, 3f, (Setting<Float> setting) -> {
            float[] tmp = {setting.getValue()};
            if (ImGui.sliderFloat(setting.getIdentifier().getDescription(), tmp, 1f, 10f)) {
                setting.setValue(tmp[0]);
                Constants.GIVE_UP_COOLDOWN = setting.getValue();
            }
        }));
        settings.put(SettingIdentifiers.MARKER_COOLDOWN, new Setting<>(SettingIdentifiers.MARKER_COOLDOWN, 2f, (Setting<Float> setting) -> {
            float[] tmp = {setting.getValue()};
            if (ImGui.sliderFloat(setting.getIdentifier().getDescription(), tmp, 1f, 5f)) {
                setting.setValue(tmp[0]);
                Constants.MARKER_COOLDOWN = setting.getValue();
            }
        }));
        settings.put(SettingIdentifiers.MARKER_ATTENUATION, new Setting<>(SettingIdentifiers.MARKER_ATTENUATION, 0.99f, (Setting<Float> setting) -> {
            float[] tmp = {setting.getValue()};
            if (ImGui.sliderFloat(setting.getIdentifier().getDescription(), tmp, 0.5f, 0.9999f)) {
                setting.setValue(tmp[0]);
                Constants.MARKER_ATTENUATION = setting.getValue();
            }
        }));
        settings.put(SettingIdentifiers.SPEED, new Setting<>(SettingIdentifiers.SPEED, 1f, (Setting<Float> setting) -> {
            float[] tmp = {setting.getValue()};
            if (ImGui.sliderFloat(setting.getIdentifier().getDescription(), tmp, 0.1f, 10f)) {
                setting.setValue(tmp[0]);
                Engine.TARGET_UPS = setting.getValue();
            }
        }));
        settings.put(SettingIdentifiers.SHOW_MARKERS, new Setting<>(SettingIdentifiers.SHOW_MARKERS, true, (Setting<Boolean> setting) -> {
            ImBoolean tmp = new ImBoolean(setting.getValue());
            if (ImGui.checkbox(setting.getIdentifier().getDescription(), tmp)) {
                setting.setValue(tmp.get());
                Constants.SHOW_MARKERS = setting.getValue();
            }
        }));
        settings.put(SettingIdentifiers.VSYNC, new Setting<>(SettingIdentifiers.VSYNC, true, (Setting<Boolean> setting) -> {
            ImBoolean tmp = new ImBoolean(setting.getValue());
            if (ImGui.checkbox(setting.getIdentifier().getDescription(), tmp)) {
                setting.setValue(tmp.get());
                Constants.VSYNC = setting.getValue();
            }
        }));
        loadFile();
    }

    public void loadFile() {
        try {
            Properties prop = new Properties();
            prop.load(new FileReader(file));
            for (Setting<?> setting : settings.values())
                setting.setSerializedValue(prop.getProperty(setting.getIdentifier().toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        applySettings();
    }

    public void saveFile() {
        try {
            Properties prop = new Properties();
            for (Setting<?> setting : settings.values())
                prop.put(setting.getIdentifier().toString(), setting.serializedValue());
            prop.store(new FileWriter(file), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Setting<?> getSetting(SettingIdentifiers name) {
        return settings.get(name);
    }

    private void applySettings() {
        for (Setting<?> setting : settings.values()) {
            switch (setting.getIdentifier()) {
                case MAX_MOVE_DIST -> Constants.MAX_MOVE_DIST = (int) setting.getValue();
                case K_PLUS -> Constants.K_PLUS = (float) setting.getValue();
                case K_MINUS -> Constants.K_MINUS = (float) setting.getValue();
                case A_COUNT -> Constants.A_COUNT = (int) setting.getValue();
                case B_COUNT -> Constants.B_COUNT = (int) setting.getValue();
                case C_COUNT -> Constants.C_COUNT = (int) setting.getValue();
                case AGENT_COUNT -> Constants.AGENT_COUNT = (int) setting.getValue();
                case MEMORY_SIZE -> Constants.MEMORY_SIZE = (int) setting.getValue();
                case ERROR_RATE -> Constants.ERROR_RATE = (float) setting.getValue();
                case GIVE_UP_COOLDOWN -> Constants.GIVE_UP_COOLDOWN = (float) setting.getValue();
                case MARKER_COOLDOWN -> Constants.MARKER_COOLDOWN = (float) setting.getValue();
                case MARKER_RADIUS -> Constants.MARKER_RADIUS = (int) setting.getValue();
                case MARKER_ATTENUATION -> Constants.MARKER_ATTENUATION = (float) setting.getValue();
                case WORLD_SIZE -> Constants.WORLD_SIZE = (int) setting.getValue();
                case SHOW_MARKERS -> Constants.SHOW_MARKERS = (boolean) setting.getValue();
                case A_COLOR -> Constants.A_COLOR = (Vector4f) setting.getValue();
                case B_COLOR -> Constants.B_COLOR = (Vector4f) setting.getValue();
                case C_COLOR -> Constants.C_COLOR = (Vector4f) setting.getValue();
                case SPEED -> Engine.TARGET_UPS = (float) setting.getValue();
                case VSYNC -> Constants.VSYNC = (boolean) setting.getValue();
                case FPS_TARGET -> Engine.TARGET_FPS = (int) setting.getValue();
            }
        }
    }
}
