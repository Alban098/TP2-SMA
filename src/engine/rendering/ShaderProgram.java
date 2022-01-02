package engine.rendering;

import java.util.HashMap;
import java.util.Map;

import engine.objects.PointLight;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import static org.lwjgl.opengl.GL20.*;
import org.lwjgl.system.MemoryStack;

/**
 * This class represent a Shader used by the renderer
 */
public class ShaderProgram {

    private final int programId;
    private int vertexShaderId;
    private int fragmentShaderId;
    private final Map<String, Integer> uniforms;

    /**
     * Create a new Shader
     * @throws Exception thrown when unable to create the Shader
     */
    public ShaderProgram() throws Exception {
        programId = glCreateProgram();
        if (programId == 0) {
            throw new Exception("Could not create Shader");
        }
        uniforms = new HashMap<>();
    }

    /**
     * Create a Uniform
     * @param uniformName the name of the Uniform
     * @throws Exception thrown when unable to create the uniform
     */
    public void createUniform(String uniformName) throws Exception {
        int uniformLocation = glGetUniformLocation(programId, uniformName);
        if (uniformLocation < 0) {
            throw new Exception("Could not find uniform:" + uniformName);
        }
        uniforms.put(uniformName, uniformLocation);
    }

    /**
     * Create a Uniform array of type PointLight
     * @param uniformName the name of the Uniform
     * @throws Exception thrown when unable to create the uniform
     */
    public void createPointLightListUniform(String uniformName, int size) throws Exception {
        for (int i = 0; i < size; i++) {
            createPointLightUniform(uniformName + "[" + i + "]");
        }
    }

    /**
     * Create a Uniform of type PointLight
     * @param uniformName the name of the Uniform
     * @throws Exception thrown when unable to create the uniform
     */
    public void createPointLightUniform(String uniformName) throws Exception {
        createUniform(uniformName + ".colour");
        createUniform(uniformName + ".position");
        createUniform(uniformName + ".intensity");
        createUniform(uniformName + ".att.constant");
        createUniform(uniformName + ".att.linear");
        createUniform(uniformName + ".att.exponent");
    }

    /**
     * Create a Uniform of type Material
     * @param uniformName the name of the Uniform
     * @throws Exception thrown when unable to create the uniform
     */
    public void createMaterialUniform(String uniformName) throws Exception {
        createUniform(uniformName + ".ambient");
        createUniform(uniformName + ".diffuse");
        createUniform(uniformName + ".specular");
        createUniform(uniformName + ".hasTexture");
        createUniform(uniformName + ".hasNormal");
        createUniform(uniformName + ".reflectance");
    }

    /**
     * Load a Uniform of type Matrix4f
     * @param uniformName the name of the Uniform to load
     * @param value the value to load
     */
    public void setUniform(String uniformName, Matrix4f value) {
        // Dump the matrix into a float buffer
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniformMatrix4fv(uniforms.get(uniformName), false,
                               value.get(stack.mallocFloat(16)));
        }
    }

    /**
     * Load a Uniform of type Integer
     * @param uniformName the name of the Uniform to load
     * @param value the value to load
     */
    public void setUniform(String uniformName, int value) {
        glUniform1i(uniforms.get(uniformName), value);
    }

    /**
     * Load a Uniform of type Float
     * @param uniformName the name of the Uniform to load
     * @param value the value to load
     */
    public void setUniform(String uniformName, float value) {
        glUniform1f(uniforms.get(uniformName), value);
    }

    /**
     * Load a Uniform of type Vector3f
     * @param uniformName the name of the Uniform to load
     * @param value the value to load
     */
    public void setUniform(String uniformName, Vector3f value) {
        glUniform3f(uniforms.get(uniformName), value.x, value.y, value.z);
    }

    /**
     * Load a Uniform of type Vector4f
     * @param uniformName the name of the Uniform to load
     * @param value the value to load
     */
    public void setUniform(String uniformName, Vector4f value) {
        glUniform4f(uniforms.get(uniformName), value.x, value.y, value.z, value.w);
    }

    /**
     * Load a Uniform of type PointLight by its id
     * @param uniformName the name of the Uniform to load
     * @param pointLight the value to load
     * @param pos the id of the Light
     */
    public void setUniform(String uniformName, PointLight pointLight, int pos) {
        setUniform(uniformName + "[" + pos + "]", pointLight);
    }

    /**
     * Load a Uniform of type PointLight
     * @param uniformName the name of the Uniform to load
     * @param pointLight the value to load
     */
    public void setUniform(String uniformName, PointLight pointLight) {
        setUniform(uniformName + ".colour", pointLight.getColor());
        setUniform(uniformName + ".position", pointLight.getPosition());
        setUniform(uniformName + ".intensity", pointLight.getIntensity());
        PointLight.Attenuation att = pointLight.getAttenuation();
        setUniform(uniformName + ".att.constant", att.constant());
        setUniform(uniformName + ".att.linear", att.linear());
        setUniform(uniformName + ".att.exponent", att.exponent());
    }

    /**
     * Load a Uniform of type Material
     * @param uniformName the name of the Uniform to load
     * @param material the value to load
     */
    public void setUniform(String uniformName, Material material) {
        setUniform(uniformName + ".ambient", material.getAmbientColour());
        setUniform(uniformName + ".diffuse", material.getDiffuseColour());
        setUniform(uniformName + ".specular", material.getSpecularColour());
        setUniform(uniformName + ".hasTexture", material.isTextured() ? 1 : 0);
        setUniform(uniformName + ".hasNormal", material.hasNormal() ? 1 : 0);
        setUniform(uniformName + ".reflectance", material.getReflectance());
    }

    /**
     * Create a Vertex Shader from a shader file
     * @param shaderCode Path to the shader file
     * @throws Exception thrown when unable to load the Shader file
     */
    public void createVertexShader(String shaderCode) throws Exception {
        vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
    }

    /**
     * Create a Fragment Shader from a shader file
     * @param shaderCode Path to the shader file
     * @throws Exception thrown when unable to load the Shader file
     */
    public void createFragmentShader(String shaderCode) throws Exception {
        fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
    }

    /**
     * Create a new Shader from a shader code file
     * @param shaderCode Path to the shader file
     * @param shaderType the Shader type
     * @return the Shader id
     * @throws Exception thrown when unable to load the Shader file
     */
    protected int createShader(String shaderCode, int shaderType) throws Exception {
        int shaderId = glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new Exception("Error creating shader. Type: " + shaderType);
        }

        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new Exception("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024));
        }

        glAttachShader(programId, shaderId);

        return shaderId;
    }

    /**
     * Validate and Link the Shader program to the GPU
     * @throws Exception thrown when unable to Link or Validate
     */
    public void link() throws Exception {
        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw new Exception("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

        if (vertexShaderId != 0) {
            glDetachShader(programId, vertexShaderId);
        }
        if (fragmentShaderId != 0) {
            glDetachShader(programId, fragmentShaderId);
        }

        glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(programId, 1024));
        }
    }

    /**
     * Bind the Shader for rendering
     */
    public void bind() {
        glUseProgram(programId);
    }

    /**
     * Unbind the Shader
     */
    public void unbind() {
        glUseProgram(0);
    }

    /**
     * Cleanup the Shader
     */
    public void cleanup() {
        unbind();
        if (programId != 0) {
            glDeleteProgram(programId);
        }
    }
}
