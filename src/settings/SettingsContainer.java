package settings;

import imgui.ImGui;
import imgui.flag.ImGuiColorEditFlags;
import imgui.type.ImBoolean;
import org.joml.Vector4f;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * This class represent a Container that hold all settings
 */
public class SettingsContainer {

    private final Map<SettingIdentifiers, Setting<?>> settings;

    /**
     * Create a new SettingsContainer from a file
     * @param file the config file path
     */
    public SettingsContainer(String file) {
        this.settings = new HashMap<>();

        settings.put(SettingIdentifiers.MAX_MOVE_DIST, new Setting<>(SettingIdentifiers.MAX_MOVE_DIST, 1, (Setting<Integer> setting) -> {
            int[] tmp = {setting.getValue()};
            if (ImGui.sliderInt(setting.getIdentifier().getDescription(), tmp, 1, 10)) {
                setting.setValue(tmp[0]);
                SettingsInterface.MAX_MOVE_DIST = setting.getValue();
            }
        }));
        settings.put(SettingIdentifiers.FPS_TARGET, new Setting<>(SettingIdentifiers.FPS_TARGET, 60, (Setting<Integer> setting) -> {
            int[] tmp = {setting.getValue()};
            if (ImGui.sliderInt(setting.getIdentifier().getDescription(), tmp, 20, 240)) {
                setting.setValue(tmp[0]);
                SettingsInterface.TARGET_FPS = setting.getValue();
            }
        }));
        settings.put(SettingIdentifiers.K_PLUS, new Setting<>(SettingIdentifiers.K_PLUS, 0.1f, (Setting<Float> setting) -> {
            float[] tmp = {setting.getValue()};
            if (ImGui.sliderFloat(setting.getIdentifier().getDescription(), tmp, 0.001f, 0.999f)) {
                setting.setValue(tmp[0]);
                SettingsInterface.K_PLUS = setting.getValue();
            }
        }));
        settings.put(SettingIdentifiers.K_MINUS, new Setting<>(SettingIdentifiers.K_MINUS, 0.3f, (Setting<Float> setting) -> {
            float[] tmp = {setting.getValue()};
            if (ImGui.sliderFloat(setting.getIdentifier().getDescription(), tmp, 0.001f, 0.999f)) {
                setting.setValue(tmp[0]);
                SettingsInterface.K_MINUS = setting.getValue();
            }
        }));
        settings.put(SettingIdentifiers.A_COLOR, new Setting<>( SettingIdentifiers.A_COLOR, new Vector4f(1, 0, 0, 1), (Setting<Vector4f> setting) -> {
            float[] tmp = {setting.getValue().x, setting.getValue().y, setting.getValue().z};
            if (ImGui.colorEdit3(setting.getIdentifier().getDescription(), tmp, ImGuiColorEditFlags.PickerHueBar)) {
                setting.setValue(new Vector4f(tmp[0], tmp[1] , tmp[2], 1));
                SettingsInterface.A_COLOR = setting.getValue();
            }
        }));
        settings.put(SettingIdentifiers.B_COLOR, new Setting<>( SettingIdentifiers.B_COLOR, new Vector4f(0, 0, 1, 1), (Setting<Vector4f> setting) -> {
            float[] tmp = {setting.getValue().x, setting.getValue().y, setting.getValue().z};
            if (ImGui.colorEdit3(setting.getIdentifier().getDescription(), tmp, ImGuiColorEditFlags.PickerHueBar)) {
                setting.setValue(new Vector4f(tmp[0], tmp[1] , tmp[2], 1));
                SettingsInterface.B_COLOR = setting.getValue();

            }
        }));
        settings.put(SettingIdentifiers.C_COLOR, new Setting<>( SettingIdentifiers.C_COLOR, new Vector4f(1, 0, 1, 1), (Setting<Vector4f> setting) -> {
            float[] tmp = {setting.getValue().x, setting.getValue().y, setting.getValue().z};
            if (ImGui.colorEdit3(setting.getIdentifier().getDescription(), tmp, ImGuiColorEditFlags.PickerHueBar)) {
                setting.setValue(new Vector4f(tmp[0], tmp[1] , tmp[2], 1));
                SettingsInterface.C_COLOR = setting.getValue();
            }
        }));
        settings.put(SettingIdentifiers.A_COUNT, new Setting<>(SettingIdentifiers.A_COUNT, 200, (Setting<Integer> setting) -> {
            int[] tmp = {setting.getValue()};
            if (ImGui.sliderInt(setting.getIdentifier().getDescription(), tmp, 0, 7500)) {
                setting.setValue(tmp[0]);
                SettingsInterface.A_COUNT = setting.getValue();
            }
        }));
        settings.put(SettingIdentifiers.B_COUNT, new Setting<>(SettingIdentifiers.B_COUNT, 200, (Setting<Integer> setting) -> {
            int[] tmp = {setting.getValue()};
            if (ImGui.sliderInt(setting.getIdentifier().getDescription(), tmp, 0, 7500)) {
                setting.setValue(tmp[0]);
                SettingsInterface.B_COUNT = setting.getValue();
            }
        }));
        settings.put(SettingIdentifiers.C_COUNT, new Setting<>(SettingIdentifiers.C_COUNT, 0, (Setting<Integer> setting) -> {
            int[] tmp = {setting.getValue()};
            if (ImGui.sliderInt(setting.getIdentifier().getDescription(), tmp, 0, 7500)) {
                setting.setValue(tmp[0]);
                SettingsInterface.C_COUNT = setting.getValue();
            }
        }));
        settings.put(SettingIdentifiers.AGENT_COUNT, new Setting<>(SettingIdentifiers.AGENT_COUNT, 20, (Setting<Integer> setting) -> {
            int[] tmp = {setting.getValue()};
            if (ImGui.sliderInt(setting.getIdentifier().getDescription(), tmp, 1, 750)) {
                setting.setValue(tmp[0]);
                SettingsInterface.AGENT_COUNT = setting.getValue();
            }
        }));
        settings.put(SettingIdentifiers.MEMORY_SIZE, new Setting<>(SettingIdentifiers.MEMORY_SIZE, 10, (Setting<Integer> setting) -> {
            int[] tmp = {setting.getValue()};
            if (ImGui.sliderInt(setting.getIdentifier().getDescription(), tmp, 1, 25)) {
                setting.setValue(tmp[0]);
                SettingsInterface.MEMORY_SIZE = setting.getValue();
            }
        }));
        settings.put(SettingIdentifiers.MARKER_RADIUS, new Setting<>(SettingIdentifiers.MARKER_RADIUS, 2, (Setting<Integer> setting) -> {
            int[] tmp = {setting.getValue()};
            if (ImGui.sliderInt(setting.getIdentifier().getDescription(), tmp, 1, 20)) {
                setting.setValue(tmp[0]);
                SettingsInterface.MARKER_RADIUS = setting.getValue();
            }
        }));
        settings.put(SettingIdentifiers.WORLD_SIZE, new Setting<>(SettingIdentifiers.WORLD_SIZE, 50, (Setting<Integer> setting) -> {
            int[] tmp = {setting.getValue()};
            if (ImGui.sliderInt(setting.getIdentifier().getDescription(), tmp, 10, 250)) {
                setting.setValue(tmp[0]);
                SettingsInterface.WORLD_SIZE = setting.getValue();
            }
        }));
        settings.put(SettingIdentifiers.ERROR_RATE, new Setting<>(SettingIdentifiers.ERROR_RATE, 0f, (Setting<Float> setting) -> {
            float[] tmp = {setting.getValue()};
            if (ImGui.sliderFloat(setting.getIdentifier().getDescription(), tmp, 0f, 1f)) {
                setting.setValue(tmp[0]);
                SettingsInterface.ERROR_RATE = setting.getValue();
            }
        }));
        settings.put(SettingIdentifiers.GIVE_UP_COOLDOWN, new Setting<>(SettingIdentifiers.GIVE_UP_COOLDOWN, 3f, (Setting<Float> setting) -> {
            float[] tmp = {setting.getValue()};
            if (ImGui.sliderFloat(setting.getIdentifier().getDescription(), tmp, 1f, 10f)) {
                setting.setValue(tmp[0]);
                SettingsInterface.GIVE_UP_COOLDOWN = setting.getValue();
            }
        }));
        settings.put(SettingIdentifiers.MARKER_COOLDOWN, new Setting<>(SettingIdentifiers.MARKER_COOLDOWN, 2f, (Setting<Float> setting) -> {
            float[] tmp = {setting.getValue()};
            if (ImGui.sliderFloat(setting.getIdentifier().getDescription(), tmp, 1f, 5f)) {
                setting.setValue(tmp[0]);
                SettingsInterface.MARKER_COOLDOWN = setting.getValue();
            }
        }));
        settings.put(SettingIdentifiers.MARKER_ATTENUATION, new Setting<>(SettingIdentifiers.MARKER_ATTENUATION, 0.99f, (Setting<Float> setting) -> {
            float[] tmp = {setting.getValue()};
            if (ImGui.sliderFloat(setting.getIdentifier().getDescription(), tmp, 0.5f, 0.9999f)) {
                setting.setValue(tmp[0]);
                SettingsInterface.MARKER_ATTENUATION = setting.getValue();
            }
        }));
        settings.put(SettingIdentifiers.SPEED, new Setting<>(SettingIdentifiers.SPEED, 60, (Setting<Integer> setting) -> {
            int[] tmp = {setting.getValue()};
            if (ImGui.sliderInt(setting.getIdentifier().getDescription(), tmp, 1, 3000)) {
                setting.setValue(tmp[0]);
                SettingsInterface.TARGET_UPS = setting.getValue();
            }
        }));
        settings.put(SettingIdentifiers.FOV, new Setting<>(SettingIdentifiers.FOV, 60, (Setting<Integer> setting) -> {
            int[] tmp = {setting.getValue()};
            if (ImGui.sliderInt(setting.getIdentifier().getDescription(), tmp, 30, 150)) {
                setting.setValue(tmp[0]);
                SettingsInterface.FOV = (float) Math.toRadians(setting.getValue());
            }
        }));
        settings.put(SettingIdentifiers.SHOW_MARKERS, new Setting<>(SettingIdentifiers.SHOW_MARKERS, true, (Setting<Boolean> setting) -> {
            ImBoolean tmp = new ImBoolean(setting.getValue());
            if (ImGui.checkbox(setting.getIdentifier().getDescription(), tmp)) {
                setting.setValue(tmp.get());
                SettingsInterface.SHOW_MARKERS = setting.getValue();
            }
        }));
        settings.put(SettingIdentifiers.VSYNC, new Setting<>(SettingIdentifiers.VSYNC, true, (Setting<Boolean> setting) -> {
            ImBoolean tmp = new ImBoolean(setting.getValue());
            if (ImGui.checkbox(setting.getIdentifier().getDescription(), tmp)) {
                setting.setValue(tmp.get());
                SettingsInterface.VSYNC = setting.getValue();
            }
        }));
        settings.put(SettingIdentifiers.MOUSE_SENSITIVITY, new Setting<>(SettingIdentifiers.MOUSE_SENSITIVITY, 5f, (Setting<Float> setting) -> {
            float[] tmp = {setting.getValue()};
            if (ImGui.sliderFloat(setting.getIdentifier().getDescription(), tmp, 1f, 10f)) {
                setting.setValue(tmp[0]);
                SettingsInterface.MOUSE_SENSITIVITY = setting.getValue()/25f;
            }
        }));
        settings.put(SettingIdentifiers.CAMERA_POS_STEP, new Setting<>(SettingIdentifiers.CAMERA_POS_STEP, 5f, (Setting<Float> setting) -> {
            float[] tmp = {setting.getValue()};
            if (ImGui.sliderFloat(setting.getIdentifier().getDescription(), tmp, 1f, 10f)) {
                setting.setValue(tmp[0]);
                SettingsInterface.CAMERA_POS_STEP = setting.getValue()/100f;
            }
        }));
        loadFile(file);
    }

