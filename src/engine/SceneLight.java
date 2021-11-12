package engine;


import engine.graph.PointLight;
import org.joml.Vector3f;

public class SceneLight {

    private Vector3f ambientLight;

    private PointLight[] pointLightList;

    public Vector3f getAmbientLight() {
        return ambientLight;
    }

    public void setAmbientLight(Vector3f ambientLight) {
        this.ambientLight = ambientLight;
    }

    public PointLight[] getPointLightList() {
        return pointLightList;
    }

    public void setPointLightList(PointLight[] pointLightList) {
        this.pointLightList = pointLightList;
    }
}