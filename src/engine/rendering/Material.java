package engine.rendering;

import org.joml.Vector4f;

/**
 * This class represent a Material that can be applied to a model
 * it contains properties that dictate how light interact with it
 * can be textured or a set color
 */
public class Material {

    private static final Vector4f DEFAULT_COLOUR = new Vector4f(1f, 1f, 1f, 1f);

    private Vector4f ambientColour;
    private final Vector4f diffuseColour;
    private final Vector4f specularColour;
    private final float reflectance;
    private final Texture texture;
    private Texture normal;

    /**
     * Create a new Material with default properties
     * White, non-textured and non-reflective
     */
    public Material() {
        this.ambientColour = DEFAULT_COLOUR;
        this.diffuseColour = DEFAULT_COLOUR;
        this.specularColour = DEFAULT_COLOUR;
        this.texture = null;
        this.reflectance = 0;
    }

    /**
     * Create a new non-textured Material
     * @param colour the Material color as RGBA (0f-1f)
     * @param reflectance the Material reflectance  from 0f to 1f
     */
    public Material(Vector4f colour, float reflectance) {
        this(colour, colour, colour, null, null, reflectance);
    }

    /**
     * Create a new textured Material with a normal map
     * @param texture the Material Texture
     * @param normal the Material normal map
     */
    public Material(Texture texture, Texture normal) {
        this(DEFAULT_COLOUR, DEFAULT_COLOUR, DEFAULT_COLOUR, texture, normal, 0);
    }

    /**
     * Create a new textured Material with a normal map and reflectance value
     * @param texture the Material Texture
     * @param normal the Material normal map
     * @param reflectance the Material reflectance from 0f to 1f
     */
    public Material(Texture texture, Texture normal, float reflectance) {
        this(DEFAULT_COLOUR, DEFAULT_COLOUR, DEFAULT_COLOUR, texture, normal, reflectance);
    }

    /**
     * Create a new Material with all properties
     * @param ambientColour the Material color as RGBA (0f-1f)
     * @param diffuseColour the Material diffuse color as RGBA (0f-1f)
     * @param specularColour the Material specular color as RGBA (0f-1f)
     * @param texture the Material Texture
     * @param normal the Material normal map
     * @param reflectance the Material reflectance from 0f to 1f
     */
    public Material(Vector4f ambientColour, Vector4f diffuseColour, Vector4f specularColour, Texture texture, Texture normal, float reflectance) {
        this.ambientColour = ambientColour;
        this.diffuseColour = diffuseColour;
        this.specularColour = specularColour;
        this.texture = texture;
        this.normal = normal;
        this.reflectance = reflectance;
    }

    /**
     * Return the Material ambient color
     * @return the Material ambient color as RGBA (0f-1f)
     */
    public Vector4f getAmbientColour() {
        return ambientColour;
    }

    /**
     * Set the Material ambient color
     * @param ambientColour the Material ambient color as RGBA (0f-1f)
     */
    public void setAmbientColour(Vector4f ambientColour) {
        this.ambientColour = ambientColour;
    }

    /**
     * Return the Material diffuse color
     * @return the Material diffuse color as RGBA (0f-1f)
     */
    public Vector4f getDiffuseColour() {
        return diffuseColour;
    }

    /**
     * Return the Material specular color
     * @return the Material specular color as RGBA (0f-1f)
     */
    public Vector4f getSpecularColour() {
        return specularColour;
    }

    /**
     * Return the Material reflectance
     * @return the Material reflectance from 0f to 1f
     */
    public float getReflectance() {
        return reflectance;
    }

    /**
     * Return whether the Material is textured or not
     * @return is the Material textured or not
     */
    public boolean isTextured() {
        return this.texture != null;
    }

    /**
     * Return whether the Material is normal mapped or not
     * @return is the Material normal mapped or not
     */
    public boolean hasNormal() { return this.normal != null; }

    /**
     * Return the Material Texture
     * @return the Material Texture
     */
    public Texture getTexture() {
        return texture;
    }

    /**
     * Return the Material normal map
     * @return the Material normal map
     */
    public Texture getNormal() {
        return normal;
    }
}
