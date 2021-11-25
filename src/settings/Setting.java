package settings;

import org.joml.Vector4f;
import java.util.function.Consumer;

/**
 * This class represent a Setting of the Simulation
 * @param <T> the type of the Setting's value
 */
public class Setting<T> {

    private final SettingIdentifiers identifier;
    private T value;
    private final Consumer<Setting<T>> renderer;

    /**
     * Create a new Setting
     * @param identifier the Identifier of the Setting
     * @param defaultValue the default value of the Setting
     * @param renderer a function used to render the Setting field to the Settings Layer
     */
    public Setting(SettingIdentifiers identifier, T defaultValue, Consumer<Setting<T>> renderer) {
        this.identifier = identifier;
        this.value = defaultValue;
        this.renderer = renderer;
    }

    /**
     * Return the current setting's value
     * @return the Setting's value
     */
    public T getValue() {
        return value;
    }

    /**
     * Set the current Setting's value
     * @param value the new Setting's value
     */
    public void setValue(T value) {
        this.value = value;
    }

    /**
     * Return the Identifier of the Setting
     * @return the Setting's Identifier
     */
    public SettingIdentifiers getIdentifier() {
        return identifier;
    }

    /**
     * Render the Setting's field in accordance to its renderer function
     */
    public void process() {
        renderer.accept(this);
    }

    /**
     * Serialize the current Setting's value
     * @return a String representing the value
     */
    public String serializedValue() {
        if (value instanceof Vector4f val) {
            return val.x + "/" + val.y + "/" + val.z + "/" + val.w;
        } else
            return value.toString();
    }

    /**
     * Deserialize a String and set the Setting's value to it
     * @param val the serialized value
     */
    public void setSerializedValue(String val) {
        if (val != null) {
            if (value instanceof Vector4f) {
                String[] elements = val.split("/");
                value = (T) new Vector4f(Float.parseFloat(elements[0]), Float.parseFloat(elements[1]), Float.parseFloat(elements[2]), Float.parseFloat(elements[3]));
            } else if (value instanceof Float)
                value = (T) Float.valueOf(val);
            else if (value instanceof Boolean)
                value = (T) Boolean.valueOf(val);
            else if (value instanceof Integer)
                value = (T) Integer.valueOf(val);
            else if (value instanceof String)
                value = (T) val;
        }
    }
}