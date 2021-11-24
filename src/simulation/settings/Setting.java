package simulation.settings;

import org.joml.Vector4f;

import java.awt.*;
import java.util.function.Consumer;

public class Setting<T> {

    private final SettingIdentifiers identifier;
    private T value;
    private final Consumer<Setting<T>> renderer;

    public Setting(SettingIdentifiers identifier, T defaultValue, Consumer<Setting<T>> renderer) {
        this.identifier = identifier;
        this.value = defaultValue;
        this.renderer = renderer;
    }

    public T getValue() {
        return value;
    }

    public Setting<T> setValue(T value) {
        this.value = value;
        return this;
    }

    public SettingIdentifiers getIdentifier() {
        return identifier;
    }

    public Class<?> getType() {
        return value.getClass();
    }

    public void process() {
        renderer.accept(this);
    }

    public String serializedValue() {
        if (value instanceof Vector4f val) {
            return val.x + "/" + val.y + "/" + val.z + "/" + val.w;
        } else
            return value.toString();
    }

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