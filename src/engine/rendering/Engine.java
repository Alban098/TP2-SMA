package engine.rendering;

import engine.utils.MouseInput;
import engine.utils.Timer;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.glfw.GLFW;
import simulation.imgui.ImGuiLayer;

public class Engine implements Runnable {

    public static final int TARGET_FPS = 60;
    public static int TARGET_UPS = 60;

    private final Window window;
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

    private final ImGuiLayer imGuiLayer;

    private final Timer timer;

    private final ILogic gameLogic;

    private final MouseInput mouseInput;

    public Engine(String windowTitle, int width, int height, boolean vSync, ILogic gameLogic, ImGuiLayer imGuiLayer) {
        window = new Window(windowTitle, width, height, vSync);
        mouseInput = new MouseInput();
        this.gameLogic = gameLogic;
        timer = new Timer();
        this.imGuiLayer = imGuiLayer;
    }

    @Override
    public void run() {
        try {
            init();
            gameLoop();
        } catch (Exception excp) {
            excp.printStackTrace();
        } finally {
            cleanup();
        }
    }

    protected void init() throws Exception {
        window.init();
        initImGui();
        imGuiGlfw.init(window.getWindowHandle(), true);
        imGuiGl3.init(null);
        timer.init();
        mouseInput.init(window);
        gameLogic.init(window);
    }

    private void initImGui() {
        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
    }

    protected void gameLoop() {
        float elapsedTime;
        float accumulator = 0f;

        while (!window.windowShouldClose()) {
            float interval = 1f / TARGET_UPS;
            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;

            imGuiGlfw.newFrame();
            ImGui.newFrame();

            input();
            while (accumulator >= interval) {
                update(interval);
                accumulator -= interval;
            }

            render(Math.min(accumulator / interval, 1));

            renderImGui();
            ImGui.render();
            imGuiGl3.renderDrawData(ImGui.getDrawData());

            if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
                final long backupWindowPtr = org.lwjgl.glfw.GLFW.glfwGetCurrentContext();
                ImGui.updatePlatformWindows();
                ImGui.renderPlatformWindowsDefault();
                GLFW.glfwMakeContextCurrent(backupWindowPtr);
            }

            window.update();

            if (!window.isvSync()) {
                sync();
            }
        }
    }

    private void renderImGui() {
        imGuiLayer.render();
    }

    protected void cleanup() {
        gameLogic.cleanup();
        imGuiGl3.dispose();
        imGuiGlfw.dispose();
        ImGui.destroyContext();
    }
    
    private void sync() {
        float loopSlot = 1f / TARGET_FPS;
        double endTime = timer.getLastLoopTime() + loopSlot;
        while (timer.getTime() < endTime) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored) {}
        }
    }

    protected void input() {
        if (!imGuiLayer.hasLayerVisible()) {
            mouseInput.input(window);
            gameLogic.input(window, mouseInput);
        }
    }

    protected void update(float interval) {
        gameLogic.update(window, interval, mouseInput);
    }

    protected void render(float updatePercent) {
        gameLogic.updateCamera(window, updatePercent, mouseInput);
        gameLogic.render(window);
    }
}
