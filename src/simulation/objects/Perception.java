package simulation.objects;

public class Perception {

    private final Object object;
    private final Agent source;
    private final float help_marker;

    public Perception(Agent source, Object object, float help_marker) {
        this.object = object;
        this.source = source;
        this.help_marker = help_marker;
    }

    public Object getObject() {
        return object;
    }

    public Agent getSource() {
        return source;
    }

    public float getHelpMarker() {
        return help_marker;
    }
}
