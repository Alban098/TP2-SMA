package engine.objects;

import org.joml.Vector3f;

/**
 * This class represent a Point emitting light
 */
public class PointLight {

    private final Vector3f color;
    private Vector3f position;
    private final float intensity;
    private Attenuation attenuation;

    /**
     * Create a new PointLight
     * @param color the light color
     * @param position the light position
     * @param intensity the light intensity
     */
    public PointLight(Vector3f color, Vector3f position, float intensity) {
        attenuation = new Attenuation(1, 0, 0);
        this.color = color;
        this.position = position;
        this.intensity = intensity;
    }

    /**
     * Create a new PointLight with an attenuation factor
     * @param color the light color
     * @param position the light position
     * @param intensity the light intensity
     * @param attenuation the attenuation factor
     */
    public PointLight(Vector3f color, Vector3f position, float intensity, Attenuation attenuation) {
        this(color, position, intensity);
        this.attenuation = attenuation;
    }

    /**
     * Create a new PointLight from an existing one
     * @param pointLight the PointLight to copy
     */
    public PointLight(PointLight pointLight) {
        this(new Vector3f(pointLight.getColor()), new Vector3f(pointLight.getPosition()),
                pointLight.getIntensity(), pointLight.getAttenuation());
    }

    /**
     * Return the Light's color
     * @return the Light's color
     */
    public Vector3f getColor() {
        return color;
    }

    /**
     * Return the Light's position
     * @return the Light's position
     */
    public Vector3f getPosition() {
        return position;
    }

    /**
     * Set the Light's position
     * @param position the new position
     */
    public void setPosition(Vector3f position) {
        this.position = position;
    }

    /**
     * Return the Light's intensity
     * @return the Light's intensity
     */
    public float getIntensity() {
        return intensity;
    }

    /**
     * Return the Light's attenuation factor
     * @return the Light's attenuation factor
     */
    public Attenuation getAttenuation() {
        return attenuation;
    }

    /**
     * Represent an Attenuation unit that can be applied to a Light Source
     */
    public record Attenuation(float constant, float linear, float exponent) {}
}