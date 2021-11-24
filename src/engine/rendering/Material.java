package engine.rendering;

import org.joml.Vector4f;

public class Material {

    private static final Vector4f DEFAULT_COLOUR = new Vector4f(1f, 1f, 1f, 1f);

    private Vector4f ambientColour;

    private Vector4f diffuseColour;

    private Vector4f specularColour;

    private float reflectance;

    private Texture texture;

    private Texture normal;

    public Material() {
        this.ambientColour = DEFAULT_COLOUR;
        this.diffuseColour = DEFAULT_COLOUR;
        this.specularColour = DEFAULT_COLOUR;
        this.texture = null;
        this.reflectance = 0;
    }

    public Material(Vector4f colour, float reflectance) {
        this(colour, colour, colour, null, null, reflectance);
    }

    public Material(Texture texture, Texture normal) {
        this(DEFAULT_COLOUR, DEFAULT_COLOUR, DEFAULT_COLOUR, texture, normal, 0);
    }

    public Material(Texture texture, Texture normal, float reflectance) {
        this(DEFAULT_COLOUR, DEFAULT_COLOUR, DEFAULT_COLOUR, texture, normal, reflectance);
    }

    public Material(Vector4f ambientColour, Vector4f diffuseColour, Vector4f specularColour, Texture texture, Texture normal, float reflectance) {
        this.ambientColour = ambientColour;
        this.diffuseColour = diffuseColour;
        this.specularColour = specularColour;
        this.texture = texture;
        this.normal = normal;
        this.reflectance = reflectance;
    }

    public Vector4f getAmbientColour() {
        return ambientColour;
    }

    public void setAmbientColour(Vector4f ambientColour) {
        this.ambientColour = ambientColour;
    }

    public Vector4f getDiffuseColour() {
        return diffuseColour;
    }

    public Vector4f getSpecularColour() {
        return specularColour;
    }

    public float getReflectance() {
        return reflectance;
    }

    public boolean isTextured() {
        return this.texture != null;
    }

    public boolean hasNormal() { return this.normal != null; }

    public Texture getTexture() {
        return texture;
    }

    public Texture getNormal() {
        return normal;
    }
}
