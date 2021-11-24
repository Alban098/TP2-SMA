package engine.rendering;

import engine.utils.MouseInput;
import engine.utils.Timer;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.glfw.GLFW;
import simulation.Constants;
import simulation.imgui.ImGuiLayer;

public class Engine implements Runnable {

    public static int TARGET_FPS = 1200;
    public static float TARGET_UPS = 1f;

    private final Window window;
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

    private final ImGuiLayer imGuiLayer;

    private final Timer timer;

    private final ILogic gameLogic;

    private final MouseInput mouseInput;

    public Engine(String windowTitle, int width, int height, ILogic gameLogic, ImGuiLayer imGuiLayer) {
        window = new Window(windowTitle, width, height);
        mouseInput = new MouseInput();
        this.gameLogic = gameLogic;
        timer = new Timer();
        this.imGuiLayer = imGuiLayer;
        imGuiLayer.linkEngine(this);
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
        imGuiLayer.init();
        timer.init();
        mouseInput.init(window);
        gameLogic.init(window);
    }

    public void reinitImGuiTexture() {
        imGuiLayer.init();
    }

    private void initImGui() {
        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
    }

    protected void gameLoop() {
        float elapsedTime;
        float accumulator = 0f;
        float interval;
        int nbUpdate;
        while (!window.windowShouldClose()) {

            interval = 1f / Math.max((int) (TARGET_FPS * TARGET_UPS), 1);
            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;

            imGuiGlfw.newFrame();
            ImGui.newFrame();

            input();
            nbUpdate = 0;
            while (accumulator >= interval) {
                update(interval);
                accumulator -= interval;
                nbUpdate++;
            }

            render(Math.min(accumulator / interval, 1));

            renderImGui(elapsedTime, nbUpdate == 0 ? Math.max((int) (TARGET_FPS * TARGET_UPS), 1) : (int) (nbUpdate / elapsedTime));
            ImGui.render();
            imGuiGl3.renderDrawData(ImGui.getDrawData());

            if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
                final long backupWindowPtr = org.lwjgl.glfw.GLFW.glfwGetCurrentContext();
                ImGui.updatePlatformWindows();
                ImGui.renderPlatformWindowsDefault();
                GLFW.glfwMakeContextCurrent(backupWindowPtr);
            }

            window.update();

            if (!Constants.VSYNC) {
                sync();
            }
        }
    }

    private void renderImGui(float elapsedTime, int nbUpdate) {
        imGuiLayer.render(elapsedTime, nbUpdate);
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
        mouseInput.input(window);
        gameLogic.input(window, mouseInput);
    }

    protected void update(float interval) {
        gameLogic.update(window, interval, mouseInput);
    }

    protected void render(float updatePercent) {
        gameLogic.updateCamera(window, updatePercent, mouseInput);
        gameLogic.render(window);
    }
}
