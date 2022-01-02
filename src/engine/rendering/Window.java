package engine.rendering;

import static org.lwjgl.glfw.GLFW.*;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import settings.SettingsInterface;
import imgui.ImGuiLayer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * This class represent a Window used as a render target
 */
public class Window {

    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private final ImGuiLayer imGuiLayer;

    private final String title;

    private int width;
    private int height;
    private long windowHandle;
    private boolean resized;

    /**
     * Create a new Window
     * @param title the Window title
     * @param width the Window width in pixels
     * @param height the Window height in pixels
     * @param imGuiLayer the ImGui Main Layer
     */
    public Window(String title, int width, int height, ImGuiLayer imGuiLayer) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.resized = false;
        this.imGuiLayer = imGuiLayer;
    }

    /**
     * Initialize the Window
     */
    public void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        glfwWindowHint(GLFW_SAMPLES, 4);


        // Create the window
        windowHandle = glfwCreateWindow(width, height, title, NULL, NULL);
        if (windowHandle == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Setup resize callback
        glfwSetFramebufferSizeCallback(windowHandle, (window, width, height) -> {
            this.width = width;
            this.height = height;
            this.setResized(true);
        });

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            }
        });

        // Get the resolution of the primary monitor
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        // Center our window
        glfwSetWindowPos(
                windowHandle,
                (vidmode.width() - width) / 2,
                (vidmode.height() - height) / 2
        );

        // Make the OpenGL context current
        glfwMakeContextCurrent(windowHandle);

        if (SettingsInterface.VSYNC) {
            // Enable v-sync
            glfwSwapInterval(1);
        }

        // Make the window visible
        glfwShowWindow(windowHandle);

        GL.createCapabilities();

        glEnable(GL_MULTISAMPLE);  // Enabled Multisample
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glEnable(GL_DEPTH_TEST);

        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);

        imGuiGlfw.init(windowHandle, true);
        imGuiGl3.init(null);
        imGuiLayer.init();
    }

    /**
     * Return the Window id
     * @return the Window id
     */
    public long getWindowHandle() {
        return windowHandle;
    }

    /**
     * Return whether a key is pressed in the Window of not
     * @param keyCode the key to test for
     * @return is the Key pressed or not in the Window context
     */
    public boolean isKeyPressed(int keyCode) {
        return glfwGetKey(windowHandle, keyCode) == GLFW_PRESS;
    }

    /**
     * Return whether the window should be closed or not
     * @return Should the window be closed or not
     */
    public boolean windowShouldClose() {
        return glfwWindowShouldClose(windowHandle);
    }

    /**
     * Return the Window width in pixels
     * @return the Window width in pixels
     */
    public int getWidth() {
        return width;
    }

    /**
     * Return the Window height in pixels
     * @return the Window height in pixels
     */
    public int getHeight() {
        return height;
    }

    /**
     * Return whether the Window has been resized or not
     * @return Has the Window been resized or not
     */
    public boolean isResized() {
        return resized;
    }

    /**
     * Notify that the Window has been resized or not
     * @param resized the resized flag
     */
    public void setResized(boolean resized) {
        this.resized = resized;
    }

    /**
     * Grab the VSync attribute from Setting Container
     */
    public void updateVSync() {
        glfwSwapInterval(SettingsInterface.VSYNC ? 1 : 0);
    }

    /**
     * Update the Window by swapping the buffers
     */
    public void update() {
        updateVSync();
        glfwSwapBuffers(windowHandle);
        glfwPollEvents();
    }

    /**
     * Prepare the ImGui Main Layer
     */
    public void prepareImGui() {
        imGuiGlfw.newFrame();
        ImGui.newFrame();
    }

    /**
     * Update the ImGui Main Layer
     * @param elapsedTime the elapsed time in seconds
     * @param nbUpdate the number of update since last ImGui update
     */
    public void updateImGui(double elapsedTime, int nbUpdate) {
        imGuiLayer.render((float) elapsedTime, nbUpdate == 0 ? SettingsInterface.TARGET_UPS : (int) (nbUpdate / elapsedTime));
        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupWindowPtr = org.lwjgl.glfw.GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            org.lwjgl.glfw.GLFW.glfwMakeContextCurrent(backupWindowPtr);
        }
    }

    /**
     * Cleanup the Window and dispose it
     */
    public void cleanup() {
        imGuiGl3.dispose();
        imGuiGlfw.dispose();
        ImGui.destroyContext();
    }

    /**
     * Initialize the ImGui Main Layer
     */
    public void prepareImGuiTexture() {
        imGuiLayer.init();
    }
}
