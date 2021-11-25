package engine.objects;


import org.joml.Vector3f;

/**
 * This class represent all the lights present in the Scene
 */
public class SceneLight {

    private Vector3f ambientLight;
    private PointLight[] pointLightList;

    /**
     * Get the ambient light color
     * @return the ambient light color
     */
    public Vector3f getAmbientLight() {
        return ambientLight;
    }

    /**
     * Set the ambient light color
     * @param ambientLight the new ambient light color
     */
    public void setAmbientLight(Vector3f ambientLight) {
        this.ambientLight = ambientLight;
    }

    /**
     * Get all the PointLights of the Scene
     * @return an array of all the PointLights of the Scene
     */
    public PointLight[] getPointLightList() {
        return pointLightList;
    }

    /**
     * Replace all the PointLights of the Scene
     * @param pointLightList the new PointLights as an array
     */
    public void setPointLightList(PointLight[] pointLightList) {
        this.pointLightList = pointLightList;
    }
}