    /**
     * Load a file and fill the Container with its content
     * @param file the file's path to load
     */
    public void loadFile(String file) {
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

    /**
     * Save the current settings to a file
     * @param file the file's path to save to
     */
    public void saveFile(String file) {
        try {
            Properties prop = new Properties();
            for (Setting<?> setting : settings.values())
                prop.put(setting.getIdentifier().toString(), setting.serializedValue());
            prop.store(new FileWriter(file), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a setting by its Identifier
     * @param identifier the Setting Identifier to retrieve
     * @return the retrieved Setting, null otherwise
     */
    public Setting<?> getSetting(SettingIdentifiers identifier) {
        return settings.get(identifier);
    }

    /**
     * Apply all the current Settings to the Interface, thus to the Simulation
     */
    private void applySettings() {
        for (Setting<?> setting : settings.values()) {
            switch (setting.getIdentifier()) {
                case MAX_MOVE_DIST -> SettingsInterface.MAX_MOVE_DIST = (int) setting.getValue();
                case K_PLUS -> SettingsInterface.K_PLUS = (float) setting.getValue();
                case K_MINUS -> SettingsInterface.K_MINUS = (float) setting.getValue();
                case A_COUNT -> SettingsInterface.A_COUNT = (int) setting.getValue();
                case B_COUNT -> SettingsInterface.B_COUNT = (int) setting.getValue();
                case C_COUNT -> SettingsInterface.C_COUNT = (int) setting.getValue();
                case AGENT_COUNT -> SettingsInterface.AGENT_COUNT = (int) setting.getValue();
                case MEMORY_SIZE -> SettingsInterface.MEMORY_SIZE = (int) setting.getValue();
                case ERROR_RATE -> SettingsInterface.ERROR_RATE = (float) setting.getValue();
                case GIVE_UP_COOLDOWN -> SettingsInterface.GIVE_UP_COOLDOWN = (float) setting.getValue();
                case MARKER_COOLDOWN -> SettingsInterface.MARKER_COOLDOWN = (float) setting.getValue();
                case MARKER_RADIUS -> SettingsInterface.MARKER_RADIUS = (int) setting.getValue();
                case MARKER_ATTENUATION -> SettingsInterface.MARKER_ATTENUATION = (float) setting.getValue();
                case WORLD_SIZE -> SettingsInterface.WORLD_SIZE = (int) setting.getValue();
                case SHOW_MARKERS -> SettingsInterface.SHOW_MARKERS = (boolean) setting.getValue();
                case A_COLOR -> SettingsInterface.A_COLOR = (Vector4f) setting.getValue();
                case B_COLOR -> SettingsInterface.B_COLOR = (Vector4f) setting.getValue();
                case C_COLOR -> SettingsInterface.C_COLOR = (Vector4f) setting.getValue();
                case SPEED -> SettingsInterface.TARGET_UPS = (int) setting.getValue();
                case VSYNC -> SettingsInterface.VSYNC = (boolean) setting.getValue();
                case MOUSE_SENSITIVITY -> SettingsInterface.MOUSE_SENSITIVITY = (float) setting.getValue()/25f;
                case CAMERA_POS_STEP -> SettingsInterface.CAMERA_POS_STEP = (float) setting.getValue()/100f;
                case FPS_TARGET -> SettingsInterface.TARGET_FPS = (int) setting.getValue();
                case FOV -> SettingsInterface.FOV = (float) Math.toRadians((int) setting.getValue());

            }
        }
    }
}
